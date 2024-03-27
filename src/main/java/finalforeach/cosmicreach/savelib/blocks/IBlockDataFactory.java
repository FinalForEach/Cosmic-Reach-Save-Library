package finalforeach.cosmicreach.savelib.blocks;

import finalforeach.cosmicreach.savelib.blockdata.IBlockData;

public interface IBlockDataFactory<T>
{
	IBlockData<T> createChunkData();
}
