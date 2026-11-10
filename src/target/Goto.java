package target;

public class Goto extends Instruction{

    public Goto(String label) {
        op = "goto";
        args = new String[]{ label };
    }
}
