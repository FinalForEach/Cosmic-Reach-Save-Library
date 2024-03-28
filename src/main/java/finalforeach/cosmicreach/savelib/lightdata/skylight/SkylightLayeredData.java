package finalforeach.cosmicreach.savelib.lightdata.skylight;

import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.ISkylightDataLayer;
import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.SkylightDataSingleLayer;

public class SkylightLayeredData implements ISkylightData
{
	private ISkylightDataLayer[] layers = new ISkylightDataLayer[CHUNK_WIDTH];
	
	public SkylightLayeredData() 
	{
		this((byte)0);
	}
	public SkylightLayeredData(byte skylightValue)
	{
		for(int i = 0; i < layers.length; i++)
		{
			layers[i] = new SkylightDataSingleLayer(i, skylightValue);
		}
	}

	@Override
	public int getSkyLight(int localX, int localY, int localZ) 
	{
		return layers[localY].getSkyLight(localX, localZ);
	}

	@Override
	public void setSkyLight(int lightLevel, int localX, int localY, int localZ)
	{
		layers[localY].setSkyLight(this, lightLevel, localX, localZ);
	}

	public void setLayer(int yLevel, ISkylightDataLayer skyLightLayer) {
		layers[yLevel] = skyLightLayer;
	}

	public ISkylightDataLayer getLayer(int yLevel) 
	{
		return layers[yLevel];
	}
	public ISkylightDataLayer[] getLayers() {
		return layers;
	}
	
	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.SKYLIGHTDATA_LAYERED;
	}
}
