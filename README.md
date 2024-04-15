# Binary Format Definition Reader

A code generator class that reads a file written in Binary Format Definition language and generates Java classes which can then be used to read files in the binary formats defined in the file.

A format definition file may contain multiple formats and objects. Objects can be defined in other objects, but a format cannot be defined inside any other block. The generated format constructors are **public** and the object constructors are **private**. All generated fields are **public final**, object fields are read by invoking the constructors of their generated classes and primitive fields and **Strings** are read from BinaryReader passed to every format and object constructor.

### Usage

A simple example of using the BFDReader is shown in the **kaba4cow.example.Example** class.
 - Use parse() to read and parse the BFD script. The function may be provided either with an **InputStream**, **File** or a resource path to read from.
 - Use generate() to generate the source code for the formats. The **className** parameter is mandatory and is used for the name of the class containing the defined formats. The **packageName** parameter is used in the package declaration and can be passed as **null** so that package declaration won't be generated.
 - Use getSource() to get a **String** representation of the generated source.
 - Use write() to write the generated source to a file.
 
### Files

 - **example.bfd** is a Binary Format Definition script used in the **kaba4cow.example.Example** class.
 - **Formats.java** is a class containing the formats generated from the original **example.bfd** script.
 - **bfd4npp.xml** is a Notepad++ language definition file for Binary Format Definition language. 

## Binary Format Definition Language

The syntax for defining a binary format is as follows:

	format FormatName {
		object ObjectName {
			byte objectFieldName;
		}
		long formatFieldName;
		ObjectName formatObjectFieldName;
	}

In the above syntax:
 - **format** keyword is used to define a new binary format.
 - **object** keyword is used to define a new object within the format.
 - **FormatName** is the name of the format.
 - **ObjectName** is the name of the object within the format.
 - **objectFieldName** is the name of the field of type **byte** within the **FormatName** format.
 - **formatFieldName** is the name of the field of type **long** within the **ObjectName** object.
 - **formatFieldName** is the name of the field of type **ObjectName** within the **FormatName** format.
 - **DataType** is the type of **objectFieldName** and **objectFieldName** fields.

The primitive data types supported by the Binary Format Definition language are:
 - **byte** reads a 1-byte signed integer as a Java **byte**
 - **short** reads a 2-byte signed integer as a Java **short**
 - **int** reads a 4-byte signed integer as a Java **int**
 - **long** reads an 8-byte signed integer as a Java **long**
 - **u_byte** reads a 1-byte unsigned integer as a Java **short**
 - **u_short** reads a 2-byte unsigned integer as a Java **int**
 - **u_int** reads a 4-byte unsigned integer as a Java **long**
 - **float2** reads a 2-byte float as a Java **float**
 - **float4** reads a 4-byte float as a Java **float**
 - **double** reads a 8-byte float as a Java **double**
 - **char** reads a 2-byte character as a Java **char**
 - **string** reads a sequence of bytes ending with a null-terminator as a Java **String**

The language supports arrays which are declared as follows:

	DataType[arrayLength] variableName;

Array lengths are read as expressions and get written to the Java array initializers directly. Here is an example of a format containing arrays:

	format FormatName {
		int[4] array1;
		int[(2 + array1.length) / 3] array2;
		int array_length;
		int[array_length] array3;
	}

and its generated constructor:

	public FormatName(BinaryReader reader) throws IOException {
		this.array1 = new int[(int) (4)];
		this.array2 = new int[(int) ((2 + array1.length) / 3)];
		this.array_length = reader.readInt();
		this.array3 = new int[(int) (array_length)];
	}

As seen in the generated code, the array length expressions are wrapped in parenthesis and casted to **int** to ensure that the array is initialized with an **int** length.

The language supports conditions which are also read as expressions and get generated in Java conditional expressions as declared. Here is an example of a format containing a condition:

	format FormatName {
		object ObjectName {
		}
		u_byte condition_parameter;
		if (condition_parameter > 0) {
			short primitive_field;
			string string_field;
			ObjectName object_field;
		}
		}
	}

and its generated constructor:

	public FormatName(BinaryReader reader) throws IOException {
		this.condition_parameter = reader.readUnsignedByte();
		if (condition_parameter > 0) {
			this.primitive_field = reader.readShort();
			this.string_field = reader.readString();
			this.object_field = new ObjectName(reader);
		} else {
			this.primitive_field = (short) 0;
			this.string_field = "";
			this.object_field = null;
		}
	}

As seen above, if condition passes, the fields are initialized as usual, otherwise, the primitive fields are set to **0**, string fields are initialized with empty **Strings** and object fields are set to **null**.