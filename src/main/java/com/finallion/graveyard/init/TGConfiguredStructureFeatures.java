package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class TGConfiguredStructureFeatures {

    public static ConfiguredStructureFeature<?, ?> CONFIGURED_SMALL_WALLED_GRAVEYARD = TGStructures.SMALL_WALLED_GRAVEYARD.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_SMALL_WALLED_GRAVEYARD_SAVANNA = TGStructures.SMALL_WALLED_GRAVEYARD_SAVANNA.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_SMALL_WALLED_GRAVEYARD_DESERT = TGStructures.SMALL_WALLED_GRAVEYARD_DESERT.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_SMALL_GRAVE = TGStructures.SMALL_GRAVE.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_MUSHROOM_GRAVE = TGStructures.MUSHROOM_GRAVE.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_LARGE_BIRCH_TREE = TGStructures.LARGE_BIRCH_TREE.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_MEDIUM_WALLED_GRAVEYARD = TGStructures.MEDIUM_WALLED_GRAVEYARD.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_LARGE_WALLED_GRAVEYARD = TGStructures.LARGE_WALLED_GRAVEYARD.configure(DefaultFeatureConfig.DEFAULT);
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_HAUNTED_HOUSE = TGStructures.HAUNTED_HOUSE.configure(DefaultFeatureConfig.DEFAULT);

    public static void registerConfiguredStructures() {
        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;

        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_small_walled_graveyard"), CONFIGURED_SMALL_WALLED_GRAVEYARD);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_small_walled_graveyard_savanna"), CONFIGURED_SMALL_WALLED_GRAVEYARD_SAVANNA);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_small_walled_graveyard_desert"), CONFIGURED_SMALL_WALLED_GRAVEYARD_DESERT);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_small_grave"), CONFIGURED_SMALL_GRAVE);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_mushroom_grave"), CONFIGURED_MUSHROOM_GRAVE);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_large_birch_tree"), CONFIGURED_LARGE_BIRCH_TREE);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_medium_walled_graveyard"), CONFIGURED_MEDIUM_WALLED_GRAVEYARD);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_large_walled_graveyard"), CONFIGURED_LARGE_WALLED_GRAVEYARD);
        Registry.register(registry, new Identifier(TheGraveyard.MOD_ID, "configured_haunted_house"), CONFIGURED_HAUNTED_HOUSE);

    }
}
