package finalforeach.cosmicreach.savelib.lightdata.skylight;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.SkylightDataNibbleLayer;
import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.SkylightDataSingleLayer;
import static finalforeach.cosmicreach.savelib.ISavedChunk.CHUNK_WIDTH;

public interface ISkylightData extends ISaveFileConstant
{
	int getSkyLight(int localX, int localY, int localZ);

	void setSkyLight(ISavedChunk<?> chunk, int lightLevel, int localX, int localY, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);

	public static ISkylightData readSkylightData(IChunkByteReader reader) throws IOException
	{
		int skylightDataType = reader.readByte();
		switch (skylightDataType)
		{
		case SaveFileConstants.SKYLIGHTDATA_NULL:
			return null;
		case SaveFileConstants.SKYLIGHTDATA_SINGLE: {
			return SkylightSingleData.getForLightValue(reader.readByte());
		}
		case SaveFileConstants.SKYLIGHTDATA_LAYERED: {
			final var skyLayeredData = new SkylightLayeredData();

			for (int l = 0; l < CHUNK_WIDTH; l++)
			{
				int layerType = reader.readByte();
				switch (layerType)
				{
				case SaveFileConstants.SKYLIGHTDATA_LAYER_SINGLE: {
					var skySingleLayer = SkylightDataSingleLayer.getForLightValue(reader.readByte());
					skyLayeredData.setLayer(l, skySingleLayer);
					break;
				}
				case SaveFileConstants.SKYLIGHTDATA_LAYER_NIBBLE: {
					byte[] bytes = new byte[CHUNK_WIDTH * CHUNK_WIDTH / 2];
					reader.readFully(bytes);
					var layer = new SkylightDataNibbleLayer(bytes);
					skyLayeredData.setLayer(l, layer);
					break;
				}
				default:
					throw new RuntimeException("Unknown layerType: " + layerType);
				}
			}
			return skyLayeredData;
		}
		default:
			throw new RuntimeException("Unknown skylightDataType: " + skylightDataType);
		}
	}
}
