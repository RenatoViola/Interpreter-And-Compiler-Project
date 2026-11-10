package symbols;

import datastructures.Pair;

import java.util.Hashtable;
import java.util.Map;

public class CompEnv {

    private final Map<String, Integer> table;
    private final CompEnv prev;

    public CompEnv() {
        this.table = new Hashtable<>(20);
        this.prev = null;
    }

    private CompEnv(Map<String, Integer> table, CompEnv prev) {
        this.table = table;
        this.prev = prev;
    }

    public void bind(String id, Integer val) {
        table.put(id, val);
    }

    public Pair<Integer, Integer> findPair(String id) {
        CompEnv env = this;
        int depth = 0;

        while (env != null) {
            Integer width = env.table.get(id);
            if (width != null) {
                return new Pair<>(depth, width);
            }
            env = env.endScope();
            depth++;
        }

        throw new IllegalArgumentException("Identifier not found: " + id);
    }

    public CompEnv beginScope() {
        return new CompEnv(new Hashtable<>(20), this);
    }

    public CompEnv endScope() {
        return this.prev;
    }
}
