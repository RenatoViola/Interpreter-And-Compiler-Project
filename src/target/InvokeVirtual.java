package target;

public class InvokeVirtual extends Instruction {

    public InvokeVirtual(String className, String methodName, String methodDescriptor) {
        op = "invokevirtual";
        args = new String[]{ className + "/" + methodName + methodDescriptor };
    }
}
