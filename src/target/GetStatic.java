package target;

public class GetStatic extends Instruction {

    public GetStatic(String className, String fieldName, String fieldDescriptor) {
        op = "getstatic";
        args = new String[]{ className + "/" + fieldName + " " + fieldDescriptor };
    }
}
