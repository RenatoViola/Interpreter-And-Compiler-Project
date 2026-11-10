package ast.types;

import ast.Exp;
import types.TypingException;
import types.UnitType;

public class ASTUnit implements Exp {

    public ASTUnit() {}

    @Override
    public UnitType getType() {
        return UnitType.singleton;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException {
        return v.visit(this, env);
    }
}
