package com.finallion.graveyard.biomes;

import com.finallion.graveyard.biomes.surfacebuilders.TGSurfaceBuilders;
import com.finallion.graveyard.init.TGEntities;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class HauntedForestBiomes {

    public static Biome HauntedForestBiome() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        //spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(TGEntities.GHOUL, 1, 2, 5));


        // TODO: Mineshafts etc.
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        generationSettings.surfaceBuilder(TGSurfaceBuilders.HAUNTED_FOREST_SURFACE_CONFIG);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
        DefaultBiomeFeatures.addLandCarvers(generationSettings);
        DefaultBiomeFeatures.addDungeons(generationSettings);
        DefaultBiomeFeatures.addMineables(generationSettings);
        DefaultBiomeFeatures.addDefaultOres(generationSettings);
        DefaultBiomeFeatures.addDefaultDisks(generationSettings);
        DefaultBiomeFeatures.addSprings(generationSettings);
        DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
        DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
        DefaultBiomeFeatures.addMossyRocks(generationSettings);
        DefaultBiomeFeatures.addTaigaGrass(generationSettings);
        DefaultBiomeFeatures.addGiantTaigaGrass(generationSettings);
        DefaultBiomeFeatures.addLargeFerns(generationSettings);


        TGBiomeFeatures.addGraveyardSpruceTrees(generationSettings);



        return (new Biome.Builder())
                .precipitation(Biome.Precipitation.RAIN)
                .category(Biome.Category.FOREST)
                .depth(0.125F)
                .scale(0.05F)
                .temperature(0.8F)
                .downfall(0.4F)
                .effects((new BiomeEffects.Builder())
                        .waterColor(0xC60B21)
                        .waterFogColor(0xD80D28)
                        .fogColor(0x878787)
                        .skyColor(0x878787)
                        .music(MusicType.GAME)
                        .loopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D))
                        .additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D))
                        //.particleConfig(new BiomeParticleConfig(ParticleTypes.SOUL, 0.625F))
                        .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();

    }

}



