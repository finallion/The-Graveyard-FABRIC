package com.finallion.graveyard.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public record BoulderFeatureConfig(BlockState firstState, BlockState secondState, BlockState thirdState, BlockState forthState, int radius) implements FeatureConfig {

    public static final Codec<BoulderFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BlockState.CODEC.fieldOf("first_state").forGetter(BoulderFeatureConfig::firstState),
            BlockState.CODEC.fieldOf("second_state").forGetter(BoulderFeatureConfig::secondState),
            BlockState.CODEC.fieldOf("third_state").forGetter(BoulderFeatureConfig::thirdState),
            BlockState.CODEC.fieldOf("forth_state").forGetter(BoulderFeatureConfig::forthState),
            Codec.INT.fieldOf("radius").forGetter(BoulderFeatureConfig::radius)
    ).apply(instance, BoulderFeatureConfig::new));

}
