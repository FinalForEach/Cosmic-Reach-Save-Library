package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockByteLayer<T> implements IBlockLayer<T>
{
	public static final int TOTAL_BYTES = CHUNK_WIDTH * CHUNK_WIDTH;
	private final byte[] blockIDs;

	public BlockByteLayer(byte[] bytes) 
	{
		this.blockIDs = bytes;
	}

	public BlockByteLayer(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		this.blockIDs = new byte[TOTAL_BYTES];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, blockValue, i, localY, k);
			}
		}
	}
	
	public BlockByteLayer(LayeredBlockData<T> chunkData, int localY, IBlockLayer<T> nibbleLayer) 
	{
		this.blockIDs = new byte[TOTAL_BYTES];
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

		return blockID & 0xFF;
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
		/*if(!chunkData.paletteHasValue(blockValue))
		{
			chunkData.addToPalette(blockValue);
		}
		
		int fullPaletteID = chunkData.getBlockValueID(blockValue);
		
		if(fullPaletteID > 255)
		{
			chunkData.cleanPalette();
			if(chunkData.getPaletteSize() <= 255) 
			{
				chunkData.setBlockValue(blockValue, localX, localY, localZ);
				return;
			}
			final var layer = new BlockShortLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return;
		}*/
		
		final T oldBlock = getBlockValue(chunkData, localX, localZ);
		if(blockValue != oldBlock) 
		{
			final int idx = localX + (localZ * CHUNK_WIDTH);
			blockIDs[idx] = (byte) paletteID;
		}
	}

	public byte[] getBytes() {
		return blockIDs;
	}
	
	@Override
	public int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		return SaveFileConstants.BLOCK_LAYER_BYTE;
	}

	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeBytes(getBytes());
	}
}
