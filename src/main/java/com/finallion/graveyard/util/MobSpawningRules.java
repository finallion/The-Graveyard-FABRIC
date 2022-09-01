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
                 .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("ghoul").whitelist, TheGraveyard.config.mobConfigEntries.get("ghoul").blacklist, context.getBiomeRegistryEntry()))
                 .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("ghoul").enabled)),
                SpawnGroup.MONSTER, TGEntities.GHOUL,
                TheGraveyard.config.mobConfigEntries.get("ghoul").weight,
                TheGraveyard.config.mobConfigEntries.get("ghoul").minGroup,
                TheGraveyard.config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("acolyte").whitelist, TheGraveyard.config.mobConfigEntries.get("acolyte").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("acolyte").enabled)),
                SpawnGroup.MONSTER, TGEntities.ACOLYTE,
                TheGraveyard.config.mobConfigEntries.get("acolyte").weight,
                TheGraveyard.config.mobConfigEntries.get("acolyte").minGroup,
                TheGraveyard.config.mobConfigEntries.get("acolyte").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("revenant").whitelist, TheGraveyard.config.mobConfigEntries.get("revenant").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("revenant").enabled)),
                SpawnGroup.MONSTER, TGEntities.REVENANT,
                TheGraveyard.config.mobConfigEntries.get("revenant").weight,
                TheGraveyard.config.mobConfigEntries.get("revenant").minGroup,
                TheGraveyard.config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("reaper").whitelist, TheGraveyard.config.mobConfigEntries.get("reaper").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("reaper").enabled)),
                SpawnGroup.MONSTER, TGEntities.REAPER,
                TheGraveyard.config.mobConfigEntries.get("reaper").weight,
                TheGraveyard.config.mobConfigEntries.get("reaper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("nightmare").whitelist, TheGraveyard.config.mobConfigEntries.get("nightmare").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("nightmare").enabled)),
                SpawnGroup.MONSTER, TGEntities.NIGHTMARE,
                TheGraveyard.config.mobConfigEntries.get("nightmare").weight,
                TheGraveyard.config.mobConfigEntries.get("nightmare").minGroup,
                TheGraveyard.config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").whitelist, TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").enabled)),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").weight,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("wraith").whitelist, TheGraveyard.config.mobConfigEntries.get("wraith").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("wraith").enabled)),
                SpawnGroup.MONSTER, TGEntities.WRAITH,
                TheGraveyard.config.mobConfigEntries.get("wraith").weight,
                TheGraveyard.config.mobConfigEntries.get("wraith").minGroup,
                TheGraveyard.config.mobConfigEntries.get("wraith").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").whitelist, TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").enabled)),
                SpawnGroup.MONSTER, TGEntities.CORRUPTED_PILLAGER,
                TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").weight,
                TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").minGroup,
                TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> BiomeSelectionUtil.parseBiomes(TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").whitelist, TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").blacklist, context.getBiomeRegistryEntry()))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").enabled)),
                SpawnGroup.MONSTER, TGEntities.CORRUPTED_VINDICATOR,
                TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").weight,
                TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").minGroup,
                TheGraveyard.config.mobConfigEntries.get("corrupted_vindicator").maxGroup);
    }

}

