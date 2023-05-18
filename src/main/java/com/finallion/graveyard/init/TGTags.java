package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class TGTags {

    public static TagKey<Biome> GHOUL_SPAWNS = TagKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "ghoul_spawns"));
    public static TagKey<Biome> REVENANT_SPAWNS = TagKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "revenant_spawns"));
    public static TagKey<Biome> NIGHTMARE_SPAWNS = TagKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "nightmare_spawns"));
    public static TagKey<Biome> SKELETON_CREEPER_SPAWNS = TagKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "skeleton_creeper_spawns"));
    public static TagKey<Biome> REAPER_SPAWNS = TagKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "reaper_spawns"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.of(Registry.STRUCTURE_KEY, new Identifier(TheGraveyard.MOD_ID, "on_ruins_explorer_maps"));

}
