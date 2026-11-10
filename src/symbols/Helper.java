package symbols;

import types.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Helper {

    public static String getFieldDescriptor(Type type) {
        return switch (type) {
            case IntType intType -> "I";
            case BoolType boolType -> "Z";
            case UnitType unitType -> "V";
            case RefType refType -> "L" + refType + ";";
            case FunType funType -> "L" + interfaceName(funType.types(), funType.returnType()) + ";";
            case null, default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    public static void writeToJasminFile(String str, String filename) throws FileNotFoundException {

        String outputDir = "src/jasmin/jfiles/";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fullPath = outputDir + filename;
        PrintStream out = new PrintStream(new FileOutputStream(fullPath));
        out.print(str);
        out.close();
    }

    public static String interfaceName(List<Type> types, Type returnType) {
        StringBuilder sb = new StringBuilder("closure_interface");
        for (Type t : types) {
            sb.append("_").append(t);
        }
        sb.append("_").append(returnType);

        return sb.toString().trim();
    }

    public static String applyDescriptor(List<Type> types, Type returnType) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Type t : types) {
            sb.append(getFieldDescriptor(t));
        }
        sb.append(")");
        sb.append(getFieldDescriptor(returnType));

        return sb.toString();
    }

}
