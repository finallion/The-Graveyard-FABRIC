package com.finallion.graveyard.config;

import com.finallion.graveyard.init.TGStructures;
import com.finallion.graveyard.world.structures.AbstractGraveyardStructure;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.*;

public class GraveyardConfig implements Config {
    @Comment("""
             Welcome to The Graveyard Config!
             //
             // Structures:
             // Disable structures to spawn, change their chance of spawning, change their separation and also change their salt.
             //
             // Mob Spawning:
             // Disable, which and if graveyard mobs spawn in the world (this does not affect the spawning in structures/of spawners), and set their spawning weight and group size.
             // Disable, if hordes can spawn in the world and set their spawning chance and size.
             //
             // Biome whitelist and blacklist:
             // Set in which biomes or in which biome categories the graveyard mobs and structures can spawn.
             // Valid vanilla biome categories are: taiga, extreme_hills, jungle, mesa, plains, savanna, icy, beach, forest, desert, swamp, mushroom, underground, mountain, nether.
             // A full list of all the biomes can be found here https://minecraft.fandom.com/wiki/Biome#Biome_IDs.
             // Modded Biomes: You can find the biome name for modded biomes in game by pressing F3.
             // So for example to allow a structure to only spawn in one specific biome, whitelist the biome category and blacklist all biomes of that category except the chosen one.
             //
             // Graveyard Fog:
             // Turn the graveyard fog particles off and set the chance of spawning them (higher numbers = lower chance of spawning).
             // Graveyard fog particles spawn in graveyards and add an atmospheric and spooky element while exploring.
             //
            """)

    public final Map<String, StructureConfigEntry> structureConfigEntries = new HashMap<>();
    public final Map<String, ParticleConfigEntry> particleConfigEntries = new HashMap<>(1);
    public final Map<String, MobConfigEntry> mobConfigEntries = new HashMap<>();
    public final Map<String, HordeConfigEntry> hordeConfigEntries = new HashMap<>();

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
    public boolean mobSpawn(Identifier id) {
        return getMob(id).enabled;
    }
    public boolean hordeSpawn(Identifier id) {
        return getHorde(id).enabled;
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

    // mob spawn config
    public MobConfigEntry getMob(Identifier id) {
        for (Map.Entry<String, MobConfigEntry> entry : mobConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NullPointerException("Tried MobConfigEntry with id: " + id + ", but it was null!");
    }

    // horde spawn config
    public HordeConfigEntry getHorde(Identifier id) {
        for (Map.Entry<String, HordeConfigEntry> entry : hordeConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NullPointerException("Tried HordeConfigEntry with id: " + id + ", but it was null!");
    }

    @Override
    public void save() {

        for (StructureFeature<?> structure : getStructures()) {
            AbstractGraveyardStructure abstractStructure = (AbstractGraveyardStructure) structure;
            StructureConfigEntry entry = abstractStructure.getStructureConfigEntry();
            structureConfigEntries.putIfAbsent(abstractStructure.getStructureName(), entry);
        }

        mobConfigEntries.putIfAbsent("ghoul", MobConfigEntry.of(45, 2, 5, getAllOverworldBiomeCategories()));
        mobConfigEntries.putIfAbsent("revenant", MobConfigEntry.of(45, 5, 8, getAllOverworldBiomeCategories()));
        mobConfigEntries.putIfAbsent("nightmare", MobConfigEntry.of(15, 1, 1, getAllOverworldBiomeCategories()));
        mobConfigEntries.putIfAbsent("skeleton_creeper", MobConfigEntry.of(35, 1, 4, getAllOverworldBiomeCategories()));
        mobConfigEntries.putIfAbsent("acolyte", MobConfigEntry.of(0, 2, 3, getAllOverworldBiomeCategories()));
        mobConfigEntries.putIfAbsent("reaper", MobConfigEntry.of(0, 2, 3, getAllOverworldBiomeCategories()));

        particleConfigEntries.putIfAbsent("graveyard_fog_particle", ParticleConfigEntry.of(50));

        hordeConfigEntries.putIfAbsent("horde_spawn", HordeConfigEntry.of(40, 12000));
        Config.super.save();
    }

    // config gets called earlier than the structure registry
    private List<StructureFeature<?>> getStructures() {
        List<StructureFeature<?>> structures = new ArrayList<>();
        structures.add(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE);
        structures.add(TGStructures.SMALL_GRAVEYARD_STRUCTURE);
        structures.add(TGStructures.LARGE_GRAVEYARD_STRUCTURE);
        structures.add(TGStructures.MUSHROOM_GRAVE_STRUCTURE);
        structures.add(TGStructures.HAUNTED_HOUSE_STRUCTURE);
        structures.add(TGStructures.MEMORIAL_TREE_STRUCTURE);
        structures.add(TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE);
        structures.add(TGStructures.SMALL_GRAVE_STRUCTURE);
        structures.add(TGStructures.SMALL_DESERT_GRAVE_STRUCTURE);
        structures.add(TGStructures.SMALL_SAVANNA_GRAVE_STRUCTURE);
        structures.add(TGStructures.SMALL_MOUNTAIN_GRAVE_STRUCTURE);
        return structures;
    }

    private List<String> getAllOverworldBiomes() {
        Set<Biome> biomes = MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME).getBiomes();
        List<String> biomeNames = new ArrayList<>();
        for (Biome biome : biomes) {
            biomeNames.add(biome.toString());
        }
        return biomeNames;

    }

    private List<String> getAllOverworldBiomeCategories() {
        Biome.Category[] biomeCategory = Biome.Category.values();
        List<String> biomeNames = new ArrayList<>();
        for (Biome.Category biome : biomeCategory) {
            if (biome.getName().contains("river") || biome.getName().contains("ocean") || biome.getName().contains("none") || biome.getName().contains("the_end") || biome.getName().contains("nether")) {
                continue;
            }
            biomeNames.add(biome.toString().toLowerCase(Locale.ROOT));
        }
        return biomeNames;

    }

}


