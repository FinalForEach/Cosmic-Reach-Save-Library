package finalforeach.cosmicreach.savelib.blockdata;

import java.util.function.Predicate;

public class SingleBlockData<T> implements IBlockData<T>
{
	private T blockValue;

	public SingleBlockData() 
	{
	}
	public SingleBlockData(T blockValue) 
	{
		this.blockValue = blockValue;
	}

	public T getBlockState() 
	{
		return blockValue;
	}
	
	@Override
	public T getBlockState(int localX, int localY, int localZ) 
	{
		return blockValue;
	}
	@Override
	public int getBlockStateID(int localX, int localY, int localZ) {
		return 0;
	}

	@Override
	public IBlockData<T> setBlockValue(T blockState, int localX, int localY, int localZ)
	{
		if(this.blockValue!=blockState) 
		{
			var chunkData = new LayeredBlockData<T>(this.blockValue);
			return chunkData.setBlockValue(blockState, localX, localY, localZ);
		}
		return this;
	}

	public IBlockData<T> fill(T blockState) 
	{	
		this.blockValue = blockState;
		return this;
	}
	
	@Override
	public IBlockData<T> fillLayer(T blockState, int localY) 
	{
		if(this.blockValue!=blockState) 
		{
			var chunkData = new LayeredBlockData<T>(this.blockValue);
			return chunkData.fillLayer(blockState, localY);
		}
		return this;
	}
	
	@Override
	public boolean isEntirely(T blockValue) 
	{
		return this.blockValue == blockValue;
	}
	
	@Override
	public boolean isEntirely(Predicate<T> predicate) 
	{
		return predicate.test(blockValue);
	}

	@Override
	public int getBlockValueID(T blockState) {
		return blockState==this.blockValue? 0 : -1;
	}

	@Override
	public T getBlockValueFromPaletteId(int bId) {
		return bId==0 ? blockValue : null;
	}
	@Override
	public int getUniqueBlockValuesCount() 
	{
		return 1;
	}

}
