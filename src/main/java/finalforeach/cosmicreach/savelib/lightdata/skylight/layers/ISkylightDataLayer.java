package finalforeach.cosmicreach.savelib.lightdata.skylight.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightLayeredData;

public interface ISkylightDataLayer extends ISaveFileConstant
{
	static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;

	void setSkyLight(SkylightLayeredData skylightData, int lightLevel, int localX, int localY, int localZ);

	int getSkyLight(int localX, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);

}
