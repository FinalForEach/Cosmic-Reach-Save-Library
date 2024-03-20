package finalforeach.cosmicreach.savelib.blockdata;

import java.util.Arrays;
import java.util.function.Predicate;

import finalforeach.cosmicreach.savelib.blockdata.layers.BlockSingleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.IBlockLayer;


public class LayeredBlockData<T> implements IBlockData<T>
{
	@SuppressWarnings("unchecked")
	private IBlockLayer<T>[] layers = new IBlockLayer[CHUNK_WIDTH];

	@SuppressWarnings("unchecked")
	private T[] blockStatePalette = (T[])new Object[8];
	private int paletteSize = 0;

	public LayeredBlockData() {}
	public LayeredBlockData(T defaultBlockState)
	{
		addToPalette(defaultBlockState);
		for(int j = 0; j < CHUNK_WIDTH; j++) 
		{
			fillLayer(defaultBlockState, j);
		}
	}

	public IBlockLayer<T>[] getLayers()
	{
		return layers;
	}
	@Override
	public T getBlockState(int localX, int localY, int localZ) 
	{
		return layers[localY].getBlockValue(this, localX, localZ);
	}

	@Override
	public int getBlockStateID(int localX, int localY, int localZ) {
		return layers[localY].getBlockValueID(this, localX, localZ);
	}
	@Override
	public IBlockData<T> setBlockValue(T blockState, int localX, int localY, int localZ) 
	{
		layers[localY].setBlockValue(this, blockState, localX, localY, localZ);
		return this;
	}
	@Override
	public IBlockData<T> fill(T blockState) 
	{
		return new SingleBlockData<T>(blockState);
	}

	@Override
	public IBlockData<T> fillLayer(T blockState, int localY)
	{
		if(layers[localY] instanceof BlockSingleLayer<T> s) 
		{
			s.fill(this, blockState);
		}else 
		{
			layers[localY] = new BlockSingleLayer<T>(this, blockState);
		}

		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			if(!(layers[i] instanceof BlockSingleLayer<T> s && s.blockValue == blockState)) 
			{
				return this;
			}
		}
		return fill(blockState);
	}

	@Override
	public boolean isEntirely(Predicate<T> predicate) 
	{
		var palette = blockStatePalette;
		final int paletteSize = getPaletteSize();
		for(int i = 0; i < paletteSize; i++) 
		{
			T b = palette[i];
			if(!predicate.test(b))return false;
		}

		return true;
	}

	@Override
	public boolean isEntirely(T blockValue) 
	{
		return getPaletteSize()==1 && getBlockValueFromPaletteId(0) == blockValue;
	}
	
	@Override
	public int getUniqueBlockValuesCount() 
	{
		return getPaletteSize();
	}
	
	public int getPaletteSize() 
	{
		return paletteSize;
	}

	public void setLayer(int yLevel, IBlockLayer<T> layer) 
	{
		layers[yLevel] = layer;
	}

	public IBlockLayer<T> getLayer(int yLevel) 
	{
		return layers[yLevel];
	}
	
	@Override
	public int getBlockValueID(T blockValue) 
	{
		final int paletteSize = getPaletteSize();
		for(int i = 0; i < paletteSize; i++) 
		{
			if(blockStatePalette[i]==blockValue) 
			{
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public T getBlockValueFromPaletteId(int bId) 
	{
		return blockStatePalette[bId];
	}
	
	public void addToPalette(T blockValue) 
	{
		if(paletteSize == blockStatePalette.length) 
		{
			int newSize = (int)(paletteSize*1.75f);
			newSize = Math.max(newSize, paletteSize+1);
			blockStatePalette = Arrays.copyOf(blockStatePalette, newSize);
		}
		blockStatePalette[paletteSize] = blockValue;
		paletteSize++;
	}
	public boolean paletteHasValue(T blockValue) 
	{
		return getBlockValueID(blockValue) != -1;
	}
	
}