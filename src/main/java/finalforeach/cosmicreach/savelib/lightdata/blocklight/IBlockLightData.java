package finalforeach.cosmicreach.savelib.lightdata.blocklight;

import static finalforeach.cosmicreach.savelib.ISavedChunk.CHUNK_WIDTH;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.IBlockLightLayer;

public interface IBlockLightData extends ISaveFileConstant
{
	public static final IBlockLightData NEEDS_CALCULATION = new IBlockLightData()
	{
		@Override
		public int getSaveFileConstant()
		{
			return SaveFileConstants.BLOCKLIGHTDATA_NEEDS_CALCULATION;
		}

		@Override
		public short getBlockLight(int localX, int localY, int localZ)
		{
			return 0;
		}

		@Override
		public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localY,
				int localZ)
		{
		}

		@Override
		public void writeTo(IChunkByteWriter allChunksWriter)
		{
		}
	};

	short getBlockLight(int localX, int localY, int localZ);

	void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localY, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);

	public static IBlockLightData readBlocklightData(IChunkByteReader reader) throws IOException
	{
		int blockLightDataType = reader.readByte();
		switch (blockLightDataType)
		{
		case SaveFileConstants.BLOCKLIGHTDATA_NULL:
			return null;
		case SaveFileConstants.BLOCKLIGHTDATA_NEEDS_CALCULATION:
			return NEEDS_CALCULATION;
		case SaveFileConstants.BLOCKLIGHTDATA_LAYERED: {
			final var blockLightLayeredData = new BlockLightLayeredData();

			for (int l = 0; l < CHUNK_WIDTH; l++)
			{
				int layerType = reader.readByte();

				IBlockLightLayer layer = IBlockLightLayer.readFrom(layerType, l, blockLightLayeredData, reader);
				blockLightLayeredData.setLayer(l, layer);
			}
			return blockLightLayeredData;
		}
		default:
			throw new RuntimeException("Unknown blockLightDataType: " + blockLightDataType);
		}
	}
}
