package kaba4cow.bfdreader.parser.objects;

import kaba4cow.bfdreader.parser.ParsingException;
import kaba4cow.bfdreader.parser.PrimitiveType;
import kaba4cow.bfdreader.parser.SourceBuilder;
import kaba4cow.bfdreader.parser.TokenBuffer;

public class PrimitiveVariable implements Variable {

	private final PrimitiveType type;
	private final String name;
	private final String length;

	public PrimitiveVariable(TokenBuffer tokens) throws ParsingException {
		type = PrimitiveType.get(tokens.next().value);
		if (tokens.next().valueEquals("[")) {
			length = tokens.next().value;
			tokens.next();
			name = tokens.next().value;
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
			builder.indent(indent).write("public final %s %s;\n", type.type, name);
		else
			builder.indent(indent).write("public final %s[] %s;\n", type.type, name);
	}

	@Override
	public void generateDefinition(SourceBuilder builder, int indent) {
		if (length == null)
			builder.indent(indent).write("this.%s = %s;\n", name, type.instruction);
		else {
			builder.indent(indent).write("this.%s = new %s[(int) (%s)];\n", name, type.type, length);
			builder.indent(indent).write(
					"for (int %s_index_generated = 0; %s_index_generated < this.%s.length; %s_index_generated++)\n",
					name, name, name, name);
			builder.indent(indent + 1).write("this.%s[%s_index_generated] = %s;\n", name, name, type.instruction);
		}
	}

	@Override
	public void generateDefinitionEmpty(SourceBuilder builder, int indent) {
		if (length == null)
			builder.indent(indent).write("this.%s = %s;\n", name, type.empty);
		else
			builder.indent(indent).write("this.%s = new %s[(int) (%s)];\n", name, type.type, length);
	}

}
