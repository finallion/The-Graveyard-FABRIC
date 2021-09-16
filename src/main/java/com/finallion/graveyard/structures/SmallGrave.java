package com.finallion.graveyard.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;


public class SmallGrave extends TGBaseStructure {

    public SmallGrave(Codec<DefaultFeatureConfig> codec) {
        super(codec, "small_grave", 1, 1);
    }
}