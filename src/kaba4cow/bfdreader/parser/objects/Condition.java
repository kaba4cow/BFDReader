package kaba4cow.bfdreader.parser.objects;

import java.util.ArrayList;

import kaba4cow.bfdreader.parser.ParsingException;
import kaba4cow.bfdreader.parser.PrimitiveType;
import kaba4cow.bfdreader.parser.SourceBuilder;
import kaba4cow.bfdreader.parser.Token;
import kaba4cow.bfdreader.parser.TokenBuffer;

public class Condition implements Variable {

	private final String expression;

	private final ArrayList<Variable> variables = new ArrayList<>();

	public Condition(TokenBuffer tokens) throws ParsingException {
		if (!tokens.skip().next().valueEquals("("))
			throw new ParsingException(tokens.last().cursor, "Unexpected token, expected (");
		else
			expression = tokens.next().value;
		if (!tokens.next().valueEquals(")"))
			throw new ParsingException(tokens.last().cursor, "Unexpected token, expected )");
		if (!tokens.next().valueEquals("{"))
			throw new ParsingException(tokens.last().cursor, "Unexpected token, expected {");
		while (tokens.hasNext()) {
			Token token = tokens.get(0);
			if (token.valueEquals("}")) {
				tokens.next();
				return;
			} else if (token.valueEquals("if"))
				variables.add(new Condition(tokens));
			else if (PrimitiveType.isPrimitive(token.value))
				variables.add(new PrimitiveVariable(tokens));
			else
				variables.add(new ObjectVariable(tokens));
		}
	}

	@Override
	public void generateDeclaration(SourceBuilder builder, int indent) {
		for (Variable variable : variables)
			variable.generateDeclaration(builder, indent);
	}

	@Override
	public void generateDefinition(SourceBuilder builder, int indent) {
		builder.indent(indent).write("if (%s) {\n", expression);
		for (Variable variable : variables)
			variable.generateDefinition(builder, indent + 1);
		builder.indent(indent).write("} else {\n");
		for (Variable variable : variables)
			variable.generateDefinitionEmpty(builder, indent + 1);
		builder.indent(indent).write("}\n");
	}

	@Override
	public void generateDefinitionEmpty(SourceBuilder builder, int indent) {
		for (Variable variable : variables)
			variable.generateDefinitionEmpty(builder, indent);
	}

}
