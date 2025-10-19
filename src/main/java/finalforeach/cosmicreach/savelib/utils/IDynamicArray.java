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

	/** Returns true if this array contains the specified value.
	 * @param value May be null.
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used. */
	boolean contains(E value, boolean identity);

	/** Returns the index of first occurrence of value in the array, or -1 if no such value exists.
	 * @param value May be null.
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return An index of first occurrence of value in array or -1 if no such value exists */
	int indexOf(E value, boolean identity);

	E[] items();

	void clear();

	E removeIndex(int index);

	default boolean isEmpty() 
	{
		return size() != 0;
	}
	
	default boolean notEmpty() 
	{
		return size() == 0;
	}

	void truncate(int numberOfSlots);
}