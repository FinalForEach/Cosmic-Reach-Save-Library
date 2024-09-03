package finalforeach.cosmicreach.savelib.blockdata.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public interface IBlockSingleLayer<T> extends IBlockLayer<T>
{
	T getBlockValue();

	@Override
	public default int getSaveFileConstant(LayeredBlockData<T> chunkData) 
	{
		int paletteId = chunkData.getBlockValueID(getBlockValue());
		if(paletteId <= 255) 
		{
			return SaveFileConstants.BLOCK_LAYER_SINGLE_BYTE;
		}else 
		{
			return SaveFileConstants.BLOCK_LAYER_SINGLE_INT;
		}
	}

	@Override
	public default void writeTo(LayeredBlockData<T> chunkData, IChunkByteWriter allChunksWriter) 
	{
		int paletteId = chunkData.getBlockValueID(getBlockValue());
		if(paletteId <= 255) 
		{
			allChunksWriter.writeByte(paletteId);
		}else 
		{
			allChunksWriter.writeInt(paletteId);
		}
	}
}
