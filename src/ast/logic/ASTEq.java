package ast.logic;

import ast.Exp;
import types.BoolType;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;

public class ASTEq implements Exp {

    public Exp e1, e2;

    public ASTEq(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
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
