package ast.identifiers;

import ast.Exp;
import types.Type;
import types.TypingException;

public class ASTId implements Exp {

    public String name;
    private Type type;

    public ASTId(String n) {
        name = n;
        type = null;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException {
        return v.visit(this, env);
    }

}
