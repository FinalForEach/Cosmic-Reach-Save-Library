package finalforeach.cosmicreach.savelib.utils;

public interface IObjectIntMap<K> 
{
	int get(K key, int defaultValue);

	void put(K key, int value);

	boolean containsKey(K key);
}
