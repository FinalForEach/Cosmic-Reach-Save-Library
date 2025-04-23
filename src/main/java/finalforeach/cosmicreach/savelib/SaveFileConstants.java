package finalforeach.cosmicreach.savelib;

public class SaveFileConstants
{
	// [F]inal[F]or[E]ach [C]osmi[c]r[eac]h
	public static final int MAGIC = 0xFFECCEAC;
	public static final int FILE_VERSION = 4; // TODO: Upgrade to 5 when adding LZ4 properly
	public static final int COMPRESSION_TYPE_NONE = 0;
	public static final int COMPRESSION_TYPE_LZ4 = 1;

	public static int PREFERRED_COMPRESSION_TYPE = COMPRESSION_TYPE_NONE;// COMPRESSION_TYPE_LZ4;

	public static final int BLOCK_NULL = 0;
	public static final int BLOCK_SINGLE = 1;
	public static final int BLOCK_LAYERED = 2;

	public static final int BLOCK_LAYER_SINGLE_BYTE = 1;
	public static final int BLOCK_LAYER_SINGLE_INT = 2;
	public static final int BLOCK_LAYER_HALFNIBBLE = 3;
	public static final int BLOCK_LAYER_NIBBLE = 4;
	public static final int BLOCK_LAYER_BYTE = 5;
	public static final int BLOCK_LAYER_SHORT = 6;
	public static final int BLOCK_LAYER_BIT = 7;

	public static final int SKYLIGHTDATA_NULL = 1;
	public static final int SKYLIGHTDATA_LAYERED = 2;
	public static final int SKYLIGHTDATA_SINGLE = 3;

	public static final int SKYLIGHTDATA_LAYER_SINGLE = 1;
	public static final int SKYLIGHTDATA_LAYER_NIBBLE = 2;

	public static final int BLOCKLIGHTDATA_NULL = 1;
	public static final int BLOCKLIGHTDATA_LAYERED = 2;

	public static final int BLOCKLIGHTDATA_LAYER_SINGLE = 1;
	public static final int BLOCKLIGHTDATA_LAYER_SHORT = 2;

	public static final int BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_RED = 3;
	public static final int BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_GREEN = 4;
	public static final int BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_BLUE = 5;

	public static final int BLOCKENTITY_NULL = 0;
	public static final int BLOCKENTITY_DATA = 1;

	public static final int ENTITIES_NULL = 0;
	public static final int ENTITIES_DATA = 1;

	public enum NumberType
	{
		BYTE(1, 1), SHORT(2, 2), INT(3, 4), LONG(4, 8);

		public byte id;
		public int numBytes;

		NumberType(int id, int numBytes)
		{
			this.id = (byte) id;
			this.numBytes = numBytes;
		}

		public static NumberType get(byte id)
		{
			switch (id)
			{
			case 1:
				return BYTE;
			case 2:
				return SHORT;
			case 3:
				return INT;
			case 4:
				return LONG;
			default:
				throw new RuntimeException("Unknown number type for id: " + id);
			}
		}

		public int numBytes()
		{
			return numBytes;
		}
	}

}
