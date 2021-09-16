package com.finallion.graveyard.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;


public class LargeBirchTree extends TGBaseStructure {
    public LargeBirchTree(Codec<DefaultFeatureConfig> codec) {
        super(codec, "large_birch_tree", 1, 0);
    }
}
