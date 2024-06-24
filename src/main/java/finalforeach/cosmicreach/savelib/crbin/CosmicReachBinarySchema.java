package finalforeach.cosmicreach.savelib.crbin;

import java.util.Objects;

import finalforeach.cosmicreach.savelib.utils.DynamicArrays;
import finalforeach.cosmicreach.savelib.utils.IDynamicArray;
import finalforeach.cosmicreach.savelib.utils.RawByteArrayUtils;

public class CosmicReachBinarySchema
{
	public record SchemaItem(String name, SchemaType type) {}

	private IDynamicArray<SchemaItem> schema = DynamicArrays.newDynamicArray();

	public void add(String name, SchemaType type)
	{
		if(name==null) 
		{
			return;
		}
		
		for(int i = 0; i < schema.size(); i++)
		{
			var s = schema.get(i);
			if(s.name.equalsIgnoreCase(name))
			{
				throw new RuntimeException("Duplicate name in schema: " + name);
			}
		}
		var item = new SchemaItem(name, type);
		schema.add(item);		
	}
	
	public Iterable<SchemaItem> getSchema()
	{
		return schema;
	}

	public byte[] getBytes()
	{
		int byteCount = 0;

		for(var item : schema)
		{
			byteCount++; // For the schema item type
			byteCount+=Integer.BYTES; // For the string length or null specifier
			if(item.name!=null) 
			{
				byteCount+=item.name.getBytes().length; // the string's bytes
			}
		}
		byteCount++; // For schema end
		
		byte[] bytes = new byte[byteCount];
		int byteIdx = 0;
		for(var item : schema)
		{
			byteIdx = RawByteArrayUtils.writeByte(byteIdx, bytes, item.type.byteId);
			byteIdx = RawByteArrayUtils.writeString(byteIdx, bytes, item.name);	
		}
		
		byteIdx = RawByteArrayUtils.writeByte(byteIdx, bytes, SchemaType.SCHEMA_END.byteId);
				
		return bytes;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(schema);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		CosmicReachBinarySchema other = (CosmicReachBinarySchema) obj;
		return Objects.equals(schema, other.schema);
	}
}