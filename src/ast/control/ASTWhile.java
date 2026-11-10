package ast.control;

import ast.Exp;
import types.TypingException;
import types.UnitType;

import java.io.FileNotFoundException;

public class ASTWhile implements Exp{

    public Exp condition, doExp;

    public ASTWhile(Exp condition, Exp doExp){
        this.condition = condition;
        this.doExp = doExp;
    }

    @Override
    public UnitType getType() {
        return UnitType.singleton;
    }

    @Override
    public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
