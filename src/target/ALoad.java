package target;

public class ALoad extends Instruction {
    public ALoad(int index) {
        op = "aload";
        args = new String[]{ String.valueOf(index) };
    }
}
