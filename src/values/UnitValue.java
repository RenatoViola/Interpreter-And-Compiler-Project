package values;

public class UnitValue implements Value {
    public static final UnitValue singleton = new UnitValue();

    private UnitValue() {}

    @Override
    public String toString() {
        return "()";
    }
}


