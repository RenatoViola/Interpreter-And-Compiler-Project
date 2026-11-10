package target;

public class IConst extends Instruction {
    public IConst(int value) {
        op = "iconst_" + value;
        args = null;
    }
}
