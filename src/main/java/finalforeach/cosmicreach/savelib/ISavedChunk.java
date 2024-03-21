package finalforeach.cosmicreach.savelib;

import finalforeach.cosmicreach.savelib.blockdata.IBlockData;

public interface ISavedChunk {
	public static final int CHUNK_WIDTH = 16;
	public static final int NUM_BLOCKS_IN_CHUNK = CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_WIDTH;
	public boolean isEntirelyOpaque();
	public boolean isEntirelyOneBlockSelfCulling();
	public int getMaxNonEmptyBlockIdxYXZ();
	public int getBlockX();
	public int getBlockY();
	public int getBlockZ();
	public IBlockData<?> getBlockData();

}
