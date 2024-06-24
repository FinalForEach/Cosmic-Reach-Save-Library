package finalforeach.cosmicreach.savelib.utils;

@FunctionalInterface
public interface IDynamicArrayInstantiator
{
	<E> IDynamicArray<E> create();
}