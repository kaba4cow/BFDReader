package kaba4cow.bfdreader.parser.objects;

import java.io.IOException;
import java.util.ArrayList;

import kaba4cow.bfdreader.parser.ParsingException;
import kaba4cow.bfdreader.parser.PrimitiveType;
import kaba4cow.bfdreader.parser.SourceBuilder;
import kaba4cow.bfdreader.parser.Token;
import kaba4cow.bfdreader.parser.TokenBuffer;

public class ObjectData {

	private final boolean format;
	private final String name;

	private final ArrayList<ObjectData> objects = new ArrayList<>();
	private final ArrayList<Variable> variables = new ArrayList<>();

	public ObjectData(TokenBuffer tokens) throws ParsingException {
		format = tokens.next().valueEquals("format");
		name = tokens.next().value;
		if (!tokens.next().valueEquals("{"))
			throw new ParsingException(tokens.last().cursor, "Unexpected token, expected {");
		while (tokens.hasNext()) {
			Token token = tokens.get(0);
			if (token.valueEquals("}")) {
				tokens.next();
				return;
			} else if (token.valueEquals("object"))
				objects.add(new ObjectData(tokens));
			else if (token.valueEquals("format"))
				throw new ParsingException(token.cursor, "Format cannot be defined inside another block");
			else if (token.valueEquals("if"))
				variables.add(new Condition(tokens));
			else if (PrimitiveType.isPrimitive(token.value))
				variables.add(new PrimitiveVariable(tokens));
			else
				variables.add(new ObjectVariable(tokens));
		}
	}

	public void generate(SourceBuilder builder, int indent) throws IOException {
		builder.indent(indent).write("public static class %s {\n\n", name);
		for (Variable variable : variables)
			variable.generateDeclaration(builder, indent + 1);
		builder.write("\n");
		builder.indent(indent + 1).write("%s %s(BinaryReader reader) throws IOException {\n",
				format ? "public" : "private", name);
		for (Variable variable : variables)
			variable.generateDefinition(builder, indent + 2);
		builder.indent(indent + 1).write("}\n");
		builder.write("\n");
		for (ObjectData object : objects)
			object.generate(builder, indent + 1);
		builder.indent(indent).write("}\n\n");
	}

}
