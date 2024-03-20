package finalforeach.cosmicreach.savelib.blockdata;

import java.util.function.Predicate;

import finalforeach.cosmicreach.savelib.ISavedChunk;

public interface IBlockData<T>
{
	public static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	public T getBlockState(int localX, int localY, int localZ);
	public int getBlockStateID(int localX, int localY, int localZ);
	
	public IBlockData<T> setBlockValue(T block, int localX, int localY, int localZ);
	public IBlockData<T> fill(T block);
	public IBlockData<T> fillLayer(T block, int localY);
	
	public int getBlockValueID(T blockValue);
	public T getBlockValueFromPaletteId(int bId);
	
	boolean isEntirely(T blockValue);
	boolean isEntirely(Predicate<T> predicate);
	public int getUniqueBlockValuesCount();
}