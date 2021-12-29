package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.structures.*;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;

import java.util.ArrayList;
import java.util.List;

public class TGStructures {
    public static List<StructureFeature> structures = new ArrayList<>();


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

    private static void register(String id, StructureFeature<?> feature, int spacing, int separation, int salt) {
        structures.add(feature);

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, id), feature)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(spacing, separation, salt)
                .enableSuperflat()
                .adjustsSurface()
                .register();
    }

    public static void registerStructures() {
        register("medium_graveyard", MEDIUM_GRAVEYARD_STRUCTURE, 18, 16, 1690192399);
        register("small_graveyard", SMALL_GRAVEYARD_STRUCTURE, 20, 18, 240451934);
        register("large_graveyard", LARGE_GRAVEYARD_STRUCTURE, 20, 18, 304812394);
        register("mushroom_grave", MUSHROOM_GRAVE_STRUCTURE, 24, 18, 379123039);
        register("haunted_house", HAUNTED_HOUSE_STRUCTURE, 25, 20, 451235912);
        register("memorial_tree", MEMORIAL_TREE_STRUCTURE, 14, 12, 529239621);
        register("small_desert_graveyard", SMALL_DESERT_GRAVEYARD_STRUCTURE, 32, 28, 598017285);
        register("small_grave", SMALL_GRAVE_STRUCTURE, 12, 8, 661903018);
        register("small_desert_grave", SMALL_DESERT_GRAVE_STRUCTURE, 20, 16, 681236914);
        register("small_savanna_grave", SMALL_SAVANNA_GRAVE_STRUCTURE, 12, 8, 709787761);
        register("small_mountain_grave", SMALL_MOUNTAIN_GRAVE_STRUCTURE, 12, 8, 725689810);

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
