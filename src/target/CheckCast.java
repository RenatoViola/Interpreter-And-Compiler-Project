package target;

public class CheckCast extends Instruction {
    public CheckCast(String className) {
        op = "checkcast";
        args = new String[]{ className };
    }
}
