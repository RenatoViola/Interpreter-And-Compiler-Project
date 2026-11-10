package types;

import java.util.List;

import static symbols.Helper.interfaceName;


public record FunType(List<Type> types, Type returnType) implements Type {
    @Override
    public String toString() {
        return interfaceName(types, returnType);
    }
}