package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class TGTags {

    public static TagKey<Biome> GHOUL_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("ghoul_spawns"));
    public static TagKey<Biome> REVENANT_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("revenant_spawns"));
    public static TagKey<Biome> NIGHTMARE_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("nightmare_spawns"));
    public static TagKey<Biome> SKELETON_CREEPER_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("skeleton_creeper_spawns"));
    public static TagKey<Biome> REAPER_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("reaper_spawns"));
    public static TagKey<Biome> RAVEN_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("raven_spawns"));
    public static TagKey<Biome> MAGPIE_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("magpie_spawns"));
    public static TagKey<Biome> CROW_SPAWNS = TagKey.of(RegistryKeys.BIOME, Graveyard.id("crow_spawns"));

    public static TagKey<Block> NATURAL_HELPER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("natural_helper_blocks"));
    public static TagKey<Block> CORVID_PERCHES = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("corvid_perches"));
    public static TagKey<Block> CORVID_BONES = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("corvid_bones"));
    public static TagKey<Block> CROW_SETTLE = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("crow_settle"));
    public static TagKey<Block> CROW_LANDMARKS = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("crow_landmarks"));
    public static TagKey<Block> CROW_REPELLENTS = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("crow_repellents"));
    public static TagKey<Block> SCARECROW_HEADS = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("scarecrow_heads"));
    public static TagKey<Block> SCARECROW_BODIES = TagKey.of(RegistryKeys.BLOCK, Graveyard.id("scarecrow_bodies"));

    public static TagKey<Item> MAGPIE_TREASURES = TagKey.of(RegistryKeys.ITEM, Graveyard.id("magpie_treasures"));
    public static TagKey<Item> SKELETON_DECORATION = TagKey.of(RegistryKeys.ITEM, Graveyard.id("skeleton_decoration"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, Graveyard.id("on_ruins_explorer_maps"));

}
