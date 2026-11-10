package target;

public class ILoad extends Instruction {
    public ILoad(int index) {
        op = "iload";
        args = new String[]{ String.valueOf(index) };
    }
}
