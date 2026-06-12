package finalforeach.cosmicreach.savelib.utils;

import java.nio.charset.StandardCharsets;

/**
 * Utility methods to write to a raw byte array as required by the cosmic reach binary serialization format.
 * */
public class RawByteArrayUtils 
{
	public static int writeByte(int offset, byte[] byteArr, byte i)
	{
		// big endian format
		byteArr[offset++] = i;
		return offset;
	}
	
	public static int writeInt(int offset, byte[] byteArr, int i)
	{
		// big endian format
		byteArr[offset++] = (byte) (i >> 24);
		byteArr[offset++] = (byte) (i >> 16);
		byteArr[offset++] = (byte) (i >> 8);
		byteArr[offset++] = (byte) (i);
		return offset;
	}	
	
	public static int writeString(int offset, byte[] byteArr, String str) 
	{
		if(str==null) 
		{
			return writeInt(offset, byteArr, -1);
		}
		
		byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
		offset = writeInt(offset, byteArr, strBytes.length);
		
		System.arraycopy(strBytes, 0, byteArr, offset, strBytes.length);
		offset+=strBytes.length;
		return offset;
	}

	public static int writeVarInt(int offset, byte[] byteArr, int value)
	{
		int zz = (value << 1) ^ (value >> 31);
		while ((zz & ~0x7F) != 0)
		{
			byteArr[offset++] = (byte) ((zz & 0x7F) | 0x80);
			zz >>>= 7;
		}
		byteArr[offset++] = (byte) zz;
		return offset;
	}
}
