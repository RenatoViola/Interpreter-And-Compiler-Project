package ast.references;

import ast.Exp;
import types.RefType;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;

public class ASTDeref implements Exp {

    public Exp e;

    public ASTDeref(Exp e) {
        this.e = e;
    }

    @Override
    public Type getType() {
        return ((RefType) e.getType()).referencedType();
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
