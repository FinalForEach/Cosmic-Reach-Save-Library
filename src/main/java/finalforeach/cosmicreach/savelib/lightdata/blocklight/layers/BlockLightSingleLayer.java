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
	public short getBlockLight(int localX, int localZ)
	{
		return lightLevel;
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ)
	{
		short newLightLevel = (short)((lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
		if(newLightLevel != lightLevel) 
		{
			int oldR = ((byte)((lightLevel & 0xF00) >> 8));
			int oldG = ((byte)((lightLevel & 0x0F0) >> 4));
			int oldB = ((byte)((lightLevel & 0x00F)));

			IBlockLightLayer lightLayer = null;
			if(oldR!=lightLevelRed && oldG == lightLevelGreen && oldB == lightLevelBlue) 
			{
				//lightLayer = new BlockLightMonoRedNibbleLayer(lightData, yLevel, lightLevel);
				//lightLayer.setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, localX, localZ);
			}

			if(lightLayer == null)
			{
				lightLayer = new BlockLightShortLayer(lightLevel);
				lightLayer.setBlockLight(lightLevelRed, lightLevelGreen,lightLevelBlue, localX, localZ);
			}

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
