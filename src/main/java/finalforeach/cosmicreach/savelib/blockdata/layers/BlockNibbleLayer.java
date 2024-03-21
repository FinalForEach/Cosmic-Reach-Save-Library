package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockNibbleLayer<T> implements IBlockLayer<T>
{
	private final byte[] blockIDs;

	public BlockNibbleLayer(byte[] bytes) 
	{
		this.blockIDs = bytes;
	}
	public BlockNibbleLayer(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		this.blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 2];
		int paletteID = chunkData.getBlockValueID(blockValue);
		if(paletteID!=0) 
		{
			for(int i = 0; i < CHUNK_WIDTH; i++) 
			{
				for(int k = 0; k < CHUNK_WIDTH; k++) 
				{
					setBlockValue(chunkData, blockValue, i, localY, k);
				}
			}
		}
	}
	
	public byte[] getBytes() 
	{
		return blockIDs;
	}

	public BlockNibbleLayer(LayeredBlockData<T> chunkData, int localY, BlockHalfNibbleLayer<T> halfNibbleLayer) 
	{
		this.blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 2];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, halfNibbleLayer.getBlockValue(chunkData, i, k), i, localY, k);
			}
		}
	}

	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ) 
	{
		return chunkData.getBlockValueFromPaletteId(getBlockValueID(chunkData, localX, localZ));
	}
	
	@Override
	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ) {
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;

		final int b = blockIDs[idx];
		int blockID;
		if(localX % 2 == 0) 
		{
			blockID = b & 0x0F;	
		}else 
		{
			blockID = (b & 0xF0) >> 4;
		}
		return blockID;
	}

	@Override
	public void setBlockValue(LayeredBlockData<T> chunkData, T blockValue, int localX, int localY, int localZ) 
	{
		int paletteID = chunkData.getBlockValueID(blockValue);
		if(paletteID==-1) 
		{
			paletteID = chunkData.getPaletteSize();
			chunkData.addToPalette(blockValue);
		}
		if(paletteID > 15)
		{
			final var layer = new BlockByteLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return;
		}
		
		int oldBlockID = getBlockValueID(null, localX, localZ);
		if(paletteID!=oldBlockID) 
		{			
			final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;
			final int b = blockIDs[idx];
			if(localX % 2 == 0) 
			{
				blockIDs[idx] = (byte)((b & 0xF0) | paletteID);
			}else 
			{
				blockIDs[idx] = (byte)((b & 0x0F) | (paletteID << 4));
			}
		}
	}
	
	@Override
	public int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		return SaveFileConstants.LAYER_NIBBLE;
	}

	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeBytes(getBytes());
	}
}
