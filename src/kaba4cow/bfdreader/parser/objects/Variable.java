package kaba4cow.bfdreader.parser.objects;

import kaba4cow.bfdreader.parser.SourceBuilder;

public interface Variable {

	public void generateDeclaration(SourceBuilder builder, int indent);

	public void generateDefinition(SourceBuilder builder, int indent);

	public void generateDefinitionEmpty(SourceBuilder builder, int indent);

}
