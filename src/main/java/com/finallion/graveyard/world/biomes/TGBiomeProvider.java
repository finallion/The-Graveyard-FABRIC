package com.finallion.graveyard.world.biomes;

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
            builder.replaceBiome(BiomeKeys.DARK_FOREST, TGBiomes.HAUNTED_LAKES_KEY);
            builder.replaceBiome(BiomeKeys.DARK_FOREST, TGBiomes.ERODED_HAUNTED_FOREST_KEY);
            // More complex example:
            // Replace specific parameter points for the frozen peaks with our cold_blue biome

            /*
            List<MultiNoiseUtil.NoiseHypercube> frozenPeaksPoints = new ParameterUtils.ParameterPointListBuilder()
                    .temperature(ParameterUtils.Temperature.ICY, ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL)
                    .humidity(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY, ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET, ParameterUtils.Humidity.HUMID)
                    .continentalness(ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.COAST, ParameterUtils.Continentalness.FAR_INLAND), ParameterUtils.Continentalness.span(ParameterUtils.Continentalness.MID_INLAND, ParameterUtils.Continentalness.FAR_INLAND))
                    .erosion(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)
                    .depth(ParameterUtils.Depth.SURFACE, ParameterUtils.Depth.FLOOR)
                    .weirdness(ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_ASCENDING, ParameterUtils.Weirdness.PEAK_VARIANT, ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_DESCENDING)
                    .buildVanilla();

            frozenPeaksPoints.forEach(point -> builder.replaceBiome(point, TGBiomes.ERODED_HAUNTED_FOREST_KEY));

             */

        });
    }


    @Override
    public Optional<MaterialRules.MaterialRule> getOverworldSurfaceRules()
    {
        return Optional.of(TGSurfaceRules.makeRules());
    }
}

