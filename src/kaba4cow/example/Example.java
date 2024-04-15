package kaba4cow.example;

import java.io.File;

import kaba4cow.bfdreader.BFDReader;

public class Example {

	public Example() {
	}

	public static void main(String[] args) throws Exception {
		BFDReader builder = new BFDReader(); // constructs a new BFDReader
		builder.parse(new File("example.bfd")); // parses the "example.bfd" file
		builder.generate("kaba4cow.example", "Formats"); // generates a "Formats" class with a package
															// "kaba4cow.example"
		System.out.println(builder.getSource()); // prints the generated source
		builder.write(new File("Formats.java")); // writes the generated source to the "Formats.java" file
	}

}
