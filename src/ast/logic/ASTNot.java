package ast.logic;

import ast.Exp;
import types.BoolType;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;

public class ASTNot implements Exp {

    public Exp e;

    public ASTNot(Exp e) {
        this.e = e;
    }

    @Override
    public Type getType() {
        return BoolType.singleton;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
