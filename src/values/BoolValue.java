package values;

public record BoolValue(boolean value) implements Value {

	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BoolValue && value == ((BoolValue) obj).value();
	}


}
