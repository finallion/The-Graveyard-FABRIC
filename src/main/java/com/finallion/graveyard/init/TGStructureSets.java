package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.structureKeys.TGStructureSetKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSets;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public class TGStructureSets {

    public static void init() {

        StructureSets.register(TGStructureSetKeys.HAUNTED_HOUSES, TGConfiguredStructureFeatures.HAUNTED_HOUSE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("haunted_house").spacing,
                        TheGraveyard.config.structureConfigEntries.get("haunted_house").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("haunted_house").salt));

        StructureSets.register(TGStructureSetKeys.LARGE_GRAVEYARDS, TGConfiguredStructureFeatures.LARGE_GRAVEYARD_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("large_graveyard").spacing,
                        TheGraveyard.config.structureConfigEntries.get("large_graveyard").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("large_graveyard").salt));

        StructureSets.register(TGStructureSetKeys.MEDIUM_GRAVEYARDS, TGConfiguredStructureFeatures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("medium_graveyard").spacing,
                        TheGraveyard.config.structureConfigEntries.get("medium_graveyard").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("medium_graveyard").salt));

        StructureSets.register(TGStructureSetKeys.SMALL_GRAVES, TGConfiguredStructureFeatures.SMALL_GRAVE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("small_grave").spacing,
                        TheGraveyard.config.structureConfigEntries.get("small_grave").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("small_grave").salt));

        StructureSets.register(TGStructureSetKeys.SMALL_DESERT_GRAVES, TGConfiguredStructureFeatures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("small_desert_grave").spacing,
                        TheGraveyard.config.structureConfigEntries.get("small_desert_grave").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("small_desert_grave").salt));

        StructureSets.register(TGStructureSetKeys.SMALL_SAVANNA_GRAVES, TGConfiguredStructureFeatures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").spacing,
                        TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").salt));

        StructureSets.register(TGStructureSetKeys.SMALL_MOUNTAIN_GRAVES, TGConfiguredStructureFeatures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").spacing,
                        TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").salt));

        StructureSets.register(TGStructureSetKeys.SMALL_GRAVEYARDS, TGConfiguredStructureFeatures.SMALL_GRAVEYARD_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("small_graveyard").spacing,
                        TheGraveyard.config.structureConfigEntries.get("small_graveyard").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("small_graveyard").salt));

        StructureSets.register(TGStructureSetKeys.SMALL_DESERT_GRAVEYARDS, TGConfiguredStructureFeatures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").spacing,
                        TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").salt));

        StructureSets.register(TGStructureSetKeys.MUSHROOM_GRAVES, TGConfiguredStructureFeatures.MUSHROOM_GRAVE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("mushroom_grave").spacing,
                        TheGraveyard.config.structureConfigEntries.get("mushroom_grave").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("mushroom_grave").salt));

        StructureSets.register(TGStructureSetKeys.MEMORIAL_TREES, TGConfiguredStructureFeatures.MEMORIAL_TREE_STRUCTURE_CONFIG,
                new RandomSpreadStructurePlacement(
                        TheGraveyard.config.structureConfigEntries.get("memorial_tree").spacing,
                        TheGraveyard.config.structureConfigEntries.get("memorial_tree").separation,
                        SpreadType.LINEAR,
                        TheGraveyard.config.structureConfigEntries.get("memorial_tree").salt));

    }

}
