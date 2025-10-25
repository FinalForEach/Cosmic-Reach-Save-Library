package finalforeach.cosmicreach.savelib;

import java.io.IOException;
import java.nio.ByteBuffer;

import finalforeach.cosmicreach.savelib.blockentities.IBlockEntityInstantiator;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;

public class ChunkBlockEntityLoader
{
	public static void readBlockEntities(IChunkByteReader reader, IBlockEntityInstantiator instantiator)
			throws IOException
	{
		int blockEntityDataType = reader.readByte();
		switch (blockEntityDataType)
		{
		case SaveFileConstants.BLOCKENTITY_NULL:
			return;
		case SaveFileConstants.BLOCKENTITY_DATA: {
			int numBytes = reader.readInt();
			var bytes = new byte[numBytes];
			reader.readFully(bytes);
			CRBinDeserializer deserial = CRBinDeserializer.getNew();
			deserial.prepareForRead(ByteBuffer.wrap(bytes));
			var objDeserializers = deserial.readRawObjArray("blockEntities");
			for (var d : objDeserializers)
			{
				instantiator.instantiate(d);
			}

			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + blockEntityDataType);
		}

	}
}
