package com.lion.graveyard.init;


import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.world.structures.TGJigsawStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;

public class TGStructures {
    public static List<Structure> structures = new ArrayList<>();

    public static StructureType<TGJigsawStructure> TG_JIGSAW = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> ALTAR = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> DEAD_TREE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> CRYPT = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> GIANT_MUSHROOM = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> HAUNTED_HOUSE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> LARGE_GRAVEYARD = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> LICH_PRISON = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> MEDIUM_GRAVEYARD = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> MEMORIAL_TREE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> MUSHROOM_GRAVE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> RUINS = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> SMALL_DESERT_GRAVE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> SMALL_DESERT_GRAVEYARD = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> SMALL_GRAVE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> SMALL_GRAVEYARD = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> SMALL_MOUNTAIN_GRAVE = () -> TGJigsawStructure.CODEC;
    public static StructureType<TGJigsawStructure> SMALL_SAVANNA_GRAVE = () -> TGJigsawStructure.CODEC;

    static {
        RegistryHelper.registerStructureType("tg_jigsaw", TG_JIGSAW);

        RegistryHelper.registerStructureType("altar", ALTAR);
        RegistryHelper.registerStructureType("crypt", CRYPT);
        RegistryHelper.registerStructureType("giant_mushroom", GIANT_MUSHROOM);
        RegistryHelper.registerStructureType("haunted_house", HAUNTED_HOUSE);
        RegistryHelper.registerStructureType("large_graveyard", LARGE_GRAVEYARD);
        RegistryHelper.registerStructureType("lich_prison", LICH_PRISON);
        RegistryHelper.registerStructureType("medium_graveyard", MEDIUM_GRAVEYARD);
        RegistryHelper.registerStructureType("memorial_tree", MEMORIAL_TREE);
        RegistryHelper.registerStructureType("mushroom_grave", MUSHROOM_GRAVE);
        RegistryHelper.registerStructureType("ruins", RUINS);
        RegistryHelper.registerStructureType("small_desert_grave", SMALL_DESERT_GRAVE);
        RegistryHelper.registerStructureType("small_desert_graveyard", SMALL_DESERT_GRAVEYARD);
        RegistryHelper.registerStructureType("small_grave", SMALL_GRAVE);
        RegistryHelper.registerStructureType("small_graveyard", SMALL_GRAVEYARD);
        RegistryHelper.registerStructureType("small_mountain_grave", SMALL_MOUNTAIN_GRAVE);
        RegistryHelper.registerStructureType("small_savanna_grave", SMALL_SAVANNA_GRAVE);
        RegistryHelper.registerStructureType("dead_tree", DEAD_TREE);
    }

    public static void init() {}
}
