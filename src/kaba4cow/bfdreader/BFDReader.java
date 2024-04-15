package kaba4cow.bfdreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kaba4cow.bfdreader.binary.BinaryReader;
import kaba4cow.bfdreader.parser.ParsingException;
import kaba4cow.bfdreader.parser.SourceBuilder;
import kaba4cow.bfdreader.parser.TokenBuffer;
import kaba4cow.bfdreader.parser.Tokenizer;
import kaba4cow.bfdreader.parser.objects.ObjectData;

/**
 * <p>
 * A code generator class that reads a file written in Binary Format Definition
 * language and generates Java classes which can then be used to read files in
 * the binary formats defined in the file.
 * 
 * <p>
 * A format definition file may contain multiple formats and objects. Objects
 * can be defined in other objects, but a format cannot be defined inside any
 * other block. The generated format constructors are {@code public} and the
 * object constructors are {@code private}. All generated fields are
 * {@code public final}, object fields are read by invoking the constructors of
 * their generated classes and primitive fields and {@code Strings} are read
 * from BinaryReader passed to every format and object constructor.
 * 
 * <p>
 * The syntax for defining a binary format is as follows:
 *
 * <pre>{@code
 * format FormatName {
 * 	object ObjectName {
 * 		byte objectFieldName;
 * 	}
 * 	long formatFieldName;
 * 	ObjectName formatObjectFieldName;
 * }
 * }</pre>
 * 
 * In the above syntax:
 * <ul>
 * <li>{@code format} keyword is used to define a new binary format.</li>
 * <li>{@code object} keyword is used to define a new object within the
 * format.</li>
 * <li>{@code FormatName} is the name of the format.</li>
 * <li>{@code ObjectName} is the name of the object within the format.</li>
 * <li>{@code objectFieldName} is the name of the field of type {@code byte}
 * within the {@code FormatName} format.</li>
 * <li>{@code formatFieldName} is the name of the field of type {@code long}
 * within the {@code ObjectName} object.</li>
 * <li>{@code formatFieldName} is the name of the field of type
 * {@code ObjectName} within the {@code FormatName} format.</li>
 * <li>{@code DataType} is the type of {@code objectFieldName} and
 * {@code objectFieldName} fields.</li>
 * </ul>
 * 
 * <p>
 * The primitive data types supported by the Binary Format Definition language
 * are:
 * <ul>
 * <li>{@code byte} reads a 1-byte signed integer as a Java {@code byte}</li>
 * <li>{@code short} reads a 2-byte signed integer as a Java {@code short}</li>
 * <li>{@code int} reads a 4-byte signed integer as a Java {@code int}</li>
 * <li>{@code long} reads an 8-byte signed integer as a Java {@code long}</li>
 * <li>{@code u_byte} reads a 1-byte unsigned integer as a Java
 * {@code short}</li>
 * <li>{@code u_short} reads a 2-byte unsigned integer as a Java
 * {@code int}</li>
 * <li>{@code u_int} reads a 4-byte unsigned integer as a Java {@code long}</li>
 * <li>{@code float2} reads a 2-byte float as a Java {@code float}</li>
 * <li>{@code float4} reads a 4-byte float as a Java {@code float}</li>
 * <li>{@code double} reads a 8-byte float as a Java {@code double}</li>
 * <li>{@code char} reads a 2-byte character as a Java {@code char}</li>
 * <li>{@code string} reads a sequence of bytes ending with a null-terminator,
 * generates Java {@code String}</li>
 * </ul>
 * 
 * <p>
 * The language supports arrays which are declared as follows:
 * 
 * <pre>{@code DataType[arrayLength] variableName;}</pre>
 * 
 * <p>
 * Array lengths are read as expressions and get written to the Java array
 * initializers directly. Here is an example of a format containing arrays:
 *
 * <pre>{@code
 * format FormatName {
 * 	int[4] array1;
 * 	int[(2 + array1.length) / 3] array2;
 * 	int array_length;
 * 	int[array_length] array3;
 * }
 * }</pre>
 * 
 * <p>
 * and its generated constructor:
 *
 * <pre>{@code
 * public FormatName(BinaryReader reader) throws IOException {
 * 	this.array1 = new int[(int) (4)];
 * 	this.array2 = new int[(int) ((2 + array1.length) / 3)];
 * 	this.array_length = reader.readInt();
 * 	this.array3 = new int[(int) (array_length)];
 * }
 * }</pre>
 * 
 * <p>
 * As seen in the generated code, the array length expressions are wrapped in
 * parenthesis and casted to {@code int} to ensure that the array is initialized
 * with an {@code int} length.
 * 
 * <p>
 * The language supports conditions which are also read as expressions and get
 * generated in Java conditional expressions as declared. Here is an example of
 * a format containing a condition:
 *
 * <pre>{@code
 * format FormatName {
 * 	object ObjectName {
 * 	}
 * 	u_byte condition_parameter;
 * 	if (condition_parameter > 0) {
 * 		short primitive_field;
 * 		string string_field;
 * 		ObjectName object_field;
 * 	}
 * }
 * }</pre>
 * 
 * <p>
 * and its generated constructor:
 *
 * <pre>{@code
 * public FormatName(BinaryReader reader) throws IOException {
 * 	this.condition_parameter = reader.readUnsignedByte();
 * 	if (condition_parameter > 0) {
 * 		this.primitive_field = reader.readShort();
 * 		this.string_field = reader.readString();
 * 		this.object_field = new ObjectName(reader);
 * 	} else {
 * 		this.primitive_field = (short) 0;
 * 		this.string_field = "";
 * 		this.object_field = null;
 * 	}
 * }
 * }</pre>
 * 
 * <p>
 * As seen above, if condition passes, the fields are initialized as usual,
 * otherwise, the primitive fields are set to {@code 0}, string fields are
 * initialized with empty {@code Strings} and object fields are set to
 * {@code null}.
 *
 * @see BinaryReader
 * @version 1.0
 * @author Yaroslav
 */
public class BFDReader {

	private TokenBuffer tokens;
	private final ArrayList<ObjectData> objects;
	private final SourceBuilder builder;

	/**
	 * Constructs a new BFDReader.
	 */
	public BFDReader() {
		objects = new ArrayList<>();
		builder = new SourceBuilder();
	}

	/**
	 * Reads and parses the Binary Format Definition script from the specified
	 * InputStream.
	 * 
	 * @param input the InputStream to read from.
	 * @return a reference to this object.
	 * @throws IOException      if an I/O error occurs.
	 * @throws ParsingException if a parsing error occurs.
	 */
	public BFDReader parse(InputStream input) throws IOException, ParsingException {
		objects.clear();
		tokens = new Tokenizer(input).getTokens();
		while (tokens.hasNext())
			if (tokens.get(0).valueEquals("format", "object"))
				objects.add(new ObjectData(tokens));
		tokens = null;
		return this;
	}

	/**
	 * Reads and parses the Binary Format Definition script from the specified File.
	 * 
	 * @param file the File to read from.
	 * @return a reference to this object.
	 * @throws IOException      if an I/O error occurs.
	 * @throws ParsingException if a parsing error occurs.
	 */
	public BFDReader parse(File file) throws IOException, ParsingException {
		return parse(new FileInputStream(file));
	}

	/**
	 * Reads and parses the Binary Format Definition script from the specified
	 * resource path.
	 * 
	 * @param path the resource path to read from.
	 * @return a reference to this object.
	 * @throws IOException      if an I/O error occurs.
	 * @throws ParsingException if a parsing error occurs.
	 */
	public BFDReader parse(String path) throws IOException, ParsingException {
		return parse(getClass().getClassLoader().getResourceAsStream(path));
	}

	/**
	 * Generates a Java class containing formats and objects defined in the parsed
	 * Binary Format Definition script.
	 * 
	 * @param packageName the name of the package for the package declaration. If
	 *                    {@code packageName == null} the package declaration won't
	 *                    be generated.
	 * @param className   the name of the Java class containing formats and objects
	 *                    defined in the parsed Binary Format Definition script.
	 * @return a reference to this object.
	 * @throws IOException              if an I/O error occurs.
	 * @throws IllegalArgumentException if {@code className == null}.
	 */
	public BFDReader generate(String packageName, String className) throws IOException {
		if (className == null)
			throw new IllegalArgumentException("className cannot be null");
		builder.clear();
		if (packageName != null)
			builder.write("package %s;\n\n", packageName);
		builder.write("import %s.%s;\n", IOException.class.getPackageName(), IOException.class.getSimpleName());
		builder.write("import %s.%s;\n", BinaryReader.class.getPackageName(), BinaryReader.class.getSimpleName());
		builder.write("\npublic class %s {\n\n", className);
		for (ObjectData object : objects)
			object.generate(builder, 1);
		objects.clear();
		builder.write("}\n");
		return this;
	}

	/**
	 * Writes the generated source to the specified file.
	 * 
	 * @param file the file to write to.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BFDReader write(File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(builder.toString());
		builder.clear();
		writer.close();
		return this;
	}

	/**
	 * Returns the source of the generated Java class.
	 * 
	 * @return the source of the generated Java class.
	 */
	public String getSource() {
		return builder.toString();
	}

}