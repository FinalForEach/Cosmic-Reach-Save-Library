package finalforeach.cosmicreach.savelib;

import finalforeach.cosmicreach.savelib.blocks.IBlockState;

public interface IChunkByteWriter
{
	void writeInt(int i);

	void writeByte(int b);

	void writeShort(short s);

	void writeString(String s);

	default <T> void writeBlockValue(T blockValue)
	{
		if (blockValue instanceof IBlockState blockState)
		{
			String saveKey = blockState.getSaveKey();
			writeString(saveKey);
		} else
		{
			throw new RuntimeException(
					"writeBlockValue() not implemented for " + blockValue.getClass().getSimpleName());
		}
	}

	default void writeShorts(short[] shorts)
	{
		for (short s : shorts)
		{
			writeShort(s);
		}
	}

	default void writeBytes(byte[] bytes)
	{
		for (byte b : bytes)
		{
			writeByte(b);
		}
	}

	default void writeBytes(byte[] bytes, int len)
	{
		for (int i = 0; i < len; i++)
		{
			writeByte(bytes[i]);
		}
	}

	default void saveConstantByte(ISaveFileConstant item, int nullConstant)
	{
		if (item == null)
		{
			writeByte(nullConstant);
		} else
		{
			writeByte(item.getSaveFileConstant());
		}
	}
}
