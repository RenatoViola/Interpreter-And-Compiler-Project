package symbols;

import types.Type;

import java.io.FileNotFoundException;
import java.util.List;

import static symbols.Helper.getFieldDescriptor;
import static symbols.Helper.writeToJasminFile;

public record Frame(int nFields, List<Type> types, int id, Frame prev) {
    public String name() {
        return "frame_" + id;
    }

    public void generateCode() throws FileNotFoundException {

        StringBuilder fields = new StringBuilder();
        for (int i = 0; i < nFields; i++) {
            fields.append(String.format("""
                    .field public loc_%d %s
                    """, i, getFieldDescriptor(types.get(i))));
        }

        String fieldDescriptor = "L" + (prev == null ? "java/lang/Object" : prev.name()) + ";";
        String str = String.format("""
                .class public %s
                .super java/lang/Object
                .field public sl %s
                %s
                .method public <init>()V
                aload_0
                invokenonvirtual java/lang/Object/<init>()V
                return
                .end method
                """, this.name(), fieldDescriptor, fields);

        writeToJasminFile(str, this.name() + ".j");
    }
}