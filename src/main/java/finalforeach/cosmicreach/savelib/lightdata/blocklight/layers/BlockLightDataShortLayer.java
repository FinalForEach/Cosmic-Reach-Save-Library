package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BlockLightDataShortLayer implements IBlockLightDataLayer
{
	private short[] lightLevels = new short[CHUNK_WIDTH * CHUNK_WIDTH];	

	public BlockLightDataShortLayer(short lightLevel) 
	{
		for(int i = 0; i < lightLevels.length; i++) 
		{
			lightLevels[i] = lightLevel;
		}
	}
	
	public static BlockLightDataShortLayer fromRandomAccessFileShortArray(RandomAccessFile raf) throws IOException 
	{
		BlockLightDataShortLayer layer = new BlockLightDataShortLayer();
		int l = layer.lightLevels.length;
		for(int i = 0; i < l; i++) 
		{
			layer.lightLevels[i] = raf.readShort(); 
		}
		return layer;
	}
	
	public BlockLightDataShortLayer() 
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

}
