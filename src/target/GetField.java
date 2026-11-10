package target;

public class GetField extends Instruction {

    public GetField(String className, String fieldName, String fieldDescriptor) {
        op = "getfield";
        args = new String[]{ className + "/" + fieldName + " " + fieldDescriptor };
    }
}
