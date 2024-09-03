package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class BlockSingleLayer<T> implements IBlockLayer<T>
{
	public T blockValue;
	
	public BlockSingleLayer(LayeredBlockData<T> chunkData, T blockState) 
	{
		this.blockValue = blockState;
		if(!chunkData.paletteHasValue(blockValue))
		{
			chunkData.addToPalette(blockState);
		}
	}

	public T getBlockState()
	{
		return blockValue;
	}
	
	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ)
	{
		return blockValue;
	}
	
	@Override
	public int getBlockValueID(LayeredBlockData<T> chunkData, int localX, int localZ)
	{
		return chunkData.getBlockValueID(blockValue);
	}
		
	@Override
	public void setBlockValue(LayeredBlockData<T> chunkData, T blockState, int localX, int localY, int localZ) 
	{
		if(this.blockValue == blockState) 
		{
			return;
		}else 
		{
			final var layer = new BlockBitLayer<T>(chunkData, localY, this.blockValue);
			//final var layer = new BlockHalfNibbleLayer<T>(chunkData, localY, this.blockValue);
			//final var layer = new BlockShortLayer<T>(chunkData, localY, this.blockValue);
			//final var layer = new BlockNibbleLayer<T>(chunkData, localY, this.blockValue);
			chunkData.setLayer(localY, layer);
			layer.setBlockValue(chunkData, blockState, localX, localY, localZ);
		}
	}

	public void fill(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		this.blockValue = blockValue;
		if(!chunkData.paletteHasValue(blockValue)) 
		{
			chunkData.addToPalette(blockValue);
		}
	}

	@Override
	public int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		int paletteId = chunkData.getBlockValueID(blockValue);
		if(paletteId <= 255) 
		{
			return SaveFileConstants.BLOCK_LAYER_SINGLE_BYTE;
		}else 
		{
			return SaveFileConstants.BLOCK_LAYER_SINGLE_INT;
		}
	}

	@Override
	public void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		int paletteId = chunkData.getBlockValueID(blockValue);
		if(paletteId <= 255) 
		{
			allChunksWriter.writeByte(paletteId);
		}else 
		{
			allChunksWriter.writeInt(paletteId);
		}
	}

}
