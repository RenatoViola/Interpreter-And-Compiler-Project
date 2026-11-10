package target;

import types.Type;

public class PutField extends Instruction {

    public PutField(String className, String fieldName, String fieldDescriptor) {
        op = "putfield";
        args = new String[]{ className + "/" + fieldName + " " + fieldDescriptor };
    }
}
