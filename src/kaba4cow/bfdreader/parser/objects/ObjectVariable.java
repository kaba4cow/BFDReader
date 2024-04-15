package kaba4cow.bfdreader.parser.objects;

import kaba4cow.bfdreader.parser.ParsingException;
import kaba4cow.bfdreader.parser.SourceBuilder;
import kaba4cow.bfdreader.parser.TokenBuffer;

public class ObjectVariable implements Variable {

	private String type;
	private String name;
	private String length;

	public ObjectVariable(TokenBuffer tokens) throws ParsingException {
		type = tokens.next().value;
		if (tokens.next().valueEquals("[")) {
			length = tokens.next().value;
			name = tokens.skip().next().value;
		} else {
			length = null;
			name = tokens.last().value;
		}
		if (!tokens.next().valueEquals(";"))
			throw new ParsingException(tokens.last().cursor, "Unexpected token, expected ;");
	}

	@Override
	public void generateDeclaration(SourceBuilder builder, int indent) {
		if (length == null)
			builder.indent(indent).write("public final %s %s;\n", type, name);
		else
			builder.indent(indent).write("public final %s[] %s;\n", type, name);
	}

	@Override
	public void generateDefinition(SourceBuilder builder, int indent) {
		if (length == null)
			builder.indent(indent).write("this.%s = new %s(reader);\n", name, type);
		else {
			builder.indent(indent).write("this.%s = new %s[(int) (%s)];\n", name, type, length);
			builder.indent(indent).write(
					"for (int %s_index_generated = 0; %s_index_generated < this.%s.length; %s_index_generated++)\n",
					name, name, name, name);
			builder.indent(indent + 1).write("this.%s[%s_index_generated] = new %s(reader);\n", name, name, type);
		}
	}

	@Override
	public void generateDefinitionEmpty(SourceBuilder builder, int indent) {
		if (length == null)
			builder.indent(indent).write("this.%s = null;\n", name);
		else
			builder.indent(indent).write("this.%s = new %s[(int) (%s)];\n", name, type, length);
	}

}
