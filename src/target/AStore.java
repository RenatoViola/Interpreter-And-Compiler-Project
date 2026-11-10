package target;

public class AStore extends Instruction {
    public AStore(int index) {
        op = "astore";
        args = new String[]{ String.valueOf(index) };
    }
}