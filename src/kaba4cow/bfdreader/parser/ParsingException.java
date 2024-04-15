package kaba4cow.bfdreader.parser;

public class ParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParsingException(int[] cursor, String format, Object... args) {
		super(String.format(format, args) + String.format(" at [%d:%d]", cursor[0], cursor[1]));
	}

}
