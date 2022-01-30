package com.finallion.graveyard.world.biomes;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGBiomes;
import com.finallion.graveyard.world.surfacerules.TGSurfaceRules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;


import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/*
public class TGBiomeProvider extends BiomeProvider {
    public TGBiomeProvider(Identifier name, int overworldWeight) {
        super(name, overworldWeight);
    }

    @Override
    public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, RegistryKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {

            if (TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID,"haunted_forest")).canGenerate) {
                builder.replaceBiome(BiomeKeys.WINDSWEPT_FOREST, TGBiomes.HAUNTED_FOREST_KEY);
            }

            if (TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest")).canGenerate) {
                builder.replaceBiome(BiomeKeys.MEADOW, TGBiomes.ERODED_HAUNTED_FOREST_KEY);
            }

            if (TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID,"haunted_lakes")).canGenerate) {
                builder.replaceBiome(BiomeKeys.SWAMP, TGBiomes.HAUNTED_LAKES_KEY);
            }

            if (TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID,"ancient_dead_coral_reef")).canGenerate) {
                builder.replaceBiome(BiomeKeys.BEACH, TGBiomes.ANCIENT_DEAD_CORAL_REEF_KEY);
            }

        });
    }


    @Override
    public Optional<MaterialRules.MaterialRule> getOverworldSurfaceRules() {
        return Optional.of(TGSurfaceRules.makeRules());
    }
}

*/
