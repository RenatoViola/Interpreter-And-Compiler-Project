package types;

public class IntType implements Type {

	public static final IntType singleton = new IntType();
	
	protected IntType() {}

	@Override
	public String toString() {
		return "int";
	}

	//	@Override
//	public boolean equals(Object obj) {
//		return this == obj;
//	}

}
