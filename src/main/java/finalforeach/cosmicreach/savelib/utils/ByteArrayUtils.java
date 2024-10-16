package finalforeach.cosmicreach.savelib.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import finalforeach.cosmicreach.savelib.IByteArray;


public class ByteArrayUtils 
{
	public static byte readByte(ByteBuffer bytes) 
	{
		return bytes.get();
	}

	public static Double readDouble(ByteBuffer bytes) 
	{
		return Double.longBitsToDouble(readLong(bytes));
	}
		
	public static float readFloat(ByteBuffer bytes) 
	{
		int i = readInt(bytes);
		return Float.intBitsToFloat(i);
	}

	public static int readInt(ByteBuffer bytes) 
	{
		// big endian format
		byte[] intBytes = new byte[4];
		bytes.get(intBytes);
		int i = ((intBytes[0] & 0xFF) << 24) | ((intBytes[1] & 0xFF) << 16) | ((intBytes[2] & 0xFF) << 8) | (intBytes[3] & 0xFF);
		return i;
	}

	public static long readLong(ByteBuffer bytes) {
		// big endian format
		int i1 = readInt(bytes);
		int i2 = readInt(bytes);
		long l = ((long)i1 << 32) | (i2 & 0xFFFFFFFFL);
		return l;
	}

	public static short readShort(ByteBuffer bytes)
	{
		// big endian format
		byte[] shortBytes = new byte[2];
		bytes.get(shortBytes);
		short i = (short) (((shortBytes[0] & 0xFF) << 8) | (shortBytes[1] & 0xFF));
		return i;
	}

	public static String readString(ByteBuffer bytes) 
	{
		int stringLength = readInt(bytes);
		if(stringLength==-1) 
		{
			// Null strings are written as having a length of -1.
			return null;
		}
		
		byte[] strBytes = new byte[stringLength];
		bytes.get(strBytes);
		// Remember to set the character set to UTF-8 to keep behavior the same
		// across all computers!
		return new String(strBytes, StandardCharsets.UTF_8);	
	}
	
	public static void setInt(IByteArray byteArr, int index, int val) 
	{
		byteArr.set(index  , (byte) (val >> 24));
		byteArr.set(index+1, (byte) (val >> 16));
		byteArr.set(index+2, (byte) (val >> 8));
		byteArr.set(index+3, (byte) (val));
	}

	public static void writeByte(IByteArray byteArr, int i)
	{
		// big endian format
		byteArr.add((byte) (i));	
	}

	public static void writeDouble(IByteArray bytes, double d) {
		writeLong(bytes, Double.doubleToRawLongBits(d));
	}

	public static void writeFloat(IByteArray byteArr, float f) 
	{
		writeInt(byteArr, Float.floatToRawIntBits(f));
	}

	public static void writeInt(IByteArray byteArr, int i)
	{
		// big endian format
		byteArr.add((byte) (i >> 24));
		byteArr.add((byte) (i >> 16));
		byteArr.add((byte) (i >> 8));
		byteArr.add((byte) (i));
	}

	public static void writeLong(IByteArray bytes, long l) {
		writeInt(bytes, (int)(l >> 32));
		writeInt(bytes, (int)(l));
	}

	public static void writeShort(IByteArray byteArr, int i) 
	{
		// big endian format
		byteArr.add((byte) (i >> 8));
		byteArr.add((byte) (i));
	}

	public static void writeString(IByteArray byteArr, String str) 
	{
		if(str == null) 
		{
			writeInt(byteArr, -1);
			return;
		}
		
		byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
		writeInt(byteArr, strBytes.length);
		byteArr.addAll(strBytes);
	}


}
