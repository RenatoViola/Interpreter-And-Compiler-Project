package ast.functions;

import ast.Exp;
import types.FunType;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;
import java.util.List;

public class ASTApply implements Exp{

    public Exp function;
    public List<Exp> arguments;

    public ASTApply(Exp function, List<Exp> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public Type getType() {
        return ((FunType) function.getType()).returnType();
    }

    @Override
    public <T, E> T accept(Exp.Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
        return v.visit(this, env);
    }
}
