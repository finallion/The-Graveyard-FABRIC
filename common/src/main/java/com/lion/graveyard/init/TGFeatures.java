package com.lion.graveyard.init;


import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.world.features.BoulderFeature;
import com.lion.graveyard.world.features.BoulderFeatureConfig;

public class TGFeatures {

    public static final BoulderFeature BOULDER_FEATURE = new BoulderFeature(BoulderFeatureConfig.CODEC);

    public static void init() {
        RegistryHelper.registerFeature("boulder", BOULDER_FEATURE);
    }
}
