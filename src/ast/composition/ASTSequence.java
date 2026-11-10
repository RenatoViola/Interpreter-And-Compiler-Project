package ast.composition;

import ast.Exp;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;
import java.util.List;

public class ASTSequence implements Exp {

    public List<Exp> sequence;

    public ASTSequence(List<Exp> sequence) {
        this.sequence = sequence;
    }

    @Override
    public Type getType() {
        return sequence.getLast().getType();
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
