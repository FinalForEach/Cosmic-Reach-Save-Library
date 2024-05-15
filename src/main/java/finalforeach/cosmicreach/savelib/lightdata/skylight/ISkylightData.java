package finalforeach.cosmicreach.savelib.lightdata.skylight;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;
import finalforeach.cosmicreach.savelib.ISavedChunk;

public interface ISkylightData extends ISaveFileConstant
{
	static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	
	int getSkyLight(int localX, int localY, int localZ);
	
	void setSkyLight(ISavedChunk<?> chunk, int lightLevel, int localX, int localY, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);
	
}
