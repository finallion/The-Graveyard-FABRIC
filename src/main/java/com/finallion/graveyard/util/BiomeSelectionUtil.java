package com.finallion.graveyard.util;


import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.stream.Collectors;

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
                TagKey<Biome> tag = TagKey.of(RegistryKeys.BIOME, new Identifier(parts[0], parts[1]));

                RegistryEntryLookup<Biome> biomeLookup = BuiltinRegistries.createWrapperLookup().createRegistryLookup().getOrThrow(RegistryKeys.BIOME);
                //blockLookup.getOptional(ConventionalBiomeTags.IN_OVERWORLD).stream().map(b -> b.toString()).collect(Collectors.toList());

                //BuiltinRegistries.createWrapperLookup().createRegistryLookup().
                //if (Registries.BIOME.getOrCreateEntryList(tag).contains(biome)) {
                if (biomeLookup.getOptional(tag).isPresent()) {
                    return true;
                }
            } else if (biomeWhitelist.contains(biomeName)) { // biome is whitelisted
                return true;
            }
        }

        return false;
    }
}

