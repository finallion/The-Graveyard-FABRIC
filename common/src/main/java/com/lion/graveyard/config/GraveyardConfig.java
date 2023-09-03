package com.lion.graveyard.config;

import com.lion.graveyard.config.annotations.Comment;
import com.lion.graveyard.config.entries.*;
import net.minecraft.util.Identifier;

import java.util.*;

public class GraveyardConfig implements Config {
    @Comment("""
             Welcome to The Graveyard Config!
             
             Structures:
             Enable or disable structure spawns.
             
             Mobs:
             Configure spawning weight and group size of the spawn (ghoul, revenant, reaper, nightmare, skeleton creeper).
             Configure if mobs burn in sunlight and/or if mobs are affected by the wither effect.

             Additional:
             Configure graveyard fog particles rising from moss and set the chance of spawning them (higher numbers = lower chance of spawning).
             Configure if hordes of graveyard mobs can spawn and set their size and frequency.
             Configure if urns have a double chest inventory.
             Configure if the wither skeleton spawner in the large graveyard will be replaced by a skeleton spawner.
             
            """)

    public final Map<String, StructureConfigEntry> structureConfigEntries = new HashMap<>();
    public final Map<String, ParticleConfigEntry> particleConfigEntries = new HashMap<>(1);
    public final Map<String, MobConfigEntry> mobConfigEntries = new HashMap<>();
    public final Map<String, HordeConfigEntry> hordeConfigEntries = new HashMap<>();
    public final Map<String, CorruptedChampionEntry> corruptedChampionConfigEntries = new HashMap<>();
    public final Map<String, Boolean> booleanEntries = new HashMap<>();

    @Override
    public String getName() {
        return "the-graveyard-3.0-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    @Override
    public String getDirectory() {
        return "graveyard";
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
        throw new NoSuchElementException("The Graveyard Config is not up to date. Please delete the config and restart the game to create the current config.");
    }

    // moss particle config
    public ParticleConfigEntry getParticle(Identifier id) {
        for (Map.Entry<String, ParticleConfigEntry> entry : particleConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NoSuchElementException("The Graveyard Config is not up to date. Please delete the config and restart the game to create the current config.");
    }

    // mob spawn config
    public MobConfigEntry getMob(Identifier id) {
        for (Map.Entry<String, MobConfigEntry> entry : mobConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NoSuchElementException("The Graveyard Config is not up to date. Please delete the config and restart the game to create the current config.");
    }

    // horde spawn config
    public HordeConfigEntry getHorde(Identifier id) {
        for (Map.Entry<String, HordeConfigEntry> entry : hordeConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NoSuchElementException("The Graveyard Config is not up to date. Please delete the config and restart the game to create the current config.");
    }

    @Override
    public void save() {
        booleanEntries.putIfAbsent("urnHasDoubleInventory", true);
        booleanEntries.putIfAbsent("disableWitherSkeletonSpawner", false);
        booleanEntries.putIfAbsent("enableBossMusic", true);

        particleConfigEntries.putIfAbsent("graveyard_fog_particle", ParticleConfigEntry.of(50));

        hordeConfigEntries.putIfAbsent("horde_spawn", HordeConfigEntry.of(40, 24000, 1200));

        structureConfigEntries.putIfAbsent("haunted_house", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("large_graveyard", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("altar", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("crypt", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("giant_mushroom", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("medium_graveyard", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("memorial_tree", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("mushroom_grave", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("small_desert_graveyard", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("small_graveyard", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("small_desert_grave", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("small_grave", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("small_mountain_grave", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("small_savanna_grave", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("lich_prison", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("ruins", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("dead_tree", StructureConfigEntry.of());

        mobConfigEntries.putIfAbsent("ghoul", MobConfigEntry.of(true, 25, 2, 5, true, false));
        mobConfigEntries.putIfAbsent("revenant", MobConfigEntry.of(true,25, 5, 8, true, false));
        mobConfigEntries.putIfAbsent("nightmare", MobConfigEntry.of(true,10, 1, 1, false, false));
        mobConfigEntries.putIfAbsent("skeleton_creeper", MobConfigEntry.of(true,25, 1, 4, true, false));
        mobConfigEntries.putIfAbsent("acolyte", MobConfigEntry.of(false, false, false));
        mobConfigEntries.putIfAbsent("reaper", MobConfigEntry.of(true,5, 2, 3, true, false));
        mobConfigEntries.putIfAbsent("corrupted_vindicator", MobConfigEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("corrupted_pillager", MobConfigEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("wraith", MobConfigEntry.of(false,true, false));

        corruptedChampionConfigEntries.putIfAbsent("corrupted_champion", CorruptedChampionEntry.of(
              400.0F, 200.0F, 30.0D, 40.0D, 14.0D, 12.0D, 0.15D,
                800, 400,  700, 150, 5,
                30, 5, 6000, List.of("entity.minecraft.villager"), List.of("item.minecraft.debug_stick"),
                true, true, true, 400, 600, 400, -15
        ));
        Config.super.save();
    }

}


