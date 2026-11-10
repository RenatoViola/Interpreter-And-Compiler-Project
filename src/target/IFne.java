package target;

public class IFne extends Instruction{

    public IFne(String label) {
        op = "ifne";
        args = new String[]{ label };
    }
}
