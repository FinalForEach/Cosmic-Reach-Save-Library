package finalforeach.cosmicreach.savelib.lightdata;

import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.BlockLightLayeredData;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.IBlockLightData;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.BlockLightMonoGreenNibbleLayer;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.BlockLightMonoNibbleLayer;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.BlockLightMonoRedNibbleLayer;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.BlockLightShortLayer;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.BlockLightSingleLayer;
import finalforeach.cosmicreach.savelib.lightdata.blocklight.layers.IBlockLightLayer;
import finalforeach.cosmicreach.savelib.lightdata.skylight.ISkylightData;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightLayeredData;
import finalforeach.cosmicreach.savelib.lightdata.skylight.SkylightSingleData;
import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.ISkylightDataLayer;
import finalforeach.cosmicreach.savelib.lightdata.skylight.layers.SkylightDataSingleLayer;

public class LightDataCompactor 
{
	public static final int CHUNK_WIDTH = ISavedChunk.CHUNK_WIDTH;

	public static ISkylightData compactSky(ISkylightData skyLightData)
	{
		if(skyLightData!=null && skyLightData instanceof SkylightLayeredData layered) 
		{
			boolean allLayersSame = true;
			int lastLayerLightLevel = -1;
			var allLayers = layered.getLayers();
			for(int yLevel = 0; yLevel < allLayers.length; yLevel++) 
			{
				ISkylightDataLayer layer = allLayers[yLevel];
				if(layer instanceof SkylightDataSingleLayer) 
				{
					continue;
				}
				int layerLightLevel = -1;
				singleLevelCheck:
					for(int i = 0; i < CHUNK_WIDTH; i++) 
					{
						for(int k = 0; k < CHUNK_WIDTH; k++) 
						{
							int curSkyLightLevel = layer.getSkyLight(i, k);
							if(layerLightLevel == -1) 
							{
								layerLightLevel = curSkyLightLevel;
								continue;
							}else if(layerLightLevel != curSkyLightLevel) 
							{
								layerLightLevel = -1;
								break singleLevelCheck;
							}
						}
					}
				if(layerLightLevel!=-1) 
				{
					var newLayer = SkylightDataSingleLayer.getForLightValue((byte)layerLightLevel);
					layered.setLayer(yLevel, newLayer);
					allLayersSame &= (lastLayerLightLevel == layerLightLevel || lastLayerLightLevel==-1);
					lastLayerLightLevel = layerLightLevel;
				}else 
				{
					allLayersSame = false;
				}	
			}

			if(allLayersSame && lastLayerLightLevel!=-1) 
			{
				skyLightData = SkylightSingleData.getForLightValue((byte)lastLayerLightLevel);
			}
		}
		return skyLightData;
	}

	public static IBlockLightData compactBlockLights(IBlockLightData blockLightData)
	{
		if(1==1)return blockLightData;
		if(blockLightData instanceof BlockLightLayeredData layered) 
		{
			var layers = layered.getLayers();
			for(int yLevel = 0; yLevel < CHUNK_WIDTH; yLevel++) 
			{
				var layer = layers[yLevel];
				if(layer instanceof BlockLightShortLayer shortLayer) 
				{
					int r = -1, g = -1, b = -1;
					compactCheck:
					for(int i = 0; i < CHUNK_WIDTH; i++) 
					{
						for(int k = 0; k < CHUNK_WIDTH; k++) 
						{
							short light = shortLayer.getBlockLight(i, k);
							int sr = (byte)((light & 0xF00) >> 8);
							int sg = (byte)((light & 0x0F0) >> 4);
							int sb = (byte)((light & 0x00F));
							if(r==-1)r = sr; else if(r!=sr) r = -2;
							if(g==-1)g = sg; else if(g!=sg) g = -2;
							if(b==-1)b = sb; else if(b!=sb) b = -2;
							
							if(r==-2 && g == -2 && b==-2) break compactCheck;							
						}
					}
					int diffCount = r==-2 ? 1 : 0;
					diffCount += g==-2 ? 1 : 0;
					diffCount += b==-2 ? 1 : 0;
					if(diffCount == 0) 
					{
						layers[yLevel] = new BlockLightSingleLayer(layered, yLevel, r, g, b);
					}
					else if(diffCount == 1) 
					{
						BlockLightMonoNibbleLayer newLayer;
						int n;
						if(r == -2) 
						{
							short baseLight = (short)((g << 4) + b);
							newLayer = new BlockLightMonoRedNibbleLayer(layered, yLevel, baseLight);
							n = r;
						}else if(g == -2) 
						{
							short baseLight = (short)((r << 8) + b);
							newLayer = new BlockLightMonoGreenNibbleLayer(layered, yLevel, baseLight);
							n = g;	
						}else 
						{
							short baseLight = (short)((r << 8) + (g << 4));
							newLayer = new BlockLightMonoRedNibbleLayer(layered, yLevel, baseLight);
							n = b;		
						}
						for(int i = 0; i < CHUNK_WIDTH; i++) 
						{
							for(int k = 0; k < CHUNK_WIDTH; k++) 
							{
								newLayer.setNibbleLight(n, i, k);			
							}
						}
						layers[yLevel] = newLayer;
					}
				}
			}
			
		}

		return blockLightData;
	}

}
