package ast.types;

import ast.Exp;
import types.IntType;
import types.TypingException;
import values.IntValue;

public class ASTInt implements Exp {

    public final IntValue value;

    public ASTInt(int v) {
        value = new IntValue(v);
    }

    @Override
    public IntType getType() {
        return IntType.singleton;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException {
        return v.visit(this, env);
    }
}
