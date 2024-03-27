package finalforeach.cosmicreach.savelib;

import finalforeach.cosmicreach.savelib.blockdata.IBlockData;
import finalforeach.cosmicreach.savelib.blocks.IBlockDataFactory;
import finalforeach.cosmicreach.savelib.blocks.IBlockState;

public interface ISavedChunk<T extends IBlockState> {
	public static final int CHUNK_WIDTH = 16;
	public static final int NUM_BLOCKS_IN_CHUNK = CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_WIDTH;
	public boolean isEntirelyOpaque();
	public boolean isEntirelyOneBlockSelfCulling();
	public int getMaxNonEmptyBlockIdxYXZ();
	public int getBlockX();
	public int getBlockY();
	public int getBlockZ();
	public IBlockData<?> getBlockData();
	public void initChunkData(IBlockDataFactory<T> layeredChunkDataFactory);
	public void fillLayer(T block, int localY);
	public void fill(T block);
	public void setBlockState(T block, int i, int j, int k);
	public boolean isSaved();
	public void compactChunkData();
	public int getChunkX();
	public int getChunkY();
	public int getChunkZ();

}
