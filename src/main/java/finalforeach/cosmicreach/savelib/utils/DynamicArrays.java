package finalforeach.cosmicreach.savelib.utils;

public class DynamicArrays
{
	public static IDynamicArrayInstantiator instantiator;
	
	public static <E> IDynamicArray<E> newDynamicArray()
	{
		return instantiator.create();
	}
}