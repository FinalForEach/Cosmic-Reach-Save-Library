package finalforeach.cosmicreach.savelib.lightdata.skylight.layers;

import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightLayeredData;

public class SkylightDataSingleLayer implements ISkylightDataLayer
{
	public static SkylightDataSingleLayer[] allSingleLayerValues = new SkylightDataSingleLayer[16];
	static 
	{
		for(byte i = 0; i < allSingleLayerValues.length; i++) 
		{
			allSingleLayerValues[i] = new SkylightDataSingleLayer(i);
		}
	}
	public byte lightLevel;
	
	private SkylightDataSingleLayer(byte lightLevel)
	{
		this.lightLevel = lightLevel;
	}

	@Override
	public void setSkyLight(SkylightLayeredData skylightData, int lightLevel, int localX, int localY, int localZ) 
	{
		if(this.lightLevel!=lightLevel) 
		{
			final var nibbleLayer = new SkylightDataNibbleLayer(this.lightLevel);
			nibbleLayer.setSkyLight(skylightData, lightLevel, localX, localY, localZ);
			skylightData.setLayer(localY, nibbleLayer);
		}
	}

	@Override
	public int getSkyLight(int localX, int localZ) 
	{
		return lightLevel;
	}

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.SKYLIGHTDATA_LAYER_SINGLE;
	}

	public static ISkylightDataLayer getForLightValue(byte skylightValue) 
	{
		if(skylightValue < 0 || skylightValue > 15) 
		{
			throw new RuntimeException("Sky light values are only valid from 0-15, but got: " + skylightValue);
		}
		return allSingleLayerValues[skylightValue];
	}

}
