package com.lion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public class TGTags {

    public static TagKey<Biome> GHOUL_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(TheGraveyard.MOD_ID, "ghoul_spawns"));
    public static TagKey<Biome> REVENANT_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(TheGraveyard.MOD_ID, "revenant_spawns"));
    public static TagKey<Biome> NIGHTMARE_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(TheGraveyard.MOD_ID, "nightmare_spawns"));
    public static TagKey<Biome> SKELETON_CREEPER_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(TheGraveyard.MOD_ID, "skeleton_creeper_spawns"));
    public static TagKey<Biome> REAPER_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(TheGraveyard.MOD_ID, "reaper_spawns"));

    public static TagKey<Structure> ON_RUINS_EXPLORER_MAPS = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(TheGraveyard.MOD_ID, "on_ruins_explorer_maps"));

}
