package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;


import java.util.List;
import java.util.Locale;

public class MobSpawningRules {

    public static void addSpawnEntries() {
        BiomeModifications.addSpawn(BiomeSelectors.tag(TGTags.GHOUL_SPAWNS),
                SpawnGroup.MONSTER, TGEntities.GHOUL,
                TheGraveyard.config.mobConfigEntries.get("ghoul").weight,
                TheGraveyard.config.mobConfigEntries.get("ghoul").minGroup,
                TheGraveyard.config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(TGTags.REVENANT_SPAWNS),
                SpawnGroup.MONSTER, TGEntities.REVENANT,
                TheGraveyard.config.mobConfigEntries.get("revenant").weight,
                TheGraveyard.config.mobConfigEntries.get("revenant").minGroup,
                TheGraveyard.config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(TGTags.REAPER_SPAWNS),
                SpawnGroup.MONSTER, TGEntities.REAPER,
                TheGraveyard.config.mobConfigEntries.get("reaper").weight,
                TheGraveyard.config.mobConfigEntries.get("reaper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(TGTags.NIGHTMARE_SPAWNS),
                SpawnGroup.MONSTER, TGEntities.NIGHTMARE,
                TheGraveyard.config.mobConfigEntries.get("nightmare").weight,
                TheGraveyard.config.mobConfigEntries.get("nightmare").minGroup,
                TheGraveyard.config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(TGTags.SKELETON_CREEPER_SPAWNS),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").weight,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").maxGroup);
    }

}

