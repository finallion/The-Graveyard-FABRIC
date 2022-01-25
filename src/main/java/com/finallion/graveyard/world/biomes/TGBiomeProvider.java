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
import terrablender.api.BiomeProvider;
import terrablender.api.ParameterUtils;
import terrablender.worldgen.TBClimate;


import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


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

            /*
            List<MultiNoiseUtil.NoiseHypercube> swampPoint = new ParameterUtils.ParameterPointListBuilder()
                    .temperature(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL, ParameterUtils.Temperature.WARM)
                    .humidity(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID)
                    .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.NEAR_INLAND, ParameterUtils.Continentalness.FAR_INLAND), ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.FAR_INLAND))
                    .erosion(ParameterUtils.Erosion.EROSION_5)
                    .weirdness(ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING, ParameterUtils.Weirdness.MID_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.MID_SLICE_VARIANT_DESCENDING)
                    .buildVanilla();
            swampPoint.forEach(point -> builder.replaceBiome(point, TGBiomes.HAUNTED_LAKES_KEY));
             */
        });

        /*
        addBiome(mapper, TBClimate.parameters(
                MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),  // temperature
                MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),  // humidity
                MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),  // continentalness
                MultiNoiseUtil.ParameterRange.of(0.7F, 1.0F),   // erosion
                MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F),  // depth 1.0F is underground
                MultiNoiseUtil.ParameterRange.of(0.4F, 0.56666666F).combine(MultiNoiseUtil.ParameterRange.of(0.7666667F, 0.93333334F).combine(MultiNoiseUtil.ParameterRange.of(-0.56666666F, -0.4F))),  // weirdness
                getUniquenessParameter(),
                0.0F
        ), TGBiomes.HAUNTED_LAKES_KEY);

         */

    }


    @Override
    public Optional<MaterialRules.MaterialRule> getOverworldSurfaceRules() {
        return Optional.of(TGSurfaceRules.makeRules());
    }
}

