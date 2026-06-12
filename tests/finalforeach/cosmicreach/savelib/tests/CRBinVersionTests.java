package finalforeach.cosmicreach.savelib.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import finalforeach.cosmicreach.savelib.IByteArray;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.savelib.crbin.ICRBinSerializable;
import finalforeach.cosmicreach.savelib.crbin.SchemaType;
import finalforeach.cosmicreach.savelib.crbin.UnsupportedVersionException;
import finalforeach.cosmicreach.savelib.utils.DynamicArrays;
import finalforeach.cosmicreach.savelib.utils.IDynamicArray;
import finalforeach.cosmicreach.savelib.utils.IDynamicArrayInstantiator;
import finalforeach.cosmicreach.savelib.utils.IObjectFloatMap;
import finalforeach.cosmicreach.savelib.utils.IObjectIntMap;
import finalforeach.cosmicreach.savelib.utils.IObjectLongMap;
import finalforeach.cosmicreach.savelib.utils.IObjectMap;
import finalforeach.cosmicreach.savelib.utils.IObjectMapInstantiator;
import finalforeach.cosmicreach.savelib.utils.ObjectMaps;

class TestDynamicArrayV2<T> implements IDynamicArray<T>
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
	public void truncate(int numberOfSlots) {
		while (backingArray.size() > numberOfSlots) {
			backingArray.remove(backingArray.size() - 1);
		}
	}
}

class TestByteArrayV2 implements IByteArray
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

	@Override
	public void clear() {
		size = 0;
	}
}

class TestObjectLongMapV2<K> implements IObjectLongMap<K>
{
	HashMap<K, Long> map = new HashMap<>();
	@Override
	public long get(K key, long defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public void put(K key, long value) {
		map.put(key, value);
	}
}

class TestObjectIntMapV2<K> implements IObjectIntMap<K>
{
	HashMap<K, Integer> map = new HashMap<>();
	@Override
	public int get(K key, int defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public void put(K key, int value) {
		map.put(key, value);
	}
}

class TestObjectFloatMapV2<K> implements IObjectFloatMap<K>
{
	HashMap<K, Float> map = new HashMap<>();
	@Override
	public float get(K key, float defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public void put(K key, float value) {
		map.put(key, value);
	}
}

class TestObjectMapV2<K, V> implements IObjectMap<K, V>
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

class V1CompatTest implements ICRBinSerializable
{
	int x;
	String s;
	float f;

	@Override
	public void read(CRBinDeserializer deserial)
	{
		x = deserial.readInt("x", 0);
		s = deserial.readString("s");
		f = deserial.readFloat("f", 0.0f);
	}

	@Override
	public void write(CRBinSerializer serial)
	{
		serial.writeInt("x", x);
		serial.writeString("s", s);
		serial.writeFloat("f", f);
	}
}

class SizeComparisonTest implements ICRBinSerializable
{
	int a = 5;
	int b = 10;
	String s = "hello";
	boolean[] flags = new boolean[] { true, false, true, true, false, false, true, true, false, true };

	@Override
	public void read(CRBinDeserializer deserial)
	{
		a = deserial.readInt("a", 0);
		b = deserial.readInt("b", 0);
		s = deserial.readString("s");
		flags = deserial.readBooleanArray("flags");
	}

	@Override
	public void write(CRBinSerializer serial)
	{
		serial.writeInt("a", a);
		serial.writeInt("b", b);
		serial.writeString("s", s);
		serial.writeBooleanArray("flags", flags);
	}
}

public class CRBinVersionTests
{
	@BeforeAll
	static void setupBeforeAll()
	{
		DynamicArrays.instantiator = new IDynamicArrayInstantiator() {
			@Override
			public IByteArray createByteArray() {
				return new TestByteArrayV2();
			}
			@Override
			public <E> IDynamicArray<E> create(Class<E> clazz, int initialCapacity) {
				return create(clazz);
			}
			@Override
			public <E> IDynamicArray<E> create(Class<E> clazz) {
				return new TestDynamicArrayV2<E>();
			}
		};
		ObjectMaps.instantiator = new IObjectMapInstantiator() {
			@Override
			public <K> IObjectLongMap<K> createObjectLongMap() {
				return new TestObjectLongMapV2<K>();
			}
			@Override
			public <K> IObjectIntMap<K> createObjectIntMap() {
				return new TestObjectIntMapV2<K>();
			}
			@Override
			public <K> IObjectFloatMap<K> createObjectFloatMap() {
				return new TestObjectFloatMapV2<K>();
			}
			@Override
			public <K, V> IObjectMap<K, V> create() {
				return new TestObjectMapV2<K, V>();
			}
			@Override
			public <K, V> IObjectMap<K, V> create(IObjectMap<K, V> srcMap) {
				IObjectMap<K, V> m = new TestObjectMapV2<K, V>();
				m.putAll(srcMap);
				return m;
			}
		};
	}

	private byte[] writeV1Bytes(V1CompatTest obj)
	{
		ArrayList<String> strings = new ArrayList<>();
		strings.add("x");
		strings.add("s");
		strings.add("f");
		if (obj.s != null)
		{
			strings.add(obj.s);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		try
		{
			dos.writeInt(strings.size());
			for (String str : strings)
			{
				if (str == null)
				{
					dos.writeInt(-1);
				}
				else
				{
					byte[] b = str.getBytes(StandardCharsets.UTF_8);
					dos.writeInt(b.length);
					dos.write(b);
				}
			}

			dos.writeByte(SchemaType.INT.byteId);
			byte[] nameX = "x".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameX.length);
			dos.write(nameX);

			dos.writeByte(SchemaType.STRING.byteId);
			byte[] nameS = "s".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameS.length);
			dos.write(nameS);

			dos.writeByte(SchemaType.FLOAT.byteId);
			byte[] nameF = "f".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameF.length);
			dos.write(nameF);

			dos.writeByte(SchemaType.SCHEMA_END.byteId);

			dos.writeInt(0);

			dos.writeInt(obj.x);
			int sIdx = obj.s == null ? -1 : strings.indexOf(obj.s);
			dos.writeInt(sIdx);
			dos.writeFloat(obj.f);

			dos.flush();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return bos.toByteArray();
	}

	private byte[] writeV1BytesForSizeTest(SizeComparisonTest obj)
	{
		ArrayList<String> strings = new ArrayList<>();
		strings.add("a");
		strings.add("b");
		strings.add("s");
		strings.add("flags");
		if (obj.s != null)
		{
			strings.add(obj.s);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		try
		{
			dos.writeInt(strings.size());
			for (String str : strings)
			{
				if (str == null)
				{
					dos.writeInt(-1);
				}
				else
				{
					byte[] b = str.getBytes(StandardCharsets.UTF_8);
					dos.writeInt(b.length);
					dos.write(b);
				}
			}

			dos.writeByte(SchemaType.INT.byteId);
			byte[] nameA = "a".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameA.length);
			dos.write(nameA);

			dos.writeByte(SchemaType.INT.byteId);
			byte[] nameB = "b".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameB.length);
			dos.write(nameB);

			dos.writeByte(SchemaType.STRING.byteId);
			byte[] nameS = "s".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameS.length);
			dos.write(nameS);

			dos.writeByte(SchemaType.BOOLEAN_ARRAY.byteId);
			byte[] nameFlags = "flags".getBytes(StandardCharsets.UTF_8);
			dos.writeInt(nameFlags.length);
			dos.write(nameFlags);

			dos.writeByte(SchemaType.SCHEMA_END.byteId);

			dos.writeInt(0);

			dos.writeInt(obj.a);
			dos.writeInt(obj.b);
			int sIdx = obj.s == null ? -1 : strings.indexOf(obj.s);
			dos.writeInt(sIdx);

			if (obj.flags == null)
			{
				dos.writeInt(-1);
			}
			else
			{
				dos.writeInt(obj.flags.length);
				for (boolean flag : obj.flags)
				{
					dos.writeByte(flag ? 1 : 0);
				}
			}

			dos.flush();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return bos.toByteArray();
	}

	@Test
	void testV2SerializationDeserialization()
	{
		SizeComparisonTest source = new SizeComparisonTest();
		source.a = 999;
		source.b = -12345;
		source.s = "V2 serialization test string";
		source.flags = new boolean[] { false, true, false, false, true };

		CRBinSerializer serializer = new CRBinSerializer();
		source.write(serializer);
		byte[] payload = serializer.toBytes();

		CRBinDeserializer deserializer = new CRBinDeserializer();
		deserializer.prepareForRead(ByteBuffer.wrap(payload));

		SizeComparisonTest result = new SizeComparisonTest();
		result.read(deserializer);

		assertEquals(999, result.a);
		assertEquals(-12345, result.b);
		assertEquals("V2 serialization test string", result.s);
		assertNotNull(result.flags);
		assertEquals(5, result.flags.length);
		assertTrue(!result.flags[0] && result.flags[1] && !result.flags[2] && !result.flags[3] && result.flags[4]);
	}

	@Test
	void testV1BackwardCompatibility()
	{
		V1CompatTest source = new V1CompatTest();
		source.x = 88;
		source.s = "Backward compat string";
		source.f = 2.718f;

		byte[] v1Payload = writeV1Bytes(source);

		CRBinDeserializer deserializer = new CRBinDeserializer();
		deserializer.prepareForRead(ByteBuffer.wrap(v1Payload));

		V1CompatTest result = new V1CompatTest();
		result.read(deserializer);

		assertEquals(88, result.x);
		assertEquals("Backward compat string", result.s);
		assertEquals(2.718f, result.f, 0.0001f);
	}

	@Test
	void testForwardCompatibilityV3Rejection()
	{
		byte[] v3Bytes = new byte[] { 'C', 'R', 'B', 3, 0, 0, 0, 0 };
		CRBinDeserializer deserializer = new CRBinDeserializer();
		assertThrows(UnsupportedVersionException.class, () -> {
			deserializer.prepareForRead(ByteBuffer.wrap(v3Bytes));
		});
	}

	@Test
	void testSizeDifferenceV1VsV2()
	{
		SizeComparisonTest obj = new SizeComparisonTest();
		byte[] v1Bytes = writeV1BytesForSizeTest(obj);

		CRBinSerializer v2Serial = new CRBinSerializer();
		obj.write(v2Serial);
		byte[] v2Bytes = v2Serial.toBytes();

		System.out.println("V1 Payload Size: " + v1Bytes.length + " bytes");
		System.out.println("V2 Payload Size: " + v2Bytes.length + " bytes");

		assertTrue(v2Bytes.length < v1Bytes.length,
			"V2 size (" + v2Bytes.length + " bytes) should be smaller than V1 size (" + v1Bytes.length + " bytes)");
	}
}
