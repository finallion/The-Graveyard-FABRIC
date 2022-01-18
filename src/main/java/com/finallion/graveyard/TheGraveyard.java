package com.finallion.graveyard;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.init.*;

import com.finallion.graveyard.mixin.StructuresConfigAccessor;
import com.finallion.graveyard.util.BiomeInjection;
import com.finallion.graveyard.util.BiomeUtils;
import com.finallion.graveyard.util.MobSpawningRules;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import software.bernie.geckolib3.GeckoLib;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TheGraveyard implements ModInitializer {
    public static final String MOD_ID = "graveyard";
    public static final GraveyardConfig config = OmegaConfig.register(GraveyardConfig.class);

    // chisel gravestone
    // double skeleton

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        if (config.getHorde(new Identifier(MOD_ID, "horde_spawn")).enabled) {
            ServerWorldEvents.LOAD.register(new TGSpawner.WorldLoad());
            ServerLifecycleEvents.SERVER_STOPPED.register(new TGSpawner.ServerStopped());
            ServerTickEvents.END_WORLD_TICK.register(new TGSpawner.OnWorldTick());
        }

        TGBlocks.registerBlocks();
        TGItems.registerItems();
        TGEntities.registerEntities();
        TGProcessors.registerProcessors();
        TGConfiguredFeatures.registerFeatures();
        TGConfiguredFeatures.registerConfiguredFeatures();
        TGStructures.registerStructures();
        TGStructures.registerConfiguredStructures();
        addStructureSpawningToDimensionsAndBiomes();
        MobSpawningRules.addSpawnEntries();

    }

    public static ItemGroup GROUP = FabricItemGroupBuilder.create(
                    new Identifier(MOD_ID, "group"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL)).build();


    public static void addStructureSpawningToDimensionsAndBiomes() {
        // thanks to TelephaticGrunt https://github.com/TelepathicGrunt/RepurposedStructures-Fabric/blob/1.18/src/main/java/com/telepathicgrunt/repurposedstructures/RepurposedStructures.java
        Identifier runAfterFabricAPIPhase = new Identifier(TheGraveyard.MOD_ID, "run_after_fabric_api");
        ServerWorldEvents.LOAD.addPhaseOrdering(Event.DEFAULT_PHASE, runAfterFabricAPIPhase);

        ServerWorldEvents.LOAD.register(runAfterFabricAPIPhase, (MinecraftServer minecraftServer, ServerWorld serverWorld) -> {
            StructuresConfig worldStructureSettings = serverWorld.getChunkManager().getChunkGenerator().getStructuresConfig();

            Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> tempStructureToMultiMap = new HashMap<>();
            ((StructuresConfigAccessor) worldStructureSettings).getConfiguredStructures().forEach((key, value) -> tempStructureToMultiMap.put(key, HashMultimap.create(value)));
            BiomeInjection.addStructureToBiomes(tempStructureToMultiMap, minecraftServer.getRegistryManager().get(Registry.BIOME_KEY));

            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> immutableOuterMap = ImmutableMap.builder();
            tempStructureToMultiMap.forEach((key, value) -> {
                ImmutableMultimap.Builder<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> immutableInnerMultiMap = ImmutableMultimap.builder();
                immutableInnerMultiMap.putAll(value);
                immutableOuterMap.put(key, immutableInnerMultiMap.build());
            });

            ((StructuresConfigAccessor) worldStructureSettings).setConfiguredStructures(immutableOuterMap.build());

            Map<StructureFeature<?>, StructureConfig> tempMap = new HashMap<>(worldStructureSettings.getStructures());

            if (serverWorld.getChunkManager().getChunkGenerator() instanceof FlatChunkGenerator && serverWorld.getDimension().equals(World.OVERWORLD)) {
                tempMap.keySet().removeAll(TGStructures.structures);
            }
            ((StructuresConfigAccessor) worldStructureSettings).setStructures(tempMap);
        });

    }


    /*
    private static void addStructureToBiomes(Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> tempStructureToMultiMap, Registry<Biome> biomeRegistry) {
        // desert graveyard
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG)));

        // graves
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_desert_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_desert_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG)));

        // mushroom grave
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("mushroom_grave").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("mushroom_grave").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG)));

        // small graveyard
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_graveyard"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("small_graveyard").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG)));

        // memorial tree
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "memorial_tree"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("memorial_tree").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("memorial_tree").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG)));

        // medium graveyard
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "medium_graveyard"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("medium_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("medium_graveyard").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG)));

        // large graveyard
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_graveyard"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("large_graveyard").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("large_graveyard").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG)));

        // haunted house
        BiomeModifications.addStructure(BiomeSelectors.all()
                .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "haunted_house"))))
                .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("haunted_house").allowedBiomeCategories, TheGraveyard.config.structureConfigEntries.get("haunted_house").blacklistedBiomes, context)), RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG)));
    }

     */


}
