package finalforeach.cosmicreach.savelib.utils;

public interface IObjectMapInstantiator 
{
	<K, V> IObjectMap<K, V> create(IObjectMap<K, V> srcMap);

	<K, V> IObjectMap<K, V> create();

	<K> IObjectIntMap<K> createObjectIntMap();

	<K> IObjectLongMap<K> createObjectLongMap();
	
	<K> IObjectFloatMap<K> createObjectFloatMap();

}
