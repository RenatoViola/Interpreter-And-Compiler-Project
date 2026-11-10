package ast.identifiers;

import ast.Exp;
import datastructures.Triple;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;
import java.util.List;


public class ASTLet implements Exp {

    public List<Triple<String, Exp, Type>> bindings;

    public Exp body;

    public ASTLet(List<Triple<String, Exp, Type>> bindings, Exp body){
        this.bindings = bindings;
        this.body = body;
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
