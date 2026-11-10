package target;

import types.Type;

public class New extends Instruction{

    public New(String ref){
        op="new";
        args = new String[]{ ref };
    }

}
