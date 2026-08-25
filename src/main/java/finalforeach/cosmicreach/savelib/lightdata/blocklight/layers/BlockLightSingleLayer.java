package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public class BlockLightSingleLayer implements IBlockLightLayer
{
	private BlockLightLayeredData lightData;
	public short lightLevel;
	private int yLevel;

	public BlockLightSingleLayer(BlockLightLayeredData lightData, int yLevel, int lightLevelRed, int lightLevelGreen, int lightLevelBlue) 
	{
		this.lightData = lightData;
		this.yLevel = yLevel;
		this.lightLevel = (short)((lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
	}

	@Override
	public long getBlockLight(int localX, int localZ)
	{
		return (long) (lightLevel & 0xFFFFL);
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
		if (lightLevelLime > 0 || lightLevelMint > 0 || lightLevelAzure > 0 || lightLevelViolet > 0 || lightLevelRose > 0)
		{
			IBlockLightLayer lightLayer = new BlockLightLongLayer((long) (lightLevel & 0xFFFFL));
			lightLayer.setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, lightLevelOrange, lightLevelLime,
					lightLevelMint, lightLevelAzure, lightLevelViolet, lightLevelRose, localX, localZ);
			lightData.setLayer(yLevel, lightLayer);
			return;
		}
		short newLightLevel = (short) ((lightLevelOrange << 12) + (lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
		if (newLightLevel != lightLevel)
		{
			IBlockLightLayer lightLayer = new BlockLightShortLayer(lightData, yLevel, lightLevel);
			lightLayer.setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, lightLevelOrange, localX, localZ);
			lightData.setLayer(yLevel, lightLayer);
		}
	}

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYER_SINGLE;
	}

	@Override
	public void writeTo(IChunkByteWriter allChunksWriter)
	{
		allChunksWriter.writeByte((byte)((lightLevel & 0xF00) >> 8));
		allChunksWriter.writeByte((byte)((lightLevel & 0x0F0) >> 4));
		allChunksWriter.writeByte((byte)((lightLevel & 0x00F)));
	}

}
