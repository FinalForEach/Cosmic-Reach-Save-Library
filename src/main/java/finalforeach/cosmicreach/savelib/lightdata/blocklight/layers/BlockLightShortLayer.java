package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.SaveFileConstants;

public class BlockLightShortLayer implements IBlockLightLayer
{
	private short[] lightLevels = new short[CHUNK_WIDTH * CHUNK_WIDTH];	

	public BlockLightShortLayer(short lightLevel) 
	{
		for(int i = 0; i < lightLevels.length; i++) 
		{
			lightLevels[i] = lightLevel;
		}
	}
	
	public BlockLightShortLayer() 
	{
	}

	@Override
	public short getBlockLight(int localX, int localZ) {
		int idx = localX + (localZ * CHUNK_WIDTH);
		return lightLevels[idx];
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ)
	{
		int idx = localX + (localZ * CHUNK_WIDTH);
		lightLevels[idx] = (short)((lightLevelRed << 8) + (lightLevelGreen << 4) + lightLevelBlue);
	}

	public short[] getShorts() {
		return lightLevels;
	}

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYER_SHORT;
	}

	@Override
	public void writeTo(IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeShorts(getShorts());
	}

	public static BlockLightShortLayer readFrom(IChunkByteReader reader) throws IOException 
	{
		BlockLightShortLayer layer = new BlockLightShortLayer();
		int l = layer.lightLevels.length;
		for(int i = 0; i < l; i++) 
		{
			layer.lightLevels[i] = reader.readShort(); 
		}
		return layer;
	}
}
