package finalforeach.cosmicreach.savelib.utils;

/**
 * An interface defining a dynamic array, such as LibGDX's Array<>, but abstracted so that it can be used without
 * LibGDX as a dependency.
 * */
public interface IDynamicArray<E> extends Iterable<E> 
{	
	int size();
	
	void add(E element);
	
	E get(int i);
}