package target;

public class IStore extends Instruction {
    public IStore(int index) {
        op = "istore";
        args = new String[]{ String.valueOf(index) };
    }
}
