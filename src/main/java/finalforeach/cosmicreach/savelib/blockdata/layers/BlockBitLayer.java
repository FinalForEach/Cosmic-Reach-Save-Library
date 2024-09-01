package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockBitLayer<T> implements IBlockLayer<T>
{
	private final byte[] blockIDs;
	
	public BlockBitLayer(byte[] bytes) 
	{
		this.blockIDs = bytes;
	}
	
	public BlockBitLayer(LayeredBlockData<T> chunkData, int localY, T blockState) 
	{
		this.blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 8];
		
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				setBlockValue(chunkData, blockState, i, localY, k);
			}
		}
	}

	@Override
	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ) {
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 8;
		final byte b = blockIDs[idx];
		
		int mod = localX % 8;
		int blockID = switch (mod) 
		{
			case 0 -> b & 0x01;
			case 1 -> (b & 0x02) >> mod;
			case 2 -> ((b & 0x04) >> mod);
			case 3 -> ((b & 0x08) >> mod);
			case 4 -> ((b & 0x10) >> mod);
			case 5 -> ((b & 0x20) >> mod);
			case 6 -> ((b & 0x40) >> mod);
			case 7 -> ((b & 0x80) >> mod);
			default -> throw new IllegalArgumentException("Unexpected value: " + mod);
		};
		return blockID;
	}

	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ) 
	{
		return chunkData.getBlockValueFromPaletteId(getBlockValueID(chunkData, localX, localZ));
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
		if(paletteID > 1)
		{
			final var layer = new BlockHalfNibbleLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return;
		}
		
		final T oldBlock = getBlockValue(chunkData, localX, localZ);
		if(blockValue!=oldBlock) 
		{
			final int idx = (localX + (localZ * CHUNK_WIDTH)) / 8;
			final int b = blockIDs[idx];

			int mod = localX % 8;
			blockIDs[idx] = switch (mod) 
			{
				case 0 -> (byte) ((b & 0x01) | paletteID);
				case 1 -> (byte) ((b & 0x02) | (paletteID << mod));
				case 2 -> (byte) ((b & 0x04) | (paletteID << mod));
				case 3 -> (byte) ((b & 0x08) | (paletteID << mod));
				case 4 -> (byte) ((b & 0x10) | (paletteID << mod));
				case 5 -> (byte) ((b & 0x20) | (paletteID << mod));
				case 6 -> (byte) ((b & 0x40) | (paletteID << mod));
				case 7 -> (byte) ((b & 0x80) | (paletteID << mod));
				default -> throw new IllegalArgumentException("Unexpected value: " + mod);
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
