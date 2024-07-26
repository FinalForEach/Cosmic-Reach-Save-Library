package finalforeach.cosmicreach.savelib.utils;

public interface IDynamicArrayInstantiator
{
	<E> IDynamicArray<E> create(Class<E> clazz);
	<E> IDynamicArray<E> create(Class<E> clazz, int initialCapacity);
}