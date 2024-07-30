package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISaveFileConstant;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public interface IBlockLightLayer extends ISaveFileConstant
{
	final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;

	short getBlockLight(int localX, int localZ);

	void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ);

	void writeTo(IChunkByteWriter allChunksWriter);

	static IBlockLightLayer readFrom(int layerType, int yLevel, BlockLightLayeredData blockLightLayeredData, IChunkByteReader reader) throws IOException 
	{
		switch(layerType) 
		{
		case SaveFileConstants.BLOCKLIGHTDATA_LAYER_SINGLE:
		{
			int r = reader.readByte();
			int g = reader.readByte();
			int b = reader.readByte();
			var layer = new BlockLightSingleLayer(blockLightLayeredData, yLevel, r, g, b);
			blockLightLayeredData.setLayer(yLevel, layer);
			return layer;
		}
		case SaveFileConstants.BLOCKLIGHTDATA_LAYER_SHORT:
		{
			var layer = BlockLightShortLayer.readFrom(reader);
			blockLightLayeredData.setLayer(yLevel, layer);
			return layer;								
		}
		case SaveFileConstants.BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_RED:
		{
			var layer = BlockLightMonoRedNibbleLayer.readFrom(reader, blockLightLayeredData, yLevel);
			blockLightLayeredData.setLayer(yLevel, layer);
			return layer;
		}
		case SaveFileConstants.BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_GREEN:
		{
			var layer = BlockLightMonoGreenNibbleLayer.readFrom(reader, blockLightLayeredData, yLevel);
			blockLightLayeredData.setLayer(yLevel, layer);
			return layer;
		}
		case SaveFileConstants.BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_BLUE:
		{
			var layer = BlockLightMonoBlueNibbleLayer.readFrom(reader, blockLightLayeredData, yLevel);
			blockLightLayeredData.setLayer(yLevel, layer);
			return layer;
		}
		default:
			throw new RuntimeException("Unknown layerType: " + layerType);
		}
	}

	class DebugLightInfo
	{
		int r, g, b;
		DebugLightInfo(int r, int g, int b)
		{
			this.r=r;
			this.g=g;
			this.b=b;
		}
		@Override
		public String toString() 
		{
			return r + "," + g + "," + b;
		}
	}
	default DebugLightInfo[] getDebugLights() 
	{
		var d = new DebugLightInfo[CHUNK_WIDTH * CHUNK_WIDTH];

		int di = 0;
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				int l = getBlockLight(i, k);
				int r = (l & 0xF00) >> 8;
				int g = (l & 0x0F0) >> 4;
				int b = (l & 0x00F);
				d[di++] = new DebugLightInfo(r, g, b);
			}
		}

		return d;
	}

}
