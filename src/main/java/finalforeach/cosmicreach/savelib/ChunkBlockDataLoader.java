package finalforeach.cosmicreach.savelib;

import java.io.IOException;
import java.util.function.Function;

import finalforeach.cosmicreach.savelib.blockdata.IBlockData;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;
import finalforeach.cosmicreach.savelib.blockdata.SingleBlockData;
import finalforeach.cosmicreach.savelib.blocks.IBlockState;

public class ChunkBlockDataLoader
{
	public static <T extends IBlockState> IBlockData<T> readBlockData(IChunkByteReader reader, Function<String, T> instantiator) throws IOException
	{
		final int chunkDataType = reader.readByte();

		switch (chunkDataType)
		{
		case SaveFileConstants.BLOCK_SINGLE: {
			return SingleBlockData.readFrom(reader, instantiator);
		}
		case SaveFileConstants.BLOCK_LAYERED: {
			return LayeredBlockData.readFrom(reader, instantiator);
		}
		default:
			throw new RuntimeException("Unknown chunkDataType: " + chunkDataType);
		}
	}
}
