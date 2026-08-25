package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public class BlockLightLongLayer implements IBlockLightLayer
{
	private long[] lightLevels = new long[CHUNK_WIDTH * CHUNK_WIDTH];

	public BlockLightLongLayer(long lightLevel)
	{
		for (int i = 0; i < lightLevels.length; i++)
		{
			lightLevels[i] = lightLevel;
		}
	}

	public BlockLightLongLayer()
	{
	}

	@Override
	public long getBlockLight(int localX, int localZ)
	{
		int idx = localX + (localZ * CHUNK_WIDTH);
		return lightLevels[idx];
	}

	public void setBlockLight(long packed, int localX, int localZ)
	{
		int idx = localX + (localZ * CHUNK_WIDTH);
		lightLevels[idx] = packed;
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ)
	{
		setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, 0, localX, localZ);
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int lightLevelOrange, int localX, int localZ)
	{
		setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, lightLevelOrange, 0, 0, 0, 0, 0, localX, localZ);
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int lightLevelOrange,
			int lightLevelLime, int lightLevelMint, int lightLevelAzure, int lightLevelViolet, int lightLevelRose,
			int localX, int localZ)
	{
		int idx = localX + (localZ * CHUNK_WIDTH);
		lightLevels[idx] = ((long) lightLevelRose << 32) | ((long) lightLevelViolet << 28)
				| ((long) lightLevelAzure << 24) | ((long) lightLevelMint << 20) | ((long) lightLevelLime << 16)
				| ((long) lightLevelOrange << 12) | ((long) lightLevelRed << 8) | ((long) lightLevelGreen << 4)
				| (long) lightLevelBlue;
	}

	public long[] getLongs()
	{
		return lightLevels;
	}

	@Override
	public int getSaveFileConstant()
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYER_LONG;
	}

	@Override
	public void writeTo(IChunkByteWriter allChunksWriter)
	{
		allChunksWriter.writeLongs(getLongs());
	}

	public static BlockLightLongLayer readFrom(IChunkByteReader reader, BlockLightLayeredData blockLightLayeredData, int yLevel) throws IOException
	{
		BlockLightLongLayer layer = new BlockLightLongLayer();
		int l = layer.lightLevels.length;
		for (int i = 0; i < l; i++)
		{
			layer.lightLevels[i] = reader.readLong();
		}
		return layer;
	}
}
