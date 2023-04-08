package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.structures.*;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structure.Config;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TGConfiguredStructureFeatures {
    public static List<Structure> structures = new ArrayList<>();

    public static StructureType<TGJigsawStructure> ALTAR;
    public static StructureType<TGJigsawStructure> DEAD_TREE;
    public static StructureType<TGJigsawStructure> CRYPT;
    public static StructureType<TGJigsawStructure> GIANT_MUSHROOM;
    public static StructureType<TGJigsawStructure> HAUNTED_HOUSE;
    public static StructureType<TGJigsawStructure> LARGE_GRAVEYARD;
    public static StructureType<TGJigsawStructure> LICH_PRISON;
    public static StructureType<TGJigsawStructure> MEDIUM_GRAVEYARD;
    public static StructureType<TGJigsawStructure> MEMORIAL_TREE;
    public static StructureType<TGJigsawStructure> MUSHROOM_GRAVE;
    public static StructureType<TGJigsawStructure> RUINS;
    public static StructureType<TGJigsawStructure> SMALL_DESERT_GRAVE;
    public static StructureType<TGJigsawStructure> SMALL_DESERT_GRAVEYARD;
    public static StructureType<TGJigsawStructure> SMALL_GRAVE;
    public static StructureType<TGJigsawStructure> SMALL_GRAVEYARD;
    public static StructureType<TGJigsawStructure> SMALL_MOUNTAIN_GRAVE;
    public static StructureType<TGJigsawStructure> SMALL_SAVANNA_GRAVE;

    public static void registerStructureFeatures() {
        ALTAR = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "altar"), () -> TGJigsawStructure.CODEC);
        CRYPT = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "crypt"), () -> TGJigsawStructure.CODEC);
        GIANT_MUSHROOM = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "giant_mushroom"), () -> TGJigsawStructure.CODEC);
        HAUNTED_HOUSE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "haunted_house"), () -> TGJigsawStructure.CODEC);
        LARGE_GRAVEYARD = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "large_graveyard"), () -> TGJigsawStructure.CODEC);
        LICH_PRISON = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "lich_prison"), () -> TGJigsawStructure.CODEC);
        MEDIUM_GRAVEYARD = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "medium_graveyard"), () -> TGJigsawStructure.CODEC);
        MEMORIAL_TREE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "memorial_tree"), () -> TGJigsawStructure.CODEC);
        MUSHROOM_GRAVE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "mushroom_grave"), () -> TGJigsawStructure.CODEC);
        RUINS = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "ruins"), () -> TGJigsawStructure.CODEC);
        SMALL_DESERT_GRAVE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "small_desert_grave"), () -> TGJigsawStructure.CODEC);
        SMALL_DESERT_GRAVEYARD = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard"), () -> TGJigsawStructure.CODEC);
        SMALL_GRAVE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "small_grave"), () -> TGJigsawStructure.CODEC);
        SMALL_GRAVEYARD = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "small_graveyard"), () -> TGJigsawStructure.CODEC);
        SMALL_MOUNTAIN_GRAVE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "small_mountain_grave"), () -> TGJigsawStructure.CODEC);
        SMALL_SAVANNA_GRAVE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "small_savanna_grave"), () -> TGJigsawStructure.CODEC);
        DEAD_TREE = Registry.register(Registries.STRUCTURE_TYPE, new Identifier(TheGraveyard.MOD_ID, "dead_tree"), () -> TGJigsawStructure.CODEC);

    }

    public static void init() {}
}
