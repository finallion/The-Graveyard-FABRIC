package com.finallion.graveyard.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class GraveyardConfig implements Config {
    @Comment("""
             Welcome to The Graveyard Config!
             //
             // Here, you can disable structures to spawn, change their chance of spawning, change their separation and also change their salt.
             // Additionally, you can turn the graveyard fog particles on and set the chance of spawning them (higher numbers = lower chance of spawning).
             // Graveyard fog particles spawn in graveyards and add an atmospheric and spooky element while exploring.
             // To disable a structure to spawn, simply go to the corresponding entry and set `enabled` to false.
             //
             // The Graveyard uses the vanilla structure spawning system. That is -
             // - Separation is the minimum chunks between structures
             // - Spacing is the average chunks between structures (Spacing value needs to be higher than Separation value!)
             // - Salt is a special field that gives structures unique spawning positions. (Only change if you know what you are doing!)
             //
             // Notice: reducing the generation settings will increase the risk of structures overlapping. Especially jigsaw structures are unpredictable!  
            """)

    public final Map<String, StructureConfigEntry> structureConfigEntries = new HashMap<>();
    public final Map<String, ParticleConfigEntry> particleConfigEntries = new HashMap<>(1);


    @Override
    public String getName() {
        return "the-graveyard-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    // generation booleans
    public boolean enabled(Identifier id) {
        return getStructure(id).enabled;
    }

    public boolean fogSpawn(Identifier id) {
        return getParticle(id).canGenerate;
    }


    // structure config
    public StructureConfigEntry getStructure(Identifier id) {
        for (Map.Entry<String, StructureConfigEntry> entry : structureConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }

        throw new NullPointerException("Tried StructureConfigEntry with id: " + id + ", but it was null!");
    }

    // moss particle config
    public ParticleConfigEntry getParticle(Identifier id) {
        for (Map.Entry<String, ParticleConfigEntry> entry : particleConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }

        throw new NullPointerException("Tried ParticleConfigEntry with id: " + id + ", but it was null!");
    }


    @Override
    public void save() {
        structureConfigEntries.putIfAbsent("memorial_tree", StructureConfigEntry.of(14, 12, 529239621));
        structureConfigEntries.putIfAbsent("medium_graveyard", StructureConfigEntry.of(18, 16, 1690192399));
        structureConfigEntries.putIfAbsent("mushroom_grave", StructureConfigEntry.of(24, 18, 379123039));
        structureConfigEntries.putIfAbsent("small_grave", StructureConfigEntry.of(12, 8, 661903018));
        structureConfigEntries.putIfAbsent("small_graveyard", StructureConfigEntry.of(20, 18, 240451934));
        structureConfigEntries.putIfAbsent("small_desert_graveyard", StructureConfigEntry.of(32, 28, 598017285));
        structureConfigEntries.putIfAbsent("large_graveyard", StructureConfigEntry.of(16, 12, 304812394));
        structureConfigEntries.putIfAbsent("haunted_house", StructureConfigEntry.of(25, 20, 451235912));
        structureConfigEntries.putIfAbsent("small_desert_grave", StructureConfigEntry.of(20, 16, 681236914));
        structureConfigEntries.putIfAbsent("small_savanna_grave", StructureConfigEntry.of(12, 8, 709787761));
        structureConfigEntries.putIfAbsent("small_mountain_grave", StructureConfigEntry.of(12, 8, 725689810));


        particleConfigEntries.putIfAbsent("graveyard_fog_particle", ParticleConfigEntry.of(50));
        Config.super.save();
    }


}
