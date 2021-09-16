package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;

import com.finallion.graveyard.structures.*;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.List;

public class TGStructures {

    public static List<StructureFeature<DefaultFeatureConfig>> structures = new ArrayList<>();

    public static StructureFeature<DefaultFeatureConfig> SMALL_WALLED_GRAVEYARD = new SmallWalledGraveyard(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> SMALL_WALLED_GRAVEYARD_SAVANNA = new SmallWalledGraveyardSavanna(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> SMALL_WALLED_GRAVEYARD_DESERT = new SmallWalledGraveyardDesert(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> SMALL_GRAVE = new SmallGrave(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> MUSHROOM_GRAVE = new MushroomGrave(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> LARGE_BIRCH_TREE = new LargeBirchTree(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> MEDIUM_WALLED_GRAVEYARD = new MediumWalledGraveyard(DefaultFeatureConfig.CODEC);
    public static StructureFeature<DefaultFeatureConfig> LARGE_WALLED_GRAVEYARD = new LargeWalledGraveyard(DefaultFeatureConfig.CODEC);

    public static void setupAndRegisterStructureFeatures() {
        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard"), SMALL_WALLED_GRAVEYARD)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.small_walled_graveyard_spacing, TheGraveyard.config.structureChances.small_walled_graveyard_separation, TheGraveyard.config.structureChances.small_walled_graveyard_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "small_grave"), SMALL_GRAVE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.small_grave_spacing, TheGraveyard.config.structureChances.small_grave_separation, TheGraveyard.config.structureChances.small_grave_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard_desert"), SMALL_WALLED_GRAVEYARD_DESERT)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.small_walled_graveyard_desert_spacing, TheGraveyard.config.structureChances.small_walled_graveyard_desert_separation, TheGraveyard.config.structureChances.small_walled_graveyard_desert_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard_savanna"), SMALL_WALLED_GRAVEYARD_SAVANNA)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.small_walled_graveyard_savanna_spacing, TheGraveyard.config.structureChances.small_walled_graveyard_savanna_separation, TheGraveyard.config.structureChances.small_walled_graveyard_savanna_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "large_birch_tree"), LARGE_BIRCH_TREE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.large_birch_tree_spacing, TheGraveyard.config.structureChances.large_birch_tree_separation, TheGraveyard.config.structureChances.large_birch_tree_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "medium_walled_graveyard"), MEDIUM_WALLED_GRAVEYARD)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.medium_walled_graveyard_spacing, TheGraveyard.config.structureChances.medium_walled_graveyard_separation, TheGraveyard.config.structureChances.medium_walled_graveyard_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard"), LARGE_WALLED_GRAVEYARD)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.large_walled_graveyard_spacing, TheGraveyard.config.structureChances.large_walled_graveyard_separation, TheGraveyard.config.structureChances.large_walled_graveyard_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();



        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave"), MUSHROOM_GRAVE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(TheGraveyard.config.structureChances.mushroom_grave_spacing, TheGraveyard.config.structureChances.mushroom_grave_separation, TheGraveyard.config.structureChances.mushroom_grave_salt)
                .superflatFeature(SMALL_WALLED_GRAVEYARD.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();


        structures.add(SMALL_GRAVE);
        structures.add(SMALL_WALLED_GRAVEYARD_SAVANNA);
        structures.add(SMALL_WALLED_GRAVEYARD_DESERT);
        structures.add(SMALL_WALLED_GRAVEYARD);
        structures.add(MEDIUM_WALLED_GRAVEYARD);
        structures.add(LARGE_BIRCH_TREE);
        structures.add(LARGE_WALLED_GRAVEYARD);
        structures.add(MUSHROOM_GRAVE);





    }
}
