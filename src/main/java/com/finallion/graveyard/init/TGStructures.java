package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.world.structures.*;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TGStructures {
    public static Set<StructureFeature<StructurePoolFeatureConfig>> structures = new HashSet<>();

    // structure features
    public static final StructureFeature<StructurePoolFeatureConfig> MEDIUM_GRAVEYARD_STRUCTURE = new MediumGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_GRAVEYARD_STRUCTURE = new SmallGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> LARGE_GRAVEYARD_STRUCTURE = new LargeGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> MUSHROOM_GRAVE_STRUCTURE = new MushroomGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> HAUNTED_HOUSE_STRUCTURE = new HauntedHouseStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> MEMORIAL_TREE_STRUCTURE = new MemorialTreeStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_DESERT_GRAVEYARD_STRUCTURE = new SmallDesertGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_GRAVE_STRUCTURE = new SmallGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_DESERT_GRAVE_STRUCTURE = new SmallDesertGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_SAVANNA_GRAVE_STRUCTURE = new SmallSavannaGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_MOUNTAIN_GRAVE_STRUCTURE = new SmallMountainGraveStructure(StructurePoolFeatureConfig.CODEC);


    // configured structure features, registry via mixin
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> MEDIUM_GRAVEYARD_STRUCTURE_CONFIG = MEDIUM_GRAVEYARD_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return MediumGraveyardStructure.MediumGraveyardGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> SMALL_GRAVEYARD_STRUCTURE_CONFIG = SMALL_GRAVEYARD_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return SmallGraveyardStructure.SmallGraveyardGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> LARGE_GRAVEYARD_STRUCTURE_CONFIG = LARGE_GRAVEYARD_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return LargeGraveyardStructure.LargeGraveyardGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> MUSHROOM_GRAVE_STRUCTURE_CONFIG = MUSHROOM_GRAVE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return MushroomGraveStructure.MushroomGraveGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> HAUNTED_HOUSE_STRUCTURE_CONFIG = HAUNTED_HOUSE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return HauntedHouseStructure.HauntedHouseGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> MEMORIAL_TREE_STRUCTURE_CONFIG = MEMORIAL_TREE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return MemorialTreeStructure.MemorialTreeGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG = SMALL_DESERT_GRAVEYARD_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return SmallDesertGraveyardStructure.SmallDesertGraveyardGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> SMALL_GRAVE_STRUCTURE_CONFIG = SMALL_GRAVE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return SmallGraveStructure.SmallGraveGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> SMALL_DESERT_GRAVE_STRUCTURE_CONFIG = SMALL_DESERT_GRAVE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return SmallDesertGraveStructure.SmallDesertGraveGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG = SMALL_SAVANNA_GRAVE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return SmallSavannaGraveStructure.SmallSavannaGraveGenerator.STARTING_POOL;}, 0));

    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG = SMALL_MOUNTAIN_GRAVE_STRUCTURE.configure(
            new StructurePoolFeatureConfig(() -> {return SmallMountainGraveStructure.SmallMountainGraveGenerator.STARTING_POOL;}, 0));


    private static void register() {
        for (StructureFeature<?> structure : structures) {
            AbstractGraveyardStructure abstractStructure = (AbstractGraveyardStructure) structure;
            StructureConfig entry = abstractStructure.getStructureFeatureConfiguration();

            FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, abstractStructure.getStructureName()), structure)
                    .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                    .defaultConfig(entry)
                    .enableSuperflat()
                    .adjustsSurface()
                    .register();

        }
    }

    public static void registerStructures() {
        structures.add(MEDIUM_GRAVEYARD_STRUCTURE);
        structures.add(SMALL_GRAVEYARD_STRUCTURE);
        structures.add(LARGE_GRAVEYARD_STRUCTURE);
        structures.add(MUSHROOM_GRAVE_STRUCTURE);
        structures.add(HAUNTED_HOUSE_STRUCTURE);
        structures.add(MEMORIAL_TREE_STRUCTURE);
        structures.add(SMALL_DESERT_GRAVEYARD_STRUCTURE);
        structures.add(SMALL_GRAVE_STRUCTURE);
        structures.add(SMALL_DESERT_GRAVE_STRUCTURE);
        structures.add(SMALL_SAVANNA_GRAVE_STRUCTURE);
        structures.add(SMALL_MOUNTAIN_GRAVE_STRUCTURE);

        register();


        // link to the structure pools
        MediumGraveyardStructure.MediumGraveyardGenerator.init();
        SmallGraveyardStructure.SmallGraveyardGenerator.init();
        LargeGraveyardStructure.LargeGraveyardGenerator.init();
        MushroomGraveStructure.MushroomGraveGenerator.init();
        HauntedHouseStructure.HauntedHouseGenerator.init();
        MemorialTreeStructure.MemorialTreeGenerator.init();
        SmallDesertGraveyardStructure.SmallDesertGraveyardGenerator.init();
        SmallGraveStructure.SmallGraveGenerator.init();
        SmallDesertGraveStructure.SmallDesertGraveGenerator.init();
        SmallSavannaGraveStructure.SmallSavannaGraveGenerator.init();
        SmallMountainGraveStructure.SmallMountainGraveGenerator.init();

    }

    public static void registerConfiguredStructures() {
        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "medium_graveyard_config"), MEDIUM_GRAVEYARD_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "small_graveyard_config"), SMALL_GRAVEYARD_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "large_graveyard_config"), LARGE_GRAVEYARD_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "mushroom_grave_config"), MUSHROOM_GRAVE_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "haunted_house_config"), HAUNTED_HOUSE_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "memorial_tree_config"), MEMORIAL_TREE_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard_config"), SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "small_grave_config"), SMALL_GRAVE_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "small_desert_grave_config"), SMALL_DESERT_GRAVE_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave_config"), SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave_config"), SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG);

    }



}
