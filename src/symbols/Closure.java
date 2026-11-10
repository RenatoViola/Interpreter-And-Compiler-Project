package symbols;

import ast.Exp;
import compiler.CodeGen;
import datastructures.Pair;
import target.*;
import types.*;

import java.io.FileNotFoundException;
import java.util.List;

import static symbols.Helper.*;

public record Closure(List<Pair<String, Type>> params, Exp body, int id, Frame frame, CompEnv env) {

    public String name() {
        return "closure_" + id;
    }

    public void generateInterfaceCode() throws FileNotFoundException {
        List<Type> types = params.stream().map(Pair::second).toList();
        Type rType = body.getType();
        String interfaceName = interfaceName(types, rType);

        String str = String.format("""
                .interface public %s
                .super java/lang/Object
                .method public abstract apply%s
                .end method
                """, interfaceName, applyDescriptor(types, rType));

        writeToJasminFile(str, interfaceName + ".j");
    }

    public void generateCode() throws FileNotFoundException, TypingException {

        List<Type> types = params.stream().map(Pair::second).toList();
        Type rType = body.getType();

        String fieldDescriptor = "L" + (frame.prev() == null ? "java/lang/Object" : frame.prev().name()) + ";";
        String str = String.format("""
                .class public %s
                .super java/lang/Object
                .implements %s
                .field public sl %s
                %s
                .method public <init>()V
                aload_0
                invokenonvirtual java/lang/Object/<init>()V
                return
                .end method
                """, this.name(), interfaceName(types, rType), fieldDescriptor, this.generateApplyCode(types, rType));

        writeToJasminFile(str, this.name() + ".j");
    }

    private String generateApplyCode(List<Type> types, Type rType) throws FileNotFoundException, TypingException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("""
                .method public apply%s
                .limit locals %d
                .limit stack %d
                """, applyDescriptor(types, rType), params.size() + 2, params.size() + 2));

        BlockSeq blocks = new BlockSeq(env);
        CodeGen functionCodeGen = new CodeGen(blocks);

        Pair<Frame, CompEnv> p = blocks.beginScope(frame);
        Frame newFrame = p.first();
        CompEnv newEnv = p.second();

        blocks.addInstruction(new New(newFrame.name()));
        blocks.addInstruction(new Dup());
        blocks.addInstruction(new InvokeSpecial(newFrame.name()));
        blocks.addInstruction(new Dup());
        blocks.addInstruction(new ALoad(0));

        String fieldDescriptor = "L" + newFrame.prev().name() + ";";
        blocks.addInstruction(new GetField(this.name(), "sl", fieldDescriptor));
        blocks.addInstruction(new PutField(newFrame.name(), "sl", fieldDescriptor));

        for (int i = 0; i < params.size(); i++) {
            Pair<String, Type> param = params.get(i);
            String id = param.first();
            Type t = param.second();
            newFrame.types().add(t);
            newEnv.bind(id, i);

            blocks.addInstruction(new Dup());

            if (t instanceof IntType || t instanceof BoolType) {
                blocks.addInstruction(new ILoad(i + 1));
            } else if (t instanceof RefType || t instanceof FunType) {
                blocks.addInstruction(new ALoad(i + 1));
            } else {
                throw new IllegalArgumentException("Unsupported type: " + t);
            }

            blocks.addInstruction(new PutField(newFrame.name(), "loc_" + i, getFieldDescriptor(t)));
        }
        blocks.addInstruction(new AStore(0));

        newFrame.generateCode();

        body.accept(functionCodeGen, null);
        Type t = body.getType();

        if (t instanceof IntType || t instanceof BoolType) {
            blocks.addInstruction(new IReturn());
        }   else if (t instanceof UnitType) {
            blocks.addInstruction(new Return());
        } else if (t instanceof RefType || t instanceof FunType) {
            blocks.addInstruction(new AReturn());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + t);
        }

        blocks.build(sb);
        blocks.endScope(frame, newEnv);

        sb.append("""
                .end method""");

        return sb.toString();
    }
}
