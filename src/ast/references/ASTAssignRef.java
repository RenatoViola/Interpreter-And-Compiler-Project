package ast.references;

import ast.Exp;
import types.Type;
import types.TypingException;
import types.UnitType;

import java.io.FileNotFoundException;

public class ASTAssignRef implements Exp {

    public Exp e1, e2;

    public ASTAssignRef(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Type getType() {
        return UnitType.singleton;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
