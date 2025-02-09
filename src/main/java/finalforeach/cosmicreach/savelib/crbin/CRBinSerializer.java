package finalforeach.cosmicreach.savelib.crbin;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.IntConsumer;

import finalforeach.cosmicreach.savelib.IByteArray;
import finalforeach.cosmicreach.savelib.utils.ByteArrayUtils;
import finalforeach.cosmicreach.savelib.utils.DynamicArrays;
import finalforeach.cosmicreach.savelib.utils.IDynamicArray;
import finalforeach.cosmicreach.savelib.utils.TriConsumer;

public class CRBinSerializer
{	
	private static final HashMap<Class<?>, TriConsumer<CRBinSerializer, String, ?>> defaultClassSerializers = new HashMap<>();
	private HashMap<Class<?>, TriConsumer<CRBinSerializer, String, ?>> classSerializers = defaultClassSerializers;

	CRBinSchema schema = new CRBinSchema();
	IDynamicArray<CRBinSchema> altSchemas = DynamicArrays.getNew(CRBinSchema.class);
	IDynamicArray<String> strings = DynamicArrays.getNew(String.class);
	IByteArray bytes = DynamicArrays.getNewByteArray();

	public static CRBinSerializer getNew() 
	{
		return new CRBinSerializer();
	}
	
	public static boolean isThereDefaultClassSerializer(Class<?> clazz) 
	{
		return defaultClassSerializers.containsKey(clazz);
	}

	public static <T> void registerDefaultClassSerializer(Class<T> clazz, TriConsumer<CRBinSerializer, String, T> consumer) 
	{
		synchronized (defaultClassSerializers) 
		{
			if(isThereDefaultClassSerializer(clazz)) 
			{
				throw new RuntimeException("Cannot register a default class serializer twice!");
			}
			defaultClassSerializers.put(clazz, consumer);
		}
	}

	public <T> void registerClassSerializer(Class<T> clazz, TriConsumer<CRBinSerializer, String, T> consumer) 
	{
		if(classSerializers == defaultClassSerializers) 
		{
			classSerializers = new HashMap<>(defaultClassSerializers);
		}
		classSerializers.put(clazz, consumer);
	}
	
	public byte[] toBytes() 
	{
		return toByteArray().toArray();
	}

	public IByteArray toByteArray() 
	{		
		IByteArray bytesToWrite = DynamicArrays.getNewByteArray();
		// First write all common strings

		ByteArrayUtils.writeInt(bytesToWrite, strings.size());
		for(String s : strings) 
		{
			ByteArrayUtils.writeString(bytesToWrite, s);
		}

		// Then write the bytes of the current schema

		bytesToWrite.addAll(schema.getBytes());

		// Then write the bytes of the other schemas

		ByteArrayUtils.writeInt(bytesToWrite, altSchemas.size());
		for(var s : altSchemas) 
		{
			bytesToWrite.addAll(s.getBytes());	
		}

		// Then write the binary data
		bytesToWrite.addAll(bytes);

		return bytesToWrite;
	}

	public String toBase64() 
	{
		return Base64.getEncoder().encodeToString(toBytes());
	}


	private void writeNullArray(String name, SchemaType type) 
	{
		schema.add(name, type);
		ByteArrayUtils.writeInt(bytes, -1);
	}

	public void writeArray(String name, SchemaType type, int arrayLength, IntConsumer forEach) 
	{
		schema.add(name, type);
		ByteArrayUtils.writeInt(bytes, arrayLength);
		for(int i = 0; i < arrayLength; i++) 
		{
			forEach.accept(i);
		}
	}
	
	public void writeArray(String name, SchemaType type, int offset, int arrayLength, IntConsumer forEach) 
	{
		schema.add(name, type);
		ByteArrayUtils.writeInt(bytes, arrayLength);
		for(int i = offset; i < arrayLength; i++) 
		{
			forEach.accept(i);
		}
	}

	public void writeByteArray(String name, byte[] array) 
	{
		writeArray(name, SchemaType.BYTE_ARRAY, array.length, (i) -> writeByte(null, array[i]));
	}

	public void writeBooleanArray(String name, boolean[] array) 
	{
		writeArray(name, SchemaType.BOOLEAN_ARRAY, array.length, (i) -> writeBoolean(null, array[i]));
	}

	public void writeShortArray(String name, short[] array) 
	{
		writeArray(name, SchemaType.SHORT_ARRAY, array.length, (i) -> writeShort(null, array[i]));
	}

	public void writeIntArray(String name, int[] array) 
	{
		writeArray(name, SchemaType.INT_ARRAY, array.length, (i) -> writeInt(null, array[i]));
	}

	public void writeLongArray(String name, long[] array) 
	{
		writeArray(name, SchemaType.LONG_ARRAY, array.length, (i) -> writeLong(null, array[i]));
	}

	public void writeFloatArray(String name, float[] array) 
	{
		writeArray(name, SchemaType.FLOAT_ARRAY, array.length, (i) -> writeFloat(null, array[i]));
	}

	public void writeDoubleArray(String name, double[] array) 
	{
		writeArray(name, SchemaType.DOUBLE_ARRAY, array.length, (i) -> writeDouble(null, array[i]));
	}

	public void writeStringArray(String name, String[] array) 
	{
		writeArray(name, SchemaType.STRING_ARRAY, array.length, (i) -> writeString(null, array[i]));
	}
	public void writeStringArray(String name, String[] array, int length) 
	{
		writeArray(name, SchemaType.STRING_ARRAY, length, (i) -> writeString(null, array[i]));
	}
	

	public void writeNullByteArray(String name) 
	{
		writeNullArray(name, SchemaType.BYTE_ARRAY);
	}

	public void writeNullBooleanArray(String name) 
	{
		writeNullArray(name, SchemaType.BOOLEAN_ARRAY);
	}

	public void writeNullShortArray(String name) 
	{
		writeNullArray(name, SchemaType.SHORT_ARRAY);
	}

	public void writeNullIntArray(String name) 
	{
		writeNullArray(name, SchemaType.INT_ARRAY);
	}

	public void writeNullLongArray(String name) 
	{
		writeNullArray(name, SchemaType.LONG_ARRAY);
	}

	public void writeNullFloatArray(String name) 
	{
		writeNullArray(name, SchemaType.FLOAT_ARRAY);
	}

	public void writeNullDoubleArray(String name) 
	{
		writeNullArray(name, SchemaType.DOUBLE_ARRAY);
	}

	public void writeNullStringArray(String name) 
	{
		writeNullArray(name, SchemaType.STRING_ARRAY);
	}

	public void writeNullObjectArray(String name) 
	{
		writeNullArray(name, SchemaType.OBJ_ARRAY);
	}

	public <T extends ICRBinSerializable> void writeObjArray(String name, T[] array)
	{
		writeArray(name, SchemaType.OBJ_ARRAY, array.length, (i) -> writeObj(null, array[i]));
	}
	
	public <T extends ICRBinSerializable> void writeObjArray(String name, T[] array, int offset, int length)
	{
		writeArray(name, SchemaType.OBJ_ARRAY, offset, length, (i) -> writeObj(null, array[i]));
	}

	public void writeBoolean(String name, boolean bool) 
	{
		schema.add(name, SchemaType.BOOLEAN);
		ByteArrayUtils.writeByte(bytes, bool ? 1 : 0);
	}

	public void writeByte(String name, byte i) 
	{
		schema.add(name, SchemaType.BYTE);
		ByteArrayUtils.writeByte(bytes, i);
	}

	public void writeInt(String name, int i) 
	{
		schema.add(name, SchemaType.INT);
		ByteArrayUtils.writeInt(bytes, i);
	}

	public void writeShort(String name, short s) 
	{
		schema.add(name, SchemaType.SHORT);
		ByteArrayUtils.writeShort(bytes, s);
	}

	public void writeLong(String name, long l) 
	{
		schema.add(name, SchemaType.LONG);
		ByteArrayUtils.writeLong(bytes, l);
	}

	public void writeFloat(String name, float f) 
	{
		schema.add(name, SchemaType.FLOAT);
		ByteArrayUtils.writeFloat(bytes, f);
	}
	public void writeDouble(String name, double d) 
	{
		schema.add(name, SchemaType.DOUBLE);
		ByteArrayUtils.writeDouble(bytes, d);
	}

	public void writeString(String name, String value) 
	{
		schema.add(name, SchemaType.STRING);

		// Reference the string by ID rather than serializing it directly 
		int stringId = strings.indexOf(value, false);
		if(stringId == -1) 
		{
			// It's not stored yet, so add it to the list of strings,
			// so that there is a valid ID to reference
			stringId = strings.size();
			strings.add(value);
		}

		// Write the string's ID
		ByteArrayUtils.writeInt(bytes, stringId);

	}

	@SuppressWarnings("unchecked")
	public <T> void writeObj(Class<T> elementType, String name, Object item) 
	{
		var consumer = (TriConsumer<CRBinSerializer, String, T>)classSerializers.get(elementType);
		if(consumer != null) 
		{
			consumer.accept(this, name, (T) item);
			return;
		}
		if(ICRBinSerializable.class.isAssignableFrom(elementType))
		{
			writeObj(name, (ICRBinSerializable) item);
			return;
		}
		throw new RuntimeException(elementType.getSimpleName() 
				+ " neither has an associated class serializer, nor is derived from ICosmicReachBinarySerializable!");
	}
	
	public <T extends ICRBinSerializable> void writeObj(String name, T item) 
	{
		var oldSchema = schema;
		var oldBytes = bytes;

		bytes = DynamicArrays.getNewByteArray();

		if(name != null) 
		{
			oldSchema.add(name, SchemaType.OBJ);
		}

		if(item != null) 
		{
			schema = new CRBinSchema();
			item.write(this);
			if(!altSchemas.contains(schema, false)) 
			{
				altSchemas.add(schema);
			}

			for(int i = 0; i < altSchemas.size(); i++) 
			{
				var s = altSchemas.get(i);
				if(s.equals(schema)) 
				{
					ByteArrayUtils.writeInt(oldBytes, i);
					break;
				}
			}
		}else 
		{
			ByteArrayUtils.writeInt(oldBytes, -1);
		}

		oldBytes.addAll(bytes);

		schema = oldSchema;
		bytes = oldBytes;
	}

	public void autoWriteDifference(Object prototype, Object obj) 
	{
		Class<?> prototypeClazz = prototype.getClass();
		Class<?> clazz = obj.getClass();
		
		if(!prototypeClazz.isInstance(obj)) 
		{
			throw new RuntimeException("Object is not an instance of the class or instance of a subclass of the prototype's class.");
		}
		
		while(clazz != Object.class) 
		{
			var fields = clazz.getDeclaredFields();
			
			for(Field field : fields) 
			{
				if(field.isAnnotationPresent(CRBSerialized.class)) 
				{
					field.setAccessible(true);
					try 
					{
						String name = field.getName();
						var type = field.getType();
						Field protoField;
						try
						{
							if(prototypeClazz == clazz) 
							{
								protoField = field; 
							}
							protoField = prototypeClazz.getDeclaredField(name);
							protoField.setAccessible(true);
						} catch (NoSuchFieldException | SecurityException e)
						{
							protoField = null;
						}
						if(type == int.class) 
						{
							var objVal = field.getInt(obj);
							if(protoField != null) 
							{
								var protoVal = protoField.getInt(prototype);
								if(protoVal == objVal) 
								{
									continue;
								}
							}
							writeInt(name, objVal);
						}
						else if(type == float.class) 
						{
							var objVal = field.getFloat(obj);
							if(protoField != null) 
							{
								var protoVal = protoField.getFloat(prototype);
								if(protoVal == objVal) 
								{
									continue;
								}
							}
							writeFloat(name, objVal);
						}else if(type == boolean.class) 
						{
							var objVal = field.getBoolean(obj);
							if(protoField != null) 
							{
								var protoVal = protoField.getBoolean(prototype);
								if(protoVal == objVal) 
								{
									continue;
								}
							}
							writeBoolean(name, objVal);
						}
						else if(!type.isPrimitive())
						{
							var objVal = field.get(obj);
							if(protoField != null) 
							{
								var protoVal = protoField.get(prototype);
								if(Objects.equals(objVal, protoVal)) 
								{
									continue;
								}
							}
							if(type == String.class) 
							{
								writeString(name, (String)objVal);
							}else 
							{
								writeObj(type, name, objVal);	
							}
						}else 
						{
							throw new RuntimeException("Not yet implemented for type: " + type.getSimpleName());
						}
					} catch (IllegalArgumentException | IllegalAccessException e) 
					{
						System.err.println("Write error for " + obj.getClass().getSimpleName());
						e.printStackTrace();
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
	
	public void autoWrite(Object obj)
	{
		Class<?> clazz = obj.getClass();
		while(clazz != Object.class) 
		{
			var fields = clazz.getDeclaredFields();
			
			for(Field field : fields) 
			{
				if(field.isAnnotationPresent(CRBSerialized.class)) 
				{
					field.setAccessible(true);
					try 
					{
						String name = field.getName();
						var type = field.getType();
						if(type == int.class) 
						{
							writeInt(name, field.getInt(obj));
						}
						else if(type == float.class) 
						{
							writeFloat(name, field.getFloat(obj));
						}else if(type == boolean.class) 
						{
							writeBoolean(name, field.getBoolean(obj));
						}else if(type == String.class) 
						{
							writeString(name, (String) field.get(obj));
						}
						else if(!type.isPrimitive())
						{
							writeObj(type, name, field.get(obj));
						}else 
						{
							throw new RuntimeException("Not yet implemented for type: " + type.getSimpleName());
						}
					} catch (IllegalArgumentException | IllegalAccessException e) 
					{
						System.err.println("Write error for " + obj.getClass().getSimpleName());
						e.printStackTrace();
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		
	}
}
