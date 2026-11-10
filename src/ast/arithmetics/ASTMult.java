package ast.arithmetics;


import ast.Exp;
import types.IntType;
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;

public class ASTMult implements Exp {

	public Exp e1, e2;

	public ASTMult(Exp e1, Exp e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public Type getType() {
		return IntType.singleton;
	}

	@Override
	public <T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException {
		return v.visit(this, env);
	}
}
