package finalforeach.cosmicreach.savelib.lightdata.skylight;

import finalforeach.cosmicreach.savelib.ISavedChunk;

public interface ISkylightData 
{
	static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	
	int getSkyLight(int localX, int localY, int localZ);
	
	void setSkyLight(ISavedChunk<?> chunk, int lightLevel, int localX, int localY, int localZ);
	
	int getSaveFileConstant();
}
