package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public class BlockLightMonoBlueNibbleLayer extends BlockLightMonoNibbleLayer {

	public BlockLightMonoBlueNibbleLayer(BlockLightLayeredData lightData, int yLevel, short baseLightLevel,
			byte[] bytes) 
	{
		super(lightData, yLevel, baseLightLevel, bytes);
	}

	public BlockLightMonoBlueNibbleLayer(BlockLightLayeredData lightData, int yLevel, short lightLevel) {
		super(lightData, yLevel, lightLevel);
	}

	@Override
	public int getSaveFileConstant()
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_BLUE;
	}

	@Override
	protected int getBlue(int localX, int localZ) {
		return super.getNibbleLight(localX, localZ);
	}
	
	@Override
	protected boolean isNibbleLightRed() {
		return false;
	}

	@Override
	protected boolean isNibbleLightGreen() {
		return false;
	}

	@Override
	protected boolean isNibbleLightBlue() {
		return true;
	}

	public static BlockLightMonoBlueNibbleLayer readFrom(IChunkByteReader reader, BlockLightLayeredData lightData, int yLevel) throws IOException 
	{
		int r = reader.readByte();
		int g = reader.readByte();
		int b = reader.readByte();
		short baseLightLevel = (short)((r << 8) + (g << 4) + b);
		BlockLightMonoBlueNibbleLayer layer = new BlockLightMonoBlueNibbleLayer(lightData, yLevel, baseLightLevel);
		reader.readFully(layer.bytes);
		return layer;
	}

}
