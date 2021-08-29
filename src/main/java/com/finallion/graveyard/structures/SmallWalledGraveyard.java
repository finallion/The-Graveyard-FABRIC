package com.finallion.graveyard.structures;



import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;


public class SmallWalledGraveyard extends TGBaseStructure {

    public SmallWalledGraveyard(Codec<DefaultFeatureConfig> codec) {
        super(codec, "small_walled_graveyard", 1.5, 0);
    }
}
