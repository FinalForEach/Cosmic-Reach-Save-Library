package finalforeach.cosmicreach.savelib.blocks;

import finalforeach.cosmicreach.savelib.blockdata.IBlockData;

public interface IBlockDataFactory<B extends IBlockState>
{
	IBlockData<B> createChunkData();
}
