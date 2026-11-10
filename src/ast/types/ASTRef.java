package ast.types;

import ast.Exp;
import types.RefType;
import types.TypingException;

import java.io.FileNotFoundException;

public class ASTRef implements Exp {

    public Exp e;

    public ASTRef(Exp e) {
        this.e = e;
    }

    @Override
    public RefType getType() {
        return new RefType(e.getType());
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
