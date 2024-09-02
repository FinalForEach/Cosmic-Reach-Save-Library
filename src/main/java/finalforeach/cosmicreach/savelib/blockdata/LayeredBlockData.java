package finalforeach.cosmicreach.savelib.blockdata;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockBitLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockByteLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockHalfNibbleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockNibbleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockShortLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockSingleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.IBlockLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.SharedBlockSingleLayer;

public class LayeredBlockData<T> implements IBlockData<T>
{
	@SuppressWarnings("unchecked")
	private IBlockLayer<T>[] layers = new IBlockLayer[CHUNK_WIDTH];

	private T[] blockStatePalette;
	private int paletteSize = 0;

	private boolean allowCleaning = true;

	@SuppressWarnings("unchecked")
	public LayeredBlockData(int defaultPaletteSize)
	{
		blockStatePalette = (T[])new Object[defaultPaletteSize];
	}
	public LayeredBlockData() 
	{
		this(8);
	}
	public LayeredBlockData(T defaultBlockState)
	{
		this(8);
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
	public boolean hasValueInPalette(T value) 
	{
		var palette = blockStatePalette;
		final int paletteSize = getPaletteSize();
		for(int i = 0; i < paletteSize; i++) 
		{
			T b = palette[i];
			if(b == value)return true;
		}
		return false;
	}

	@Override
	public boolean isEntirely(T blockValue) 
	{
		return getPaletteSize() == 1 && getBlockValueFromPaletteId(0) == blockValue;
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
		int currentPaletteSize = getPaletteSize();
		if(!allowCleaning || currentPaletteSize == 1) 
		{
			// Prevents possible infinite recursions
			// and early exits if there's only a single palette value
			return;
		}
		
		
		LayeredBlockData<T> tempBlockData = new LayeredBlockData<>(getBlockValue(0, 0, 0));
		tempBlockData.allowCleaning  = false;
		
		for(int j = 0; j < CHUNK_WIDTH; j++) 
		{
			var layer = getLayer(j);
			if(layer instanceof SharedBlockSingleLayer<T>) 
			{
				tempBlockData.setLayer(j, layer);
			}else 
			{		
				for(int i = 0; i < CHUNK_WIDTH; i++) 
				{
					for(int k = 0; k < CHUNK_WIDTH; k++) 
					{
						var curBlockValue = getBlockValue(i, j, k);
						tempBlockData.setBlockValue(curBlockValue, i, j, k);
					}
				}
			}
		}		
		
		paletteSize = tempBlockData.paletteSize;
		blockStatePalette = tempBlockData.blockStatePalette;
		layers = tempBlockData.layers;
		
		if(getPaletteSize() > ISavedChunk.NUM_BLOCKS_IN_CHUNK) 
		{
			throw new RuntimeException("Failed to clean palette: This should never happen.");
		}
	}
	
	public static <T> IBlockData<T> readFrom(IChunkByteReader reader, Function<String, T> saveKeyToBlockValue) throws IOException 
	{
		int paletteSize = reader.readInt();
		final var chunkData = new LayeredBlockData<T>(paletteSize);
		
		for(int i = 0; i < paletteSize; i++) 
		{
			String blockStateSaveKey = reader.readString();
			chunkData.addToPalette(saveKeyToBlockValue.apply(blockStateSaveKey));
		}
		for(int l = 0; l < CHUNK_WIDTH; l++) 
		{
			int layerType = reader.readByte();
			
			switch(layerType) 
			{
			case SaveFileConstants.BLOCK_LAYER_SINGLE_BYTE:
			{
				int blockId = reader.readByte();
				var layer = SharedBlockSingleLayer.get(chunkData, chunkData.getBlockValueFromPaletteId(blockId));
				chunkData.setLayer(l, layer);
				break;
			}
			case SaveFileConstants.BLOCK_LAYER_SINGLE_INT:
			{
				int blockId = reader.readByte();
				var layer = SharedBlockSingleLayer.get(chunkData, chunkData.getBlockValueFromPaletteId(blockId));
				chunkData.setLayer(l, layer);
				break;
			}
			case SaveFileConstants.BLOCK_LAYER_HALFNIBBLE:
			{
				byte[] bytes = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 4];
				reader.readFully(bytes);
				var layer = new BlockHalfNibbleLayer<T>(bytes);
				chunkData.setLayer(l, layer);
				break;
			}
			case SaveFileConstants.BLOCK_LAYER_NIBBLE:
			{
				byte[] bytes = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 2];
				reader.readFully(bytes);
				var layer = new BlockNibbleLayer<T>(bytes);
				chunkData.setLayer(l, layer);
				break;
			}
			case SaveFileConstants.BLOCK_LAYER_BYTE:
			{
				byte[] bytes = new byte[CHUNK_WIDTH * CHUNK_WIDTH];
				reader.readFully(bytes);
				var layer = new BlockByteLayer<T>(bytes);
				chunkData.setLayer(l, layer);
				break;
			}
			case SaveFileConstants.BLOCK_LAYER_SHORT:
			{
				BlockShortLayer<T> layer = BlockShortLayer.readFrom(reader);
				chunkData.setLayer(l, layer);
				break;
			}
			case SaveFileConstants.BLOCK_LAYER_BIT:
			{
				byte[] bytes = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 8];
				reader.readFully(bytes);
				var layer = new BlockBitLayer<T>(bytes);
				chunkData.setLayer(l, layer);
				break;
			}
			default:
				throw new RuntimeException("Unknown layerType: " + layerType);
			}
		}
		return chunkData;
	}
}
