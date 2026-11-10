package interpreter;

import ast.Exp;
import ast.arithmetics.*;
import ast.identifiers.ASTId;
import ast.identifiers.ASTLet;
import ast.control.ASTIfThenElse;
import ast.logic.*;
import ast.control.ASTPrint;
import ast.composition.ASTSequence;
import ast.references.ASTAssignRef;
import ast.references.ASTDeref;
import ast.types.ASTBool;
import ast.types.ASTInt;
import ast.types.ASTRef;
import ast.types.ASTUnit;
import ast.control.ASTWhile;
import ast.functions.*;
import datastructures.Triple;
import symbols.Env;
import types.Type;
import types.TypingException;
import values.*;

import java.io.FileNotFoundException;

public class Interpreter implements ast.Exp.Visitor<Value, Env<Value>> {

	// Types

	@Override
	public IntValue visit(ASTInt i, Env<Value> env) {
		return i.value;
	}

	@Override
	public BoolValue visit(ASTBool b, Env<Value> env) {
		return b.value;
	}

	@Override
	public UnitValue visit(ASTUnit u, Env<Value> env) {
		return UnitValue.singleton;
	}

	// Arithmetics

	@Override
	public IntValue visit(ASTAdd e, Env<Value> env) throws TypingException, FileNotFoundException {
		IntValue n1 = (IntValue) e.e1.accept(this, env);
		IntValue n2 = (IntValue) e.e2.accept(this, env);
		return new IntValue(n1.value() + n2.value());
	}

	@Override
	public IntValue visit(ASTMult e, Env<Value> env) throws TypingException, FileNotFoundException {
		IntValue n1 = (IntValue) e.e1.accept(this, env);
		IntValue n2 = (IntValue) e.e2.accept(this, env);
		return new IntValue(n1.value() * n2.value());
	}

	@Override
	public IntValue visit(ASTDiv e, Env<Value> env) throws TypingException, FileNotFoundException {
		IntValue n1 = (IntValue) e.e1.accept(this, env);
		IntValue n2 = (IntValue) e.e2.accept(this, env);
		return new IntValue(n1.value() / n2.value());
	}

	@Override
	public IntValue visit(ASTSub e, Env<Value> env) throws TypingException, FileNotFoundException {
		IntValue n1 = (IntValue) e.e1.accept(this, env);
		IntValue n2 = (IntValue) e.e2.accept(this, env);
		return new IntValue(n1.value() - n2.value());
	}

	// Relational Logic

	@Override
	public BoolValue visit(ASTAnd e, Env<Value> env) throws TypingException, FileNotFoundException {
		BoolValue b1 = (BoolValue) e.e1.accept(this, env);
		BoolValue b2 = (BoolValue) e.e2.accept(this, env);
		return new BoolValue(b1.value() && b2.value());
	}

	@Override
	public BoolValue visit(ASTOr e, Env<Value> env) throws TypingException, FileNotFoundException {
		BoolValue b1 = (BoolValue) e.e1.accept(this, env);
		BoolValue b2 = (BoolValue) e.e2.accept(this, env);
		return new BoolValue(b1.value() || b2.value());
	}

	@Override
	public BoolValue visit(ASTEq e, Env<Value> env) throws TypingException, FileNotFoundException {
		IntValue n1 = (IntValue) e.e1.accept(this, env);
		IntValue n2 = (IntValue) e.e2.accept(this, env);
		return new BoolValue(n1.value() == n2.value());
	}

	@Override
	public BoolValue visit(ASTNot e, Env<Value> env) throws TypingException, FileNotFoundException {
		BoolValue b = (BoolValue) e.e.accept(this, env);
		return new BoolValue(!b.value());
	}

	@Override
	public BoolValue visit(ASTGt e, Env<Value> env) throws TypingException, FileNotFoundException {
		IntValue n1 = (IntValue) e.e1.accept(this, env);
		IntValue n2 = (IntValue) e.e2.accept(this, env);
		return new BoolValue(n1.value() > n2.value());
	}

	// Identifiers and declaration

	@Override
	public Value visit(ASTId x, Env<Value> env) throws TypingException {
		return env.find(x.name);
	}

	@Override
	public Value visit(ASTLet e, Env<Value> env) throws TypingException, FileNotFoundException {
		env = env.beginScope();
		for (Triple<String, Exp, Type> t : e.bindings){
			Value v = t.second().accept(this, env);
			env.bind(t.first(), v);
		}
		Value val = e.body.accept(this, env);
		env = env.endScope();
		return val;
	}

	// Control Structures

	@Override
	public Value visit(ASTIfThenElse e, Env<Value> env) throws TypingException, FileNotFoundException {
		BoolValue condition = (BoolValue) e.condition.accept(this, env);

		if(condition.value())
			return e.thenExp.accept(this, env);
		else
			return (e.elseExp != null) ? e.elseExp.accept(this, env) : UnitValue.singleton;
	}

	@Override
	public UnitValue visit(ASTWhile w, Env<Value> env) throws TypingException, FileNotFoundException {
		while(((BoolValue) w.condition.accept(this, env)).value())
			w.doExp.accept(this, env);

		return UnitValue.singleton;
	}

	@Override
	public UnitValue visit(ASTPrint e, Env<Value> env) throws TypingException, FileNotFoundException {
		Value value = e.e.accept(this, env);

        if (e.EOL)
			System.out.println(value);
        else
			System.out.print(value);

        return UnitValue.singleton;
	}

	@Override
	public Value visit(ASTSequence s, Env<Value> env) throws TypingException, FileNotFoundException {
		Value v = null;
		for (Exp e : s.sequence)
			v = e.accept(this, env);

		return v;
	}

	// Reference operations

	@Override
	public RefValue visit(ASTRef r, Env<Value> env) throws TypingException, FileNotFoundException {
		return new RefValue(r.e.accept(this, env));
	}

	@Override
	public UnitValue visit(ASTAssignRef a, Env<Value> env) throws TypingException, FileNotFoundException {
		RefValue value1 = (RefValue) a.e1.accept(this, env);
		Value value2 = a.e2.accept(this, env);
		value1.setValue(value2);

		return UnitValue.singleton;
	}

	@Override
	public Value visit(ASTDeref d, Env<Value> env) throws TypingException, FileNotFoundException {
		RefValue value = (RefValue) d.e.accept(this, env);

		return value.getValue();
	}

	// Functions

	@Override
	public ClosureValue visit(ASTFun f, Env<Value> env) throws TypingException {
		return new ClosureValue(f.params, f.body, env);
	}

	@Override
	public Value visit(ASTApply f, Env<Value> env) throws TypingException, FileNotFoundException {
		Value function = f.function.accept(this, env);

		if (!(function instanceof ClosureValue closure)) {
			throw new RuntimeException("Expected a function");
		}

        Env<Value> newEnv = closure.env().beginScope();

		for (int i = 0; i < closure.params().size(); i++) {
			Value exp = f.arguments.get(i).accept(this, env);
			newEnv.bind(closure.params().get(i).first(), exp);
		}

		Value val = closure.body().accept(this, newEnv);
		newEnv.endScope();

		return val;
	}

	public static Value interpret(Exp e) throws TypingException, FileNotFoundException {
		Interpreter i = new Interpreter();
		return e.accept(i, new Env<>());
	}
}
