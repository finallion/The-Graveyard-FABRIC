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
    public final Map<String, ConfigStructureEntry> structureConfigEntries = new HashMap<>(8);

    @Override
    public String getName() {
        return "the-graveyard-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    public boolean enabled(Identifier id) {
        return get(id).enabled;
    }

    public ConfigStructureEntry get(Identifier id) {
        for (Map.Entry<String, ConfigStructureEntry> entry : structureConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }

        throw new NullPointerException("Tried StructureConfigEntry with id: " + id + ", but it was null!");
    }


    @Override
    public void save() {
        structureConfigEntries.putIfAbsent("large_birch_tree", ConfigStructureEntry.of(14, 12, 304812394));
        structureConfigEntries.putIfAbsent("medium_walled_graveyard", ConfigStructureEntry.of(16, 14, 379123039));
        structureConfigEntries.putIfAbsent("mushroom_grave", ConfigStructureEntry.of(24, 18, 598017285));
        structureConfigEntries.putIfAbsent("small_grave", ConfigStructureEntry.of(12, 8, 240451934));
        structureConfigEntries.putIfAbsent("small_walled_graveyard", ConfigStructureEntry.of(20, 18, 1690192399));
        structureConfigEntries.putIfAbsent("small_walled_graveyard_desert", ConfigStructureEntry.of(32, 28, 661903018));
        structureConfigEntries.putIfAbsent("small_walled_graveyard_savanna", ConfigStructureEntry.of(14, 12, 451235912));
        structureConfigEntries.putIfAbsent("large_walled_graveyard", ConfigStructureEntry.of(20, 18, 739017628));


        Config.super.save();
    }
}
