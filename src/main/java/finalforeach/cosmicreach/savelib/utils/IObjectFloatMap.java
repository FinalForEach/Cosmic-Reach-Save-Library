package finalforeach.cosmicreach.savelib.utils;

public interface IObjectFloatMap<K> 
{
	float get(K key, float defaultValue);

	void put(K key, float value);
}
