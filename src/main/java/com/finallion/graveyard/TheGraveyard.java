package com.finallion.graveyard;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.init.*;

import com.finallion.graveyard.util.BiomeUtils;
import com.finallion.graveyard.util.MobSpawningRules;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import software.bernie.geckolib3.GeckoLib;

import java.util.List;
import java.util.Locale;

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

    private static boolean parseBiomes(List<String> allowedBiomeCategory, List<String> blacklistedBiomes, BiomeSelectionContext biomeContext) {
        // no blacklist and biome is allowed
        if (allowedBiomeCategory.contains(biomeContext.getBiome().getCategory().toString().toLowerCase(Locale.ROOT)) && blacklistedBiomes.isEmpty()) {
            return true;
        }

        // blacklist and check if biome is on the blacklist
        if (allowedBiomeCategory.contains(biomeContext.getBiome().getCategory().toString().toLowerCase(Locale.ROOT)) && !blacklistedBiomes.isEmpty()) {
            if (blacklistedBiomes.contains(biomeContext.getBiomeKey().getValue().getPath())) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

}
