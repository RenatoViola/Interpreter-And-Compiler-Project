package values;

public record IntValue(int value) implements Value {

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntValue && value == ((IntValue) obj).value();
	}


}
