package finalforeach.cosmicreach.savelib.lightdata.blocklight;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.BlockLightDataSingleLayer;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.IBlockLightDataLayer;

public class BlockLightLayeredData implements IBlockLightData
{
	protected static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	private IBlockLightDataLayer[] layers = new IBlockLightDataLayer[CHUNK_WIDTH];
	
	public BlockLightLayeredData() 
	{
		for(int i = 0; i < layers.length; i++)
		{
			layers[i] = new BlockLightDataSingleLayer(this, i, 0, 0, 0);
		}
	}
	
	@Override
	public short getBlockLight(int localX, int localY, int localZ) {
		return layers[localY].getBlockLight(localX, localZ);
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, 
			int localX, int localY, int localZ)
	{
		layers[localY].setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, localX, localZ);
	}

	public void setLayer(int yLevel, IBlockLightDataLayer layer) {
		layers[yLevel] = layer;
	}

	public IBlockLightDataLayer[] getLayers() {
		return layers;
	}

	@Override
	public int getSaveFileConstant() 
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYERED;
	}

	@Override
	public void writeTo(IChunkByteWriter allChunksWriter) 
	{
		for(var layer : getLayers()) 
		{
			allChunksWriter.writeByte(layer.getSaveFileConstant());
			layer.writeTo(allChunksWriter);
		}
	}

}
