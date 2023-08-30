package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import net.minecraft.block.Block;
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

    public static TagKey<Block> NATURAL_HELPER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "natural_helper_blocks"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(Graveyard.MOD_ID, "on_ruins_explorer_maps"));

}
