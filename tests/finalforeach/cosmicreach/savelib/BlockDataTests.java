package finalforeach.cosmicreach.savelib;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
}
public class BlockDataTests 
{
	TestBlockValue valueA = new TestBlockValue();
	TestBlockValue valueNeverUsed = new TestBlockValue();
	LayeredBlockData<TestBlockValue> blockData;
	
	@BeforeEach
	void setup() 
	{
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
    		IBlockLayer<TestBlockValue> layer = createLayer(layerClazz, blockData, i, valueA);
    		blockData.setLayer(i, layer);
    	}
        assertTrue(blockData.hasValueInPalette(valueA));
        assertFalse(blockData.hasValueInPalette(valueNeverUsed));
    }

}

