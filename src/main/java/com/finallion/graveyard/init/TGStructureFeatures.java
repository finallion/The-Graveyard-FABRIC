package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.mixin.StructureFeatureAccessor;
import com.finallion.graveyard.world.structures.*;
import com.finallion.graveyard.world.structures.configs.TGStructureConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.HashSet;
import java.util.Set;

public class TGStructureFeatures {
    public static Set<StructureFeature<?>> structures = new HashSet<>();

    // structure features
    public static final StructureFeature<StructurePoolFeatureConfig> MEDIUM_GRAVEYARD_STRUCTURE = new MediumGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_GRAVEYARD_STRUCTURE = new SmallGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> LARGE_GRAVEYARD_STRUCTURE = new LargeGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> MUSHROOM_GRAVE_STRUCTURE = new MushroomGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> HAUNTED_HOUSE_STRUCTURE = new HauntedHouseStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> MEMORIAL_TREE_STRUCTURE = new MemorialTreeStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_DESERT_GRAVEYARD_STRUCTURE = new SmallDesertGraveyardStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_GRAVE_STRUCTURE = new SmallGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_DESERT_GRAVE_STRUCTURE = new SmallDesertGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_SAVANNA_GRAVE_STRUCTURE = new SmallSavannaGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> SMALL_MOUNTAIN_GRAVE_STRUCTURE = new SmallMountainGraveStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> ALTAR_STRUCTURE = new AltarStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> GIANT_MUSHROOM_STRUCTURE = new GiantMushroomStructure(StructurePoolFeatureConfig.CODEC);
    public static final StructureFeature<StructurePoolFeatureConfig> CRYPT_STRUCTURE = new CryptStructure(StructurePoolFeatureConfig.CODEC);


    public static void init() {
        register(HAUNTED_HOUSE_STRUCTURE, "haunted_house");
        register(MEDIUM_GRAVEYARD_STRUCTURE, "medium_graveyard");
        register(SMALL_GRAVEYARD_STRUCTURE, "small_graveyard");
        register(LARGE_GRAVEYARD_STRUCTURE, "large_graveyard");
        register(MUSHROOM_GRAVE_STRUCTURE, "mushroom_grave");
        register(MEMORIAL_TREE_STRUCTURE, "memorial_tree");
        register(SMALL_DESERT_GRAVEYARD_STRUCTURE, "small_desert_graveyard");
        register(SMALL_GRAVE_STRUCTURE, "small_grave");
        register(SMALL_DESERT_GRAVE_STRUCTURE, "small_desert_grave");
        register(SMALL_SAVANNA_GRAVE_STRUCTURE, "small_savanna_grave");
        register(SMALL_MOUNTAIN_GRAVE_STRUCTURE, "small_mountain_grave");
        register(ALTAR_STRUCTURE, "altar");
        register(GIANT_MUSHROOM_STRUCTURE, "giant_mushroom");
        registerUnderground(CRYPT_STRUCTURE, "crypt");
    }

    private static void registerUnderground(StructureFeature<?> structure, String name) {
        Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(TheGraveyard.MOD_ID, name), structure);
        StructureFeatureAccessor.getStructureToGenerationStep().put(structure, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    }


    private static void register(StructureFeature<?> structure, String name) {
        Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(TheGraveyard.MOD_ID, name), structure);
        StructureFeatureAccessor.getStructureToGenerationStep().put(structure, GenerationStep.Feature.SURFACE_STRUCTURES);
        structures.add(structure);
    }

}
