package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class TGTags {

    public static TagKey<Biome> GHOUL_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "ghoul_spawns"));
    public static TagKey<Biome> REVENANT_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "revenant_spawns"));
    public static TagKey<Biome> NIGHTMARE_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "nightmare_spawns"));
    public static TagKey<Biome> SKELETON_CREEPER_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "skeleton_creeper_spawns"));
    public static TagKey<Biome> REAPER_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "reaper_spawns"));
    public static TagKey<Biome> RAVEN_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "raven_spawns"));
    public static TagKey<Biome> MAGPIE_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "magpie_spawns"));
    public static TagKey<Biome> CROW_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "crow_spawns"));

    public static TagKey<Block> NATURAL_HELPER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "natural_helper_blocks"));
    public static TagKey<Block> CORVID_PERCHES = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "corvid_perches"));
    public static TagKey<Block> CORVID_BONES = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "corvid_bones"));
    public static TagKey<Block> CROW_SETTLE = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "crow_settle"));
    public static TagKey<Block> CROW_LANDMARKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "crow_landmarks"));
    public static TagKey<Block> CROW_REPELLENTS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "crow_repellents"));
    public static TagKey<Block> SCARECROW_HEADS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "scarecrow_heads"));
    public static TagKey<Block> SCARECROW_BODIES = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "scarecrow_bodies"));

    public static TagKey<Item> MAGPIE_TREASURES = TagKey.of(RegistryKeys.ITEM, new Identifier(Graveyard.MOD_ID, "magpie_treasures"));
    public static TagKey<Item> SKELETON_DECORATION = TagKey.of(RegistryKeys.ITEM, new Identifier(Graveyard.MOD_ID, "skeleton_decoration"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(Graveyard.MOD_ID, "on_ruins_explorer_maps"));

}
