package main;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import ast.Exp;

import compiler.CodeGen;
import interpreter.*;
import parser.ParseException;
import parser.Parser;
import parser.TokenMgrError;
import typechecker.TypeCheck;
import types.TypingException;

public class Console {

	@SuppressWarnings("static-access")
	public static void main(String args[]) {
		Parser parser = new Parser(System.in);

		while (true) {
			try {
				Exp e = parser.Start();
				System.out.println("Parse OK!" );
				TypeCheck.typecheck(e);
				Interpreter.interpret(e);
				CodeGen.writeToFile(e, "MicroML.j");
			} catch (TokenMgrError e) {
				System.out.println("Lexical Error!");
				e.printStackTrace();
				parser.ReInit(System.in);
			} catch (ParseException e) {
				System.out.println("Syntax Error!");
				e.printStackTrace();
				parser.ReInit(System.in);
			} catch (FileNotFoundException e) {
				System.out.println("File not found!");
				e.printStackTrace();
				parser.ReInit(System.in);
			} catch (TypingException e) {
				System.out.println("Typing error: " + e.getMessage());
				e.printStackTrace();
				parser.ReInit(System.in);
			}
		}
	}

	public static boolean accept(String s) throws ParseException {
		Parser parser = new Parser(new ByteArrayInputStream(s.getBytes()));
		try {
			parser.Start();
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
}
