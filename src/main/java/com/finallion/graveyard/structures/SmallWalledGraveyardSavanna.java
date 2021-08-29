package com.finallion.graveyard.structures;



import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;


public class SmallWalledGraveyardSavanna extends TGBaseStructure {

    public SmallWalledGraveyardSavanna(Codec<DefaultFeatureConfig> codec) {
        super(codec, "small_walled_graveyard_savanna", 1.5, 0);
    }
}
