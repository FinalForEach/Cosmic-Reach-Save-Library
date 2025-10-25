package finalforeach.cosmicreach.savelib.blockentities;

import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;

@FunctionalInterface
public interface IBlockEntityInstantiator {
    void instantiate(CRBinDeserializer blockEntityDeserializer);
}
