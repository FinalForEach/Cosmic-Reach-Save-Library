package finalforeach.cosmicreach.savelib.utils;

import finalforeach.cosmicreach.savelib.IByteArray;

public class DynamicArrays
{
	public static IDynamicArrayInstantiator instantiator;

	public static <E> IDynamicArray<E> getNew(Class<E> clazz)
	{
		return instantiator.create(clazz);
	}
	
	public static <E> IDynamicArray<E> getNew(Class<E> clazz, int initialCapacity)
	{
		return instantiator.create(clazz, initialCapacity);
	}

	public static IByteArray getNewByteArray() 
	{
		return instantiator.createByteArray();
	}
}