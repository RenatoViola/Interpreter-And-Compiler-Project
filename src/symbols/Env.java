package symbols;

import java.util.Hashtable;
import java.util.Map;

public class Env<T> {

    Map<String,T> table;
    Env<T> prev;

    public Env() {
        table = new Hashtable<>(20);
    }

    public void bind(String id, T val) {
        table.put(id, val);
    }

    public T find(String id) {
        for (Env<T> e = this; e != null; e = e.prev) {
            T found = e.table.get(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public Env<T> beginScope() {
        Env<T> e = new Env<>();
        e.prev = this;
        return e;
    }

    public Env<T> endScope() {
        return this.prev;
    }

}

