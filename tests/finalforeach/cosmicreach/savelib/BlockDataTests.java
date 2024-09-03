package finalforeach.cosmicreach.savelib;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import finalforeach.cosmicreach.savelib.blockdata.BlockDataCompactor;
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
	LayeredBlockData<TestBlockValue> blockData;
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
	    		BlockBitLayer.class, BlockByteLayer.class, 
	    		BlockHalfNibbleLayer.class, BlockNibbleLayer.class,
	    		BlockByteLayer.class,
	    		BlockShortLayer.class, BlockSingleLayer.class);
	}
	
    @ParameterizedTest
	@MethodSource("getLayerClasses")
    void testHasValue(Class<?> layerClazzParam) throws Exception 
    {
		@SuppressWarnings("unchecked")
		var layerClazz = (Class<IBlockLayer<TestBlockValue>>)layerClazzParam;
		blockData = new LayeredBlockData<>();
    	for(int i = 0; i < 16; i++) 
    	{
    		var layer = createLayer(layerClazz, blockData, i, valueA);
    		blockData.setLayer(i, layer);
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
		for(int i = 3; i < 20; i++) 
		{
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
			testForNValues(i, layerClazz);
	    	blockData = (LayeredBlockData<TestBlockValue>) BlockDataCompactor.compact(blockData);
	    	
	    	verifySet();
		}
    }
    
    public void testForNValues(int n, Class<IBlockLayer<TestBlockValue>> layerClazz) throws Exception 
    {
		blockData = new LayeredBlockData<>();
    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		var layer = createLayer(layerClazz, blockData, i, valueA);
    		blockData.setLayer(i, layer);
    	}
    	
    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		for(int j = 0; j < CHUNK_WIDTH; j++) 
        	{
    			for(int k = 0; k < CHUNK_WIDTH; k++) 
    	    	{
    				assertEquals(valueA, blockData.getBlockValue(i, j, k));
    				var value = valueA;
    				
    	    		if(rand.nextBoolean())
    	    		{
    	    			value = blockValues[rand.nextInt(0, n)];
    	    		}

    				set[i][j][k] = value;
	    			blockData.setBlockValue(value, i, j, k);
    				assertEquals(value, blockData.getBlockValue(i, j, k));
    				
    	    	}	
        	}	
    	}
    	    	
    	verifySet();
    }
    
    public void verifySet() 
    {
        assertTrue(blockData.hasValueInPalette(valueA));
        assertFalse(blockData.hasValueInPalette(valueNeverUsed));        

    	for(int i = 0; i < CHUNK_WIDTH; i++) 
    	{
    		for(int j = 0; j < CHUNK_WIDTH; j++) 
        	{
    			for(int k = 0; k < CHUNK_WIDTH; k++) 
    	    	{
    				var value = set[i][j][k];
    				assertTrue(blockData.hasValueInPalette(value));
    				assertEquals(value, blockData.getBlockValue(i, j, k));
    	    	}	
        	}	
    	}
    }
    
}

