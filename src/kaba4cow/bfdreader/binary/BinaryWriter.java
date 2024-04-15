package kaba4cow.bfdreader.binary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A utility class for writing binary data to an OutputStream.
 *
 * @version 1.0
 * @author Yaroslav
 * @see BinaryReader
 */
public class BinaryWriter {

	private final OutputStream stream;
	private final ByteArrayOutputStream output;

	private boolean closed;

	private long length;

	/**
	 * Constructs a BinaryWriter with the specified OutputStream.
	 *
	 * @param output the OutputStream to write to.
	 */
	public BinaryWriter(OutputStream stream) {
		this.stream = stream;
		this.output = new ByteArrayOutputStream();
		this.closed = false;
		this.length = 0l;
	}

	/**
	 * Constructs a BinaryWriter with the specified File.
	 *
	 * @param file the File to write to.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter(File file) throws IOException {
		this(new FileOutputStream(file));
	}

	/**
	 * Closes this writer and its OutputStream.
	 *
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter close() throws IOException {
		if (!closed) {
			output.writeTo(stream);
			stream.close();
			closed = true;
		}
		return this;
	}

	/**
	 * Writes a single byte.
	 *
	 * @param b a byte to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeByte(byte b) throws IOException {
		output.write(b);
		length++;
		return this;
	}

	/**
	 * Writes a single short.
	 *
	 * @param s a short to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeShort(short s) throws IOException {
		writeByte((byte) ((s >> 8) & 0xFF));
		writeByte((byte) ((s >> 0) & 0xFF));
		return this;
	}

	/**
	 * Writes a single char.
	 *
	 * @param c a char to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeChar(char c) throws IOException {
		writeByte((byte) ((c >> 8) & 0xFF));
		writeByte((byte) ((c >> 0) & 0xFF));
		return this;
	}

	/**
	 * Writes a single int.
	 *
	 * @param i an int to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeInt(int i) throws IOException {
		writeByte((byte) ((i >> 24) & 0xFF));
		writeByte((byte) ((i >> 16) & 0xFF));
		writeByte((byte) ((i >> 8) & 0xFF));
		writeByte((byte) ((i >> 0) & 0xFF));
		return this;
	}

	/**
	 * Writes a single long.
	 *
	 * @param l a long to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeLong(long l) throws IOException {
		writeByte((byte) ((l >> 56) & 0xFF));
		writeByte((byte) ((l >> 48) & 0xFF));
		writeByte((byte) ((l >> 40) & 0xFF));
		writeByte((byte) ((l >> 32) & 0xFF));
		writeByte((byte) ((l >> 24) & 0xFF));
		writeByte((byte) ((l >> 16) & 0xFF));
		writeByte((byte) ((l >> 8) & 0xFF));
		writeByte((byte) ((l >> 0) & 0xFF));
		return this;
	}

	/**
	 * Writes a 2-byte float.
	 *
	 * @param f a float to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeFloat2(float f) throws IOException {
		int i = shortFloatToInt(f);
		writeByte((byte) ((i >> 8) & 0xFF));
		writeByte((byte) ((i >> 0) & 0xFF));
		return this;
	}

	/**
	 * Writes a 4-byte float.
	 *
	 * @param f a float to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeFloat4(float f) throws IOException {
		return writeInt(Float.floatToIntBits(f));
	}

	private static int shortFloatToInt(float f) {
		int bits = Float.floatToIntBits(f);
		int sign = bits >>> 16 & 0x8000;
		int value = (bits & 0x7FFFFFFF) + 0x1000;
		if (value >= 0x47800000) {
			if ((bits & 0x7FFFFFFF) >= 0x47800000) {
				if (value < 0x7F800000)
					return sign | 0x7C00;
				return sign | 0x7C00 | (bits & 0x007FFFFF) >>> 13;
			}
			return sign | 0x7bFF;
		}
		if (value >= 0x38800000)
			return sign | value - 0x38000000 >>> 13;
		if (value < 0x33000000)
			return sign;
		value = (bits & 0x7FFFFFFF) >>> 23;
		return sign | ((bits & 0x7FFFFF | 0x800000) + (0x800000 >>> value - 102) >>> 126 - value);
	}

	/**
	 * Writes a single double.
	 *
	 * @param d a double to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeDouble(double d) throws IOException {
		return writeLong(Double.doubleToLongBits(d));
	}

	/**
	 * Writes all bytes of the string and a null-terminator.
	 *
	 * @param string a string to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeString(String string) throws IOException {
		return writeByteArray(string.getBytes()).writeByte((byte) 0);
	}

	/**
	 * Writes an array of bytes.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeByteArray(byte[] data) throws IOException {
		output.write(data);
		length += data.length;
		return this;
	}

	/**
	 * Writes an array of chars.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeCharArray(char[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			writeChar(data[i]);
		return this;
	}

	/**
	 * Writes an array of ints.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeIntArray(int[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			writeInt(data[i]);
		return this;
	}

	/**
	 * Writes an array of longs.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeLongArray(long[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			writeLong(data[i]);
		return this;
	}

	/**
	 * Writes an array of 2-byte floats.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeFloat2Array(float[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			writeFloat2(data[i]);
		return this;
	}

	/**
	 * Writes an array of 4-byte floats.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeFloat42Array(float[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			writeFloat4(data[i]);
		return this;
	}

	/**
	 * Writes an array of doubles.
	 *
	 * @param data the array to write.
	 * @return a reference to this object.
	 * @throws IOException if an I/O error occurs.
	 */
	public BinaryWriter writeDoubleArray(double[] data) throws IOException {
		for (int i = 0; i < data.length; i++)
			writeDouble(data[i]);
		return this;
	}

	/**
	 * Returns the amount of bytes written to the writer.
	 *
	 * @return the amount of bytes written to the writer.
	 */
	public long length() {
		return length;
	}

	/**
	 * Returns if the writer is closed.
	 *
	 * @return is the writer is closed.
	 */
	public boolean closed() {
		return closed;
	}

}
