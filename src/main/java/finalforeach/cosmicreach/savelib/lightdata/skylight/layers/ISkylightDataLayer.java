package finalforeach.cosmicreach.savelib.lightdata.skylight.layers;

import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightLayeredData;

public interface ISkylightDataLayer
{
	static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;

	void setSkyLight(SkylightLayeredData skylightData, int lightLevel, int localX, int localZ);

	int getSkyLight(int localX, int localZ);

	int getSaveFileConstant();

}
