package target;

public class IFeq extends Instruction{

    public IFeq(String label) {
        op = "ifeq";
        args = new String[]{ label };
    }
}
