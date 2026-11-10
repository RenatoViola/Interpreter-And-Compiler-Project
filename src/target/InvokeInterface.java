package target;

public class InvokeInterface extends Instruction {

    public InvokeInterface(String interfaceName, String methodCall, int argumentCount) {
        op = "invokeinterface";
        args = new String[]{interfaceName + "/" + methodCall, String.valueOf(argumentCount)};
    }
}

