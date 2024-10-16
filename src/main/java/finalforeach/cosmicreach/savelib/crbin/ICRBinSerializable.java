package finalforeach.cosmicreach.savelib.crbin;

public interface ICRBinSerializable
{
	void read(CRBinDeserializer deserial);
	void write(CRBinSerializer serial);
}