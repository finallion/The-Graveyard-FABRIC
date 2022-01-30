package com.finallion.graveyard.world.biomes;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGBiomes;
import com.finallion.graveyard.init.TGConfiguredFeatures;
import com.finallion.graveyard.init.TGEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.OceanPlacedFeatures;


public class AncientDeadCoralReef {

    public static Biome createAncientDeadCoralReef() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addOceanMobs(spawnSettings, 3, 4, 15);
        spawnSettings.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        spawnSettings.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
        DefaultBiomeFeatures.addBatsAndMonsters(spawnSettings);

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addSprings(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_WARM);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.KELP_WARM);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.ANCIENT_DEAD_CORAL_REEF_PLACED_FEATURE);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.ANCIENT_DEAD_CORAL_REEF_WATER_PLACED_FEATURE);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.DEAD_CORAL_PATCH_PLACED_FEATURE);

        // add structures to the biome
        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID + "ancient_dead_coral_reef_structures"))
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.includeByKey(TGBiomes.ANCIENT_DEAD_CORAL_REEF_KEY), ctx -> {
                    ctx.getGenerationSettings().addStructure(RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier("nether_fossil")));
                }).add(ModificationPhase.ADDITIONS, BiomeSelectors.includeByKey(TGBiomes.ANCIENT_DEAD_CORAL_REEF_KEY), ctx -> {
                    ctx.getGenerationSettings().addStructure(RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier("ocean_ruin_cold")));
                }).add(ModificationPhase.ADDITIONS, BiomeSelectors.includeByKey(TGBiomes.ANCIENT_DEAD_CORAL_REEF_KEY), ctx -> {
                    ctx.getGenerationSettings().addStructure(RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier("shipwreck")));
                }).add(ModificationPhase.ADDITIONS, BiomeSelectors.includeByKey(TGBiomes.ANCIENT_DEAD_CORAL_REEF_KEY), ctx -> {
                    ctx.getGenerationSettings().addStructure(RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier("shipwreck_beached")));
                });

        return (new Biome.Builder())
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.BEACH)
                .temperature(0.5F)
                .downfall(0.5F)
                .effects((new BiomeEffects.Builder())
                        .waterColor(0xFFFFFF)
                        .waterFogColor(0xFFFFFF)
                        .fogColor(12638463) // default
                        .skyColor(12638463) // default
                        .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();

    }
}
