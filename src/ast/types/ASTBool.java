package ast.types;

import ast.Exp;
import types.BoolType;
import types.TypingException;
import values.BoolValue;

public class ASTBool implements Exp {

    public final BoolValue value;

    public ASTBool(boolean v) {
        value = new BoolValue(v);
    }

    @Override
    public BoolType getType() {
        return BoolType.singleton;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException {
        return v.visit(this, env);
    }
}
