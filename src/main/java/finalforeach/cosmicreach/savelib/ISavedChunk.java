package finalforeach.cosmicreach.savelib;

import finalforeach.cosmicreach.savelib.blockdata.IBlockData;
import finalforeach.cosmicreach.savelib.blocks.IBlockDataFactory;
import finalforeach.cosmicreach.savelib.blocks.IBlockState;
import finalforeach.cosmicreach.savelib.lightdata.skylight.ISkylightData;

public interface ISavedChunk<B extends IBlockState> 
{
	public static final int CHUNK_WIDTH = 16;
	public static final int NUM_BLOCKS_IN_CHUNK = CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_WIDTH;
	public boolean isEntirelyOpaque();
	public boolean isEntirelyOneBlockSelfCulling();
	public int getMaxNonEmptyBlockIdxYXZ();
	public int getBlockX();
	public int getBlockY();
	public int getBlockZ();
	public IBlockData<?> getBlockData();
	public void initChunkData(IBlockDataFactory<B> layeredChunkDataFactory);
	public void fillLayer(B block, int localY);
	public void fill(B block);
	public void setBlockState(B block, int i, int j, int k);
	public boolean isSaved();
	public void compactChunkData();
	public int getChunkX();
	public int getChunkY();
	public int getChunkZ();
	public void setSkyLight(int lightLevel, int localX, int localY, int localZ);
	public void setSkylightData(ISkylightData skylightData);

}
