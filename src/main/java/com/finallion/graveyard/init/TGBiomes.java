package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.biomes.HauntedForestBiomes;
import com.finallion.graveyard.biomes.biomeFeatures.TGBiomeFeatures;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class TGBiomes {
    public static final RegistryKey<Biome> HAUNTED_FOREST_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "haunted_forest"));

    public static void registerBiomes() {
        Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(TheGraveyard.MOD_ID, "haunted_forest_moss_surface"), HauntedForestBiomes.HAUNTED_FOREST_MOSS_SURFACE);
        Registry.register(BuiltinRegistries.BIOME, HAUNTED_FOREST_KEY.getValue(), HauntedForestBiomes.HauntedForestBiome());
    }
}
