package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public interface IBlockLayer<T>
{
	public static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	public static final int NUM_BLOCKS_IN_LAYER = CHUNK_WIDTH * CHUNK_WIDTH;
	T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ);
	void setBlockValue(LayeredBlockData<T> chunkData, T blockValue, int localX, int localY, int localZ);
	int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ);
	int getSaveFileConstant(LayeredBlockData<T> chunkData);
	void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter);	
}