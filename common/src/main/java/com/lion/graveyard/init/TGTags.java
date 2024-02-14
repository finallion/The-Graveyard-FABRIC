package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TGTags {

    public static TagKey<Biome> GHOUL_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Graveyard.MOD_ID, "ghoul_spawns"));
    public static TagKey<Biome> REVENANT_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Graveyard.MOD_ID, "revenant_spawns"));
    public static TagKey<Biome> NIGHTMARE_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Graveyard.MOD_ID, "nightmare_spawns"));
    public static TagKey<Biome> SKELETON_CREEPER_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Graveyard.MOD_ID, "skeleton_creeper_spawns"));
    public static TagKey<Biome> REAPER_SPAWNS = TagKey.create(Registries.BIOME, new ResourceLocation(Graveyard.MOD_ID, "reaper_spawns"));

    public static TagKey<Block> NATURAL_HELPER_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(Graveyard.MOD_ID, "natural_helper_blocks"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.create(Registries.STRUCTURE, new ResourceLocation(Graveyard.MOD_ID, "on_ruins_explorer_maps"));

}
