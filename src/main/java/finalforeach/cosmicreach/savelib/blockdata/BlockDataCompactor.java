package finalforeach.cosmicreach.savelib.blockdata;

import finalforeach.cosmicreach.savelib.ISavedChunk;
import finalforeach.cosmicreach.savelib.blockdata.layers.BlockSingleLayer;

public class BlockDataCompactor 
{
	public static <T> IBlockData<T> compact(IBlockData<T> blockData) 
	{
		if(blockData instanceof LayeredBlockData<T> layered) 
		{
			var allLayers = layered.getLayers();
			for(int yLevel = 0; yLevel < allLayers.length; yLevel++) 
			{
				var layer = allLayers[yLevel];
				if(layer instanceof BlockSingleLayer) 
				{
					continue; // Do nothing, already compact!
				}
				
				T layerBlockState = null;
				singleBlockCheck:
				for(int i = 0; i < ISavedChunk.CHUNK_WIDTH; i++) 
				{
					for(int k = 0; k < ISavedChunk.CHUNK_WIDTH; k++) 
					{
						T curBlockState = layer.getBlockValue(layered, i, k);
						if(layerBlockState==null) 
						{
							layerBlockState = curBlockState;
							continue;
						}else if(layerBlockState!=curBlockState) 
						{
							layerBlockState = null;
							// Cannot compact it, blocks are different in layer
							break singleBlockCheck;
						}
					}
				}
				
				if(layerBlockState!=null) 
				{
					// Replace the layer with the compact equivalent!
					var newLayer = new BlockSingleLayer<T>(layered, layerBlockState);
					layered.setLayer(yLevel, newLayer);
				}
			}
			
			// If only one block in palette, no need for layers
			if(layered.getPaletteSize()==1) 
			{
				var newChunkData = new SingleBlockData<T>();
				blockData = newChunkData.fill(layered.getBlockValueFromPaletteId(0));
			}
		}
		return blockData;
	}
}
