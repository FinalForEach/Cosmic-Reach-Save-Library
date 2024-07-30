package finalforeach.cosmicreach.savelib.lightdata.blocklight.layers;

import finalforeach.cosmicreach.savelib.IChunkByteWriter;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;

public abstract class BlockLightMonoNibbleLayer implements IBlockLightLayer
{
	private BlockLightLayeredData lightData;
	public short baseLightLevel;
	private int yLevel;
	protected byte[] bytes;

	public BlockLightMonoNibbleLayer(BlockLightLayeredData lightData, int yLevel, short baseLightLevel, byte[] bytes) 
	{
		this.lightData = lightData;
		this.yLevel = yLevel;
		this.baseLightLevel = baseLightLevel;
		this.bytes = bytes;
	}

	public BlockLightMonoNibbleLayer(BlockLightLayeredData lightData, int yLevel, short lightLevel) 
	{
		this(lightData, yLevel, lightLevel, new byte[CHUNK_WIDTH * CHUNK_WIDTH / 2]);
		for(int i = 0; i < CHUNK_WIDTH; i++) 
		{
			for(int k = 0; k < CHUNK_WIDTH; k++) 
			{
				int r = (byte)((baseLightLevel & 0xF00) >> 8);
				int g = (byte)((baseLightLevel & 0x0F0) >> 4);
				int b = (byte)((baseLightLevel & 0x00F));
				setBlockLight(r, g, b, i, k);
			}
		}
	}

	@Override
	public short getBlockLight(int localX, int localZ) 
	{
		int r = getRed(localX, localZ);
		int g = getGreen(localX, localZ);
		int b = getBlue(localX, localZ);

		return (short)((r << 8) + (g << 4) + b);
	}

	protected int getRed(int localX, int localZ)
	{
		return (byte)((baseLightLevel & 0xF00) >> 8);
	}

	protected int getGreen(int localX, int localZ) 
	{
		return (byte)((baseLightLevel & 0x0F0) >> 4);
	}

	protected int getBlue(int localX, int localZ) 
	{
		return (byte)(baseLightLevel & 0x00F);
	}

	protected abstract boolean isNibbleLightRed();

	protected abstract boolean isNibbleLightGreen();

	protected abstract boolean isNibbleLightBlue();

	protected int getNibbleLight(int localX, int localZ) 
	{
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;

		final int b = bytes[idx];

		int mod2 = localX % 2;
		int nibbleLight = (mod2 * ((b & 0xF0) >> 4)) + ((1 - mod2) * b & 0x0F);

		return nibbleLight;
	}

	public void setNibbleLight(int nibble, int localX, int localZ) 
	{		
		final int idx = (localX + (localZ * CHUNK_WIDTH)) / 2;
		final int b = bytes[idx];
		if(localX % 2 == 0) 
		{
			bytes[idx] = (byte)((b & 0xF0) | nibble);
		}else 
		{
			bytes[idx] = (byte)((b & 0x0F) | (nibble << 4));
		}
	}

	@Override
	public void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localZ)
	{
		int red = getRed(localX, localZ);
		int green = getGreen(localX, localZ);
		int blue = getBlue(localX, localZ);

		boolean baseBad = false;
		if(!isNibbleLightRed() && red != lightLevelRed) 
		{
			baseBad = true;
		}
		if(!isNibbleLightGreen() && green != lightLevelGreen) 
		{
			baseBad = true;
		}
		if(!isNibbleLightBlue() && blue != lightLevelBlue) 
		{
			baseBad = true;
		}

		if(baseBad) 
		{
			final var shortLayer = new BlockLightShortLayer(baseLightLevel);

			for(int i = 0; i < CHUNK_WIDTH; i++) 
			{
				for(int k = 0; k < CHUNK_WIDTH; k++) 
				{
					red = getRed(i, k);
					green = getGreen(i, k);
					blue = getBlue(i, k);

					shortLayer.setBlockLight(red, green, blue, i, k);
				}
			}
			shortLayer.setBlockLight(lightLevelRed, lightLevelGreen, lightLevelBlue, localX, localZ);

			lightData.setLayer(yLevel, shortLayer);
		}else 
		{
			if(isNibbleLightRed()) 
			{
				setNibbleLight(lightLevelRed, localX, localZ);
			}else if(isNibbleLightGreen()) 
			{
				setNibbleLight(lightLevelGreen, localX, localZ);
			}else if(isNibbleLightBlue()) 
			{
				setNibbleLight(lightLevelBlue, localX, localZ);
			}
		}
	}

	@Override
	public void writeTo(IChunkByteWriter allChunksWriter) 
	{
		allChunksWriter.writeByte(baseLightLevel >> 8);
		allChunksWriter.writeByte(baseLightLevel >> 4);
		allChunksWriter.writeByte(baseLightLevel);
		allChunksWriter.writeBytes(bytes);
	}

}
