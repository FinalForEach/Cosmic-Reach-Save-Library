package finalforeach.cosmicreach.savelib;

import java.util.Collection;

import finalforeach.cosmicreach.savelib.blocks.IBlockState;

public interface IWorld<B extends IBlockState, C extends ISavedChunk<B>, R extends IRegion<?>> {

	C getChunkAtChunkCoords(int chunkX, int chunkY, int chunkZ);

	void addChunk(C chunk);

	C createChunk(int chunkX, int chunkY, int chunkZ);

	String getFullSaveFolder();

	String getWorldFolderName();

	Collection<R> getRegions();

}