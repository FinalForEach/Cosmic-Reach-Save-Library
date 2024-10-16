package finalforeach.cosmicreach.savelib;

public interface IByteArray 
{

	byte[] toArray();

	void addAll(byte... bytes);

	void set(int index, byte b);

	void add(byte b);

	void addAll(IByteArray bytes);

	int size();

	byte[] items();

}
