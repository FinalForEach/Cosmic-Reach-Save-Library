package finalforeach.cosmicreach.savelib.utils;

import finalforeach.cosmicreach.savelib.IByteArray;

public interface IDynamicArrayInstantiator
{
	<E> IDynamicArray<E> create(Class<E> clazz);
	<E> IDynamicArray<E> create(Class<E> clazz, int initialCapacity);
	IByteArray createByteArray();
}