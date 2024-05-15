package finalforeach.cosmicreach.savelib.lightdata.blocklight;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;

public interface IBlockLightData extends ISaveFileConstant
{
	short getBlockLight(int localX, int localY, int localZ);

	void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localY, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);

}
