package typechecker;

import ast.Exp;
import ast.arithmetics.*;
import ast.functions.ASTFun;
import ast.functions.ASTApply;
import ast.identifiers.ASTId;
import ast.identifiers.ASTLet;
import ast.control.ASTIfThenElse;
import ast.logic.*;
import ast.control.ASTPrint;
import ast.composition.ASTSequence;
import ast.references.ASTAssignRef;
import ast.references.ASTDeref;
import ast.types.*;
import ast.control.ASTWhile;
import datastructures.Pair;
import datastructures.Triple;
import symbols.Env;
import types.*;

import java.io.FileNotFoundException;


public class TypeCheck implements Exp.Visitor<Type, Env<Type>> {

    // Types

    @Override
    public IntType visit(ASTInt i, Env<Type> env) throws TypingException {
        return IntType.singleton;
    }

    @Override
    public BoolType visit(ASTBool b, Env<Type> env) throws TypingException {
        return BoolType.singleton;
    }

    @Override
    public UnitType visit(ASTUnit u, Env<Type> env) throws TypingException {
        return UnitType.singleton;
    }

    // Arithmetics

    @Override
    public IntType visit(ASTAdd e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == IntType.singleton && t1 == t2)
            return IntType.singleton;
        else throw new TypingException("Non-integer argument!");
    }

    @Override
    public IntType visit(ASTMult e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == IntType.singleton && t1 == t2)
            return IntType.singleton;
        else throw new TypingException("Non-integer argument!");
    }

    @Override
    public IntType visit(ASTDiv e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == IntType.singleton && t1 == t2)
            return IntType.singleton;
        else throw new TypingException("Non-integer argument!");
    }

    @Override
    public IntType visit(ASTSub e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == IntType.singleton && t1 == t2)
            return IntType.singleton;
        else throw new TypingException("Non-integer argument!");
    }

    // Relational Logic

    @Override
    public BoolType visit(ASTAnd e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == BoolType.singleton && t1 == t2)
            return BoolType.singleton;
        else throw new TypingException("Non-boolean argument!");
    }

    @Override
    public BoolType visit(ASTOr e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == BoolType.singleton && t1 == t2)
            return BoolType.singleton;
        else throw new TypingException("Non-boolean argument!");
    }

    @Override
    public BoolType visit(ASTEq e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == IntType.singleton && t1 == t2)
            return BoolType.singleton;
        else throw new TypingException("Non-integer argument!");
    }

    @Override
    public BoolType visit(ASTNot e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t = e.e.accept(this, env);
        if (t == BoolType.singleton)
            return BoolType.singleton;
        else throw new TypingException("Non-boolean argument!");
    }

    @Override
    public BoolType visit(ASTGt e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t1 = e.e1.accept(this, env);
        Type t2 = e.e2.accept(this, env);
        if (t1 == IntType.singleton && t1 == t2)
            return BoolType.singleton;
        else throw new TypingException("Non-integer argument!");
    }

    // Identifiers and declaration

    @Override
    public Type visit(ASTId x, Env<Type> env) throws TypingException {
        Type t = env.find(x.name);
        if (t == null)
            throw new TypingException("Identifier is not declared!");
        x.setType(t);
        return t;
    }

    @Override
    public Type visit(ASTLet e, Env<Type> env) throws TypingException, FileNotFoundException {
        env = env.beginScope();
        for (Triple<String, Exp, Type> t : e.bindings){
            Type v = t.second().accept(this, env);
            if (t.third() != null && !v.equals(t.third())) throw new TypingException("Not the expected type.");
            env.bind(t.first(), v);
        }
        Type type = e.body.accept(this, env);
        env.endScope();
        return type;
    }

    // Control Structures

    @Override
    public UnitType visit(ASTIfThenElse e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type condition = e.condition.accept(this, env);

        if(!(condition instanceof BoolType))
            throw new TypingException("Condition in if-else statement is not boolean");

        Type thenType = e.thenExp.accept(this, env);

        if(e.elseExp != null){
            Type elseType = e.elseExp.accept(this, env);
            if(!thenType.equals(elseType))
                throw new TypingException("Type mismatch in 'then' and 'else' branches");
        }

        return UnitType.singleton;
    }

    @Override
    public UnitType visit(ASTWhile w, Env<Type> env) throws TypingException, FileNotFoundException {
        Type condition = w.condition.accept(this, env);

        if(!(condition instanceof BoolType))
            throw new TypingException("Condition in while statement is not boolean");

        w.doExp.accept(this, env);

        return UnitType.singleton;
    }

    @Override
    public UnitType visit(ASTPrint e, Env<Type> env) throws TypingException, FileNotFoundException {
        Type innerType = e.e.accept(this, env);
        if (innerType instanceof RefType || innerType instanceof FunType)
            throw new TypingException("Expression must not evaluate to a reference or function type.");

        return UnitType.singleton;
    }

    @Override
    public Type visit(ASTSequence s, Env<Type> env) throws TypingException, FileNotFoundException {
        Type t = null;
        for (Exp e : s.sequence)
            t = e.accept(this, env);

        return t;
    }

    // Reference operations

    @Override
    public RefType visit(ASTRef r, Env<Type> env) throws TypingException, FileNotFoundException {
        return new RefType(r.e.accept(this, env));
    }

    @Override
    public UnitType visit(ASTAssignRef a, Env<Type> env) throws TypingException, FileNotFoundException {
        Type refType = a.e1.accept(this, env);
        Type valueType = a.e2.accept(this, env);

        if (!(refType instanceof RefType)) {
            throw new TypingException("Left-hand side of assignment is not a reference");
        }
        if (!(valueType.equals(((RefType) refType).referencedType()))) {
            throw new TypingException("Type mismatch in reference assignment");
        }

        return UnitType.singleton;
    }

    @Override
    public Type visit(ASTDeref d, Env<Type> env) throws TypingException, FileNotFoundException {
        Type refType = d.e.accept(this, env);
        if (!(refType instanceof RefType)) {
            throw new TypingException("Attempting to dereference a non-reference type");
        }
        return ((RefType) refType).referencedType();
    }

    // Functions

    @Override
    public Type visit(ASTFun f, Env<Type> env) throws TypingException, FileNotFoundException {
        env = env.beginScope();

        for (int i = 0; i < f.params.size(); i++) {
            String param = f.params.get(i).first();
            Type type = f.params.get(i).second();
            env.bind(param, type);
        }

        Type bodyType = f.body.accept(this, env);
        env.endScope();

        return new FunType(f.params.stream().map(Pair::second).toList(), bodyType);
    }

    @Override
    public Type visit(ASTApply f, Env<Type> env) throws TypingException, FileNotFoundException {
        Type functionType = f.function.accept(this, env);
        if (!(functionType instanceof FunType funType)) {
            throw new TypingException("Expected a function type");
        }

        if (f.arguments.size() != funType.types().size()) {
            throw new TypingException("Argument size mismatch");
        }

        for (int idx = 0; idx < f.arguments.size(); idx++) {
            Type type = f.arguments.get(idx).accept(this, env);
            if (!type.equals(funType.types().get(idx))) {
                throw new TypingException("Argument type mismatch");
            }
        }
        return funType.returnType();
    }

    public static Type typecheck(Exp e) throws TypingException, FileNotFoundException {
        TypeCheck t = new TypeCheck();
        return e.accept(t, new Env<>());
    }
}
