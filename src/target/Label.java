package target;

public class Label extends Instruction {
    public Label(String label) {
        this.op = label + ":";
        this.args = null;
    }
}

