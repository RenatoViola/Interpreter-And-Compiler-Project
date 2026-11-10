package types;

public class BoolType implements Type{

    public static final BoolType singleton = new BoolType();

    protected BoolType() {}

    @Override
    public String toString() {
        return "bool";
    }
}
