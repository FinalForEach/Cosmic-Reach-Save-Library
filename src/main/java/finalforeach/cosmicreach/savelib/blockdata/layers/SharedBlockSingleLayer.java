package finalforeach.cosmicreach.savelib.blockdata.layers;

import java.util.HashMap;

import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class SharedBlockSingleLayer<T> implements IBlockSingleLayer<T>
{
	public final T blockValue;
	private static HashMap<Object, SharedBlockSingleLayer<?>> sharedInstances = new HashMap<>();

	private SharedBlockSingleLayer(T blockValue)
	{
		this.blockValue = blockValue;
	}

	public void fill(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		if(this.blockValue != blockValue) 
		{
			chunkData.setLayer(localY, get(chunkData, blockValue));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> SharedBlockSingleLayer<T> get(LayeredBlockData<T> chunkData, T blockValue) 
	{
		var shared = sharedInstances.get(blockValue);
		if(shared == null) 
		{
			synchronized (sharedInstances) 
			{
				// Check again, to prevent race conditions
				shared = sharedInstances.get(blockValue);
				if(shared == null) 
				{
					shared = new SharedBlockSingleLayer<T>(blockValue);
					sharedInstances.put(blockValue, shared);
				}
			}
		}
		return (SharedBlockSingleLayer<T>) shared;
	}
	
	@Override
	public T getBlockValue(LayeredBlockData<T> chunkData, int localX, int localZ)
	{
		return blockValue;
	}

	public boolean upgradeLayer(int paletteID, LayeredBlockData<T> chunkData, T blockValue, int localX, int localY, int localZ) 
	{
		if(paletteID > 255)
		{
			final var layer = new BlockShortLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return true;
		}
		if(paletteID > 15)
		{
			final var layer = new BlockByteLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return true;
		}
		if(paletteID > 3)
		{
			final var layer = new BlockNibbleLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return true;
		}
		if(paletteID > 1)
		{
			final var layer = new BlockHalfNibbleLayer<T>(chunkData, localY, this);
			layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
			chunkData.setLayer(localY, layer);
			return true;
		}

		final var layer = new BlockBitLayer<T>(chunkData, localY, this.blockValue);
		//final var layer = new BlockHalfNibbleLayer<T>(chunkData, localY, this.blockValue);
		//final var layer = new BlockShortLayer<T>(chunkData, localY, this.blockValue);
		//final var layer = new BlockNibbleLayer<T>(chunkData, localY, this.blockValue);
		chunkData.setLayer(localY, layer);
		layer.setBlockValue(chunkData, blockValue, localX, localY, localZ);
		return false;
	}
	@Override
	public void setBlockValue(LayeredBlockData<T> chunkData, T blockState, int localX, int localY, int localZ) 
	{
		if(this.blockValue == blockState) 
		{
			return;
		}else 
		{
			upgradeLayer(chunkData.getBlockValueIDAddIfMissing(this.blockValue),
					chunkData, blockState, localX, localY, localZ);
		}
	}

	
	@Override
	public T getBlockValue() {
		return blockValue;
	}

}
