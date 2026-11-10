package target;

public class InvokeSpecial extends Instruction{

    public InvokeSpecial(String s){
        op="invokespecial";
        args = new String[]{ s+"/<init>()V" };
    }
}
