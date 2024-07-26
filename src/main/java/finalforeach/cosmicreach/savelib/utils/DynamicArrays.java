package finalforeach.cosmicreach.savelib.utils;

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
}