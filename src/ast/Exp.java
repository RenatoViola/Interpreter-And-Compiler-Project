package ast;


import ast.arithmetics.*;
import ast.functions.*;
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
import types.Type;
import types.TypingException;

import java.io.FileNotFoundException;

public interface Exp {
	interface Visitor<T, E> {

		// Types
		T visit(ASTInt i, E env) throws TypingException;
		T visit(ASTBool b, E env) throws TypingException;
		T visit(ASTUnit u, E env) throws TypingException;

		// Arithmetics
		T visit(ASTAdd e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTMult e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTDiv e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTSub e, E env) throws TypingException, FileNotFoundException;

		// Relational Logic
		T visit(ASTAnd e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTOr e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTEq e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTNot e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTGt e, E env) throws TypingException, FileNotFoundException;

		// Identifiers and declaration
		T visit(ASTId x, E env) throws TypingException;
		T visit(ASTLet e, E env) throws TypingException, FileNotFoundException;

		// Control Structures
		T visit(ASTIfThenElse e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTWhile w, E env) throws TypingException, FileNotFoundException;
		T visit(ASTPrint e, E env) throws TypingException, FileNotFoundException;
		T visit(ASTSequence s, E env) throws TypingException, FileNotFoundException;

		// Reference operations
		T visit(ASTRef r, E env) throws TypingException, FileNotFoundException;
		T visit(ASTAssignRef a, E env) throws TypingException, FileNotFoundException;
		T visit(ASTDeref d, E env) throws TypingException, FileNotFoundException;

		// Functions
		T visit(ASTFun f, E env) throws TypingException, FileNotFoundException;
		T visit(ASTApply f, E env) throws TypingException, FileNotFoundException;
	}

	Type getType();

	<T, E> T accept(Visitor<T, E> v, E env) throws TypingException, FileNotFoundException;
}

