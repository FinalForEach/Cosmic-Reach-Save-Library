package finalforeach.cosmicreach.savelib.blockdata.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockShortLayer<T> implements IBlockLayer<T>
{
	private final short[] blockIDs;

	public BlockShortLayer(short[] shorts) 
	{
		this.blockIDs = shorts;
	}

	public BlockShortLayer(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		this.blockIDs = new short[NUM_BLOCKS_IN_LAYER];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, blockValue, i, localY, k);
			}
		}
	}
	
	public BlockShortLayer(LayeredBlockData<T> chunkData, int localY, IBlockLayer<T> srcBlockLayer) {

		this.blockIDs = new short[NUM_BLOCKS_IN_LAYER];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				var blockValue = srcBlockLayer.getBlockValue(chunkData, i, k);
				setBlockValue(chunkData, blockValue, i, localY, k);
			}
		}
	}

	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ) {
		return chunkData.getBlockValueFromPaletteId(getBlockValueID(chunkData, localX, localZ));
	}

	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ) {
		int idx = localX + (localZ * CHUNK_WIDTH);
		short blockID = blockIDs[idx];

		return blockID;
	}

	@Override
	public void setBlockValue(LayeredBlockData<T> chunkData, T blockValue, int localX, int localY, int localZ) 
	{
		if(!chunkData.paletteHasValue(blockValue))
		{
			chunkData.addToPalette(blockValue);
		}
		
		int fullPaletteID = chunkData.getBlockValueID(blockValue);
		
		if(fullPaletteID > ISavedChunk.NUM_BLOCKS_IN_CHUNK - 1)
		{
			chunkData.cleanPalette();
			chunkData.setBlockValue(blockValue, localX, localY, localZ);
			return;
		}
		
		final T oldBlock = getBlockValue(chunkData, localX, localZ);
		if(blockValue!=oldBlock) 
		{
			final int idx = localX + (localZ * CHUNK_WIDTH);
			blockIDs[idx] = (short)fullPaletteID;
		}
	}
	
	@Override
	public int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		return SaveFileConstants.BLOCK_LAYER_SHORT;
	}
	
	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeShorts(blockIDs);
	}

	public static <T> BlockShortLayer<T> readFrom(IChunkByteReader reader) throws IOException 
	{
		BlockShortLayer<T> layer = new BlockShortLayer<T>(new short[NUM_BLOCKS_IN_LAYER]);
		int l = layer.blockIDs.length;
		for(int i = 0; i < l; i++) 
		{
			layer.blockIDs[i] = reader.readShort();
		}
		return layer;
	}
}
