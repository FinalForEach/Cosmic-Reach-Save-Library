package finalforeach.cosmicreach.savelib.blockdata.layers;

import java.util.HashMap;

import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;

public class SharedBlockSingleLayer<T> extends BlockSingleLayer<T>
{
	private static HashMap<Object, SharedBlockSingleLayer<?>> sharedInstances = new HashMap<>();

	private SharedBlockSingleLayer(LayeredBlockData<T> chunkData, T blockValue)
	{
		super(chunkData, blockValue);
	}

	public void fill(LayeredBlockData<T> chunkData, int localY, T blockValue) 
	{
		if(this.blockValue!=blockValue) 
		{
			chunkData.setLayer(localY, get(chunkData, blockValue));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> SharedBlockSingleLayer<T> get(LayeredBlockData<T> chunkData, T blockValue) 
	{
		var shared = sharedInstances.get(blockValue);
		if(shared==null) 
		{
			synchronized (sharedInstances) 
			{
				// Check again, to prevent race conditions
				shared = sharedInstances.get(blockValue);
				if(shared == null) 
				{
					shared = new SharedBlockSingleLayer<T>(chunkData, blockValue);
					sharedInstances.put(blockValue, shared);
				}
			}
		}
		return (SharedBlockSingleLayer<T>) shared;
	}
}
