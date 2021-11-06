package com.finallion.graveyard.biomes.surfacebuilders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class TGSurfaceBuilders {

    public static final SurfaceBuilder<TernarySurfaceConfig> HAUNTED_FOREST_SURFACE = registerSurfaceBuilder("haunted_forest_surface", new HauntedForestSurfaceBuilder(TernarySurfaceConfig.CODEC));
    public static final ConfiguredSurfaceBuilder HAUNTED_FOREST_SURFACE_CONFIG = registerConfiguredSurfaceBuilder("haunted_forest_surface_config", new ConfiguredSurfaceBuilder<>(HAUNTED_FOREST_SURFACE, Configs.MOSS_CONFIG));

    public static <SBC extends SurfaceConfig, SB extends SurfaceBuilder<SBC>> SB registerSurfaceBuilder(String id, SB surfaceBuilder) {
        Registry.register(Registry.SURFACE_BUILDER, new Identifier(TheGraveyard.MOD_ID, id), surfaceBuilder);
        return surfaceBuilder;
    }

    public static <SC extends SurfaceConfig, CSB extends ConfiguredSurfaceBuilder<SC>> CSB registerConfiguredSurfaceBuilder(String id, CSB configuredSurfaceBuilder) {
        Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(TheGraveyard.MOD_ID, id), configuredSurfaceBuilder);
        return configuredSurfaceBuilder;
    }


    public static class Configs {

        public static final TernarySurfaceConfig PARTICLE_MOSS_CONFIG = new TernarySurfaceConfig(TGBlocks.TG_MOSS_BLOCK.getDefaultState(), Blocks.ROOTED_DIRT.getDefaultState(), Blocks.DIRT.getDefaultState());
        public static final TernarySurfaceConfig MOSS_CONFIG = new TernarySurfaceConfig(Blocks.MOSS_BLOCK.getDefaultState(), Blocks.ROOTED_DIRT.getDefaultState(), Blocks.DIRT.getDefaultState());
        public static final TernarySurfaceConfig ROOTED_DIRT_CONFIG = new TernarySurfaceConfig(Blocks.ROOTED_DIRT.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState(), Blocks.DIRT.getDefaultState());
    }

}
