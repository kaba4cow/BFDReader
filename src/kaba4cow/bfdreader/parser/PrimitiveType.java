package kaba4cow.bfdreader.parser;

public enum PrimitiveType {

	BYTE("byte", "byte", "reader.readByte()", "(byte) 0"), //
	U_BYTE("u_byte", "short", "reader.readUnsignedByte()", "(short) 0"), //

	SHORT("short", "short", "reader.readShort()", "(short) 0"), //
	U_SHORT("u_short", "int", "reader.readUnsignedShort()", "(int) 0"), //

	INT("int", "int", "reader.readInt()", "(int) 0"), //
	U_INT("u_int", "long", "reader.readUnsignedInt()", "(long) 0"), //

	LONG("long", "long", "reader.readLong()", "(long) 0"), //

	FLOAT2("float2", "float", "reader.readFloat2()", "(float) 0"), //
	FLOAT4("float4", "float", "reader.readFloat4()", "(float) 0"), //
	DOUBLE("double", "double", "reader.readDouble()", "(double) 0"), //

	CHAR("char", "char", "reader.readChar()", "(char) 0"), //
	STRING("string", "String", "reader.readString()", "\"\"");

	public final String name;
	public final String type;
	public final String instruction;
	public final String empty;

	private PrimitiveType(String name, String type, String instruction, String empty) {
		this.name = name;
		this.type = type;
		this.instruction = instruction;
		this.empty = empty;
	}

	public static boolean isPrimitive(String type) {
		return get(type) != null;
	}

	public static PrimitiveType get(String type) {
		for (PrimitiveType primitive : values())
			if (primitive.name.equals(type))
				return primitive;
		return null;
	}

}
