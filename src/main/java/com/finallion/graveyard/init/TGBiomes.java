package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.biomes.AncientDeadCoralReef;
import com.finallion.graveyard.world.biomes.HauntedForestBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.lwjgl.system.CallbackI;

public class TGBiomes {
    public static final RegistryKey<Biome> ERODED_HAUNTED_FOREST_KEY = registerBiomeKeys("eroded_haunted_forest");
    public static final RegistryKey<Biome> HAUNTED_LAKES_KEY = registerBiomeKeys("haunted_lakes");
    public static final RegistryKey<Biome> HAUNTED_FOREST_KEY = registerBiomeKeys("haunted_forest");
    public static final RegistryKey<Biome> ANCIENT_DEAD_CORAL_REEF_KEY = registerBiomeKeys("ancient_dead_coral_reef");

    private static RegistryKey<Biome> registerBiomeKeys(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, name));
    }

    public static void registerBiomes() {
        register(HAUNTED_FOREST_KEY, HauntedForestBiomes.createHauntedForest());
        register(ERODED_HAUNTED_FOREST_KEY, HauntedForestBiomes.createErodedHauntedForest());
        register(HAUNTED_LAKES_KEY, HauntedForestBiomes.createHauntedLakes());
        register(ANCIENT_DEAD_CORAL_REEF_KEY, AncientDeadCoralReef.createAncientDeadCoralReef());
    }

    private static Biome register(RegistryKey<Biome> key, Biome biome) {
        return BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
    }


}
