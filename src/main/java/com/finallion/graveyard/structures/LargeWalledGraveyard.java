package com.finallion.graveyard.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class LargeWalledGraveyard extends TGBaseStructure {

    public LargeWalledGraveyard(Codec<DefaultFeatureConfig> codec) {
        super(codec, "large_walled_graveyard", 15, 0);
    }


}