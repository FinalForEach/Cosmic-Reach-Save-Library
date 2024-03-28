package finalforeach.cosmicreach.savelib.lightdata.blocklight;

public interface IBlockLightData {

	short getBlockLight(int localX, int localY, int localZ);

	void setBlockLight(int lightLevelRed, int lightLevelGreen, int lightLevelBlue, int localX, int localY, int localZ);

	int getSaveFileConstant();

}
