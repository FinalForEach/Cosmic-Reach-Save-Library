package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;
import finalforeach.cosmicreach.savelib.ISavedChunk;

public interface IBlockLightDataLayer extends ISaveFileConstant
{
	final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	
	short getBlockLight(int localX, int localZ);

	void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);

}
