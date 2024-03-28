package finalforeach.cosmicreach.savelib.lightdata.skylight.layers;

import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightLayeredData;

public class SkylightDataSingleLayer implements ISkylightDataLayer
{
	private int yLevel;
	public byte lightLevel;
	
	public SkylightDataSingleLayer(int yLevel, byte lightLevel)
	{
		this.yLevel = yLevel;
		this.lightLevel = lightLevel;
	}

	@Override
	public void setSkyLight(SkylightLayeredData skylightData, int lightLevel, int localX, int localZ) 
	{
		if(this.lightLevel!=lightLevel) 
		{
			final var nibbleLayer = new SkylightDataNibbleLayer(this.lightLevel);
			nibbleLayer.setSkyLight(skylightData, lightLevel, localX, localZ);
			skylightData.setLayer(yLevel, nibbleLayer);
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

}
