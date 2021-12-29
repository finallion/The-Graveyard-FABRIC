package com.finallion.graveyard;

import com.finallion.graveyard.init.*;
import com.finallion.graveyard.world.processors.RemoveWaterloggedProcessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import software.bernie.geckolib3.GeckoLib;

public class TheGraveyard implements ModInitializer {
    public static final String MOD_ID = "graveyard";

    // ghoul raids
    // chisel gravestone
    // double skeleton
    // coffin

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        TGBlocks.registerBlocks();
        TGItems.registerItems();
        TGEntities.registerEntities();
        TGProcessors.registerProcessors();
        TGConfiguredFeatures.registerFeatures();
        TGConfiguredFeatures.registerConfiguredFeatures();
        TGStructures.registerStructures();
        TGStructures.registerConfiguredStructures();
        addStructureSpawningToDimensionsAndBiomes();

    }

    public static ItemGroup GROUP = FabricItemGroupBuilder.create(
                    new Identifier(MOD_ID, "group"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL)).build();


    public static void addStructureSpawningToDimensionsAndBiomes() {
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.DESERT), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.DESERT), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.SAVANNA), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.MESA), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.EXTREME_HILLS), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.MOUNTAIN), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.PLAINS, Biome.Category.FOREST, Biome.Category.TAIGA), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.SWAMP, Biome.Category.JUNGLE, Biome.Category.MUSHROOM), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.PLAINS), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure((context -> context.getBiomeKey().toString().contains("birch")), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.FOREST).and((context -> !context.getBiomeKey().toString().contains("birch") || !context.getBiomeKey().toString().contains("dark_forest") )), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.TAIGA).and((context -> !context.getBiomeKey().toString().contains("birch"))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.FOREST).and((context -> context.getBiomeKey().toString().contains("dark_forest"))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.SWAMP).or(context -> context.getBiomeKey().toString().contains("dark_forest")), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG)));

    }
}
