package com.finallion.graveyard.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class HauntedHouse extends TGBaseStructure {
    public HauntedHouse(Codec<DefaultFeatureConfig> codec) {
        super(codec, "haunted_house", 2, 0);
    }
}
