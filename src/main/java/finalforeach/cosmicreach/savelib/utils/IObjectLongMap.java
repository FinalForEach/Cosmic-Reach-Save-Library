package finalforeach.cosmicreach.savelib.utils;

public interface IObjectLongMap<K> 
{
	long get(K key, long defaultValue);

	void put(K key, long value);
}
