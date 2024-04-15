package kaba4cow.bfdreader.binary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A utility class for reading binary data from an InputStream.
 *
 * @version 1.0
 * @author Yaroslav
 * @see BinaryWriter
 */
public class BinaryReader {

	private final InputStream input;

	private boolean bidEndian;

	private long position;

	private boolean endOfFile;
	private boolean closed;

	/**
	 * Constructs a BinaryReader with the specified InputStream.
	 *
	 * @param input the InputStream to read from.
	 */
	public BinaryReader(InputStream input) {
		this.input = input;
		this.bidEndian = true;
		this.position = 0l;
		this.endOfFile = false;
		this.closed = false;
	}

	/**
	 * Constructs a BinaryReader with the specified File.
	 *
	 * @param file the File to read from.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryReader(File file) throws IOException {
		this(new FileInputStream(file));
	}

	/**
	 * Constructs a BinaryReader with the specified path.
	 *
	 * @param path the resource path to read from.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryReader(String path) throws IOException {
		this(BinaryReader.class.getClassLoader().getResourceAsStream(path));
	}

	/**
	 * Closes this reader and its InputStream.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryReader close() throws IOException {
		if (!closed) {
			input.close();
			closed = true;
		}
		return this;
	}

	/**
	 * Skips specified amount of bytes.
	 *
	 * @param bytes the amount of bytes to skip.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryReader skip(long bytes) throws IOException {
		input.skip(bytes);
		position += bytes;
		return this;
	}

	/**
	 * Reads the next byte of data.
	 *
	 * @return the next byte of data, or {@code -1} if the end of the stream is
	 *         reached.
	 * @throws IOException if an I/O error occurs.
	 */
	public int read() throws IOException {
		if (endOfFile)
			return -1;
		int b = input.read();
		if (b == -1) {
			endOfFile = true;
			close();
		} else
			position++;
		return b;
	}

	private byte[] reverse(byte[] array) {
		byte[] reversed = new byte[array.length];
		for (int i = 0; i < array.length; i++)
			reversed[i] = array[array.length - 1 - i];
		return reversed;
	}

	/**
	 * Reads the next byte of data.
	 *
	 * @return the next byte of data.
	 * @throws IOException if an I/O error occurs.
	 */
	public byte readByte() throws IOException {
		return (byte) (read() & 0xFF);
	}

	/**
	 * Reads the next 2 bytes of data and converts them to short.
	 *
	 * @return the next 2 bytes of data converted to short.
	 * @throws IOException if an I/O error occurs.
	 */
	public short readShort() throws IOException {
		short s = 0;
		byte[] data = readBytes(2);
		s |= (data[0] << 8) & 0xFF00;
		s |= (data[1] << 0) & 0x00FF;
		return s;
	}

	/**
	 * Reads the next 2 bytes of data and converts them to char.
	 *
	 * @return the next 2 bytes of data converted to char.
	 * @throws IOException if an I/O error occurs.
	 */
	public char readChar() throws IOException {
		char c = 0;
		byte[] data = readBytes(2);
		c |= ((char) data[0] << 8) & 0xFF00;
		c |= ((char) data[1] << 0) & 0x00FF;
		return c;
	}

	/**
	 * Reads the next 4 bytes of data and converts them to int.
	 *
	 * @return the next 4 bytes of data converted to int.
	 * @throws IOException if an I/O error occurs.
	 */
	public int readInt() throws IOException {
		int i = 0;
		byte[] data = readBytes(4);
		i |= (data[0] << 24) & 0xFF000000;
		i |= (data[1] << 16) & 0x00FF0000;
		i |= (data[2] << 8) & 0x0000FF00;
		i |= (data[3] << 0) & 0x000000FF;
		return i;
	}

	/**
	 * Reads the next 2 bytes of data and converts them to short representing an
	 * unsigned byte.
	 *
	 * @return the next 2 bytes of data converted to short.
	 * @throws IOException if an I/O error occurs.
	 */
	public short readUnsignedByte() throws IOException {
		return (short) (readByte() & 0xFF);
	}

	/**
	 * Reads the next 4 bytes of data and converts them to int representing an
	 * unsigned short.
	 *
	 * @return the next 4 bytes of data converted to int.
	 * @throws IOException if an I/O error occurs.
	 */
	public int readUnsignedShort() throws IOException {
		return readShort() & 0xFFFF;
	}

	/**
	 * Reads the next 4 bytes of data and converts them to long representing an
	 * unsigned int.
	 *
	 * @return the next 4 bytes of data converted to long.
	 * @throws IOException if an I/O error occurs.
	 */
	public long readUnsignedInt() throws IOException {
		return readInt() & 0xFFFFFFFFl;
	}

	/**
	 * Reads the next 8 bytes of data and converts them to long.
	 *
	 * @return the next 8 bytes of data converted to long.
	 * @throws IOException if an I/O error occurs.
	 */
	public long readLong() throws IOException {
		long l = 0;
		byte[] data = readBytes(8);
		l |= ((long) data[0] << 56) & 0xFF00000000000000l;
		l |= ((long) data[1] << 48) & 0x00FF000000000000l;
		l |= ((long) data[2] << 40) & 0x0000FF0000000000l;
		l |= ((long) data[3] << 32) & 0x000000FF00000000l;
		l |= ((long) data[4] << 24) & 0x00000000FF000000l;
		l |= ((long) data[5] << 16) & 0x0000000000FF0000l;
		l |= ((long) data[6] << 8) & 0x000000000000FF00l;
		l |= ((long) data[7] << 0) & 0x00000000000000FFl;
		return l;
	}

	/**
	 * Reads the next 2 bytes of data and converts them to float.
	 *
	 * @return the next 2 bytes of data converted to float.
	 * @throws IOException if an I/O error occurs.
	 */
	public float readFloat2() throws IOException {
		int i = 0;
		byte[] data = readBytes(2);
		i |= (data[0] << 8) & 0xFF00;
		i |= (data[1] << 0) & 0x00FF;
		return intToShortFloat(i);
	}

	/**
	 * Reads the next 4 bytes of data and converts them to float.
	 *
	 * @return the next 4 bytes of data converted to float.
	 * @throws IOException if an I/O error occurs.
	 */
	public float readFloat4() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	private static float intToShortFloat(int i) {
		int mantissa = i & 0x03FF;
		int exponent = i & 0x7C00;
		if (exponent == 0x7C00)
			exponent = 0x3FC00;
		else if (exponent != 0) {
			exponent += 0x1C000;
			if (mantissa == 0 && exponent > 0x1C400)
				return Float.intBitsToFloat((i & 0x8000) << 16 | exponent << 13 | 0x3FF);
		} else if (mantissa != 0) {
			exponent = 0x1C400;
			do {
				mantissa <<= 1;
				exponent -= 0x400;
			} while ((mantissa & 0x400) == 0);
			mantissa &= 0x3FF;
		}
		return Float.intBitsToFloat((i & 0x8000) << 16 | (exponent | mantissa) << 13);
	}

	/**
	 * Reads the next 8 bytes of data and converts them to double.
	 *
	 * @return the next 8 bytes of data converted to double.
	 * @throws IOException if an I/O error occurs.
	 */
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * Reads a null-terminated string.
	 *
	 * @return the string.
	 * @throws IOException if an I/O error occurs.
	 */
	public String readString() throws IOException {
		StringBuilder string = new StringBuilder();
		byte c;
		while ((c = readByte()) != '\0')
			string.append((char) c);
		return string.toString();
	}

	/**
	 * Reads {@code length} amount of bytes to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public byte[] readBytes(int length) throws IOException {
		byte[] data = new byte[length];
		for (int i = 0; i < length; i++)
			data[i] = readByte();
		if (!bidEndian)
			return reverse(data);
		return data;
	}

	/**
	 * Reads {@code data.length} amount of bytes to the {@code data} array.
	 *
	 * @param data the array to read the bytes to.
	 * @return {@code data} array.
	 * @throws IOException if an I/O error occurs.
	 */
	public byte[] readBytes(byte[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			data[i] = readByte();
		if (!bidEndian)
			return reverse(data);
		return data;
	}

	/**
	 * Reads {@code length} amount of shorts to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of shorts to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public short[] readShortArray(int length) throws IOException {
		short[] array = new short[length];
		for (int i = 0; i < length; i++)
			array[i] = readShort();
		return array;
	}

	/**
	 * Reads {@code length} amount of chars to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of chars to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public char[] readCharArray(int length) throws IOException {
		char[] array = new char[length];
		for (int i = 0; i < length; i++)
			array[i] = readChar();
		return array;
	}

	/**
	 * Reads {@code length} amount of ints to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of ints to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public int[] readIntArray(int length) throws IOException {
		int[] array = new int[length];
		for (int i = 0; i < length; i++)
			array[i] = readInt();
		return array;
	}

	/**
	 * Reads {@code length} amount of longs to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of longs to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public long[] readLongArray(int length) throws IOException {
		long[] array = new long[length];
		for (int i = 0; i < length; i++)
			array[i] = readLong();
		return array;
	}

	/**
	 * Reads {@code length} amount of 2-byte floats to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of floats to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public float[] readFloat2Array(int length) throws IOException {
		float[] array = new float[length];
		for (int i = 0; i < length; i++)
			array[i] = readFloat2();
		return array;
	}

	/**
	 * Reads {@code length} amount of 4-byte floats to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of floats to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public float[] readFloat4Array(int length) throws IOException {
		float[] array = new float[length];
		for (int i = 0; i < length; i++)
			array[i] = readFloat4();
		return array;
	}

	/**
	 * Reads {@code length} amount of doubles to an array.
	 *
	 * @param length the length of the array.
	 * @return the array of doubles to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public double[] readDoubleArray(int length) throws IOException {
		double[] array = new double[length];
		for (int i = 0; i < length; i++)
			array[i] = readDouble();
		return array;
	}

	/**
	 * Sets a flag for reader to read bytes in a big-endian format.
	 *
	 * @return a reference to this object.
	 */
	public BinaryReader bigEndian() {
		bidEndian = true;
		return this;
	}

	/**
	 * Sets a flag for reader to read bytes in a little-endian format.
	 *
	 * @return a reference to this object.
	 */
	public BinaryReader littleEndian() {
		bidEndian = false;
		return this;
	}

	/**
	 * Returns if the reader has reached the end of the InputStream.
	 *
	 * @return if the reader has reached the end of the InputStream.
	 */
	public boolean endOfFile() {
		return endOfFile;
	}

	/**
	 * Returns if the reader is closed.
	 *
	 * @return is the reader is closed.
	 */
	public boolean closed() {
		return closed;
	}

	/**
	 * Returns the amount of bytes read since the reader initialization.
	 *
	 * @return the amount of bytes read.
	 */
	public long position() {
		return position;
	}

}
