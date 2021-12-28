package com.finallion.graveyard.mixin;


import com.finallion.graveyard.init.TGStructures;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(ConfiguredStructureFeatures.class)
public class MixinConfiguredStructureFeatures {

    @Inject(method = "registerAll", at = @At("TAIL"))
    private static void addStructuresToBiomes(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> consumer, CallbackInfo ci) {
        Set<RegistryKey<Biome>> mushroom_biomes = new HashSet<>();
        Set<RegistryKey<Biome>> forest_biomes = new HashSet<>();
        Set<RegistryKey<Biome>> plains_biomes = new HashSet<>();
        Set<RegistryKey<Biome>> haunted_biomes = new HashSet<>();
        Set<RegistryKey<Biome>> birch_biomes = new HashSet<>();
        Set<RegistryKey<Biome>> savanna_biomes = new HashSet<>();
        Set<RegistryKey<Biome>> mountain_biomes = new HashSet<>();

        mushroom_biomes.add(BiomeKeys.JUNGLE);
        mushroom_biomes.add(BiomeKeys.SPARSE_JUNGLE);
        mushroom_biomes.add(BiomeKeys.MUSHROOM_FIELDS);
        mushroom_biomes.add(BiomeKeys.SWAMP);

        birch_biomes.add(BiomeKeys.BIRCH_FOREST);
        birch_biomes.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);

        savanna_biomes.add(BiomeKeys.SAVANNA);
        savanna_biomes.add(BiomeKeys.SAVANNA_PLATEAU);
        savanna_biomes.add(BiomeKeys.WINDSWEPT_SAVANNA);
        savanna_biomes.add(BiomeKeys.BADLANDS);
        savanna_biomes.add(BiomeKeys.ERODED_BADLANDS);
        savanna_biomes.add(BiomeKeys.WOODED_BADLANDS);

        mountain_biomes.add(BiomeKeys.JAGGED_PEAKS);
        mountain_biomes.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS);
        mountain_biomes.add(BiomeKeys.WINDSWEPT_HILLS);
        mountain_biomes.add(BiomeKeys.STONY_PEAKS);
        mountain_biomes.add(BiomeKeys.STONY_SHORE);

        haunted_biomes.add(BiomeKeys.SWAMP);
        haunted_biomes.add(BiomeKeys.DARK_FOREST);
        haunted_biomes.add(BiomeKeys.TAIGA);

        forest_biomes.add(BiomeKeys.FOREST);
        forest_biomes.add(BiomeKeys.DARK_FOREST);
        forest_biomes.add(BiomeKeys.WINDSWEPT_FOREST);
        forest_biomes.add(BiomeKeys.OLD_GROWTH_PINE_TAIGA);
        forest_biomes.add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
        forest_biomes.add(BiomeKeys.TAIGA);
        forest_biomes.add(BiomeKeys.SNOWY_TAIGA);
        forest_biomes.add(BiomeKeys.GROVE);

        plains_biomes.add(BiomeKeys.PLAINS);
        plains_biomes.add(BiomeKeys.SNOWY_PLAINS);
        plains_biomes.add(BiomeKeys.MEADOW);
        plains_biomes.add(BiomeKeys.SUNFLOWER_PLAINS);
        plains_biomes.add(BiomeKeys.FLOWER_FOREST);


        // add structures to biomes
        consumer.accept(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG, BiomeKeys.DESERT);
        consumer.accept(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG, BiomeKeys.DESERT);

        plains_biomes.forEach((biome) -> {consumer.accept(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG, biome);});
        forest_biomes.forEach((biome) -> {consumer.accept(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG, biome);});
        birch_biomes.forEach((biome) -> {consumer.accept(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG, biome);});

        consumer.accept(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG, BiomeKeys.DESERT);

        plains_biomes.forEach((biome) -> {consumer.accept(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG, biome);});

        birch_biomes.forEach((biome) -> {consumer.accept(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG, biome);});

        forest_biomes.forEach((biome) -> {consumer.accept(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG, biome);});

        forest_biomes.forEach((biome) -> {consumer.accept(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG, biome);});

        haunted_biomes.forEach((biome) -> {consumer.accept(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG, biome);});

        mushroom_biomes.forEach((biome) -> {consumer.accept(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG, biome);});

        mountain_biomes.forEach((biome) -> {consumer.accept(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG, biome);});

        savanna_biomes.forEach((biome) -> {consumer.accept(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG, biome);});


    }
}