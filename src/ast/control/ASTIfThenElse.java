package ast.control;

import ast.Exp;
import types.TypingException;
import types.UnitType;

import java.io.FileNotFoundException;

public class ASTIfThenElse implements Exp {

    public Exp condition, thenExp, elseExp;

    public ASTIfThenElse(Exp condition, Exp thenExp, Exp elseExp){
        this.condition = condition;
        this.thenExp = thenExp;
        this.elseExp = elseExp;
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
