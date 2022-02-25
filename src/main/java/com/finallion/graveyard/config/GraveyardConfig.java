package com.finallion.graveyard.config;

import com.finallion.graveyard.init.TGStructures;
import com.finallion.graveyard.world.structures.AbstractGraveyardStructure;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.*;

public class GraveyardConfig implements Config {
    @Comment("""
             Welcome to The Graveyard Config!
             
             Structures:
             Configure separation (Minimum distance between two structures of this type in chunks. Must be less than spacing).
             Configure spacing (Average distance between two structure placement attempts of this type in chunks).
             Configure salt (only if you know what you are doing!)
             Configure whitelist: use "#biomeCategory" for biome categories and "minecraft:biome" for biomes (mod identifier + ":" + biome name).
             Configure blacklist: use "#biomeCategory" for biome categories and "minecraft:biome" for biomes (mod identifier + ":" + biome name).
             Configure mod whitelist: use "#modid" to whitelist the biomes of the specified mod for structures to spawn in (examples: #minecraft, #graveyard, #terralith, #byg, #bop).
             Valid vanilla biome categories are: #taiga, #extreme_hills, #jungle, #mesa, #plains, #savanna, #icy, #beach, #forest, #desert, #swamp, #mushroom, #underground, #mountain.
             A full list of all the biomes can be found here https:minecraft.fandom.com/wiki/Biome#Biome_IDs.
             
             Mob Spawning:
             Disable, which and if graveyard mobs spawn in the world (this does not affect the spawning in structures/of spawners), and set their spawning weight and group size.
             Disable, if hordes can spawn in the world and set their spawning chance and size.
             
             Biome whitelist and blacklist:
             Set in which biomes or in which biome categories the graveyard mobs and structures can spawn.
             Modded Biomes: You can find the biome name for modded biomes in game by pressing F3.
             
             Graveyard Fog:
             Turn the graveyard fog particles off and set the chance of spawning them (higher numbers = lower chance of spawning).
             Graveyard fog particles spawn in graveyards and add an atmospheric and spooky element while exploring.
             
            """)

    public final Map<String, StructureConfigEntry> structureConfigEntries = new HashMap<>();
    public final Map<String, ParticleConfigEntry> particleConfigEntries = new HashMap<>(1);
    public final Map<String, MobConfigEntry> mobConfigEntries = new HashMap<>();
    public final Map<String, HordeConfigEntry> hordeConfigEntries = new HashMap<>();
    public final Map<String, BiomeConfigEntry> biomeConfigEntries = new HashMap<>();
    public final Map<String, BiomeFogConfigEntry> biomeFogConfigEntries = new HashMap<>();
    public final Map<String, Boolean> booleanEntries = new HashMap<>();

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
    public boolean biomeFogSpawn(Identifier id) {
        return getFog(id).enabled;
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

    // biome spawn config
    public BiomeConfigEntry getBiome(Identifier id) {
        for (Map.Entry<String, BiomeConfigEntry> entry : biomeConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NullPointerException("Tried BiomeConfigEntry with id: " + id + ", but it was null!");
    }


    // fog spawn config
    public BiomeFogConfigEntry getFog(Identifier id) {
        for (Map.Entry<String, BiomeFogConfigEntry> entry : biomeFogConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        throw new NullPointerException("Tried BiomeFogConfigEntry with id: " + id + ", but it was null!");
    }

    @Override
    public void save() {
        booleanEntries.putIfAbsent("urnHasDoubleInventory", true);

        for (StructureFeature<?> structure : getStructures()) {
            AbstractGraveyardStructure abstractStructure = (AbstractGraveyardStructure) structure;
            StructureConfigEntry entry = abstractStructure.getStructureConfigEntry();
            structureConfigEntries.putIfAbsent(abstractStructure.getStructureName(), entry);
        }

        mobConfigEntries.putIfAbsent("ghoul", MobConfigEntry.of(true, 25, 2, 5, true, false, getAllOverworldBiomeCategories(), Arrays.asList("minecraft:flower_forest", "minecraft:lush_caves")));
        mobConfigEntries.putIfAbsent("revenant", MobConfigEntry.of(true,25, 5, 8, true, false, getAllOverworldBiomeCategories(), Arrays.asList("minecraft:flower_forest", "minecraft:lush_caves")));
        mobConfigEntries.putIfAbsent("nightmare", MobConfigEntry.of(true,10, 1, 1, false, false, getAllOverworldBiomeCategories(), Arrays.asList("minecraft:flower_forest", "minecraft:lush_caves")));
        mobConfigEntries.putIfAbsent("skeleton_creeper", MobConfigEntry.of(true,25, 1, 4, true, false, getAllOverworldBiomeCategories(), Arrays.asList("minecraft:flower_forest", "minecraft:lush_caves")));
        mobConfigEntries.putIfAbsent("acolyte", MobConfigEntry.of(false,0, 2, 3, false, false, getAllOverworldBiomeCategories(), Arrays.asList("minecraft:flower_forest", "minecraft:lush_caves")));
        mobConfigEntries.putIfAbsent("reaper", MobConfigEntry.of(false,0, 2, 3, true, false, getAllOverworldBiomeCategories(), Arrays.asList("minecraft:flower_forest", "minecraft:lush_caves")));

        biomeConfigEntries.putIfAbsent("haunted_forest", BiomeConfigEntry.of());
        biomeConfigEntries.putIfAbsent("haunted_lakes", BiomeConfigEntry.of());
        biomeConfigEntries.putIfAbsent("eroded_haunted_forest", BiomeConfigEntry.of());
        biomeConfigEntries.putIfAbsent("ancient_dead_coral_reef", BiomeConfigEntry.of());

        biomeFogConfigEntries.putIfAbsent("haunted_forest_fog", BiomeFogConfigEntry.of(0.35F, 63, 148));
        biomeFogConfigEntries.putIfAbsent("haunted_lakes_fog", BiomeFogConfigEntry.of(0.3F, 63, 128));
        biomeFogConfigEntries.putIfAbsent("eroded_haunted_forest_fog", BiomeFogConfigEntry.of(0.35F, 93, 160));

        particleConfigEntries.putIfAbsent("graveyard_fog_particle", ParticleConfigEntry.of(50));

        hordeConfigEntries.putIfAbsent("horde_spawn", HordeConfigEntry.of(40, 24000));
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
            if (biome.getName().contains("river") || biome.getName().contains("ocean") || biome.getName().contains("none") || biome.getName().contains("the_end") || biome.getName().contains("nether") || biome.getName().contains("mushroom")) {
                continue;
            }
            biomeNames.add("#" + biome.getName());
        }
        return biomeNames;

    }

}


