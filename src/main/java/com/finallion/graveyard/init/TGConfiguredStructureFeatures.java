package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.structureKeys.TGStructureTypeKeys;
import com.finallion.graveyard.util.TGTags;
import com.finallion.graveyard.world.structures.*;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.structure.Structure.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TGConfiguredStructureFeatures {
    public static List<Structure> structures = new ArrayList<>();

    public static final RegistryEntry<Structure> HAUNTED_HOUSE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.HAUNTED_HOUSE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("haunted_house"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    HauntedHouseStructure.HauntedHouseGenerator.STARTING_POOL, 2,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("haunted_house").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("haunted_house").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("haunted_house").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("haunted_house").modIdWhitelist,
                    "haunted_house"
            )
    );

    public static final RegistryEntry<Structure> LARGE_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.LARGE_GRAVEYARD,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("large_graveyard"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    LargeGraveyardStructure.LargeGraveyardGenerator.STARTING_POOL, 4,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("large_graveyard").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("large_graveyard").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("large_graveyard").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("large_graveyard").modIdWhitelist,
                    "large_graveyard"
            )
    );

    public static final RegistryEntry<Structure> ALTAR_STRUCTURE_CONFIG = register(TGStructureTypeKeys.ALTAR,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("altar"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    AltarStructure.AltarGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("altar").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("altar").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("altar").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("altar").modIdWhitelist,
                    "altar"
            )
    );

    public static final RegistryEntry<Structure> CRYPT_STRUCTURE_CONFIG = register(TGStructureTypeKeys.CRYPT,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("crypt"), GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE),
                    CryptStructure.CryptGenerator.STARTING_POOL, 7,
                    ConstantHeightProvider.create(YOffset.fixed(-10)), false,
                    TheGraveyard.config.structureConfigEntries.get("crypt").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("crypt").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("crypt").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("crypt").modIdWhitelist,
                    "crypt"
            )
    );

    public static final RegistryEntry<Structure> GIANT_MUSHROOM_STRUCTURE_CONFIG = register(TGStructureTypeKeys.GIANT_MUSHROOM,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("giant_mushroom"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_THIN),
                    GiantMushroomStructure.GiantMushroomGenerator.STARTING_POOL, 2,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("giant_mushroom").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("giant_mushroom").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("giant_mushroom").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("giant_mushroom").modIdWhitelist,
                    "giant_mushroom"
            )
    );

    public static final RegistryEntry<Structure> MEDIUM_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.MEDIUM_GRAVEYARD,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("medium_graveyard"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    MediumGraveyardStructure.MediumGraveyardGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("medium_graveyard").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("medium_graveyard").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("medium_graveyard").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("medium_graveyard").modIdWhitelist,
                    "medium_graveyard"
            )
    );

    public static final RegistryEntry<Structure> SMALL_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_GRAVEYARD,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("small_graveyard"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    SmallGraveyardStructure.SmallGraveyardGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("small_graveyard").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("small_graveyard").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("small_graveyard").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("small_graveyard").modIdWhitelist,
                    "small_graveyard"
            )
    );

    public static final RegistryEntry<Structure> SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_DESERT_GRAVEYARD,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("small_desert_graveyard"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    SmallDesertGraveyardStructure.SmallDesertGraveyardGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").modIdWhitelist,
                    "small_desert_graveyard"
            )
    );

    public static final RegistryEntry<Structure> SMALL_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_GRAVE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("small_grave"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    SmallGraveStructure.SmallGraveGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("small_grave").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("small_grave").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("small_grave").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("small_grave").modIdWhitelist,
                    "small_grave"
            )
    );

    public static final RegistryEntry<Structure> SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_SAVANNA_GRAVE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("small_savanna_grave"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    SmallSavannaGraveStructure.SmallSavannaGraveGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").modIdWhitelist,
                    "small_savanna_grave"
            )
    );

    public static final RegistryEntry<Structure> SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_MOUNTAIN_GRAVE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("small_mountain_grave"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    SmallMountainGraveStructure.SmallMountainGraveGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").modIdWhitelist,
                    "small_mountain_grave"
            )
    );


    public static final RegistryEntry<Structure> SMALL_DESERT_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.SMALL_DESERT_GRAVE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("small_desert_grave"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    SmallDesertGraveStructure.SmallDesertGraveGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_grave").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_grave").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_grave").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("small_desert_grave").modIdWhitelist,
                    "small_desert_grave"
            )
    );

    public static final RegistryEntry<Structure> MEMORIAL_TREE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.MEMORIAL_TREE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("memorial_tree"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    MemorialTreeStructure.MemorialTreeGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("memorial_tree").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("memorial_tree").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("memorial_tree").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("memorial_tree").modIdWhitelist,
                    "memorial_tree"
            )
    );

    public static final RegistryEntry<Structure> MUSHROOM_GRAVE_STRUCTURE_CONFIG = register(TGStructureTypeKeys.MUSHROOM_GRAVE,
            new TGJigsawStructure(createConfig(ConventionalBiomeTags.IN_OVERWORLD, addMobSpawnsToStructure("mushroom_grave"), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_BOX),
                    MushroomGraveStructure.MushroomGraveGenerator.STARTING_POOL, 1,
                    ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG,
                    TheGraveyard.config.structureConfigEntries.get("mushroom_grave").terrainCheckRadius,
                    TheGraveyard.config.structureConfigEntries.get("mushroom_grave").maxTerrainHeightDifference,
                    TheGraveyard.config.structureConfigEntries.get("mushroom_grave").biomeWhitelist,
                    TheGraveyard.config.structureConfigEntries.get("mushroom_grave").modIdWhitelist,
                    "mushroom_grave"
            )
    );


    private static Config createConfig(TagKey<Biome> biomeTag, Map<SpawnGroup, StructureSpawns> spawns, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
        return new Config(BuiltinRegistries.BIOME.getOrCreateEntryList(biomeTag), spawns, featureStep, terrainAdaptation);
    }

    private static RegistryEntry<Structure> register(RegistryKey<Structure> key, Structure configuredStructureFeature) {
        structures.add(configuredStructureFeature);
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, key, configuredStructureFeature);
    }
    public static void init() {}

    private static Map<SpawnGroup, StructureSpawns> addMobSpawnsToStructure(String name) {
        if (TheGraveyard.config.structureConfigEntries.get(name).canSpawnGraveyardMobs) {
            return Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, TGJigsawStructure.MONSTER_SPAWNS));
        }

        if (name.equals("small_desert_graveyard")) {
            return Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, TGJigsawStructure.ILLAGER_SPAWNS));
        }

        return Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, TGJigsawStructure.EMPTY));
    }
}
