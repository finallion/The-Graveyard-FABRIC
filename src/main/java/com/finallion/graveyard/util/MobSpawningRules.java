package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.BiomeTags;


import java.util.List;
import java.util.Locale;

public class MobSpawningRules {


    public static void addSpawnEntries() {
        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("ghoul").modWhitelist, context))
                 .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("ghoul").whitelist, TheGraveyard.config.mobConfigEntries.get("ghoul").blacklist, context))
                 .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("ghoul").enabled)),
                SpawnGroup.MONSTER, TGEntities.GHOUL,
                TheGraveyard.config.mobConfigEntries.get("ghoul").weight,
                TheGraveyard.config.mobConfigEntries.get("ghoul").minGroup,
                TheGraveyard.config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("acolyte").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("acolyte").whitelist, TheGraveyard.config.mobConfigEntries.get("acolyte").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("acolyte").enabled)),
                SpawnGroup.MONSTER, TGEntities.ACOLYTE,
                TheGraveyard.config.mobConfigEntries.get("acolyte").weight,
                TheGraveyard.config.mobConfigEntries.get("acolyte").minGroup,
                TheGraveyard.config.mobConfigEntries.get("acolyte").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("revenant").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("revenant").whitelist, TheGraveyard.config.mobConfigEntries.get("revenant").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("revenant").enabled)),
                SpawnGroup.MONSTER, TGEntities.REVENANT,
                TheGraveyard.config.mobConfigEntries.get("revenant").weight,
                TheGraveyard.config.mobConfigEntries.get("revenant").minGroup,
                TheGraveyard.config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("reaper").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("reaper").whitelist, TheGraveyard.config.mobConfigEntries.get("reaper").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("reaper").enabled)),
                SpawnGroup.MONSTER, TGEntities.REAPER,
                TheGraveyard.config.mobConfigEntries.get("reaper").weight,
                TheGraveyard.config.mobConfigEntries.get("reaper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("nightmare").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("nightmare").whitelist, TheGraveyard.config.mobConfigEntries.get("nightmare").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("nightmare").enabled)),
                SpawnGroup.MONSTER, TGEntities.NIGHTMARE,
                TheGraveyard.config.mobConfigEntries.get("nightmare").weight,
                TheGraveyard.config.mobConfigEntries.get("nightmare").minGroup,
                TheGraveyard.config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").whitelist, TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").enabled)),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").weight,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("wraith").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("wraith").whitelist, TheGraveyard.config.mobConfigEntries.get("wraith").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("wraith").enabled)),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("wraith").weight,
                TheGraveyard.config.mobConfigEntries.get("wraith").minGroup,
                TheGraveyard.config.mobConfigEntries.get("wraith").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").whitelist, TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").enabled)),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").weight,
                TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").minGroup,
                TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").whitelist, TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").enabled)),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").weight,
                TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").minGroup,
                TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").maxGroup);
    }

    private static boolean parseBiomes(List<String> whitelist, List<String> blacklist, BiomeSelectionContext biomeContext) {
        String biomeIdentifier = biomeContext.getBiomeKey().getValue().toString();
        String biomeCategory = biomeContext.getBiome().getCategory(biomeContext.getBiomeRegistryEntry()).getName();

        if (whitelist == null) {
            TheGraveyard.LOGGER.error("The Graveyard config file (\"the-graveyard-config.json5\") isn't up to date. Please delete the file and restart the game to create a new config file!");
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

    private static boolean parseWhitelistedMods(List<String> whitelist, BiomeSelectionContext biomeContext) {
        if (whitelist == null) {
            TheGraveyard.LOGGER.error("Error reading from the Graveyard config file: Allowed biome category/biome is null. Try to delete the file and restart the game.");
            return false;
        }

        String modid = biomeContext.getBiomeKey().getValue().getNamespace();
        return whitelist.contains("#" + modid);

    }

}
