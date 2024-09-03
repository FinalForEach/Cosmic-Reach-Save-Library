package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockHalfNibbleLayer<T> implements IBlockLayer<T>
{
	public static final int TOTAL_BYTES = CHUNK_WIDTH * CHUNK_WIDTH / 4;
	private final byte[] blockIDs;
	
	public BlockHalfNibbleLayer(byte[] bytes) 
	{
		this.blockIDs = bytes;
	}
	
	public BlockHalfNibbleLayer(LayeredBlockData<T> chunkData, int localY, T blockState) 
	{
		this.blockIDs = new byte[TOTAL_BYTES];
		
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, blockState, i, localY, k);
			}
		}
	}
	
	public BlockHalfNibbleLayer(LayeredBlockData<T> chunkData, int localY, IBlockLayer<T> srcLayer) 
	{
		this.blockIDs = new byte[TOTAL_BYTES];
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, srcLayer.getBlockValue(chunkData, i, k), i, localY, k);
			}
		}
	}


	@Override
	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ) {
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 4;
		final byte b = blockIDs[idx];
		
		int mod = localX % 4;
		int blockID = switch (mod) 
		{
			case 0 -> b & 0x03;
			case 1 -> (b & 0x0C) >> 2;
			case 2 -> (b & 0x30) >> 4;
			case 3 -> (b & 0xC0) >> 6;
			default -> throw new IllegalArgumentException("Unexpected value: " + mod);
		};
		return blockID;
	}

	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ) 
	{
		return chunkData.getBlockValueFromPaletteId(getBlockValueID(chunkData, localX, localZ));
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
		if(paletteID > 3)
		{
			final var layer = new BlockNibbleLayer<T>(chunkData, localY, this);
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
		
		final T oldBlock = getBlockValue(chunkData, localX, localZ);
		if(blockValue!=oldBlock) 
		{
			final int idx = (localX + (localZ * CHUNK_WIDTH)) / 4;
			final int b = blockIDs[idx];
			
			blockIDs[idx] = switch (localX % 4) 
			{
				case 0 -> (byte) ((b & 0xFC) | paletteID);
				case 1 -> (byte) ((b & 0xF3) | (paletteID << 2));
				case 2 -> (byte) ((b & 0xCF) | (paletteID << 4));
				case 3 -> (byte) ((b & 0x3F) | (paletteID << 6));
				default -> throw new IllegalArgumentException("Unexpected value: " + localX % 4);
			};
		}
	}

	public byte[] getBytes() {
		return blockIDs;
	}
	
	@Override
	public int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		return SaveFileConstants.BLOCK_LAYER_HALFNIBBLE;
	}

	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeBytes(getBytes());
	}
}
