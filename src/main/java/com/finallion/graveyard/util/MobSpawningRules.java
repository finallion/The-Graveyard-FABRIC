package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Predicate;

public class MobSpawningRules {


    public static void addSpawnEntries() {
        BiomeModifications.addSpawn(BiomeSelectors.all()
                 .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("ghoul").allowedBiomes, context))
                 .and(booleanToPredicate(TheGraveyard.config.mobSpawn(new Identifier(TheGraveyard.MOD_ID, "ghoul")))),
                SpawnGroup.MONSTER, TGEntities.GHOUL,
                TheGraveyard.config.mobConfigEntries.get("ghoul").weight,
                TheGraveyard.config.mobConfigEntries.get("ghoul").minGroup,
                TheGraveyard.config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("acolyte").allowedBiomes, context))
                        .and(booleanToPredicate(TheGraveyard.config.mobSpawn(new Identifier(TheGraveyard.MOD_ID, "acolyte")))),
                SpawnGroup.MONSTER, TGEntities.ACOLYTE,
                TheGraveyard.config.mobConfigEntries.get("acolyte").weight,
                TheGraveyard.config.mobConfigEntries.get("acolyte").minGroup,
                TheGraveyard.config.mobConfigEntries.get("acolyte").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("revenant").allowedBiomes, context))
                        .and(booleanToPredicate(TheGraveyard.config.mobSpawn(new Identifier(TheGraveyard.MOD_ID, "revenant")))),
                SpawnGroup.MONSTER, TGEntities.REVENANT,
                TheGraveyard.config.mobConfigEntries.get("revenant").weight,
                TheGraveyard.config.mobConfigEntries.get("revenant").minGroup,
                TheGraveyard.config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("reaper").allowedBiomes, context))
                        .and(booleanToPredicate(TheGraveyard.config.mobSpawn(new Identifier(TheGraveyard.MOD_ID, "reaper")))),
                SpawnGroup.MONSTER, TGEntities.REAPER,
                TheGraveyard.config.mobConfigEntries.get("reaper").weight,
                TheGraveyard.config.mobConfigEntries.get("reaper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("nightmare").allowedBiomes, context))
                        .and(booleanToPredicate(TheGraveyard.config.mobSpawn(new Identifier(TheGraveyard.MOD_ID, "nightmare")))),
                SpawnGroup.MONSTER, TGEntities.NIGHTMARE,
                TheGraveyard.config.mobConfigEntries.get("nightmare").weight,
                TheGraveyard.config.mobConfigEntries.get("nightmare").minGroup,
                TheGraveyard.config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addSpawn(BiomeSelectors.all()
                        .and(context -> parseBiomes(TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").allowedBiomes, context))
                        .and(booleanToPredicate(TheGraveyard.config.mobSpawn(new Identifier(TheGraveyard.MOD_ID, "skeleton_creeper")))),
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").weight,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").minGroup,
                TheGraveyard.config.mobConfigEntries.get("skeleton_creeper").maxGroup);
    }

    public static Predicate<BiomeSelectionContext> booleanToPredicate(boolean bol) {
        return (context) -> bol;
    }

    private static boolean parseBiomes(List<String> biomes, BiomeSelectionContext biomeContext) {
        return biomes.contains(biomeContext.getBiomeKey().getValue().toString()) || biomes.contains("#" + biomeContext.getBiome().getCategory().asString());
    }
}
