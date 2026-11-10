package target;

public class If_icmpgt extends Instruction{

    public If_icmpgt(String label) {
        op = "if_icmpgt";
        args = new String[]{ label };
    }
}
