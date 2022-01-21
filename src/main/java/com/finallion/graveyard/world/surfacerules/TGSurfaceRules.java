package com.finallion.graveyard.world.surfacerules;

import com.finallion.graveyard.init.TGBiomes;
import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;

public class TGSurfaceRules {
    private static final MaterialRules.MaterialRule TG_MOSS_BLOCK = block(TGBlocks.TG_MOSS_BLOCK);
    private static final MaterialRules.MaterialRule MOSS_BLOCK = block(Blocks.MOSS_BLOCK);
    private static final MaterialRules.MaterialRule ROOTED_DIRT = block(Blocks.ROOTED_DIRT);
    private static final MaterialRules.MaterialRule COARSE_DIRT = block(Blocks.COARSE_DIRT);
    private static final MaterialRules.MaterialRule DIRT = block(Blocks.DIRT);
    private static final MaterialRules.MaterialRule SOUL_SAND = block(Blocks.SOUL_SAND);
    private static final MaterialRules.MaterialRule SOUL_SOIL = block(Blocks.SOUL_SOIL);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = block(Blocks.GRASS_BLOCK);
    private static final MaterialRules.MaterialRule TERRACOTTA = block(Blocks.TERRACOTTA);


    private static MaterialRules.MaterialCondition surfaceNoiseThreshold(double min) {
        return MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, min / 8.25D, 1.7976931348623157E308D);
    }


    public static MaterialRules.MaterialRule makeRules() {
        MaterialRules.MaterialRule ruleMossMix = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.PATCH, -0.9D, 0.025D), SOUL_SAND), MOSS_BLOCK);
        MaterialRules.MaterialRule noiseMixMoss = MaterialRules.sequence(
                MaterialRules.condition(surfaceNoiseThreshold(1.75D), COARSE_DIRT),
                MaterialRules.condition(surfaceNoiseThreshold(-0.95D), ruleMossMix),
                MaterialRules.condition(MaterialRules.surface(), TERRACOTTA)
        );

        MaterialRules.MaterialCondition above97Condition = MaterialRules.aboveY(YOffset.fixed(97), 2);
        MaterialRules.MaterialCondition above63Condition = MaterialRules.aboveY(YOffset.fixed(63), 2);
        MaterialRules.MaterialCondition waterOffsetCondition = MaterialRules.water(-1, 0);
        MaterialRules.MaterialRule ruleMossCoarseDirt = MaterialRules.sequence(MaterialRules.condition(waterOffsetCondition, MOSS_BLOCK), COARSE_DIRT);
        MaterialRules.MaterialRule ruleCoarseDirtDirt = MaterialRules.sequence(MaterialRules.condition(waterOffsetCondition, COARSE_DIRT), DIRT);

        MaterialRules.MaterialRule erodedSurfaceRule = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                                MaterialRules.condition(above63Condition, noiseMixMoss)
                                ));



        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(TGBiomes.ERODED_HAUNTED_FOREST_KEY), erodedSurfaceRule),
                MaterialRules.condition(MaterialRules.biome(TGBiomes.HAUNTED_LAKES_KEY), erodedSurfaceRule)
        );
    }

    private static MaterialRules.MaterialRule block(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

}
