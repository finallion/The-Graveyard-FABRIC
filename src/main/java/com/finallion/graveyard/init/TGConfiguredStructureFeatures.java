package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.structureKeys.TGStructureTypeKeys;
import com.finallion.graveyard.util.TGTags;
import com.finallion.graveyard.world.structures.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.StructureType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TGConfiguredStructureFeatures {

    // public static final RegistryEntry<StructureType> PILLAGER_OUTPOST PILLAGER_OUTPOST = register(
    // StructureTypeKeys.PILLAGER_OUTPOST,
    // new JigsawStructure(createConfig(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE,
    // Map.of(SpawnGroup.MONSTER,
    // new StructureSpawns(BoundingBox.STRUCTURE,
    // Pool.of(new SpawnEntry[]{new SpawnEntry(EntityType.PILLAGER, 1, 1, 1)}))),
    // Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_THIN),
    // PillagerOutpostGenerator.STRUCTURE_POOLS, 7,
    // ConstantHeightProvider.create(YOffset.fixed(0)), true, Type.WORLD_SURFACE_WG));


    public static final RegistryEntry<StructureType> HAUNTED_HOUSE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.HAUNTED_HOUSE,
            new TGJigsawStructure(createConfig(TGTags.IS_OVERWORLD, Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    HauntedHouseStructure.HauntedHouseGenerator.STARTING_POOL, 2,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));


    private static StructureType.Config createConfig(TagKey<Biome> biomeTag, Map<SpawnGroup, StructureSpawns> spawns, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
        return new StructureType.Config(BuiltinRegistries.BIOME.getOrCreateEntryList(biomeTag), spawns, featureStep, terrainAdaptation);
    }


    private static RegistryEntry<StructureType> register(RegistryKey<StructureType> key, StructureType configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, key, configuredStructureFeature);
    }
    public static void init() {}
}

    /*
        public static Set<ConfiguredStructureFeature<?, ?>> configured_structures = new HashSet<>();


    public static final RegistryEntry<StructureType> HAUNTED_HOUSE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.HAUNTED_HOUSE,
            TGStructureFeatures.HAUNTED_HOUSE_STRUCTURE.configure( // structure size: 30
            new StructurePoolFeatureConfig(HauntedHouseStructure.HauntedHouseGenerator.STARTING_POOL, 2),
                    TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("haunted_house")));

    public static final RegistryEntry<StructureType> LARGE_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.LARGE_GRAVEYARD,
            TGStructureFeatures.LARGE_GRAVEYARD_STRUCTURE.configure( // structure size: 45
                    new StructurePoolFeatureConfig(LargeGraveyardStructure.LargeGraveyardGenerator.STARTING_POOL, 3), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("large_graveyard")));

    public static final RegistryEntry<StructureType> MEDIUM_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.MEDIUM_GRAVEYARD,
            TGStructureFeatures.MEDIUM_GRAVEYARD_STRUCTURE.configure( // structure size: 30
                    new StructurePoolFeatureConfig(MediumGraveyardStructure.MediumGraveyardGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("medium_graveyard")));

    public static final RegistryEntry<StructureType> MEMORIAL_TREE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.MEMORIAL_TREE,
            TGStructureFeatures.MEMORIAL_TREE_STRUCTURE.configure( // structure size: 10
                    new StructurePoolFeatureConfig(MemorialTreeStructure.MemorialTreeGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("memorial_tree")));

    public static final RegistryEntry<StructureType> MUSHROOM_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.MUSHROOM_GRAVE,
            TGStructureFeatures.MUSHROOM_GRAVE_STRUCTURE.configure( // structure size: 7
                    new StructurePoolFeatureConfig(MushroomGraveStructure.MushroomGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("mushroom_grave")));

    public static final RegistryEntry<StructureType> SMALL_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_GRAVEYARD,
            TGStructureFeatures.SMALL_GRAVEYARD_STRUCTURE.configure( // structure size: 15
                    new StructurePoolFeatureConfig(SmallGraveyardStructure.SmallGraveyardGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_graveyard")));

    public static final RegistryEntry<StructureType> SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_DESERT_GRAVEYARD,
            TGStructureFeatures.SMALL_DESERT_GRAVEYARD_STRUCTURE.configure( // structure size: 20
                    new StructurePoolFeatureConfig(SmallDesertGraveyardStructure.SmallDesertGraveyardGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_desert_graveyard")));

    public static final RegistryEntry<StructureType> SMALL_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_GRAVE,
            TGStructureFeatures.SMALL_GRAVE_STRUCTURE.configure( // structure size: 4
                    new StructurePoolFeatureConfig(SmallGraveStructure.SmallGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_grave")));

    public static final RegistryEntry<StructureType> SMALL_DESERT_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_DESERT_GRAVE,
            TGStructureFeatures.SMALL_DESERT_GRAVE_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(SmallDesertGraveStructure.SmallDesertGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_desert_grave")));

    public static final RegistryEntry<StructureType> SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_SAVANNA_GRAVE,
            TGStructureFeatures.SMALL_SAVANNA_GRAVE_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(SmallSavannaGraveStructure.SmallSavannaGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_savanna_grave")));

    public static final RegistryEntry<StructureType> SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_MOUNTAIN_GRAVE,
            TGStructureFeatures.SMALL_MOUNTAIN_GRAVE_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(SmallMountainGraveStructure.SmallMountainGraveGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("small_mountain_grave")));

    public static final RegistryEntry<StructureType> ALTAR_STRUCTURE_CONFIG = register(TGStructureTypeKeys.ALTAR,
            TGStructureFeatures.ALTAR_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(AltarStructure.AltarGenerator.STARTING_POOL, 1), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("altar")));

    public static final RegistryEntry<StructureType> GIANT_MUSHROOM_STRUCTURE_CONFIG = register(TGStructureTypeKeys.GIANT_MUSHROOM,
            TGStructureFeatures.GIANT_MUSHROOM_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(GiantMushroomStructure.GiantMushroomGenerator.STARTING_POOL, 2), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("giant_mushroom")));

    public static final RegistryEntry<StructureType> CRYPT_STRUCTURE_CONFIG = register(TGStructureTypeKeys.CRYPT,
            TGStructureFeatures.CRYPT_STRUCTURE.configure(
                    new StructurePoolFeatureConfig(CryptStructure.CryptGenerator.STARTING_POOL, 7), TGTags.IS_OVERWORLD, true, addMobSpawnsToStructure("crypt")));

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

     */
