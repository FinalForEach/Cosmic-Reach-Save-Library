package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public class BlockLightShortLayer implements IBlockLightLayer
{
	private BlockLightLayeredData blockLightLayeredData;
	private int yLevel;
	private short[] lightLevels = new short[CHUNK_WIDTH * CHUNK_WIDTH];	

	public BlockLightShortLayer(BlockLightLayeredData blockLightLayeredData, int yLevel, short lightLevel) 
	{
		this.blockLightLayeredData = blockLightLayeredData;
		this.yLevel = yLevel;
		for(int i = 0; i < lightLevels.length; i++) 
		{
			lightLevels[i] = lightLevel;
		}
	}

	public BlockLightShortLayer(short lightLevel) 
	{
		this(null, 0, lightLevel);
	}
	
	public BlockLightShortLayer() 
	{
	}

	@Override
	public long getBlockLight(int localX, int localZ) {
		int idx = localX + (localZ * CHUNK_WIDTH);
		return (long)(lightLevels[idx] & 0xFFFFL);
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ)
	{
		setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, 0, localX, localZ);
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int lightLevelOrange, int localX, int localZ)
	{
		int idx = localX + (localZ * CHUNK_WIDTH);
		lightLevels[idx] = (short)((lightLevelOrange << 12) + (lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int lightLevelOrange,
			int lightLevelLime, int lightLevelMint, int lightLevelAzure, int lightLevelViolet, int lightLevelRose,
			int localX, int localZ)
	{
		if (lightLevelLime > 0 || lightLevelMint > 0 || lightLevelAzure > 0 || lightLevelViolet > 0 || lightLevelRose > 0)
		{
			final var longLayer = new BlockLightLongLayer();
			for (int i = 0; i < CHUNK_WIDTH; i++)
			{
				for (int k = 0; k < CHUNK_WIDTH; k++)
				{
					longLayer.setBlockLight(getBlockLight(i, k), i, k);
				}
			}
			longLayer.setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, lightLevelOrange, lightLevelLime,
					lightLevelMint, lightLevelAzure, lightLevelViolet, lightLevelRose, localX, localZ);
			if (blockLightLayeredData != null)
			{
				blockLightLayeredData.setLayer(yLevel, longLayer);
			}
			return;
		}
		int idx = localX + (localZ * CHUNK_WIDTH);
		lightLevels[idx] = (short) ((lightLevelOrange << 12) + (lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
	}

	public short[] getShorts() {
		return lightLevels;
	}

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYER_SHORT;
	}

	@Override
	public void writeTo(IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeShorts(getShorts());
	}

	public static BlockLightShortLayer readFrom(IChunkByteReader reader, BlockLightLayeredData blockLightLayeredData, int yLevel) throws IOException 
	{
		BlockLightShortLayer layer = new BlockLightShortLayer(blockLightLayeredData, yLevel, (short) 0);
		int l = layer.lightLevels.length;
		for(int i = 0; i < l; i++) 
		{
			layer.lightLevels[i] = reader.readShort(); 
		}
		return layer;
	}

	public static BlockLightShortLayer readFrom(IChunkByteReader reader) throws IOException 
	{
		return readFrom(reader, null, 0);
	}
}
