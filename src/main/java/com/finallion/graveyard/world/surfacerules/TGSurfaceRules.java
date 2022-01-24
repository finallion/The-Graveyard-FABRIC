package com.finallion.graveyard.world.surfacerules;

import com.finallion.graveyard.init.TGBiomes;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.world.noise.TGNoiseParameters;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

import java.util.Random;

public class TGSurfaceRules {
    private static final MaterialRules.MaterialRule TG_MOSS_BLOCK = block(TGBlocks.TG_MOSS_BLOCK);
    private static final MaterialRules.MaterialRule MOSS_BLOCK = block(Blocks.MOSS_BLOCK);
    private static final MaterialRules.MaterialRule ROOTED_DIRT = block(Blocks.ROOTED_DIRT);
    private static final MaterialRules.MaterialRule COARSE_DIRT = block(Blocks.COARSE_DIRT);
    private static final MaterialRules.MaterialRule SOUL_SAND = block(Blocks.SOUL_SAND);
    private static final MaterialRules.MaterialRule SOUL_SOIL = block(Blocks.SOUL_SOIL);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = block(Blocks.GRASS_BLOCK);
    private static final MaterialRules.MaterialRule WATER = block(Blocks.WATER);

    public static MaterialRules.MaterialRule makeRules() {
        // TODO: take material rules together for example STONE_DEPTH_FLOOR
        MaterialRules.MaterialRule noiseGrass = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(62), 0),
                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.2, 0.0), GRASS_BLOCK)))));

        MaterialRules.MaterialRule noiseGrass93 = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(93), 3),
                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.2, 0.0), GRASS_BLOCK)))));

        MaterialRules.MaterialRule noiseCoarseDirt = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(62), 0),
                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, 0.2, 0.4), COARSE_DIRT)))));

        MaterialRules.MaterialRule noiseErosion = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, // YOffset.fixed(62)
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(93), 0),
                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.1, 1.15),
                                        MaterialRules.sequence(
                                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, -0.1, 0.25), COARSE_DIRT),
                                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, 0.25, 0.5), SOUL_SAND),
                                                //MaterialRules.sequence(MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.ICE, -0.2, 0.2), SOUL_SOIL), SOUL_SAND)),
                                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, 0.5, 0.75), SOUL_SOIL),
                                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, 0.75, 0.8), SOUL_SAND),
                                                //MaterialRules.sequence(MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.ICE, -0.2, 0.2), SOUL_SOIL), SOUL_SAND)),
                                                MaterialRules.condition(MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE, 0.8, 1.15), COARSE_DIRT)
                                        ))))));


        MaterialRules.MaterialRule noiseMoss = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(62), 0),
                                MaterialRules.condition(MaterialRules.noiseThreshold(TGNoiseParameters.HAUNTED_FOREST_NOISE, -0.2, 2.0), MOSS_BLOCK)))));

        MaterialRules.MaterialRule noiseMoss93 = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(93), 2),
                                MaterialRules.condition(MaterialRules.noiseThreshold(TGNoiseParameters.HAUNTED_FOREST_NOISE, -0.2, 2.0), MOSS_BLOCK)))));

        MaterialRules.MaterialRule noiseParticleMoss = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(62), 0),
                                MaterialRules.condition(MaterialRules.noiseThreshold(TGNoiseParameters.HAUNTED_FOREST_PARTICLE_MOSS, -0.1, 0.1), TG_MOSS_BLOCK)))));

        MaterialRules.MaterialRule noiseParticleMoss93 = MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.aboveY(YOffset.fixed(93), 2),
                                MaterialRules.condition(MaterialRules.noiseThreshold(TGNoiseParameters.HAUNTED_FOREST_PARTICLE_MOSS, -0.1, 0.1), TG_MOSS_BLOCK)))));

        MaterialRules.MaterialRule erodedHauntedForestRule =
                MaterialRules.condition(MaterialRules.biome(TGBiomes.ERODED_HAUNTED_FOREST_KEY),
                        MaterialRules.sequence(
                                noiseGrass93,
                                noiseErosion,
                                noiseMoss93,
                                noiseParticleMoss93));

        MaterialRules.MaterialCondition above62 = MaterialRules.aboveY(YOffset.fixed(62), 0);
        MaterialRules.MaterialCondition above63_0 = MaterialRules.aboveY(YOffset.fixed(63), 0);
        MaterialRules.MaterialRule surfaceRules = MaterialRules.sequence(
                MaterialRules.condition(
                        MaterialRules.STONE_DEPTH_FLOOR,
                        MaterialRules.sequence(
                                MaterialRules.condition(
                                        MaterialRules.biome(TGBiomes.HAUNTED_LAKES_KEY),
                                        MaterialRules.condition(
                                                above62,
                                                MaterialRules.condition(MaterialRules.not(above63_0),
                                                        MaterialRules.condition(
                                                                MaterialRules.noiseThreshold(NoiseParametersKeys.SURFACE_SWAMP, 0.0D),
                                                                WATER
                                                        )
                                                )
                                        )
                                )
                        )
                ));

        MaterialRules.MaterialRule hauntedForestRule =
                MaterialRules.sequence(
                        MaterialRules.condition(MaterialRules.biome(TGBiomes.HAUNTED_FOREST_KEY, TGBiomes.HAUNTED_LAKES_KEY),
                                MaterialRules.sequence(
                                        noiseGrass,
                                        noiseCoarseDirt,
                                        noiseMoss,
                                        noiseParticleMoss)),
                        MaterialRules.condition(MaterialRules.biome(TGBiomes.HAUNTED_LAKES_KEY), surfaceRules));



        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(TGBiomes.ERODED_HAUNTED_FOREST_KEY), erodedHauntedForestRule),
                MaterialRules.condition(MaterialRules.biome(TGBiomes.HAUNTED_LAKES_KEY), hauntedForestRule),
                MaterialRules.condition(MaterialRules.biome(TGBiomes.HAUNTED_FOREST_KEY), hauntedForestRule)
        );
    }

    private static MaterialRules.MaterialRule block(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

}
