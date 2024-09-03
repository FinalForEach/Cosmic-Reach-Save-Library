package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockNibbleLayer<T> implements IBlockLayer<T>
{
	public static final int TOTAL_BYTES = CHUNK_WIDTH * CHUNK_WIDTH / 2;
	private final byte[] blockIDs;

	public BlockNibbleLayer(byte[] bytes) 
	{
		this.blockIDs = bytes;
	}
	public BlockNibbleLayer(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		this.blockIDs = new byte[TOTAL_BYTES];
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

	public BlockNibbleLayer(LayeredBlockData<T> chunkData, int localY, IBlockLayer<T> srcLayer) 
	{
		this.blockIDs = new byte[TOTAL_BYTES];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				var blockValue = srcLayer.getBlockValue(chunkData, i, k);
				setBlockValue(chunkData, blockValue, i, localY, k);
			}
		}
	}

	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ) 
	{
		return chunkData.getBlockValueFromPaletteId(getBlockValueID(chunkData, localX, localZ));
	}
	
	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ)
	{
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;

		final int b = blockIDs[idx];
		
		int mod2 = localX % 2;
		int blockID = (mod2 * ((b & 0xF0) >> 4)) + ((1 - mod2) * b & 0x0F);

		return blockID;
	}

	
	public boolean upgradeLayer(int paletteID, LayeredBlockData<T> chunkData, T blockValue, int localX, int localY, int localZ) 
	{
		if(paletteID > 255)
		{
			final var layer = new BlockShortLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return true;
		}
		if(paletteID > 15)
		{
			final var layer = new BlockByteLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return true;
		}
		return false;
	}
	
	@Override
	public void setBlockValue(LayeredBlockData<T> chunkData, T blockValue, int localX, int localY, int localZ) 
	{
		int paletteID = chunkData.getBlockValueIDAddIfMissing(blockValue);
		if(upgradeLayer(paletteID, chunkData, blockValue, localX, localY, localZ)) 
		{
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
		return SaveFileConstants.BLOCK_LAYER_NIBBLE;
	}

	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeBytes(getBytes());
	}
}
