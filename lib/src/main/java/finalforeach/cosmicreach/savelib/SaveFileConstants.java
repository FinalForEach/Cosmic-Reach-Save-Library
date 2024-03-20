package finalforeach.cosmicreach.savelib;

public class SaveFileConstants 
{
	// [F]inal[F]or[E]ach [C]osmi[c]r[eac]h
	public static final int MAGIC = 0xFFECCEAC;
	public static final int FILE_VERSION = 0;	
	public static final int COMPRESSION_TYPE_NONE = 0;

	public static final int SINGLEBLOCK = 1;
	public static final int LAYERED = 2;
	
	public static final int LAYER_SINGLE_BYTE = 1;
	public static final int LAYER_SINGLE_INT = 2;
	public static final int LAYER_HALFNIBBLE = 3;
	public static final int LAYER_NIBBLE = 4;
	public static final int LAYER_BYTE = 5;
	
	public static final int SKYLIGHTDATA_NULL = 1;
	public static final int SKYLIGHTDATA_LAYERED = 2;
	public static final int SKYLIGHTDATA_SINGLE = 3;
	
	public static final int SKYLIGHTDATA_LAYER_SINGLE = 1;
	public static final int SKYLIGHTDATA_LAYER_NIBBLE = 2;
	
	public static final int BLOCKLIGHTDATA_NULL = 1;
	public static final int BLOCKLIGHTDATA_LAYERED = 2;
	
	public static final int BLOCKLIGHTDATA_LAYER_SINGLE = 1;
	public static final int BLOCKLIGHTDATA_LAYER_SHORT = 2;
}
