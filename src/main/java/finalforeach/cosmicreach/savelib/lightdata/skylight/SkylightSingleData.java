package finalforeach.cosmicreach.savelib.lightdata.skylight;

import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;

public class SkylightSingleData implements ISkylightData 
{
	ISavedChunk<?> chunk;
	public byte lightValue;
	public SkylightSingleData(ISavedChunk<?> chunk, byte lightValue) 
	{
		this.chunk = chunk;
		this.lightValue = lightValue;
	}
	@Override
	public int getSkyLight(int localX, int localY, int localZ) 
	{
		return lightValue;
	}

	@Override
	public void setSkyLight(int lightLevel, int localX, int localY, int localZ) 
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

}
