package finalforeach.cosmicreach.savelib.lightdata.skylight.layers;

import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightLayeredData;

public class SkylightDataNibbleLayer implements ISkylightDataLayer 
{
	private byte[] lightLevels = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 2];
	
	public SkylightDataNibbleLayer(byte lightLevel) 
	{
		for(int i = 0; i < lightLevels.length; i++) 
		{
			lightLevels[i] = lightLevel;
		}
	}

	public SkylightDataNibbleLayer(byte[] bytes) {
		lightLevels = bytes;
	}

	@Override
	public void setSkyLight(SkylightLayeredData skylightData, int lightLevel, int localX, int localZ)
	{
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;
		final int b = lightLevels[idx];
		if(localX % 2 == 0) 
		{
			lightLevels[idx] = (byte)((b & 0xF0) | lightLevel);
		}else 
		{
			lightLevels[idx] = (byte)((b & 0x0F) | (lightLevel << 4));
		}
	}

	@Override
	public int getSkyLight(int localX, int localZ)
	{
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;
		if(localX % 2 == 0) 
		{
			return (lightLevels[idx] & 0x0F);	
		}else 
		{
			return ((lightLevels[idx] & 0xF0) >> 4);
		}
		
	}

	public byte[] getBytes() {
		return lightLevels;
	}

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.SKYLIGHTDATA_LAYER_NIBBLE;
	}

}
