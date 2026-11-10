package target;

public class InvokeStatic extends Instruction{

    public InvokeStatic(String className, String methodName, String methodDescriptor) {
        op = "invokestatic";
        args = new String[]{ className + "/" + methodName + methodDescriptor };
    }
}
