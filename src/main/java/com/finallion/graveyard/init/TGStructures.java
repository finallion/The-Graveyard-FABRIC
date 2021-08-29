package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.ConfigStructureEntry;
import com.finallion.graveyard.structures.*;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.lwjgl.system.CallbackI;

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

    private static void register(String id, StructureFeature<DefaultFeatureConfig> feature) {
        ConfigStructureEntry entry = TheGraveyard.config.get(new Identifier(TheGraveyard.MOD_ID, id));


        FabricStructureBuilder.create(new Identifier(TheGraveyard.MOD_ID, id), feature)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(entry.spacing, entry.separation, entry.salt)
                .superflatFeature(feature.configure(FeatureConfig.DEFAULT))
                .adjustsSurface()
                .register();

        structures.add(feature);
    }



    public static void setupAndRegisterStructureFeatures() {

        register("small_walled_graveyard", SMALL_WALLED_GRAVEYARD);
        register("small_grave", SMALL_GRAVE);
        register("large_birch_tree", LARGE_BIRCH_TREE);
        register("medium_walled_graveyard", MEDIUM_WALLED_GRAVEYARD);
        register("small_walled_graveyard_savanna", SMALL_WALLED_GRAVEYARD_SAVANNA);
        register("mushroom_grave", MUSHROOM_GRAVE);
        register("small_walled_graveyard_desert", SMALL_WALLED_GRAVEYARD_DESERT);
        register("large_walled_graveyard", LARGE_WALLED_GRAVEYARD);



    }
}
