package finalforeach.cosmicreach.savelib;

public interface IChunkByteWriter {

	<T> void writeBlockValue(T blockValue);

	void writeInt(int i);

	void writeByte(int b);

	void writeBytes(byte[] bytes);

}
