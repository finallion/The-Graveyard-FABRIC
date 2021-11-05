package com.finallion.graveyard.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TheGraveyardConfig implements Config {

    @Comment("""
             Welcome to The Graveyard Config!
             //
             // Here, you can disable structures to spawn, change their chance of spawning, change their separation and also change their salt.
             // Additionally, you can turn the graveyard fog particles on and set the chance of spawning them (higher numbers = lower chance of spawning).
             // You can also turn off the cartographer map trade for the large graveyard. As minecraft structure search algorithm isn't the fastest, loading the map can cause lag.
             // Graveyard fog particles spawn in graveyards and add an atmospheric and spooky element while exploring.
             //
             // To disable a structure to spawn, simply go to the corresponding entry and set `enabled` to false.
             //
             // The Graveyard uses the vanilla structure spawning system. That is -
             // - Separation is the minimum chunks between structures
             // - Spacing is the average chunks between structures (Spacing value needs to be higher than Separation value!)
             // - Salt is a special field that gives structures unique spawning positions. (Only change if you know what you are doing!)
             //
             // Notice: reducing the generation settings will increase the risk of structures overlapping. Especially jigsaw structures are unpredictable!  
            """)
    public final Map<String, ConfigStructureEntry> structureConfigEntries = new HashMap<>(9);
    public final Map<String, ConfigFogParticleEntry> particleConfigEntries = new HashMap<>(1);
    public final Map<String, ConfigBooleanEntry> additionalGenerationEntries = new HashMap<>(1);

    @Override
    public String getName() {
        return "the-graveyard-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    public boolean fogSpawn(Identifier id) {
        return getParticle(id).canGenerate;
    }

    public boolean enabled(Identifier id) {
        return get(id).enabled;
    }

    public boolean additionalEnabled(Identifier id) {
        return getAddons(id).canGenerate;
    }

    public ConfigBooleanEntry getAddons(Identifier id) {
        for (Map.Entry<String, ConfigBooleanEntry> entry : additionalGenerationEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }

        throw new NullPointerException("Tried StructureConfigEntry with id: " + id + ", but it was null!");
    }


    public ConfigStructureEntry get(Identifier id) {
        for (Map.Entry<String, ConfigStructureEntry> entry : structureConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }

        throw new NullPointerException("Tried StructureConfigEntry with id: " + id + ", but it was null!");
    }

    public ConfigFogParticleEntry getParticle(Identifier id) {
        for (Map.Entry<String, ConfigFogParticleEntry> entry : particleConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }

        throw new NullPointerException("Tried FogParticleConfigEntry with id: " + id + ", but it was null!");
    }


    @Override
    public void save() {
        //additionalGenerationEntries.putIfAbsent("graveyard_villager_large_graveyard_map_trade", ConfigBooleanEntry.of());

        particleConfigEntries.putIfAbsent("graveyard_fog_particle", ConfigFogParticleEntry.of(50));

        structureConfigEntries.putIfAbsent("large_birch_tree", ConfigStructureEntry.of(14, 12, 304812394));
        structureConfigEntries.putIfAbsent("medium_walled_graveyard", ConfigStructureEntry.of(16, 14, 379123039));
        structureConfigEntries.putIfAbsent("mushroom_grave", ConfigStructureEntry.of(24, 18, 598017285));
        structureConfigEntries.putIfAbsent("small_grave", ConfigStructureEntry.of(12, 8, 240451934));
        structureConfigEntries.putIfAbsent("small_walled_graveyard", ConfigStructureEntry.of(20, 18, 1690192399));
        structureConfigEntries.putIfAbsent("small_walled_graveyard_desert", ConfigStructureEntry.of(32, 28, 661903018));
        structureConfigEntries.putIfAbsent("small_walled_graveyard_savanna", ConfigStructureEntry.of(14, 12, 451235912));
        structureConfigEntries.putIfAbsent("large_walled_graveyard", ConfigStructureEntry.of(11, 9, 739017628));
        structureConfigEntries.putIfAbsent("haunted_house", ConfigStructureEntry.of(25, 20, 529239621));


        Config.super.save();
    }
}
