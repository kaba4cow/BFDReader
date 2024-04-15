package kaba4cow.bfdreader.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Tokenizer {

	private static final Pattern patternSource = Pattern.compile("[\\w\\s\\d\\S]+");

	private static final String specialCharacters = "{};";

	private final BufferedReader reader;

	private final int[] cursor = { 1, 1 };

	private final LinkedList<Token> tokens = new LinkedList<>();
	private final TokenBuilder builder = new TokenBuilder();

	public Tokenizer(InputStream input) throws IOException, ParsingException {
		reader = new BufferedReader(new InputStreamReader(input));
		int read;
		while ((read = read()) != -1) {
			if (matchesSource(read)) {
				if (read == ' ')
					builder.create();
				else if (specialCharacters.indexOf(read) != -1)
					builder.create().append(read).create();
				else if (read == '(' || read == '[') {
					builder.create();
					builder.append(read).create();
					builder.append(parseExpression(read)).create();
					builder.append(read == '(' ? ')' : ']').create();
				} else
					builder.append(read);
			}
		}
		builder.create();
		reader.close();
	}

	private String parseExpression(int opener) throws IOException, ParsingException {
		StringBuilder string = new StringBuilder();
		int read;
		LinkedList<Integer> levels = new LinkedList<>();
		levels.add(opener);
		while ((read = read()) != -1)
			if (matchesSource(read)) {
				if (read == '(' || read == '[') {
					levels.add(read);
					string.append((char) read);
				} else if (read == ')' || read == ']') {
					int expected = read == ')' ? '(' : '[';
					int actual = levels.removeLast();
					if (expected != actual)
						throw new ParsingException(cursor, "Expression is not closed");
					if (levels.isEmpty())
						return string.toString();
					else
						string.append((char) read);
				} else
					string.append((char) read);
			}
		throw new ParsingException(cursor, "Expression is not closed");
	}

	private int read() throws IOException {
		int read = reader.read();
		if (read == '\n') {
			cursor[0]++;
			cursor[1] = 1;
		} else
			cursor[1]++;
		return read;
	}

	private boolean matchesSource(int c) {
		return !Character.isISOControl(c) && patternSource.matcher(Character.toString((char) c)).matches();
	}

	public TokenBuffer getTokens() {
		return new TokenBuffer(tokens);
	}

	private class TokenBuilder {

		private StringBuilder builder = new StringBuilder();

		public TokenBuilder() {
		}

		public TokenBuilder append(String string) {
			builder.append(string);
			return this;
		}

		public TokenBuilder append(int c) {
			builder.append((char) c);
			return this;
		}

		public TokenBuilder create() {
			if (builder.length() > 0) {
				tokens.add(new Token(cursor, builder.toString()));
				builder = new StringBuilder();
			}
			return this;
		}

		@Override
		public String toString() {
			return builder.toString();
		}

	}

}
