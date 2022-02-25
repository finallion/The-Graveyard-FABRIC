package com.finallion.graveyard;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.init.*;

import com.finallion.graveyard.mixin.StructuresConfigAccessor;
import com.finallion.graveyard.util.BiomeInjection;
import com.finallion.graveyard.util.BiomeUtils;
import com.finallion.graveyard.util.MobSpawningRules;
import com.finallion.graveyard.world.biomes.TGBiomeProvider;
import com.finallion.graveyard.world.noise.TGNoiseParameters;
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
import net.minecraft.client.render.DimensionEffects;
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
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import terrablender.api.BiomeProviders;
import terrablender.api.TerraBlenderApi;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TheGraveyard implements ModInitializer, TerraBlenderApi {
    public static final String MOD_ID = "graveyard";
    public static final GraveyardConfig config = OmegaConfig.register(GraveyardConfig.class);
    public static final Logger LOGGER = LogManager.getLogger();

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

        TGNoiseParameters.init();
        TGBiomes.registerBiomes();

    }


    @Override
    public void onTerraBlenderInitialized() {
        BiomeProviders.register(new TGBiomeProvider(new Identifier(MOD_ID, "biome_provider"), 1));
    }

    public static ItemGroup GROUP = FabricItemGroupBuilder.create(
                    new Identifier(MOD_ID, "group"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL)).build();


    public static void addStructureSpawningToDimensionsAndBiomes() {

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("haunted_house").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("haunted_house").whitelist, TheGraveyard.config.structureConfigEntries.get("haunted_house").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("haunted_house").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("large_graveyard").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("large_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("large_graveyard").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("large_graveyard").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.LARGE_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("medium_graveyard").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("medium_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("medium_graveyard").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("medium_graveyard").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("memorial_tree").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("memorial_tree").whitelist, TheGraveyard.config.structureConfigEntries.get("memorial_tree").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("memorial_tree").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("mushroom_grave").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("mushroom_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("mushroom_grave").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("mushroom_grave").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.MUSHROOM_GRAVE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("small_desert_grave").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_desert_grave").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("small_desert_grave").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("small_desert_graveyard").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("small_grave").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_grave").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("small_grave").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("small_graveyard").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_graveyard").whitelist, TheGraveyard.config.structureConfigEntries.get("small_graveyard").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("small_graveyard").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_GRAVEYARD_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("small_mountain_grave").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE_CONFIG)));

        BiomeModifications.addStructure(
                BiomeSelectors.all()
                        .and(context -> parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").modWhitelist, context))
                        .and(context -> parseBiomes(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").whitelist, TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").blacklist, context))
                        .and(BiomeUtils.booleanToPredicate(TheGraveyard.config.structureConfigEntries.get("small_savanna_grave").enabled)),
                RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getId(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE_CONFIG)));



        /*
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


            // structures only spawn in overworld
            //if (!serverWorld.getDimension().equals(World.OVERWORLD)) {
            //    tempMap.keySet().removeAll(TGStructures.structures);
            //}

            ((StructuresConfigAccessor) worldStructureSettings).setStructures(tempMap);
        });

         */

    }

    private static boolean parseWhitelistedMods(List<String> whitelist, BiomeSelectionContext biomeContext) {
        String modid = biomeContext.getBiomeKey().getValue().getNamespace();
        return whitelist.contains("#" + modid);

    }

    private static boolean parseBiomes(List<String> whitelist, List<String> blacklist, BiomeSelectionContext biomeContext) {
        String biomeIdentifier = biomeContext.getBiomeKey().getValue().toString();
        String biomeCategory = biomeContext.getBiome().getCategory().getName();


        if (whitelist == null) {
            TheGraveyard.LOGGER.error("Error reading from the Graveyard config file: Allowed biome category/biome is null. Try to delete the file and restart the game.");
            return false;
        }

        // no blacklist and biome is allowed
        if (whitelist.contains(biomeIdentifier) && blacklist.isEmpty()) {
            return true;
        }

        // no blacklist and biomeCategory is allowed
        if (whitelist.contains("#" + biomeCategory) && blacklist.isEmpty()) {
            return true;
        }

        // blacklist exists and check if biome is on the blacklist
        if (whitelist.contains(biomeIdentifier) && !blacklist.isEmpty()) {
            if (blacklist.contains("#" + biomeCategory)) { // whitelist weighs higher than blacklist
                //TheGraveyard.LOGGER.error("Blacklisted biome category #" + biomeCategory + " contains whitelisted biome " + biomeIdentifier + ".");
                return true;
            } else if (blacklist.contains(biomeIdentifier)) {  // blacklist weighs higher than whitelist
                TheGraveyard.LOGGER.debug("Biome " +  biomeIdentifier + " is on whitelist and blacklist.");
                return false;
            } else {
                return true;
            }
        }


        // blacklist exists and check if biomeCategory is on the blacklist
        if (whitelist.contains("#" + biomeCategory) && !blacklist.isEmpty()) {
            if (blacklist.contains("#" + biomeCategory)) { // blacklist weighs higher than whitelist
                TheGraveyard.LOGGER.debug("Biome category #" + biomeCategory + " is on whitelist and blacklist.");
                return false;
            } else if (blacklist.contains(biomeIdentifier)) { // blacklist weighs higher than whitelist
                //TheGraveyard.LOGGER.error("Biome category #" + biomeCategory + " is on whitelist and subsidiary biome " + biomeIdentifier + " is on blacklist.");
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
