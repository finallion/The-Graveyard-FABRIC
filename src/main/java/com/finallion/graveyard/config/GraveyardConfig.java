package com.finallion.graveyard.config;


import com.finallion.graveyard.util.BiomeModification;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.OverworldBiomeCreator;


import java.util.*;

public class GraveyardConfig implements Config {
    @Comment("""
             Welcome to The Graveyard Config!
             
             Structures:
             Configure separation (Minimum distance between two structures of this type in chunks. Must be less than spacing).
             Configure spacing (Average distance between two structure placement attempts of this type in chunks).
             Configure whitelist: 
                 1) use "minecraft:biome" to whitelist biomes (mod identifier + ":" + biome name).
                 A full list of all the biomes can be found here https:minecraft.fandom.com/wiki/Biome#Biome_IDs.
                 2) use "c:biomeTag" to whitelist the structure for any biome in this tag (c + ":" + tag name). 
                 A list of all valid tags can be found here: https://github.com/FabricMC/fabric/tree/1.19.1/fabric-convention-tags-v1/src/generated/resources/data/c/tags/worldgen/biome
             Configure terrain check radius: set how far from structure placement a block is checked in all cardinal directions. Only necessary for the large graveyard.
             Configure max terrain height: set how far apart the different heights from the terrain check can be. Increase this value to allow more structures to spawn, but to also increase chance of weird placement.
             Configure if graveyard mobs can spawn naturally in structures.
             
             Mobs:
             Configure spawning weight and group size of the spawn.
             Configure if mobs burn in sunlight and/or if mobs are affected by the wither effect.
             Configure whitelist: 
                 1) use "minecraft:biome" to whitelist biomes (mod identifier + ":" + biome name).
                 A full list of all the biomes can be found here https:minecraft.fandom.com/wiki/Biome#Biome_IDs.
                 2) use "c:biomeTag" to whitelist the structure for any biome in this tag (c + ":" + tag name). 
                 A list of all valid tags can be found here: https://github.com/FabricMC/fabric/tree/1.19.1/fabric-convention-tags-v1/src/generated/resources/data/c/tags/worldgen/biome
             Configure blacklist: use "minecraft:biome" to blacklist biomes (mod identifier + ":" + biome name).

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
    public final Map<String, Boolean> booleanEntries = new HashMap<>();

    @Override
    public String getName() {
        return "the-graveyard-1.19-config";
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

        particleConfigEntries.putIfAbsent("graveyard_fog_particle", ParticleConfigEntry.of(50));

        hordeConfigEntries.putIfAbsent("horde_spawn", HordeConfigEntry.of(40, 24000, 1200));

        structureConfigEntries.putIfAbsent("haunted_house", StructureConfigEntry.of(20, 18, 451235912,
                Arrays.asList(
                        "minecraft:dark_forest",
                        "c:swamp",
                        "minecraft:old_growth_pine_taiga",
                        "minecraft:old_growth_spruce_taiga",
                        "terralith:moonlight_valley",
                        "terralith:cloud_forest",
                        "graveyard_biomes:haunted_lakes"),
                Collections.emptyList(),30,3,false));

        structureConfigEntries.putIfAbsent("large_graveyard", StructureConfigEntry.of(16, 15, 304812394,
                Arrays.asList(
                        "minecraft:taiga",
                        "minecraft:snowy_taiga",
                        "c:plains",
                        "terralith:forested_highlands",
                        "terralith:lush_valley",
                        "terralith:shield",
                        "terralith:shield_clearing",
                        "terralith:wintry_forest",
                        "graveyard_biomes:haunted_forest"),
                Collections.emptyList(), 60,5,true));

        structureConfigEntries.putIfAbsent("altar", StructureConfigEntry.of(30, 24, 1093123913,
                Arrays.asList(
                        "minecraft:snowy_plains",
                        "minecraft:ice_spikes"),
                Collections.emptyList(), 7,3,false));

        structureConfigEntries.putIfAbsent("crypt", StructureConfigEntry.of(24, 22, 893183913,
                Arrays.asList(
                        "c:caves"),
                Collections.emptyList(), 1,3,false));

        structureConfigEntries.putIfAbsent("giant_mushroom", StructureConfigEntry.of(20, 18, 365012356,
                Arrays.asList(
                        "minecraft:mushroom_fields"),
                Collections.emptyList(), 10,3,false));

        structureConfigEntries.putIfAbsent("medium_graveyard", StructureConfigEntry.of(18, 16, 1690192399,
                Arrays.asList(
                        "minecraft:forest",
                        "minecraft:flower_forest",
                        "minecraft:windswept_forest",
                        "terralith:brushland",
                        "terralith:blooming_valley",
                        "terralith:temperate_highlands",
                        "graveyard_biomes:eroded_haunted_forest"),
                Collections.emptyList(), 30,3,true));

        structureConfigEntries.putIfAbsent("memorial_tree", StructureConfigEntry.of(14, 12, 529239621,
                Arrays.asList(
                        "minecraft:old_growth_birch_forest",
                        "minecraft:birch_forest",
                        "terralith:birch_taiga"),
                Collections.emptyList(), 10,3,false));

        structureConfigEntries.putIfAbsent("mushroom_grave", StructureConfigEntry.of(24, 18, 379123039,
                Arrays.asList(
                        "c:mushroom",
                        "c:jungle",
                        "c:swamp"),
                Collections.emptyList(), 7,3,false));

        structureConfigEntries.putIfAbsent("small_desert_graveyard", StructureConfigEntry.of(32, 28, 598017285,
                Arrays.asList(
                        "c:desert"),
                Collections.emptyList(), 20,3,false));

        structureConfigEntries.putIfAbsent("small_graveyard", StructureConfigEntry.of(20, 18, 598017285,
                Arrays.asList(
                        "c:plains",
                        "minecraft:meadow",
                        "terralith:blooming_plateau",
                        "terralith:blooming_valley"),
                Collections.emptyList(), 15,3,false));

        structureConfigEntries.putIfAbsent("small_desert_grave", StructureConfigEntry.of(20, 16, 681236914,
                Arrays.asList(
                        "c:desert"),
                Collections.emptyList(), 4,3,false));

        structureConfigEntries.putIfAbsent("small_grave", StructureConfigEntry.of(12, 8, 240451934,
                Arrays.asList(
                        "minecraft:forest",
                        "c:plains",
                        "c:forest",
                        "terralith:blooming_plateau",
                        "terralith:blooming_valley"),
                Collections.emptyList(), 4,3,false));

        structureConfigEntries.putIfAbsent("small_mountain_grave", StructureConfigEntry.of(12, 8, 725689810,
                Arrays.asList(
                        "c:mountain",
                        "terralith:painted_mountains",
                        "terralith:rocky_mountains",
                        "terralith:haze_mountain",
                        "terralith:mountain_steppe"),
                Collections.emptyList(), 4,3,false));

        structureConfigEntries.putIfAbsent("small_savanna_grave", StructureConfigEntry.of(12, 8, 709787761,
                Arrays.asList(
                        "c:badlands",
                        "c:savanna",
                        "terralith:savanna_badlands",
                        "terralith:savanna_slopes",
                        "terralith:fractured_savanna"),
                Collections.emptyList(), 4,3,false));




        mobConfigEntries.putIfAbsent("ghoul", MobConfigEntry.of(true, 25, 2, 5, true, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("revenant", MobConfigEntry.of(true,25, 5, 8, true, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("nightmare", MobConfigEntry.of(true,10, 1, 1, false, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("skeleton_creeper", MobConfigEntry.of(true,25, 1, 4, true, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("acolyte", MobConfigEntry.of(false,0, 2, 3, false, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("reaper", MobConfigEntry.of(true,5, 2, 3, true, false, Arrays.asList("graveyard_biomes:eroded_haunted_forest"), Collections.emptyList(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("corrupted_vindicator", MobConfigEntry.of(false,0, 2, 3, true, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("corrupted_pillager", MobConfigEntry.of(false,0, 2, 3, true, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        mobConfigEntries.putIfAbsent("wraith", MobConfigEntry.of(false,0, 2, 3, true, false, Arrays.asList("c:in_overworld"), getMobBlacklist(), Collections.emptyList()));
        Config.super.save();
    }

    private List<String> getMobBlacklist() {
        Set<RegistryKey<Biome>> biomes = new HashSet<>();
        //biomes.add(BiomeKeys.END_BARRENS);
       // biomes.add(BiomeKeys.SMALL_END_ISLANDS);
        //biomes.add(BiomeKeys.END_MIDLANDS);
        //biomes.add(BiomeKeys.END_HIGHLANDS);
        //biomes.add(BiomeKeys.THE_END);
        //biomes.add(BiomeKeys.BASALT_DELTAS);
        //biomes.add(BiomeKeys.CRIMSON_FOREST);
        //biomes.add(BiomeKeys.WARPED_FOREST);
        //biomes.add(BiomeKeys.NETHER_WASTES);
        biomes.add(BiomeKeys.DEEP_DARK);
        biomes.add(BiomeKeys.MUSHROOM_FIELDS);
        biomes.add(BiomeKeys.DEEP_FROZEN_OCEAN);
        biomes.add(BiomeKeys.FROZEN_OCEAN);
        biomes.add(BiomeKeys.DEEP_COLD_OCEAN);
        biomes.add(BiomeKeys.COLD_OCEAN);
        biomes.add(BiomeKeys.DEEP_OCEAN);
        biomes.add(BiomeKeys.OCEAN);
        biomes.add(BiomeKeys.DEEP_LUKEWARM_OCEAN);
        biomes.add(BiomeKeys.LUKEWARM_OCEAN);
        biomes.add(BiomeKeys.WARM_OCEAN);
        biomes.add(BiomeKeys.FROZEN_RIVER);
        biomes.add(BiomeKeys.RIVER);
        biomes.add(BiomeKeys.FLOWER_FOREST);
        //biomes.add(BiomeKeys.THE_VOID);
        //biomes.add(BiomeKeys.SOUL_SAND_VALLEY);


        return new ArrayList<>(biomes.stream().map(value -> value.getValue().toString()).toList());

    }

}


