package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGStructures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.List;
import java.util.Locale;

public final class GraveyardStructure {

    private GraveyardStructure() {}

    public static void addStructures(BiomeInjection.BiomeInjectionHelper event) {

        // desert graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG);
        }

        // graves
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_desert_grave").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG);
        }
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG);
        }
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG);
        }
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_grave").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG);
        }

        // mushroom grave
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("mushroom_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("mushroom_grave").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG);
        }

        // small graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_graveyard").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG);
        }

        // memorial tree
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "memorial_tree"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("memorial_tree").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("memorial_tree").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG);
        }

        // medium graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "medium_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("medium_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("medium_graveyard").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG);
        }


        // large graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("large_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("large_graveyard").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG);
        }

        // haunted house
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "haunted_house"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("haunted_house").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("haunted_house").blacklistedBiomes, event)) {
            event.addStructure(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG);
        }


    }

    private static boolean parseBiomes(List<String> allowedBiomeCategory, List<String> blacklistedBiomes, BiomeInjection.BiomeInjectionHelper biomeContext) {
        // no blacklist and biome is allowed
        if (allowedBiomeCategory.contains(biomeContext.biome.getCategory().toString().toLowerCase(Locale.ROOT)) && blacklistedBiomes.isEmpty()) {
            return true;
        }

        // blacklist and check if biome is on the blacklist
        if (allowedBiomeCategory.contains(biomeContext.biome.getCategory().toString().toLowerCase(Locale.ROOT)) && !blacklistedBiomes.isEmpty()) {
            if (blacklistedBiomes.contains(biomeContext.getBiomeKey().getValue().getPath())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
