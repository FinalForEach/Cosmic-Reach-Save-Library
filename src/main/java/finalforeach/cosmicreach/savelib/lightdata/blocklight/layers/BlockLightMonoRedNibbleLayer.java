package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import java.io.IOException;

import finalforeach.cosmicreach.savelib.IChunkByteReader;
import finalforeach.cosmicreach.savelib.SaveFileConstants;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public class BlockLightMonoRedNibbleLayer extends BlockLightMonoNibbleLayer {

	public BlockLightMonoRedNibbleLayer(BlockLightLayeredData lightData, int yLevel, short baseLightLevel,
			byte[] bytes) 
	{
		super(lightData, yLevel, baseLightLevel, bytes);
	}

	public BlockLightMonoRedNibbleLayer(BlockLightLayeredData lightData, int yLevel, short lightLevel) {
		super(lightData, yLevel, lightLevel);
	}

	@Override
	public int getSaveFileConstant()
	{
		return SaveFileConstants.BLOCKLIGHTDATA_LAYER_MONO_NIBBLE_RED;
	}

	@Override
	protected int getRed(int localX, int localZ) {
		return super.getNibbleLight(localX, localZ);
	}
	
	@Override
	protected boolean isNibbleLightRed() {
		return true;
	}

	@Override
	protected boolean isNibbleLightGreen() {
		return false;
	}

	@Override
	protected boolean isNibbleLightBlue() {
		return false;
	}

	public static BlockLightMonoRedNibbleLayer readFrom(IChunkByteReader reader, BlockLightLayeredData lightData, int yLevel) throws IOException 
	{
		int r = reader.readByte();
		int g = reader.readByte();
		int b = reader.readByte();
		short baseLightLevel = (short)((r << 8) + (g << 4) + b);
		BlockLightMonoRedNibbleLayer layer = new BlockLightMonoRedNibbleLayer(lightData, yLevel, baseLightLevel);
		reader.readFully(layer.bytes);
		return layer;
	}

}
