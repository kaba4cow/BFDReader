package kaba4cow.bfdreader.parser;

public class Token {

	public final String value;
	public final int[] cursor;

	public Token(int[] cursor, String value) {
		this.value = value;
		this.cursor = new int[] { cursor[0], cursor[1] - value.length() - 1 };
	}

	public boolean valueEquals(String... values) {
		for (int i = 0; i < values.length; i++)
			if (value.equals(values[i]))
				return true;
		return false;
	}

	@Override
	public String toString() {
		return String.format("Token at [%d:%d] = %s", cursor[0], cursor[1], value);
	}

}
