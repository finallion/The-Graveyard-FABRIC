package com.finallion.graveyard.init.structureKeys;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public class TGConfiguredStructureFeatureKeys {
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> HAUNTED_HOUSE = of("haunted_house");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> LARGE_GRAVEYARD = of("large_graveyard");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> MEDIUM_GRAVEYARD = of("medium_graveyard");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> SMALL_GRAVEYARD = of("small_graveyard");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> SMALL_DESERT_GRAVEYARD = of("small_desert_graveyard");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> SMALL_GRAVE = of("small_grave");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> SMALL_DESERT_GRAVE = of("small_desert_grave");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> SMALL_SAVANNA_GRAVE = of("small_savanna_grave");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> SMALL_MOUNTAIN_GRAVE = of("small_mountain_grave");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> MUSHROOM_GRAVE = of("mushroom_grave");
    public static RegistryKey<ConfiguredStructureFeature<?, ?>> MEMORIAL_TREE = of("memorial_tree");

    private static RegistryKey<ConfiguredStructureFeature<?, ?>> of(String id) {
        return RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, id));
    }
}
