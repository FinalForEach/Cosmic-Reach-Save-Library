package finalforeach.cosmicreach.savelib.blockdata;

import java.util.Arrays;
import java.util.function.Predicate;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockSingleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.IBlockLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.SharedBlockSingleLayer;

public class LayeredBlockData<T> implements IBlockData<T>
{
	@SuppressWarnings("unchecked")
	private IBlockLayer<T>[] layers = new IBlockLayer[CHUNK_WIDTH];

	@SuppressWarnings("unchecked")
	private T[] blockStatePalette = (T[])new Object[8];
	private int paletteSize = 0;

	private boolean allowCleaning = true;

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
	public T getBlockValue(int localX, int localY, int localZ) 
	{
		return layers[localY].getBlockValue(this, localX, localZ);
	}

	@Override
	public int getBlockValueID(int localX, int localY, int localZ) {
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
			s.fill(this, localY, blockState);
		}else 
		{
			layers[localY] = SharedBlockSingleLayer.get(this, blockState);
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
		if(layer instanceof SharedBlockSingleLayer<T> shared) 
		{
			if(!paletteHasValue(shared.blockValue)) 
			{
				addToPalette(shared.blockValue);
			}
		}
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

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.BLOCK_LAYERED;
	}
	@Override
	public void writeTo(IChunkByteWriter allChunksWriter) 
	{
		// Write the palette of the layered block data
		int paletteSize = getPaletteSize();
		allChunksWriter.writeInt(paletteSize);
		for(int i = 0; i < paletteSize; i++) 
		{
			allChunksWriter.writeBlockValue(getBlockValueFromPaletteId(i));
		}
		
		// Then write for the individual layers...
		for(var layer : getLayers())
		{
			allChunksWriter.writeByte(layer.getSaveFileConstant(this));
			layer.writeTo(this, allChunksWriter);
		}
	}
	public void cleanPalette() 
	{
		if(!allowCleaning) 
		{
			// Prevents possible infinite recursions
			return;
		}
		
		int currentPaletteSize = getPaletteSize();
		
		LayeredBlockData<T> tempBlockData = new LayeredBlockData<>(getBlockValue(0, 0, 0));
		tempBlockData.allowCleaning  = false;
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int j = 0; j < CHUNK_WIDTH; j++) 
			{
				for(int k = 0; k < CHUNK_WIDTH; k++) 
				{
					var curBlockValue = getBlockValue(i, j, k);
					tempBlockData.setBlockValue(curBlockValue, i, j, k);
				}
			}
		}
		
		paletteSize = tempBlockData.paletteSize;
		blockStatePalette = tempBlockData.blockStatePalette;
		layers = tempBlockData.layers;
		
		int numRemoved = currentPaletteSize - tempBlockData.paletteSize;
		
		
		
		System.out.println("Cleaned up " + numRemoved + " blockstates from palette.");
		if(getPaletteSize() > ISavedChunk.NUM_BLOCKS_IN_CHUNK) 
		{
			throw new RuntimeException("Failed to clean palette: This should never happen.");
		}
	}
}
