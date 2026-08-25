package finalforeach.cosmicreach.savelib;

import java.io.IOException;

public interface IChunkByteReader
{
	int readInt() throws IOException;
	byte readByte() throws IOException;
	String readString() throws IOException;
	short readShort() throws IOException;

	default long readLong() throws IOException
	{
		long high = (long) readInt() << 32;
		long low = (long) readInt() & 0xFFFFFFFFL;
		return high | low;
	}

	default void readFully(byte[] bytes) throws IOException 
	{
		int len = bytes.length;
		for(int i = 0; i < len; i++) 
		{
			bytes[i] = readByte();
		}
	}
}
