package finalforeach.cosmicreach.savelib.tests;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import finalforeach.cosmicreach.savelib.IByteArray;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.savelib.crbin.ICRBinSerializable;
import finalforeach.cosmicreach.savelib.utils.DynamicArrays;
import finalforeach.cosmicreach.savelib.utils.IDynamicArray;
import finalforeach.cosmicreach.savelib.utils.IDynamicArrayInstantiator;
import finalforeach.cosmicreach.savelib.utils.IObjectFloatMap;
import finalforeach.cosmicreach.savelib.utils.IObjectIntMap;
import finalforeach.cosmicreach.savelib.utils.IObjectLongMap;
import finalforeach.cosmicreach.savelib.utils.IObjectMap;
import finalforeach.cosmicreach.savelib.utils.IObjectMapInstantiator;
import finalforeach.cosmicreach.savelib.utils.ObjectMaps;

class TestDynamicArray<T> implements IDynamicArray<T>
{
	ArrayList<T> backingArray = new ArrayList<T>();
	@Override
	public Iterator<T> iterator() {
		return backingArray.iterator();
	}

	@Override
	public int size() {
		return backingArray.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(Object element) {
		backingArray.add((T) element);
	}

	@Override
	public T get(int i) {
		return backingArray.get(i);
	}

	@Override
	public boolean contains(Object value, boolean identity) {
		return backingArray.contains(value);
	}

	@Override
	public int indexOf(Object value, boolean identity) {
		return backingArray.indexOf(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] items() {
		return (T[]) backingArray.toArray();
	}

	@Override
	public void clear() {
		backingArray.clear();
	}

	@Override
	public T removeIndex(int index) {
		return backingArray.remove(index);
	}
	@Override
	public String toString() {
		String s = "[";
		
		for(var o : backingArray) 
		{
			s+= o + ", ";
		}
		
		s+="]";
		return s;
	}
}
class TestByteArray implements IByteArray
{
	byte[] backingArray = new byte[8];
	int size = 0;
	@Override
	public byte[] toArray() {
		return Arrays.copyOf(backingArray, size());
	}

	@Override
	public void addAll(byte... bytes)
	{
		if(size + bytes.length >= backingArray.length) 
		{
			backingArray = Arrays.copyOf(backingArray, backingArray.length + bytes.length);
		}
		for(int i = 0; i < bytes.length; i++) 
		{
			backingArray[size + i] = bytes[i];
		}
		size += bytes.length;
	}

	@Override
	public void set(int index, byte b) 
	{
		backingArray[index] = b;
	}

	@Override
	public void add(byte b) {
		addAll(b);
	}

	@Override
	public void addAll(IByteArray bytes) {
		addAll(bytes.toArray());
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public byte[] items() {
		return backingArray;
	}
}

class TestObjectLongMap<K> implements IObjectLongMap<K>
{
	HashMap<K, Long> map = new HashMap<>();
	@Override
	public long get(K key, long defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public void put(K key, long value) {
		map.put(key, value);
	}
}
class TestObjectIntMap<K> implements IObjectIntMap<K>
{
	HashMap<K, Integer> map = new HashMap<>();
	@Override
	public int get(K key, int defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public void put(K key, int value) {
		map.put(key, value);
	}
}
class TestObjectFloatMap<K> implements IObjectFloatMap<K>
{
	HashMap<K, Float> map = new HashMap<>();
	@Override
	public float get(K key, float defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public void put(K key, float value) {
		map.put(key, value);
	}
}
class TestObjectMap<K, V> implements IObjectMap<K, V>
{
	HashMap<K, V> map = new HashMap<>();
	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public <T extends K> V get(T key) {
		return map.get(key);
	}

	@Override
	public void putAll(IObjectMap<K, V> srcMap) {
		srcMap.forEachEntry((k, v) -> put(k, v));
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public void forEachEntry(BiConsumer<K, V> entryConsumer) {
		map.forEach((k, v) -> entryConsumer.accept(k, v));
	}
}
class TestVector implements ICRBinSerializable
{
	float x, y, z;
	@Override
	public boolean equals(Object obj) 
	{
		if(obj instanceof TestVector t) 
		{
			return x == t.x && y ==t.y && z ==t.z;
		}
		return super.equals(obj);
	}
	@Override
	public void read(CRBinDeserializer deserial) 
	{
		x = deserial.readFloat("x", 0);
		y = deserial.readFloat("y", 0);
		z = deserial.readFloat("z", 0);
	}
	@Override
	public void write(CRBinSerializer serial) {
		serial.writeFloat("x", x);
		serial.writeFloat("y", y);
		serial.writeFloat("z", z);
	}
}
public class CRBinTests 
{
	CRBinSerializer serial;
	CRBinDeserializer deserial;
	
	
	@BeforeAll
	static void setupBeforeAll()
	{
		
		DynamicArrays.instantiator = new IDynamicArrayInstantiator() {
			
			@Override
			public IByteArray createByteArray() {
				return new TestByteArray();
			}
			
			@Override
			public <E> IDynamicArray<E> create(Class<E> clazz, int initialCapacity) {
				return create(clazz);
			}
			
			@Override
			public <E> IDynamicArray<E> create(Class<E> clazz) {
				return new TestDynamicArray<E>();
			}
		};
		
		ObjectMaps.instantiator = new IObjectMapInstantiator() {
			
			@Override
			public <K> IObjectLongMap<K> createObjectLongMap() {
				return new TestObjectLongMap<K>();
			}
			
			@Override
			public <K> IObjectIntMap<K> createObjectIntMap() {
				return new TestObjectIntMap<K>();
			}
			
			@Override
			public <K> IObjectFloatMap<K> createObjectFloatMap() {
				return new TestObjectFloatMap<K>();
			}
			
			@Override
			public <K, V> IObjectMap<K, V> create() {
				return new TestObjectMap<K, V>();
			}
			
			@Override
			public <K, V> IObjectMap<K, V> create(IObjectMap<K, V> srcMap) {
				IObjectMap<K, V> m = new TestObjectMap<K, V>();
				m.putAll(srcMap);
				return m;
			}
		};
	}
	
	@BeforeEach
	void setup() 
	{
		serial = new CRBinSerializer();
		deserial = new CRBinDeserializer();
	}
	

    @Test
    void testString() throws Exception 
    {
    	String key = "string";
    	var value = "hello world!";
    	serial.writeString(key, value);

    	assertSerialCorrect(key, value, d -> d.readString(key));
    }
    
    @Test
    void testVector() throws Exception 
    {
    	String key = "vec";
    	var value = new TestVector();
    	value.x = 3;
    	value.y = 4;
    	value.z = 5;
    	serial.writeObj(key, value);
    	
    	assertSerialCorrect(key, value, d -> d.readObj(key, TestVector.class));
    }
    
    void assertSerialCorrect(String key, Object expectedValue, Function<CRBinDeserializer, Object> deserialCheck) 
    {
    	deserial.prepareForRead(ByteBuffer.wrap(serial.toBytes()));
    	assertEquals(expectedValue, deserialCheck.apply(deserial));
    }
}

