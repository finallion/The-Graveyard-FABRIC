package com.lion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.features.BoulderFeature;
import com.finallion.graveyard.world.features.BoulderFeatureConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;



public class TGFeatures {

    public static final BoulderFeature BOULDER_FEATURE = new BoulderFeature(BoulderFeatureConfig.CODEC);

    public static void register() {
        Registry.register(Registries.FEATURE, new Identifier(TheGraveyard.MOD_ID, "boulder"), BOULDER_FEATURE);
    }
}
