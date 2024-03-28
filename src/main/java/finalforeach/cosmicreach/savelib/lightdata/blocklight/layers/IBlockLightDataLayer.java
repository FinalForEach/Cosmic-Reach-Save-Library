package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import finalforeach.cosmicreach.savelib.ISavedChunk;

public interface IBlockLightDataLayer
{
	final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	
	short getBlockLight(int localX, int localZ);

	void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ);

}
