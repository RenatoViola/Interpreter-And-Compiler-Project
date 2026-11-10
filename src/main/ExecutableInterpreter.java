package main;

import ast.Exp;
import interpreter.Interpreter;
import parser.ParseException;
import parser.Parser;
import parser.TokenMgrError;
import typechecker.TypeCheck;
import types.TypingException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExecutableInterpreter {

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java ExecutableInterpreter <path_to_micro_ml_program>");
            System.exit(1);
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(args[0])));
            content = content.replace("\n", " ");
            content += "\r\n";

            Parser parser = new Parser(new StringReader(content));
            Exp e = parser.Start();
            TypeCheck.typecheck(e);
            Interpreter.interpret(e);

        } catch (TokenMgrError | ParseException e) {
            System.out.println("Parsing Error: " + e.getMessage());
        } catch (TypingException e) {
            System.out.println("Typing Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
