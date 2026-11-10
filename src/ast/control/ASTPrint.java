package ast.control;

import ast.Exp;
import types.TypingException;
import types.UnitType;

import java.io.FileNotFoundException;

public class ASTPrint implements Exp {

    public Exp e;
    public boolean EOL;

    public ASTPrint(Exp e, boolean endLine) {
        this.e = e;
        EOL = endLine;
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
