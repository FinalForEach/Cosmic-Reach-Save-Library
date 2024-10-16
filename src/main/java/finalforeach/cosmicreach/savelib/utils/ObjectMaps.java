package finalforeach.cosmicreach.savelib.utils;


public class ObjectMaps
{	
	public static IObjectMapInstantiator instantiator;

	public static <K, V> IObjectMap<K, V> getNew()
	{
		return instantiator.create();
	}
	
	public static <K, V> IObjectMap<K, V> getNew(IObjectMap<K, V> srcMap) 
	{
		return instantiator.create(srcMap);
	}
	
	public static <K> IObjectIntMap<K> getNewIntMap() 
	{
		return instantiator.createObjectIntMap();
	}

	public static <K> IObjectLongMap<K> getNewLongMap() {
		return instantiator.createObjectLongMap();
	}

	public static <K> IObjectFloatMap<K> getNewFloatMap() {
		return instantiator.createObjectFloatMap();
	}

}
