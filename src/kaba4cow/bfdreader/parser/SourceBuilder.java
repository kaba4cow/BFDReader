package kaba4cow.bfdreader.parser;

public class SourceBuilder {

	private StringBuilder builder;

	public SourceBuilder() {
		builder = new StringBuilder();
	}

	public SourceBuilder write(String format, Object... args) {
		builder.append(String.format(format, args));
		return this;
	}

	public SourceBuilder indent(int indent) {
		for (int i = 0; i < indent; i++)
			builder.append('\t');
		return this;
	}

	public SourceBuilder clear() {
		builder = new StringBuilder();
		return this;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

}
