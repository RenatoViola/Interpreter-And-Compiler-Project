package values;

import ast.Exp;
import datastructures.Pair;
import symbols.Env;
import types.Type;

import java.util.List;

public record ClosureValue(List<Pair<String, Type>> params, Exp body, Env<Value> env) implements Value {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, Type> param : params) {
            sb.append(param.second()).append(" ");
        }
        sb.append("-> ").append(body.getType().toString());
        return sb.toString().trim();
    }
}