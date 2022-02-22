package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;

import java.util.List;
import java.util.Locale;

public class MobSpawningRules {

    public static void addSpawnEntries() {
        BiomeModifications.addSpawn(BiomeSelectors.all()
                 .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("ghoul").whitelistedBiomesAndCategories, TheGraveyard.config.mobConfigEntries.get("ghoul").blacklistedBiomes, context))
                 .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("ghoul").enabled)),
                SpawnGroup.MONSTER, TGEntities.GHOUL,
                TheGraveyard.config.mobConfigEntries.get("ghoul").weight,
                TheGraveyard.config.mobConfigEntries.get("ghoul").minGroup,
                TheGraveyard.config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("acolyte").whitelistedBiomesAndCategories, TheGraveyard.config.mobConfigEntries.get("acolyte").blacklistedBiomes, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("acolyte").enabled)),
                SpawnGroup.MONSTER, TGEntities.ACOLYTE,
                TheGraveyard.config.mobConfigEntries.get("acolyte").weight,
                TheGraveyard.config.mobConfigEntries.get("acolyte").minGroup,
                TheGraveyard.config.mobConfigEntries.get("acolyte").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("revenant").whitelistedBiomesAndCategories, TheGraveyard.config.mobConfigEntries.get("revenant").blacklistedBiomes, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("revenant").enabled)),
                SpawnGroup.MONSTER, TGEntities.REVENANT,
                TheGraveyard.config.mobConfigEntries.get("revenant").weight,
                TheGraveyard.config.mobConfigEntries.get("revenant").minGroup,
                TheGraveyard.config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("reaper").whitelistedBiomesAndCategories, TheGraveyard.config.mobConfigEntries.get("reaper").blacklistedBiomes, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("reaper").enabled)),
                SpawnGroup.MONSTER, TGEntities.REAPER,
                TheGraveyard.config.mobConfigEntries.get("reaper").weight,
                TheGraveyard.config.mobConfigEntries.get("reaper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("nightmare").whitelistedBiomesAndCategories, TheGraveyard.config.mobConfigEntries.get("nightmare").blacklistedBiomes, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("nightmare").enabled)),
                SpawnGroup.MONSTER, TGEntities.NIGHTMARE,
                TheGraveyard.config.mobConfigEntries.get("nightmare").weight,
                TheGraveyard.config.mobConfigEntries.get("nightmare").minGroup,
                TheGraveyard.config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").whitelistedBiomesAndCategories, TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").blacklistedBiomes, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").enabled)),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").weight,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").maxGroup);
    }



    //TODO: use method from GraveyardStructure
    private static boolean parseBiomes(List<String> allowedBiomeCategory, List<String> blacklistedBiomes, BiomeSelectionContext biomeContext) {
        if (allowedBiomeCategory == null) {
            TheGraveyard.LOGGER.error("Error reading from the Graveyard config file: Allowed biome category is null. Try to delete the file and restart the game.");
            return false;
        }

        // no blacklist and biome is allowed as category or biome
        if ((allowedBiomeCategory.contains(biomeContext.getBiome().getCategory().toString().toLowerCase(Locale.ROOT)) || allowedBiomeCategory.contains(biomeContext.getBiomeKey().getValue().getPath())) && blacklistedBiomes.isEmpty()) {
            return true;
        }

        // blacklist and check if biome is on the blacklist
        if ((allowedBiomeCategory.contains(biomeContext.getBiome().getCategory().toString().toLowerCase(Locale.ROOT)) || allowedBiomeCategory.contains(biomeContext.getBiomeKey().getValue().getPath())) && !blacklistedBiomes.isEmpty()) {
            if (blacklistedBiomes.contains(biomeContext.getBiomeKey().getValue().getPath())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
