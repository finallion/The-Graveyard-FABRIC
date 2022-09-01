package com.finallion.graveyard.util;


import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class BiomeSelectionUtil {

    // structures
    public static boolean parseBiomes(List<String> biomeWhitelist, List<String> biomeBlacklist, RegistryEntry<Biome> biome) {
        if (biomeWhitelist == null || biomeBlacklist == null) {
            TheGraveyard.LOGGER.error("The Graveyard config file (\"the-graveyard-config.json5\") isn't up to date. Please delete the file in your .minecraft/config folder and restart the game to create a new config file. If the error keeps showing up, contact the mod developer via Github or Discord (links can be found here: https://www.curseforge.com/minecraft/mc-mods/the-graveyard-fabric)!");
            return false;
        }

        String biomeName = biome.getKey().orElseThrow().getValue().toString();

        if (biomeBlacklist.contains(biomeName)) {
            return false;
        }

        for (String biomeInList : biomeWhitelist) {
            // tag whitelist
            if (biomeInList.startsWith("#")) {
                String[] parts = biomeInList.substring(1).split(":");
                TagKey<Biome> tag = TagKey.of(Registry.BIOME_KEY, new Identifier(parts[0], parts[1]));
                if (BuiltinRegistries.BIOME.getOrCreateEntryList(tag).contains(biome)) {
                    return true;
                }
            } else if (biomeWhitelist.contains(biomeName)) { // biome is whitelisted
                return true;
            }
        }

        return false;
    }
}

