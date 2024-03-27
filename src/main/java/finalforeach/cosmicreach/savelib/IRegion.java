package finalforeach.cosmicreach.savelib;

import finalforeach.cosmicreach.savelib.blocks.IBlockState;

public interface IRegion<B extends IBlockState>
{
	boolean isEmpty();
	int getRegionX();
	int getRegionY();
	int getRegionZ();
}