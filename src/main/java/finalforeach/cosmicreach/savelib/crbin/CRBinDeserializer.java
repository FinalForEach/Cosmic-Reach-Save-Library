package finalforeach.cosmicreach.savelib.crbin;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

import finalforeach.cosmicreach.savelib.utils.ByteArrayUtils;
import finalforeach.cosmicreach.savelib.utils.DynamicArrays;
import finalforeach.cosmicreach.savelib.utils.IDynamicArray;
import finalforeach.cosmicreach.savelib.utils.IObjectFloatMap;
import finalforeach.cosmicreach.savelib.utils.IObjectIntMap;
import finalforeach.cosmicreach.savelib.utils.IObjectLongMap;
import finalforeach.cosmicreach.savelib.utils.IObjectMap;
import finalforeach.cosmicreach.savelib.utils.ObjectMaps;

public class CRBinDeserializer
{
	private static final IObjectMap<Class<?>, BiFunction<String, CRBinDeserializer, ?>> 
		defaultClassDeserializers = ObjectMaps.getNew();
	private IObjectMap<Class<?>, BiFunction<String, CRBinDeserializer, ?>> classDeserializers 
	= defaultClassDeserializers;

	private CRBinSchema schema;
	private IDynamicArray<CRBinSchema> altSchemas;
	private IDynamicArray<String> strings;
	private IObjectIntMap<String> intValues = ObjectMaps.getNewIntMap();
	private IObjectLongMap<String> longValues = ObjectMaps.getNewLongMap();
	private IObjectFloatMap<String> floatValues = ObjectMaps.getNewFloatMap();
	private IObjectMap<String, Double> doubleValues = ObjectMaps.getNew();
	private IObjectMap<String, Object> objValues = ObjectMaps.getNew();


	public static CRBinDeserializer getNew() 
	{
		return new CRBinDeserializer();
	}

	public static CRBinDeserializer fromBase64(String base64) 
	{
		CRBinDeserializer deserial = CRBinDeserializer.getNew();
		var byteBuf = ByteBuffer.wrap(Base64.getDecoder().decode(base64));
		deserial.prepareForRead(byteBuf);
		return deserial;
	}

	
	public static boolean isThereDefaultClassDeserializer(Class<?> clazz) 
	{
		return defaultClassDeserializers.containsKey(clazz);
	}

	public static <T> void registerDefaultClassDeserializer(Class<T> clazz, BiFunction<String, CRBinDeserializer, T> func) 
	{
		synchronized (defaultClassDeserializers) 
		{
			if(isThereDefaultClassDeserializer(clazz)) 
			{
				throw new RuntimeException("Cannot register a default class deserializer twice!");
			}
			defaultClassDeserializers.put(clazz, func);	
		}
	}

	public <T> void registerClassDeserializer(Class<?> clazz, BiFunction<String, CRBinDeserializer, T> func) 
	{
		if(classDeserializers == defaultClassDeserializers) 
		{
			classDeserializers = ObjectMaps.getNew(defaultClassDeserializers);
		}
		classDeserializers.put(clazz, func);
	}

	private void readSchema(CRBinSchema schema, ByteBuffer bytes)
	{
		boolean validSchema = false;
		schemaRead:
			while(bytes.hasRemaining()) 
			{
				byte b = bytes.get();
				var stype = SchemaType.get(b);

				if(stype == SchemaType.SCHEMA_END) 
				{
					validSchema = true;
					break schemaRead;
				}

				String name = ByteArrayUtils.readString(bytes);
				schema.add(name, stype);
			}
		if(!validSchema) 
		{
			throw new RuntimeException("Invalid schema");
		}
	}
	private CRBinDeserializer readObj(ByteBuffer bytes) 
	{
		int altSchema = ByteArrayUtils.readInt(bytes);
		if(altSchema == -1) 
		{
			return null;
		}
		var alt = altSchemas.get(altSchema);
		CRBinDeserializer subDeserial = CRBinDeserializer.getNew();
		subDeserial.classDeserializers = classDeserializers;
		subDeserial.altSchemas = altSchemas;
		subDeserial.strings = strings;
		subDeserial.readDataFromSchema(alt, bytes);
		return subDeserial;
	}
	private void readDataFromSchema(CRBinSchema schema, ByteBuffer bytes) 
	{
		// For each item in the schema,
		// the schema type tells how to interpret the following byte
		// into data which is mapped to the name.
		for(var item : schema.getSchema()) 
		{
			int length;
			String name = item.name();
			switch(item.type()) 
			{
			case BOOLEAN:
			case BYTE:
				intValues.put(name, ByteArrayUtils.readByte(bytes));
				break;
			case DOUBLE:
				doubleValues.put(name, ByteArrayUtils.readDouble(bytes));
				break;
			case FLOAT:
				floatValues.put(name, ByteArrayUtils.readFloat(bytes));
				break;
			case INT:
				intValues.put(name, ByteArrayUtils.readInt(bytes));
				break;
			case LONG:
				longValues.put(name, ByteArrayUtils.readLong(bytes));
				break;
			case SHORT:
				intValues.put(name, ByteArrayUtils.readShort(bytes));
				break;
			case STRING:
				int stringId = ByteArrayUtils.readInt(bytes);
				intValues.put(name, stringId);
				break;
			case OBJ:
				objValues.put(name, readObj(bytes));
				break;
			case OBJ_ARRAY:
				length = ByteArrayUtils.readInt(bytes);
				CRBinDeserializer[] subDeserial = new CRBinDeserializer[length];
				for(int i = 0; i < length; i++) 
				{
					subDeserial[i] = readObj(bytes);
				}
				objValues.put(name, subDeserial);
				break;
			case STRING_ARRAY:
				readArray(bytes, name, len -> new String[len],
						(arr, i) -> {arr[i] = strings.get(ByteArrayUtils.readInt(bytes));
						});
				break;
			case BOOLEAN_ARRAY:
				readArray(bytes, name, len -> new boolean[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readByte(bytes) == 1;
						});
				break;
			case BYTE_ARRAY:
				readArray(bytes, name, len -> new byte[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readByte(bytes);
						});
				break;
			case DOUBLE_ARRAY:
				readArray(bytes, name, len -> new double[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readDouble(bytes);
						});
				break;
			case FLOAT_ARRAY:
				readArray(bytes, name, len -> new float[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readFloat(bytes);
						});
				break;
			case INT_ARRAY:
				readArray(bytes, name, len -> new int[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readInt(bytes);
						});
				break;
			case LONG_ARRAY:
				readArray(bytes, name, len -> new long[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readLong(bytes);
						});
				break;
			case SHORT_ARRAY:
				readArray(bytes, name, len -> new short[len],
						(arr, i) -> {arr[i] = ByteArrayUtils.readShort(bytes);
						});
				break;
			default:
				break;

			}
		}
	}
	private <T> void readArray(ByteBuffer bytes, String name,
			IntFunction<T> arrCreator, 
			ObjIntConsumer<T> perElement) 
	{
		int length = ByteArrayUtils.readInt(bytes);
		if(length == -1) 
		{
			objValues.put(name, null);
		}else 
		{
			T arr = arrCreator.apply(length);
			for(int i = 0; i < length; i++) 
			{
				perElement.accept(arr, i);
			}
			objValues.put(name, arr);
		}
	}

	public String[] readStringArray(String name) 
	{
		return (String[]) objValues.get(name);
	}

	public boolean[] readBooleanArray(String name) 
	{
		return (boolean[]) objValues.get(name);
	}

	public byte[] readByteArray(String name) 
	{
		return (byte[]) objValues.get(name);
	}

	public short[] readShortArray(String name) 
	{
		return (short[]) objValues.get(name);
	}

	public int[] readIntArray(String name) 
	{
		return (int[]) objValues.get(name);
	}

	public long[] readLongArray(String name) 
	{
		return (long[]) objValues.get(name);
	}

	public float[] readFloatArray(String name) 
	{
		return (float[]) objValues.get(name);
	}

	public double[] readDoubleArray(String name) 
	{
		return (double[]) objValues.get(name);
	}

	public void prepareForRead(ByteBuffer bytes) 
	{
		schema = new CRBinSchema();
		altSchemas = DynamicArrays.getNew(CRBinSchema.class);
		// Read strings
		int numStrings = ByteArrayUtils.readInt(bytes);
		strings = DynamicArrays.getNew(String.class, numStrings);

		for(int i = 0; i < numStrings; i++) 
		{
			strings.add(ByteArrayUtils.readString(bytes));
		}

		// Now read schemas
		readSchema(schema, bytes);
		// Now read alt schemas
		int numAlt = ByteArrayUtils.readInt(bytes);
		for(int i = 0; i < numAlt; i++) 
		{
			var alt = new CRBinSchema();
			readSchema(alt, bytes);
			altSchemas.add(alt);
		}
		// Now read actual data:
		readDataFromSchema(schema, bytes);
	}

	public int readInt(String name, int defaultValue) 
	{
		return intValues.get(name, defaultValue);
	}

	public long readLong(String name, long defaultValue) 
	{
		return longValues.get(name, defaultValue);
	}

	public short readShort(String name, short defaultValue) 
	{
		return (short) intValues.get(name, defaultValue);
	}

	public float readFloat(String name, float defaultValue) 
	{
		return floatValues.get(name, defaultValue);
	}

	public boolean readBoolean(String name, boolean defaultValue) 
	{
		boolean b = intValues.get(name, defaultValue? 1 : 0) == 1;		
		return b;
	}
	public String readString(String name)
	{
		int stringId = intValues.get(name, -1);
		if(stringId==-1)return null;
		String s = strings.get(stringId);
		return s;
	}
	private <T extends ICRBinSerializable> T readObj(Class<T> elementType, CRBinDeserializer d)
	{
		T t = newInstance(elementType);
		t.read(d);
		return t;
	}

	@SuppressWarnings("unchecked")
	public <T> T readObj(String name, Class<T> elementType)
	{
		var func = classDeserializers.get(elementType);
		if(func!=null) 
		{
			return (T)func.apply(name, this);
		}
		var d = (CRBinDeserializer) objValues.get(name);
		if(d == null) 
		{
			return null;
		}
		if(ICRBinSerializable.class.isAssignableFrom(elementType))
		{
			return (T) readObj((Class<ICRBinSerializable>)elementType, d);	
		}
		throw new RuntimeException(elementType.getSimpleName() 
				+ " neither has an associated class deserializer, nor is derived from ICosmicReachBinarySerializable!");
	}

	public CRBinDeserializer[] readRawObjArray(String name) 
	{
		var backing = (CRBinDeserializer[]) objValues.get(name);
		return backing;
	}

	public <T extends ICRBinSerializable> IDynamicArray<T> readObjArray(String name, Class<T> elementType) 
	{
		var backing = (CRBinDeserializer[]) objValues.get(name);
		IDynamicArray<T> arr = DynamicArrays.getNew(elementType, backing.length);
		for(var d : backing) 
		{
			arr.add(d != null ? readObj(elementType, d) : null);
		}
		return arr;
	}

	private <T extends ICRBinSerializable> T newInstance(Class<T> type) 
	{
		try 
		{
			var c = type.getDeclaredConstructor();
			c.setAccessible(true);
			return (T) c.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}