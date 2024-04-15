package kaba4cow.bfdreader.parser;

import java.util.LinkedList;

public class TokenBuffer {

	private final LinkedList<Token> tokens;

	private Token last;

	public TokenBuffer(LinkedList<Token> tokens) {
		this.tokens = tokens;
		this.last = null;
	}

	public Token last() {
		return last;
	}

	public boolean has(int index) {
		return index < tokens.size();
	}

	public Token get(int index) {
		if (!has(index))
			return null;
		return tokens.get(index);
	}

	public boolean hasNext() {
		return has(0);
	}

	public Token next() {
		if (!hasNext())
			return null;
		last = tokens.removeFirst();
		return last;
	}

	public TokenBuffer skip() {
		next();
		return this;
	}

}
