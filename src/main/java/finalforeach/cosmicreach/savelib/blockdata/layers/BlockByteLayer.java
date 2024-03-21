package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockByteLayer<T> implements IBlockLayer<T>
{
	private final byte[] blockIDs;

	public BlockByteLayer(byte[] bytes) 
	{
		this.blockIDs = bytes;
	}

	public BlockByteLayer(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		this.blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, blockValue, i, localY, k);
			}
		}
	}
	
	public BlockByteLayer(LayeredBlockData<T> chunkData, int localY, BlockNibbleLayer<T> nibbleLayer) 
	{
		this.blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, nibbleLayer.getBlockValue(chunkData, i, k), i, localY, k);
			}
		}
	}

	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ) {
		return chunkData.getBlockValueFromPaletteId(getBlockValueID(chunkData, localX, localZ));
	}

	@Override
	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ) {
		int idx = localX + (localZ * CHUNK_WIDTH);
		byte blockID = blockIDs[idx];
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
		byte paletteID = (byte)fullPaletteID;
		if(paletteID != fullPaletteID)
		{
			throw new RuntimeException("Palette id is too big, implement conversion to more IDs here.");
		}
		
		final T oldBlock = getBlockValue(chunkData, localX, localZ);
		if(blockValue!=oldBlock) 
		{
			final int idx = localX + (localZ * CHUNK_WIDTH);
			blockIDs[idx] = paletteID;
		}
	}

	public byte[] getBytes() {
		return blockIDs;
	}
	
	@Override
	public int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		return SaveFileConstants.LAYER_BYTE;
	}

	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeBytes(getBytes());
	}
}
