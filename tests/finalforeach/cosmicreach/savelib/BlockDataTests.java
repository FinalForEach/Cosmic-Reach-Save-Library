package finalforeach.cosmicreach.savelib;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import finalforeach.cosmicreach.savelib.blockdata.BlockDataCompactor;
import finalforeach.cosmicreach.savelib.blockdata.IBlockData;
import finalforeach.cosmicreach.savelib.blockdata.LayeredBlockData;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockBitLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockByteLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockHalfNibbleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockNibbleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockShortLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockSingleLayer;
import finalforeach.cosmicreach.savelib.blockdata.layers.IBlockLayer;
class TestBlockValue
{
	int id;
	TestBlockValue(int id)
	{
		this.id = id;
	}
	@Override
	public String toString() {
		return "TestBlockValue<"+id+">";
	}
}
public class BlockDataTests 
{
	public static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;
	
	TestBlockValue[][][] set = new TestBlockValue[CHUNK_WIDTH][CHUNK_WIDTH][CHUNK_WIDTH];
	Random rand = new Random();
	static TestBlockValue[] blockValues = new TestBlockValue[4096];
	static TestBlockValue valueA;
	TestBlockValue valueNeverUsed = new TestBlockValue(-1);
	IBlockData<TestBlockValue> blockData;
	@BeforeAll
	static void setupBeforeAll()
	{
		for(int i = 0; i < blockValues.length; i++) 
		{
			blockValues[i] = new TestBlockValue(i);
		}
		valueA = blockValues[0];
	}
	
	@BeforeEach
	void setup() 
	{
    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		for(int j = 0; j < CHUNK_WIDTH; j++) 
        	{
    			for(int k = 0; k < CHUNK_WIDTH; k++) 
    	    	{
    				set[i][j][k] = valueA;    				
    	    	}	
        	}	
    	}
	}
	
	<T> IBlockLayer<T> createLayer(Class<IBlockLayer<T>> layerClazz, 
			LayeredBlockData<T> chunkData, int localY, T blockValue) throws Exception 
	{
		Constructor<IBlockLayer<T>> c = null;
		try
		{
			c = layerClazz.getDeclaredConstructor(LayeredBlockData.class, int.class, Object.class);		
			return c.newInstance(chunkData, localY, blockValue);	
		}catch(NoSuchMethodException ex)
		{
			c = layerClazz.getDeclaredConstructor(LayeredBlockData.class, Object.class);
			return c.newInstance(chunkData, blockValue);
		}
	}
	
	static Stream<Class<?>> getLayerClasses() 
	{
	    return Stream.of(
	    		BlockSingleLayer.class,
	    		BlockBitLayer.class, BlockHalfNibbleLayer.class, 
	    		BlockNibbleLayer.class, BlockByteLayer.class, 
	    		BlockShortLayer.class);
	}
	

    @Test
    void testHasUniqueSaveId() throws Exception 
    {
		Set<Integer> uniqueIds = new HashSet<Integer>();
    	for(var layerClazzParam : getLayerClasses().toArray())
    	{
    		@SuppressWarnings("unchecked")
    		var layerClazz = (Class<IBlockLayer<TestBlockValue>>)layerClazzParam;
    		var layered = new LayeredBlockData<TestBlockValue>();
    		blockData = layered;
    		
    		var layer = createLayer(layerClazz, layered, 0, valueA);
    		layered.setLayer(0, layer);
    		
    		int constant = layer.getSaveFileConstant(layered);
    		assertFalse(uniqueIds.contains(constant));
    		uniqueIds.add(constant);
    	}
    }
	
    @ParameterizedTest
	@MethodSource("getLayerClasses")
    void testHasValue(Class<?> layerClazzParam) throws Exception 
    {
		@SuppressWarnings("unchecked")
		var layerClazz = (Class<IBlockLayer<TestBlockValue>>)layerClazzParam;
		var layered = new LayeredBlockData<TestBlockValue>();
		blockData = layered;
    	for(int i = 0; i < 16; i++) 
    	{
    		var layer = createLayer(layerClazz, layered, i, valueA);
    		layered.setLayer(i, layer);
    	}
        assertTrue(blockData.hasValueInPalette(valueA));
        assertFalse(blockData.hasValueInPalette(valueNeverUsed));
    }

    @ParameterizedTest
	@MethodSource("getLayerClasses")
    void testFor2RandomBlocks(Class<?> layerClazzParam) throws Exception 
    {
		@SuppressWarnings("unchecked")
		var layerClazz = (Class<IBlockLayer<TestBlockValue>>)layerClazzParam;
		testForNValues(2, layerClazz);
    }

    @ParameterizedTest
	@MethodSource("getLayerClasses")
    void testForManyRandomBlocks(Class<?> layerClazzParam) throws Exception 
    {
		@SuppressWarnings("unchecked")
		var layerClazz = (Class<IBlockLayer<TestBlockValue>>)layerClazzParam;
		for(int i = 3; i < 512; i++) 
		{
			setup();
			testForNValues(i, layerClazz);	
		}
    }
    
    @ParameterizedTest
	@MethodSource("getLayerClasses")
    void testForCompactingManyRandomBlocks(Class<?> layerClazzParam) throws Exception 
    {
		@SuppressWarnings("unchecked")
		var layerClazz = (Class<IBlockLayer<TestBlockValue>>)layerClazzParam;
		for(int i = 3; i < 20; i++) 
		{
			setup();
			testForNValues(i, layerClazz);
	    	blockData = (LayeredBlockData<TestBlockValue>) BlockDataCompactor.compact(blockData);
	    	
	    	verifySet();
		}
    }
    
    public void testForNValues(int n, Class<IBlockLayer<TestBlockValue>> layerClazz) throws Exception 
    {
		var layered = new LayeredBlockData<TestBlockValue>();
		blockData = layered;
    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		var layer = createLayer(layerClazz, layered, i, valueA);
    		layered.setLayer(i, layer);
    	}
    	
    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		for(int j = 0; j < CHUNK_WIDTH; j++) 
        	{
    			for(int k = 0; k < CHUNK_WIDTH; k++) 
    	    	{
    				var preValue = blockData.getBlockValue(i, j, k);
	    			if(!valueA.equals(blockData.getBlockValue(i, j, k))) 
	    			{
	    				//oldLayer.setBlockValue(oldLayered, value, i, j, k);
	    				var q = blockData.getBlockValue(i, j, k);
	    				fail();
	    			}
    				assertEquals(valueA, preValue, "Failed preinit check at " + coords(i,j,k));
    				var value = valueA;
    				
    	    		if(rand.nextBoolean())
    	    		{
    	    			value = blockValues[rand.nextInt(0, n)];
    	    		}

    				set[i][j][k] = value;
    				
    				var oldLayered = (LayeredBlockData<TestBlockValue>)blockData;
    				var oldLayer = oldLayered.getLayer(j);
    				
    				blockData = blockData.setBlockValue(value, i, j, k);
	    			if(!value.equals(blockData.getBlockValue(i, j, k))) 
	    			{
	    				oldLayer.setBlockValue(oldLayered, value, i, j, k);
	    				fail();
	    			}
    				assertEquals(value, blockData.getBlockValue(i, j, k));
    				//verifySet("After setting " + coords(i, j, k));
    	    	}	
        	}	
    	}
    	    	
    	verifySet();
    }
    
    public void verifySet() 
    {
    	verifySet("");
    } 
    public void verifySet(String afterMessage) 
    {
        assertTrue(blockData.hasValueInPalette(valueA));
        assertFalse(blockData.hasValueInPalette(valueNeverUsed));        

    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		for(int j = 0; j < CHUNK_WIDTH; j++) 
        	{
    			for(int k = 0; k < CHUNK_WIDTH; k++) 
    	    	{
    				var expected = set[i][j][k];
    				assertTrue(blockData.hasValueInPalette(expected));
    				var actual = blockData.getBlockValue(i, j, k);
    				assertEquals(expected, actual, "Failed verifySet at " + coords(i,j,k) + " "+afterMessage);
    	    	}	
        	}	
    	}
    }
    
    public String coords(int x, int y, int z) 
    {
    	return "("+x+", "+y + ", " + z + ")";
    }
    
}

