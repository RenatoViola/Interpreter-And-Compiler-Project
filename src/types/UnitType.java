package types;

public class UnitType implements Type{

    public static final UnitType singleton = new UnitType();

    protected UnitType() {}

    @Override
    public String toString() {
        return "unit";
    }
}
