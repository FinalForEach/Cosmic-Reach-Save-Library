package finalforeach.cosmicreach.savelib;

import java.io.IOException;

public interface IChunkByteReader
{
	int readInt() throws IOException;
	byte readByte() throws IOException;
	String readString() throws IOException;
	void readFully(byte[] bytes) throws IOException;
	short readShort() throws IOException;
}
