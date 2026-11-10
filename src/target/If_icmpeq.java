package target;

public class If_icmpeq extends Instruction{

    public If_icmpeq(String label) {
        op = "if_icmpeq";
        args = new String[]{ label };
    }
}