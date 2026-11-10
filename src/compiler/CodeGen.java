package compiler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


import ast.*;
import ast.arithmetics.*;
import ast.functions.ASTFun;
import ast.functions.ASTApply;
import ast.identifiers.ASTId;
import ast.identifiers.ASTLet;
import ast.control.ASTIfThenElse;
import ast.logic.*;
import ast.control.ASTPrint;
import ast.composition.ASTSequence;
import ast.references.ASTAssignRef;
import ast.references.ASTDeref;
import ast.types.ASTBool;
import ast.types.ASTInt;
import ast.types.ASTRef;
import ast.types.ASTUnit;
import ast.control.ASTWhile;
import datastructures.Pair;
import datastructures.Triple;
import symbols.BlockSeq;
import symbols.Closure;
import symbols.CompEnv;
import symbols.Frame;
import target.*;
import types.*;
import ast.Exp.Visitor;

import static symbols.Helper.*;


public class CodeGen implements Visitor<Void, Void> {

	BlockSeq blocks;
	private int labelCounter, closureCounter, frameCounter;

	public CodeGen(BlockSeq blocks) {
		this.blocks = blocks;
		labelCounter = 0;
		closureCounter = 0;
		frameCounter = 0;
	}

	// Types

	@Override
	public Void visit(ASTInt i, Void arg) {
		blocks.addInstruction(new SIPush(i.value.value()) );

		return null;
	}

	@Override
	public Void visit(ASTBool b, Void arg) {
		blocks.addInstruction(new IConst(b.value.value() ? 1 : 0));

		return null;
	}

	@Override
	public Void visit(ASTUnit unit, Void arg) throws TypingException {
		blocks.addInstruction(new Nop());

		return null;
	}

	// Arithmetics

	@Override
	public Void visit(ASTAdd e, Void arg) throws TypingException, FileNotFoundException {
		e.e1.accept(this, null);
		e.e2.accept(this, null);

		blocks.addInstruction(new IAdd());

		return null;
	}

	@Override
	public Void visit(ASTMult e, Void arg) throws TypingException, FileNotFoundException {
		e.e1.accept(this, null);
		e.e2.accept(this, null);

		blocks.addInstruction(new IMul());

		return null;
	}

	public Void visit(ASTDiv e, Void arg) throws TypingException, FileNotFoundException {
		e.e1.accept(this, null);
		e.e2.accept(this, null);

		blocks.addInstruction(new IDiv());

		return null;
	}

	@Override
	public Void visit(ASTSub e, Void arg) throws TypingException, FileNotFoundException {
		e.e1.accept(this, null);
		e.e2.accept(this, null);

		blocks.addInstruction(new ISub());

		return null;
	}

	// Relational Logic

	@Override
	public Void visit(ASTAnd e, Void arg) throws TypingException, FileNotFoundException {
		e.e1.accept(this, null);
		e.e2.accept(this, null);
		blocks.addInstruction(new IAnd());

		return null;
	}

	@Override
	public Void visit(ASTOr e, Void arg) throws TypingException, FileNotFoundException {
		e.e1.accept(this, null);
		e.e2.accept(this, null);
		blocks.addInstruction(new IOr());

		return null;
	}

	@Override
	public Void visit(ASTEq e, Void arg) throws TypingException, FileNotFoundException {
		String label1 = generateLabel(), label2 = generateLabel();

		e.e1.accept(this, null);
		e.e2.accept(this, null);
		blocks.addInstruction(new If_icmpeq(label1));
		pushComparison(label1, label2);

		return null;
	}

	@Override
	public Void visit(ASTNot n, Void arg) throws TypingException, FileNotFoundException {
		String label1 = generateLabel(), label2 = generateLabel();

		n.e.accept(this, null);
		blocks.addInstruction(new IFeq(label1));
		pushComparison(label1, label2);

		return null;
	}

	@Override
	public Void visit(ASTGt e, Void arg) throws TypingException, FileNotFoundException {
		String label1 = generateLabel(), label2 = generateLabel();

		e.e1.accept(this, null);
		e.e2.accept(this, null);
		blocks.addInstruction(new If_icmpgt(label1));
		pushComparison(label1, label2);

		return null;
	}

	// Identifiers and declaration

	@Override
	public Void visit(ASTId x, Void arg) throws TypingException {
		blocks.fetch(x.name, x.getType());

		return null;
	}

	@Override
	public Void visit(ASTLet e, Void arg) throws TypingException, FileNotFoundException {

		Pair<Frame, CompEnv> p = blocks.beginScope(new Frame(e.bindings.size(), new ArrayList<>(), frameCounter++, blocks.getCurrFrame()));
		Frame frame = p.first();
		CompEnv newEnv = p.second();

		blocks.advanceToFrame(frame, newEnv);

		for (int idx = 0; idx < frame.nFields(); idx++) {
			Triple<String, Exp, Type> binding = e.bindings.get(idx);

			Type bindingType = binding.second().getType();
			frame.types().add(bindingType);
			newEnv.bind(binding.first(), idx);

			String location = "loc_" + idx;

			blocks.addInstruction(new ALoad(0));
			binding.second().accept(this, null);

			String fieldDescriptor = getFieldDescriptor(bindingType);
			blocks.addInstruction(new PutField(frame.name(), location, fieldDescriptor));
		}

		frame.generateCode();
		e.body.accept(this, null);
		blocks.endScope(frame, newEnv);

		return null;
	}

	// Control Structures

	@Override
	public Void visit(ASTIfThenElse e, Void arg) throws TypingException, FileNotFoundException {
		String elseLabel = generateLabel(), endLabel = generateLabel();

		e.condition.accept(this, null);
		blocks.addInstruction(new IFeq(elseLabel));
		e.thenExp.accept(this, null);

		if (e.elseExp != null) {
			blocks.addInstruction(new Goto(endLabel));
			blocks.addInstruction(new Label(elseLabel));
			e.elseExp.accept(this, null);
			blocks.addInstruction(new Label(endLabel));
		} else {
			blocks.addInstruction(new Label(elseLabel));
		}

		return null;
	}


	@Override
	public Void visit(ASTWhile w, Void arg) throws TypingException, FileNotFoundException {
		String startLabel = generateLabel();
		String endLabel = generateLabel();

		blocks.addInstruction(new Label(startLabel));
		w.condition.accept(this, null);
		blocks.addInstruction(new IFeq(endLabel));
		w.doExp.accept(this, null);
		blocks.addInstruction(new Goto(startLabel));
		blocks.addInstruction(new Label(endLabel));

		return null;
	}

	@Override
	public Void visit(ASTPrint e, Void arg) throws TypingException, FileNotFoundException {
		blocks.addInstruction(new GetStatic("java/lang/System", "out", "Ljava/io/PrintStream;"));
		e.e.accept(this, null);
		blocks.addInstruction(new InvokeStatic("java/lang/String", String.format("valueOf(%s)", getFieldDescriptor(e.e.getType())), "Ljava/lang/String;"));
		blocks.addInstruction(new InvokeVirtual("java/io/PrintStream", e.EOL ? "println" : "print", "(Ljava/lang/String;)V"));

		return null;
	}

	@Override
	public Void visit(ASTSequence s, Void arg) throws TypingException, FileNotFoundException {
		for (Exp exp : s.sequence) {
			exp.accept(this, arg);
		}

		return null;
	}

	// Reference operations

	@Override
	public Void visit(ASTRef r, Void arg) throws TypingException, FileNotFoundException {
		String ref = r.getType().toString();

		String fieldDescriptor = getFieldDescriptor(r.e.getType());
		String classContent = String.format("""
        .class %s
        .super java/lang/Object
        .field public value %s
        .method public <init>()V
        aload_0
        invokespecial java/lang/Object/<init>()V
        return
        .end method
        """, ref, fieldDescriptor);

		writeToJasminFile(classContent, ref + ".j");

		blocks.addInstruction(new New(ref));
		blocks.addInstruction(new Dup());
		blocks.addInstruction(new InvokeSpecial(ref));
		blocks.addInstruction(new Dup());
		r.e.accept(this, null);
		blocks.addInstruction(new PutField(ref, "value", fieldDescriptor));

		return null;
	}

	@Override
	public Void visit(ASTAssignRef a, Void arg) throws TypingException, FileNotFoundException {
		a.e1.accept(this, null);
		a.e2.accept(this, null);

		Type refType = a.e1.getType();
		Type valueType = a.e2.getType();
		String fieldDescriptor = getFieldDescriptor(valueType);

		blocks.addInstruction(new PutField(refType.toString(), "value", fieldDescriptor));

		return null;
	}

	@Override
	public Void visit(ASTDeref d, Void arg) throws TypingException, FileNotFoundException {
		d.e.accept(this, null);

		String fieldDescriptor = getFieldDescriptor(d.getType());
		blocks.addInstruction(new GetField(d.e.getType().toString(), "value", fieldDescriptor));

		return null;
	}

	// Functions

	@Override
	public Void visit(ASTFun f, Void arg) throws TypingException, FileNotFoundException {

		Frame frame = new Frame(f.params.size(), new ArrayList<>(), frameCounter++, blocks.getCurrFrame());
		Closure closure = new Closure(f.params, f.body, closureCounter++, frame, blocks.getEnv());

		closure.generateInterfaceCode();
		closure.generateCode();

		blocks.addInstruction(new New(closure.name()));
		blocks.addInstruction(new Dup());
		blocks.addInstruction(new InvokeSpecial(closure.name()));
		blocks.addInstruction(new Dup());
		blocks.addInstruction(new ALoad(0));
		String fieldDescriptor = "L" + (frame.prev() == null ? "java/lang/Object" : frame.prev().name()) + ";";
		blocks.addInstruction(new PutField(closure.name(), "sl", fieldDescriptor));

		return null;
	}

	@Override
	public Void visit(ASTApply f, Void arg) throws TypingException, FileNotFoundException {
		List<Type> types = f.arguments.stream().map(Exp::getType).toList();
		Type rType = f.getType();

		f.function.accept(this, null);
		blocks.addInstruction(new CheckCast(interfaceName(types, rType)));

		for (Exp exp : f.arguments) {
			exp.accept(this, null);
		}

		blocks.addInstruction(new InvokeInterface(interfaceName(types, rType), "apply" + applyDescriptor(types, rType), f.arguments.size() + 1));

		return null;
	}

	// Helper methods

	public static BlockSeq codeGen(Exp e) throws TypingException, FileNotFoundException {
		CodeGen cg = new CodeGen(new BlockSeq());
		e.accept(cg, null);
		return cg.blocks;
	}

	private static StringBuilder genPreAndPost(BlockSeq blocks) {
		String preamble = """
							.class public MicroML
							.super java/lang/Object
							.method public <init>()V
										aload_0
										invokenonvirtual java/lang/Object/<init>()V
										return
							.end method
							.method public static main([Ljava/lang/String;)V
								.limit locals 10
								.limit stack 256
								; setup local variables:
								;    1 - the PrintStream object held in java.lang.out
							""";
		String footer =
				"""
				return
				.end method
				""";
		StringBuilder sb = new StringBuilder(preamble);
		blocks.build(sb);
		sb.append(footer);
		return sb;
	}

	public static void writeToFile(Exp e, String filename) throws FileNotFoundException, TypingException {
		StringBuilder sb = genPreAndPost(codeGen(e));
		writeToJasminFile(sb.toString(), filename);
	}

	private void pushComparison(String label1, String label2) {
		blocks.addInstruction(new SIPush(0));
		blocks.addInstruction(new Goto(label2));
		blocks.addInstruction(new Label(label1));
		blocks.addInstruction(new SIPush(1));
		blocks.addInstruction(new Label(label2));
	}

	private String generateLabel() {
		return "L" + ++labelCounter;
	}
}