package ast.functions;

import datastructures.Pair;
import ast.Exp;
import types.FunType;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;
import java.util.List;

public class ASTFun implements Exp {

    public List<Pair<String, Type>> params;
    public Exp body;

    public ASTFun(List<Pair<String, Type>> params, Exp body) {
        this.params = params;
        this.body = body;
    }

    @Override
    public FunType getType() {
        return new FunType(params.stream().map(Pair::second).toList(), body.getType());
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }

}