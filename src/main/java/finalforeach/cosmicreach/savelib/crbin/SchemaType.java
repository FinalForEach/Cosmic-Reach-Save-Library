package finalforeach.cosmicreach.savelib.crbin;

public enum SchemaType
{
	SCHEMA_END(0),
	
	BYTE(1), SHORT(2), INT(3), 
	LONG(4), FLOAT(5), DOUBLE(6),
	BOOLEAN(7), STRING(9), OBJ(10),
	
	BYTE_ARRAY(11), SHORT_ARRAY(12), INT_ARRAY(13), 
	LONG_ARRAY(14), FLOAT_ARRAY(15), DOUBLE_ARRAY(16),
	BOOLEAN_ARRAY(17), STRING_ARRAY(18), OBJ_ARRAY(19);

	public static final SchemaType[] ALL_VALUES = SchemaType.values();
	public final byte byteId;
	
	SchemaType(int id)
	{
		this.byteId = (byte) id;
	}
	
	public static SchemaType get(byte b)
	{			
		for (SchemaType s : ALL_VALUES) 
		{
			if(s.byteId == b) 
			{
				return s;
			}
		}
		return null;
	}
}