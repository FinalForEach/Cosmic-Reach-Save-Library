package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public class BlockLightDataSingleLayer implements IBlockLightDataLayer
{
	private BlockLightLayeredData lightData;
	public short lightLevel;
	private int yLevel;
	
	public BlockLightDataSingleLayer(BlockLightLayeredData lightData, int yLevel, int lightLevelRed, int lightLevelGreen, int lightLevelBlue) 
	{
		this.lightData = lightData;
		this.yLevel = yLevel;
		this.lightLevel = (short)((lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
	}

	@Override
	public short getBlockLight(int localX, int localZ) {
		return lightLevel;
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ)
	{
		short newLightLevel = (short)((lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
		if(newLightLevel!=lightLevel) 
		{
			final var shortLayer = new BlockLightDataShortLayer(lightLevel);
			shortLayer.setBlockLight(lightLevelRed, lightLevelGreen,lightLevelBlue, localX, localZ);
			lightData.setLayer(yLevel, shortLayer);
		}
	}

}
