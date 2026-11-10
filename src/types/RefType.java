package types;

public record RefType(Type referencedType) implements Type {

    @Override
    public String toString() {
        return "ref_" + referencedType.toString();
    }
}
