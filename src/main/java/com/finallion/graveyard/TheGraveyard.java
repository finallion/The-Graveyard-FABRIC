package com.finallion.graveyard;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.*;

import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import software.bernie.geckolib3.GeckoLib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TheGraveyard implements ModInitializer {
    public static final String MOD_ID = "graveyard";
    public static final GraveyardConfig config = OmegaConfig.register(GraveyardConfig.class);

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
        /*
        Identifier runAfterFabricAPIPhase = new Identifier(TheGraveyard.MOD_ID, "run_after_fabric_api");
        ServerWorldEvents.LOAD.addPhaseOrdering(Event.DEFAULT_PHASE, runAfterFabricAPIPhase);

        ServerWorldEvents.LOAD.register(runAfterFabricAPIPhase, (MinecraftServer minecraftServer, ServerWorld serverWorld) -> {

            StructuresConfig worldStructureSettings = serverWorld.getChunkManager().getChunkGenerator().getStructuresConfig();

            Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> tempStructureToMultiMap = new HashMap<>();

            biomeLoop:
            for (Map.Entry<RegistryKey<Biome>, Biome> biomeEntry : serverWorld.getRegistryManager().get(Registry.BIOME_KEY).getEntries()) {
                Biome.Category biomeCategory = biomeEntry.getValue().getCategory();

                String name = biomeEntry.getValue().toString();

                if (name == null) {
                    continue;
                }

                if (biomeCategory.equals(Biome.Category.OCEAN) || biomeCategory.equals(Biome.Category.RIVER)) {
                    continue biomeLoop;
                }

                for (StructureFeature<?> structure : TGStructures.structures) {
                    AbstractGraveyardStructure abstractStructure = (AbstractGraveyardStructure) structure;
                    StructureConfigEntry structureConfig = abstractStructure.getStructureConfigEntry();

                    // check in config if structure is allowed to generate
                    if (structureConfig.canGenerate) {
                        // check the biome blacklist
                        if (checkBiome(structureConfig.getBiomeCategories(), structureConfig.getBlacklistedBiomes(), name, biomeCategory)) {
                            tempStructureToMultiMap.put(structure, HashMultimap.create());
                            Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> configuredStructureToBiomeMultiMap = tempStructureToMultiMap.get(structure);
                            configuredStructureToBiomeMultiMap.put(((AbstractGraveyardStructure) structure).getStructureFeature(), biomeEntry.getKey());

                        }
                    }

                }
            }


            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> immutableOuterMap = ImmutableMap.builder();
            tempStructureToMultiMap.forEach((key, value) -> {
                ImmutableMultimap.Builder<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> immutableInnerMultiMap = ImmutableMultimap.builder();
                immutableInnerMultiMap.putAll(value);
                immutableOuterMap.put(key, immutableInnerMultiMap.build());
            });


            ((StructuresConfigAccessor) worldStructureSettings).setConfiguredStructures(immutableOuterMap.build());

        });


         */

        // desert graveyard
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.DESERT).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG)));

        // graves
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.DESERT).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.SAVANNA).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.MESA).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.EXTREME_HILLS).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.MOUNTAIN).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.PLAINS, Biome.Category.FOREST, Biome.Category.TAIGA).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG)));

        // mushroom grave
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.SWAMP, Biome.Category.JUNGLE, Biome.Category.MUSHROOM).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG)));

        // small graveyard
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.PLAINS).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_graveyard")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG)));

        // memorial tree
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.FOREST).and(context -> context.getBiomeKey().toString().contains("birch")).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "memorial_tree")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG)));

        // medium graveyard
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.FOREST).and((context -> !context.getBiomeKey().toString().contains("birch") || !context.getBiomeKey().toString().contains("dark_forest"))).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "medium_graveyard")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG)));

        // large graveyard
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.TAIGA).and((context -> !context.getBiomeKey().toString().contains("birch"))).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_graveyard")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.FOREST).and((context -> context.getBiomeKey().toString().contains("dark_forest"))).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_graveyard")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG)));

        // haunted house
        BiomeModifications.addStructure(BiomeSelectors.categories(Biome.Category.SWAMP).or(context -> context.getBiomeKey().toString().contains("dark_forest")).and(booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "haunted_house")))), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG)));



    }

    public static Predicate<BiomeSelectionContext> booleanToPredicate(boolean bol) {
        return (context) -> bol;
    }

    private static boolean checkBiome(List<? extends String> allowedBiomeCategories, List<? extends String> blacklistedBiomes, String name, Biome.Category category) {

        // if the category of the now checked biome is in the allowed list of the structure and the blacklist is not empty
        if (allowedBiomeCategories.contains(category.getName()) && !blacklistedBiomes.isEmpty()) {
            // if the blacklist contains the biome (not category)
            if (blacklistedBiomes.contains(name)) {
                return false;
            } else {
                return true;
            }
        }

        if (allowedBiomeCategories.contains(category.getName()) && blacklistedBiomes.isEmpty()) {
            return true;
        }

        return false;
    }
}
