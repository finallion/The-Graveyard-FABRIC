package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TGTags {

    public static TagKey<Biome> GHOUL_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("ghoul_spawns"));
    public static TagKey<Biome> REVENANT_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("revenant_spawns"));
    public static TagKey<Biome> NIGHTMARE_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("nightmare_spawns"));
    public static TagKey<Biome> SKELETON_CREEPER_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("skeleton_creeper_spawns"));
    public static TagKey<Biome> REAPER_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("reaper_spawns"));
    public static TagKey<Biome> RAVEN_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("raven_spawns"));
    public static TagKey<Biome> MAGPIE_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("magpie_spawns"));
    public static TagKey<Biome> CROW_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("crow_spawns"));

    public static TagKey<Block> NATURAL_HELPER_BLOCKS = TagKey.create(Registries.BLOCK, Graveyard.id("natural_helper_blocks"));
    public static TagKey<Block> CORVID_PERCHES = TagKey.create(Registries.BLOCK, Graveyard.id("corvid_perches"));
    public static TagKey<Block> CORVID_BONES = TagKey.create(Registries.BLOCK, Graveyard.id("corvid_bones"));
    public static TagKey<Block> CROW_SETTLE = TagKey.create(Registries.BLOCK, Graveyard.id("crow_settle"));
    public static TagKey<Block> CROW_LANDMARKS = TagKey.create(Registries.BLOCK, Graveyard.id("crow_landmarks"));
    public static TagKey<Block> CROW_REPELLENTS = TagKey.create(Registries.BLOCK, Graveyard.id("crow_repellents"));
    public static TagKey<Block> SCARECROW_HEADS = TagKey.create(Registries.BLOCK, Graveyard.id("scarecrow_heads"));
    public static TagKey<Block> SCARECROW_BODIES = TagKey.create(Registries.BLOCK, Graveyard.id("scarecrow_bodies"));

    public static TagKey<Item> MAGPIE_TREASURES = TagKey.create(Registries.ITEM, Graveyard.id("magpie_treasures"));
    public static TagKey<Item> SKELETON_DECORATION = TagKey.create(Registries.ITEM, Graveyard.id("skeleton_decoration"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.create(Registries.STRUCTURE, Graveyard.id("on_ruins_explorer_maps"));

}
