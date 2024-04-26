package finalforeach.cosmicreach.savelib.lightdata.skylight;

import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;

public class SkylightSingleData implements ISkylightData 
{
	public static SkylightSingleData[] allSingleValues = new SkylightSingleData[16];
	static 
	{
		for(byte i = 0; i < allSingleValues.length; i++) 
		{
			allSingleValues[i] = new SkylightSingleData(i);
		}
	}
	
	public byte lightValue;
	private SkylightSingleData(byte lightValue) 
	{
		this.lightValue = lightValue;
	}
	
	@Override
	public int getSkyLight(int localX, int localY, int localZ) 
	{
		return lightValue;
	}

	@Override
	public void setSkyLight(ISavedChunk<?> chunk, int lightLevel, int localX, int localY, int localZ) 
	{
		if(lightLevel==lightValue) 
		{
			return;
		}
		chunk.setSkylightData(new SkylightLayeredData(this.lightValue));
		chunk.setSkyLight(lightLevel, localX, localY, localZ);
	}
	
	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.SKYLIGHTDATA_SINGLE;
	}
	
	public static ISkylightData getForLightValue(byte skylightValue) 
	{
		if(skylightValue < 0 || skylightValue > 15) 
		{
			throw new RuntimeException("Sky light values are only valid from 0-15, but got: " + skylightValue);
		}
		return allSingleValues[skylightValue];
	}

}
