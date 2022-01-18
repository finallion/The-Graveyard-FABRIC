package com.finallion.graveyard.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;
import java.util.function.Consumer;

public class BiomeInjection {

    public static void addStructureToBiomes(Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> structureToMultiMap, Registry<Biome> biomeRegistry) {
        addConfiguredStructureEntries(structureToMultiMap, biomeRegistry, GraveyardStructure::addStructures);
    }

    private static void addConfiguredStructureEntries(Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> structureToMultiMap, Registry<Biome> biomeRegistry, Consumer<BiomeInjectionHelper> structureAddition) {
        for (Map.Entry<RegistryKey<Biome>, Biome> biomeEntry : biomeRegistry.getEntries()) {
            structureAddition.accept(new BiomeInjectionHelper(biomeEntry, biomeRegistry, structureToMultiMap));
        }

    }

    public static class BiomeInjectionHelper {
        public final Biome biome;
        public final RegistryKey<Biome> biomeKey;
        public final Registry<Biome> biomeRegistry;
        public final Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> structureToMultiMap;

        public BiomeInjectionHelper(Map.Entry<RegistryKey<Biome>, Biome> biomeEntry, Registry<Biome> biomeRegistry, Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> structureToMultiMap) {
            this.biome = biomeEntry.getValue();
            this.biomeKey = biomeEntry.getKey();
            this.biomeRegistry = biomeRegistry;
            this.structureToMultiMap = structureToMultiMap;
        }

        public RegistryKey<Biome> getBiomeKey() {
            return biomeRegistry.getKey(biome).get();
        }

        public void addStructure(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
            structureToMultiMap.computeIfAbsent(configuredStructureFeature.feature, (f) -> HashMultimap.create());
            structureToMultiMap.get(configuredStructureFeature.feature).put(configuredStructureFeature, this.biomeKey);
        }

        public void removeStructure(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
            Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> structureFeatureResourceKeyMultimap = structureToMultiMap.get(configuredStructureFeature.feature);
            structureFeatureResourceKeyMultimap.remove(configuredStructureFeature, biomeKey);
        }
    }
}




