package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGStructures;
import net.minecraft.util.Identifier;

import java.util.List;

public final class GraveyardStructure {

    private GraveyardStructure() {}

    public static void addStructures(BiomeInjection.BiomeInjectionHelper event) {

        // desert graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").blacklist, event)) {
            event.addStructure(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG);
        }

        // graves
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_desert_grave").blacklist, event)) {
            event.addStructure(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG);
        }
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").blacklist, event)) {
            event.addStructure(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG);
        }
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").blacklist, event)) {
            event.addStructure(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG);
        }
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_grave").blacklist, event)) {
            event.addStructure(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG);
        }

        // mushroom grave
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("mushroom_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("mushroom_grave").blacklist, event)) {
            event.addStructure(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG);
        }

        // small graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("small_graveyard").blacklist, event)) {
            event.addStructure(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG);
        }

        // memorial tree
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "memorial_tree"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("memorial_tree").whitelist, TheGraveyard.config.structureConfigEntries.get("memorial_tree").blacklist, event)) {
            event.addStructure(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG);
        }

        // medium graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "medium_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("medium_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("medium_graveyard").blacklist, event)) {
            event.addStructure(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG);
        }


        // large graveyard
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_graveyard"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("large_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("large_graveyard").blacklist, event)) {
            event.addStructure(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG);
        }

        // haunted house
        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "haunted_house"))
                && parseBiomes(TheGraveyard.config.structureConfigEntries.get("haunted_house").whitelist, TheGraveyard.config.structureConfigEntries.get("haunted_house").blacklist, event)) {
            event.addStructure(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG);
        }


    }

    private static boolean parseBiomes(List<String> whitelist, List<String> blacklist, BiomeInjection.BiomeInjectionHelper biomeContext) {
        String biomeIdentifier = biomeContext.biomeKey.getValue().toString();
        String biomeCategory = biomeContext.biome.getCategory().getName();

        if (whitelist == null) {
            TheGraveyard.LOGGER.error("Error reading from the Graveyard config file: Allowed biome category/biome is null. Try to delete the file and restart the game.");
            return false;
        }

        // no blacklist and biome is allowed
        if (whitelist.contains(biomeIdentifier) && blacklist.isEmpty()) {
            return true;
        }

        // no blacklist and biomeCategory is allowed
        if (whitelist.contains("#" + biomeCategory) && blacklist.isEmpty()) {
            return true;
        }

        // blacklist exists and check if biome is on the blacklist
        if (whitelist.contains(biomeIdentifier) && !blacklist.isEmpty()) {
            if (blacklist.contains("#" + biomeCategory)) { // whitelist weighs higher than blacklist
                //TheGraveyard.LOGGER.error("Blacklisted biome category #" + biomeCategory + " contains whitelisted biome " + biomeIdentifier + ".");
                return true;
            } else if (blacklist.contains(biomeIdentifier)) {  // blacklist weighs higher than whitelist
                TheGraveyard.LOGGER.debug("Biome " +  biomeIdentifier + " is on whitelist and blacklist.");
                return false;
            } else {
                return true;
            }
        }


        // blacklist exists and check if biomeCategory is on the blacklist
        if (whitelist.contains("#" + biomeCategory) && !blacklist.isEmpty()) {
            if (blacklist.contains("#" + biomeCategory)) { // blacklist weighs higher than whitelist
                TheGraveyard.LOGGER.debug("Biome category #" + biomeCategory + " is on whitelist and blacklist.");
                return false;
            } else if (blacklist.contains(biomeIdentifier)) { // blacklist weighs higher than whitelist
                //TheGraveyard.LOGGER.error("Biome category #" + biomeCategory + " is on whitelist and subsidiary biome " + biomeIdentifier + " is on blacklist.");
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
