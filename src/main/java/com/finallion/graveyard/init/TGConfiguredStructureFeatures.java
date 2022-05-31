package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.structureKeys.TGConfiguredStructureFeatureKeys;
import com.finallion.graveyard.util.TGTags;
import com.finallion.graveyard.world.structures.*;
import com.finallion.graveyard.world.structures.configs.TGStructureConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TGConfiguredStructureFeatures {
    public static Set<ConfiguredStructureFeature<?, ?>> configured_structures = new HashSet<>();

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> HAUNTED_HOUSE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.HAUNTED_HOUSE,
            TGStructureFeatures.HAUNTED_HOUSE_STRUCTURE.configure( // structure size: 30
            new StructurePoolFeatureConfig(HauntedHouseStructure.HauntedHouseGenerator.STARTING_POOL, 2), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("haunted_house")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> LARGE_GRAVEYARD_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.LARGE_GRAVEYARD,
            TGStructureFeatures.LARGE_GRAVEYARD_STRUCTURE.configure( // structure size: 45
                    new StructurePoolFeatureConfig(LargeGraveyardStructure.LargeGraveyardGenerator.STARTING_POOL, 3), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("large_graveyard")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MEDIUM_GRAVEYARD_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.MEDIUM_GRAVEYARD,
            TGStructureFeatures.MEDIUM_GRAVEYARD_STRUCTURE.configure( // structure size: 30
                    new StructurePoolFeatureConfig(MediumGraveyardStructure.MediumGraveyardGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("medium_graveyard")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MEMORIAL_TREE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.MEMORIAL_TREE,
            TGStructureFeatures.MEMORIAL_TREE_STRUCTURE.configure( // structure size: 10
                    new StructurePoolFeatureConfig(MemorialTreeStructure.MemorialTreeGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("memorial_tree")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> MUSHROOM_GRAVE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.MUSHROOM_GRAVE,
            TGStructureFeatures.MUSHROOM_GRAVE_STRUCTURE.configure( // structure size: 7
                    new StructurePoolFeatureConfig(MushroomGraveStructure.MushroomGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("mushroom_grave")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SMALL_GRAVEYARD_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.SMALL_GRAVEYARD,
            TGStructureFeatures.SMALL_GRAVEYARD_STRUCTURE.configure( // structure size: 15
                    new StructurePoolFeatureConfig(SmallGraveyardStructure.SmallGraveyardGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_graveyard")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.SMALL_DESERT_GRAVEYARD,
            TGStructureFeatures.SMALL_DESERT_GRAVEYARD_STRUCTURE.configure( // structure size: 20
                    new StructurePoolFeatureConfig(SmallDesertGraveyardStructure.SmallDesertGraveyardGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_desert_graveyard")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SMALL_GRAVE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.SMALL_GRAVE,
            TGStructureFeatures.SMALL_GRAVE_STRUCTURE.configure( // structure size: 4
                    new StructurePoolFeatureConfig(SmallGraveStructure.SmallGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_grave")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SMALL_DESERT_GRAVE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.SMALL_DESERT_GRAVE,
            TGStructureFeatures.SMALL_DESERT_GRAVE_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(SmallDesertGraveStructure.SmallDesertGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_desert_grave")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.SMALL_SAVANNA_GRAVE,
            TGStructureFeatures.SMALL_SAVANNA_GRAVE_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(SmallSavannaGraveStructure.SmallSavannaGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_savanna_grave")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.SMALL_MOUNTAIN_GRAVE,
            TGStructureFeatures.SMALL_MOUNTAIN_GRAVE_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(SmallMountainGraveStructure.SmallMountainGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_mountain_grave")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> ALTAR_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.ALTAR,
            TGStructureFeatures.ALTAR_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(AltarStructure.AltarGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("altar")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> GIANT_MUSHROOM_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.GIANT_MUSHROOM,
            TGStructureFeatures.GIANT_MUSHROOM_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(GiantMushroomStructure.GiantMushroomGenerator.STARTING_POOL, 2), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("giant_mushroom")));

    public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> CRYPT_STRUCTURE_CONFIG = register(TGConfiguredStructureFeatureKeys.CRYPT,
            TGStructureFeatures.CRYPT_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(CryptStructure.CryptGenerator.STARTING_POOL, 7), TGTags.IS_OVERWORLD, false, addMobSpawnsToStructure("crypt")));

    public static void init() {}

    private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<?, ?>> register(RegistryKey<ConfiguredStructureFeature<?, ?>> key, ConfiguredStructureFeature<FC, F> configuredStructureFeature) {
        configured_structures.add(configuredStructureFeature);
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, key, configuredStructureFeature);
    }

    private static Map<SpawnGroup, StructureSpawns> addMobSpawnsToStructure(String name) {
        if (TheGraveyard.config.structureConfigEntries.get(name).canSpawnGraveyardMobs) {
            return Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, AbstractGraveyardStructure.MONSTER_SPAWNS));
        }

        if (name.equals("small_desert_graveyard")) {
            return Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, SmallDesertGraveyardStructure.ILLAGER_SPAWNS));
        }

        return Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, AbstractGraveyardStructure.EMPTY));
    }



}
