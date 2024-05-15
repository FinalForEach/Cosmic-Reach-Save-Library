package finalforeach.cosmicreach.savelib;

public class SaveFileConstants 
{
	// [F]inal[F]or[E]ach [C]osmi[c]r[eac]h
	public static final int MAGIC = 0xFFECCEAC;
	public static final int FILE_VERSION = 0;	
	public static final int COMPRESSION_TYPE_NONE = 0;

	public static final int BLOCK_NULL = 0;
	public static final int BLOCK_SINGLE = 1;
	public static final int BLOCK_LAYERED = 2;

	public static final int BLOCK_LAYER_SINGLE_BYTE = 1;
	public static final int BLOCK_LAYER_SINGLE_INT = 2;
	public static final int BLOCK_LAYER_HALFNIBBLE = 3;
	public static final int BLOCK_LAYER_NIBBLE = 4;
	public static final int BLOCK_LAYER_BYTE = 5;
	public static final int BLOCK_LAYER_SHORT = 6;
	
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
