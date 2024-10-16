package finalforeach.cosmicreach.savelib.utils;

import java.util.function.BiConsumer;

public interface IObjectMap<K, V> 
{

	boolean containsKey(K key);

	V put(K key, V value);

	<T extends K> V get(T key);
	
	void putAll(IObjectMap<K, V> srcMap); 

	int size();
	
	void forEachEntry(BiConsumer<K, V> entryConsumer);

}
