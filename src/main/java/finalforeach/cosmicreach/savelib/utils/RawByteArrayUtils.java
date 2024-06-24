package finalforeach.cosmicreach.savelib.utils;

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
		
		byte[] strBytes = str.getBytes();
		offset = writeInt(offset, byteArr, strBytes.length);
		
		System.arraycopy(strBytes, 0, byteArr, offset, strBytes.length);
		offset+=strBytes.length;
		return offset;
	}
}
