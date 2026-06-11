package finalforeach.cosmicreach.savelib.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.SkylightDataNibbleLayer;

public class SkylightNibbleTests
{

	@Test
	void testConstructorInitialization()
	{
		byte lightLevel = 15;
		SkylightDataNibbleLayer layer = new SkylightDataNibbleLayer(lightLevel);

		// Verify that every block in the layer has the expected light level.
		// The CHUNK_WIDTH is typically 16.
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				assertEquals(lightLevel, layer.getSkyLight(x, z), "Light level mismatch at (" + x + ", " + z + ")");
			}
		}
	}

	@Test
	void testSetSkyLight()
	{
		byte initialLevel = 0;
		SkylightDataNibbleLayer layer = new SkylightDataNibbleLayer(initialLevel);

		// Set an even X
		layer.setSkyLight(null, 10, 2, 0, 4);
		assertEquals(10, layer.getSkyLight(2, 4));
		assertEquals(0, layer.getSkyLight(3, 4)); // Adjacent odd X should be unaffected

		// Set an odd X
		layer.setSkyLight(null, 5, 3, 0, 4);
		assertEquals(5, layer.getSkyLight(3, 4));
		assertEquals(10, layer.getSkyLight(2, 4)); // Adjacent even X should be preserved
	}

	@Test
	void testBoundaryValues()
	{
		SkylightDataNibbleLayer layer = new SkylightDataNibbleLayer((byte) 0);

		// Test max value 15
		layer.setSkyLight(null, 15, 0, 0, 0);
		assertEquals(15, layer.getSkyLight(0, 0));

		// Test min value 0
		layer.setSkyLight(null, 0, 1, 0, 0);
		assertEquals(0, layer.getSkyLight(1, 0));

		// Ensure they didn't bleed into each other
		assertEquals(15, layer.getSkyLight(0, 0));
	}
}
