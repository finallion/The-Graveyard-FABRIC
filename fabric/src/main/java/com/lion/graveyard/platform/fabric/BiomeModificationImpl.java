package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.Graveyard;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class BiomeModificationImpl {

    public static void addSpawnEntries() {
        BiomeModifications.addSpawn(BiomeSelectors.tag(main.java.com.lion.graveyard.init.TGTags.GHOUL_SPAWNS),
                SpawnGroup.MONSTER, main.java.com.lion.graveyard.init.TGEntities.GHOUL,
                Graveyard.config.mobConfigEntries.get("ghoul").weight,
                Graveyard.config.mobConfigEntries.get("ghoul").minGroup,
                Graveyard.config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(main.java.com.lion.graveyard.init.TGTags.REVENANT_SPAWNS),
                SpawnGroup.MONSTER, main.java.com.lion.graveyard.init.TGEntities.REVENANT,
                Graveyard.config.mobConfigEntries.get("revenant").weight,
                Graveyard.config.mobConfigEntries.get("revenant").minGroup,
                Graveyard.config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(main.java.com.lion.graveyard.init.TGTags.REAPER_SPAWNS),
                SpawnGroup.MONSTER, main.java.com.lion.graveyard.init.TGEntities.REAPER,
                Graveyard.config.mobConfigEntries.get("reaper").weight,
                Graveyard.config.mobConfigEntries.get("reaper").minGroup,
                Graveyard.config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(main.java.com.lion.graveyard.init.TGTags.NIGHTMARE_SPAWNS),
                SpawnGroup.MONSTER, main.java.com.lion.graveyard.init.TGEntities.NIGHTMARE,
                Graveyard.config.mobConfigEntries.get("nightmare").weight,
                Graveyard.config.mobConfigEntries.get("nightmare").minGroup,
                Graveyard.config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.tag(main.java.com.lion.graveyard.init.TGTags.SKELETON_CREEPER_SPAWNS).and(BiomeSelectors.excludeByKey(BiomeKeys.MUSHROOM_FIELDS)),
                SpawnGroup.MONSTER, main.java.com.lion.graveyard.init.TGEntities.SKELETON_CREEPER,
                Graveyard.config.mobConfigEntries.get("skeleton_creeper").weight,
                Graveyard.config.mobConfigEntries.get("skeleton_creeper").minGroup,
                Graveyard.config.mobConfigEntries.get("skeleton_creeper").maxGroup);
    }
}
