package symbols;

import datastructures.Pair;
import target.*;
import types.Type;

import java.util.ArrayList;
import java.util.List;

import static symbols.Helper.getFieldDescriptor;

public class BlockSeq {

    List<Frame> frames;
    Frame currFrame;
    BasicBlock block;
    public CompEnv env;

    public BlockSeq() {
        this(new CompEnv());
    }

    public BlockSeq(CompEnv env) {
        frames = new ArrayList<>();
        block = new BasicBlock();
        this.env = env;
    }

    public Pair<Frame, CompEnv> beginScope(Frame f) {
        CompEnv newEnv = env.beginScope();
        frames.add(f);
        currFrame = f;
        env = newEnv;

        return new Pair<>(f, newEnv);
    }

    public Frame getCurrFrame() {
        return currFrame;
    }

    public CompEnv getEnv() {
        return env;
    }

    public void advanceToFrame(Frame f, CompEnv env) {
        currFrame = f;
        this.env = env;

        block.addInstruction(new New(f.name()));
        block.addInstruction(new Dup());
        block.addInstruction(new InvokeSpecial(f.name()));
        block.addInstruction(new Dup());
        block.addInstruction(new ALoad(0));
        String fieldDescriptor = "L" + (f.prev() == null ? "java/lang/Object" : f.prev().name()) + ";";
        block.addInstruction(new PutField(f.name(), "sl", fieldDescriptor));
        block.addInstruction(new AStore(0));
    }

    public void endScope(Frame f, CompEnv env) {
        frames.remove(f);
        currFrame = f.prev();
        this.env = env.endScope();

        block.addInstruction(new ALoad(0));
        String fieldDescriptor = "L" + (currFrame == null ? "java/lang/Object" : currFrame.name()) + ";";
        block.addInstruction(new GetField(f.name(), "sl", fieldDescriptor));
        block.addInstruction(new AStore(0));
    }

    public void addInstruction(Instruction i) {
        block.addInstruction(i);
    }

    public void build(StringBuilder sb) {
        block.build(sb);
    }

    public void fetch(String id, Type type) {
        Pair<Integer, Integer> pair = env.findPair(id);

        int depth = pair.first();
        int width = pair.second();

        block.addInstruction(new ALoad(0));

        Frame tempFrame = new Frame(currFrame.nFields(), currFrame.types(), currFrame.id(), currFrame.prev());
        block.addInstruction(new CheckCast(tempFrame.name()));

        for (int i = 0; i < depth; i++) {
            String frameName = tempFrame.name();
            block.addInstruction(new GetField(frameName, "sl", "L" + tempFrame.prev().name() + ";"));
            tempFrame = tempFrame.prev();
        }

        block.addInstruction(new GetField(tempFrame.name(), "loc_" + width, getFieldDescriptor(type)));
    }

}
