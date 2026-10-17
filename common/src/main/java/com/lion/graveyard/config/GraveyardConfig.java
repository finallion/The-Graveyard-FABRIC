package com.lion.graveyard.config;

import com.lion.graveyard.config.annotations.Description;
import com.lion.graveyard.config.entries.*;
import com.lion.graveyard.entities.MobSpawnGate;
import java.util.*;
import net.minecraft.resources.ResourceLocation;

public class GraveyardConfig implements Config {
    @Description("""
             Welcome to The Graveyard Config!
             
             Structures:
             Enable or disable structure spawns.
             
             Mobs:
             Configure if mobs burn in sunlight and/or if mobs are affected by the wither effect (all graveyard mobs).
             Configure the biomes, in which graveyard mobs spawn.
                Fabric:
                    Configure spawning weight and group size of the spawn (ghoul, revenant, nightmare, skeleton creeper).
                Forge:
                    Spawning weight and group size come from the same config. A weight of 0 turns the natural spawn off.
                Every mob entry has healthMultiplier, damageMultiplier and speedMultiplier (1.0 = unchanged).
                enabled switches a mob off, like mobSpawnEnabled. For acolyte, wraith and the corrupted illagers only mobSpawnEnabled counts.
             

             Additional:
             Configure graveyard fog particles rising from moss and set the chance of spawning them (higher numbers = lower chance of spawning).
             Configure if hordes of graveyard mobs can spawn and set their size and frequency.
             Configure if urns have a double chest inventory.
             Configure if the wither skeleton spawner in the large graveyard will be replaced by a skeleton spawner.

             Structures only:
             structuresOnly stops every graveyard mob from appearing by itself (natural spawns, structure spawns, spawners, hordes, structure guards).
             Spawn eggs, commands and summons by the player still work.
             mobSpawnEnabled turns off a single mob in the same way (set it to false).
             The overrun village variants have their own switches (overrun_plains_village and so on). overrun_village switches all of them.

             Chronicle and extras:
             chronicleInventoryButton shows the chronicle button in the inventory.
             chronicleToasts shows a toast for each found chronicle page.
             corvidsStealItems lets magpies pick up items.
             ghoulingCollectsItems lets a Ghouling with a coffin pick up items it walks over.
             crowsEatCrops lets crows eat crops.
             tormentedVillagerLight has no effect.
             smolderingBlocksInVillages generates smoldering blocks in overrun villages.
             skeletonCreeperDestroysBlocks lets the skeleton creeper explosion break blocks. skeletonCreeperExplosionRadius sets its size.
             wraithGriefing lets wraiths put out torches and lights.
             corvidTheftChance is the chance per check that a magpie goes for an item.
             namelessHangedMinOffers and namelessHangedMaxOffers set how many trades the nameless hanged offers.
             Structures: chance (0 to 1) is the chance that a placed structure generates. crypt and lich_prison also have minY and maxY.
             Horde: chancePerCheck, minDistance, maxDistance and nightOnly tune the horde spawn.
             
            """)

    public Map<String, StructureConfigEntry> structureConfigEntries = new HashMap<>();
    public Map<String, ParticleConfigEntry> particleConfigEntries = new HashMap<>(1);
    public Map<String, MobConfigEntry> mobConfigEntries = new HashMap<>();
    public Map<String, HordeConfigEntry> hordeConfigEntries = new HashMap<>();
    public Map<String, CorruptedChampionEntry> corruptedChampionConfigEntries = new HashMap<>();
    public Map<String, Boolean> booleanEntries = new HashMap<>();
    public Map<String, Boolean> mobSpawnEnabled = new HashMap<>();
    public Map<String, Double> numberEntries = new HashMap<>();

    private static final Set<String> ENABLED_IGNORED = Set.of("acolyte", "corrupted_vindicator", "corrupted_pillager", "corrupted_evoker", "wraith");

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
    public boolean enabled(ResourceLocation id) {
        String path = id.getPath();

        if (!structureSwitch(path)) {
            return false;
        }

        return !isOverrunVariant(path) || structureSwitch("overrun_village");
    }

    private static boolean isOverrunVariant(String path) {
        return path.startsWith("overrun_") && path.endsWith("_village") && !path.equals("overrun_village");
    }

    private boolean structureSwitch(String path) {
        StructureConfigEntry entry = structureConfigEntries.get(path);
        return entry == null || entry.enabled;
    }

    public double structureChance(ResourceLocation id) {
        double chance = getStructure(id).spawnChance();
        return isOverrunVariant(id.getPath()) ? chance * getStructure(new ResourceLocation(id.getNamespace(), "overrun_village")).spawnChance() : chance;
    }

    public boolean flag(String key, boolean fallback) {
        Boolean value = booleanEntries.get(key);
        return value == null ? fallback : value;
    }

    public boolean structuresOnly() {
        return flag("structuresOnly", false);
    }

    public boolean mobMayAppear(String mobName) {
        Boolean value = mobSpawnEnabled.get(mobName);

        if (value != null && !value) {
            return false;
        }

        MobConfigEntry entry = mobConfigEntries.get(mobName);
        return entry == null || entry.enabled || ENABLED_IGNORED.contains(mobName);
    }

    public double number(String key, double fallback) {
        Double value = numberEntries.get(key);
        return value == null ? fallback : value;
    }

    public MobConfigEntry mobEntry(String mobName) {
        MobConfigEntry entry = mobConfigEntries.get(mobName);
        return entry == null ? MobConfigEntry.of(true, 0, 0, 0, false, false) : entry;
    }
    public boolean fogSpawn(ResourceLocation id) {
        return getParticle(id).canGenerate;
    }

    // structure config
    public StructureConfigEntry getStructure(ResourceLocation id) {
        for (Map.Entry<String, StructureConfigEntry> entry : structureConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        return StructureConfigEntry.of();
    }

    // moss particle config
    public ParticleConfigEntry getParticle(ResourceLocation id) {
        for (Map.Entry<String, ParticleConfigEntry> entry : particleConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        return ParticleConfigEntry.of(50);
    }

    // mob spawn config
    public MobConfigEntry getMob(ResourceLocation id) {
        for (Map.Entry<String, MobConfigEntry> entry : mobConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        return MobConfigEntry.of(true, 0, 0, 0, false, false);
    }

    // horde spawn config
    public HordeConfigEntry getHorde(ResourceLocation id) {
        for (Map.Entry<String, HordeConfigEntry> entry : hordeConfigEntries.entrySet()) {
            if (entry.getKey().equals(id.getPath())) {
                return entry.getValue();
            }
        }
        return HordeConfigEntry.of(40, 24000, 1200);
    }

    @Override
    public void save() {
        fillDefaults();
        Config.super.save();
    }

    @Override
    public void fillDefaults() {
        if (structureConfigEntries == null) {
            structureConfigEntries = new HashMap<>();
        }

        if (particleConfigEntries == null) {
            particleConfigEntries = new HashMap<>(1);
        }

        if (mobConfigEntries == null) {
            mobConfigEntries = new HashMap<>();
        }

        if (hordeConfigEntries == null) {
            hordeConfigEntries = new HashMap<>();
        }

        if (corruptedChampionConfigEntries == null) {
            corruptedChampionConfigEntries = new HashMap<>();
        }

        if (booleanEntries == null) {
            booleanEntries = new HashMap<>();
        }

        if (mobSpawnEnabled == null) {
            mobSpawnEnabled = new HashMap<>();
        }

        if (numberEntries == null) {
            numberEntries = new HashMap<>();
        }

        structureConfigEntries.values().removeIf(Objects::isNull);
        particleConfigEntries.values().removeIf(Objects::isNull);
        mobConfigEntries.values().removeIf(Objects::isNull);
        hordeConfigEntries.values().removeIf(Objects::isNull);
        corruptedChampionConfigEntries.values().removeIf(Objects::isNull);
        booleanEntries.values().removeIf(Objects::isNull);
        mobSpawnEnabled.values().removeIf(Objects::isNull);
        numberEntries.values().removeIf(Objects::isNull);

        booleanEntries.putIfAbsent("urnHasDoubleInventory", true);
        booleanEntries.putIfAbsent("disableWitherSkeletonSpawner", false);
        booleanEntries.putIfAbsent("enableBossMusic", true);
        booleanEntries.putIfAbsent("chroniclePagesInLoot", true);
        booleanEntries.putIfAbsent("ashParticles", true);
        booleanEntries.putIfAbsent("structuresOnly", false);
        booleanEntries.putIfAbsent("chronicleInventoryButton", true);
        booleanEntries.putIfAbsent("chronicleToasts", true);
        booleanEntries.putIfAbsent("corvidsStealItems", true);
        booleanEntries.putIfAbsent("ghoulingCollectsItems", true);
        booleanEntries.putIfAbsent("crowsEatCrops", true);
        booleanEntries.putIfAbsent("tormentedVillagerLight", true);
        booleanEntries.putIfAbsent("smolderingBlocksInVillages", true);
        booleanEntries.putIfAbsent("skeletonCreeperDestroysBlocks", false);
        booleanEntries.putIfAbsent("wraithGriefing", true);

        numberEntries.putIfAbsent("skeletonCreeperExplosionRadius", 3.5D);
        numberEntries.putIfAbsent("corvidTheftChance", 0.1D);
        numberEntries.putIfAbsent("namelessHangedMinOffers", 7.0D);
        numberEntries.putIfAbsent("namelessHangedMaxOffers", 9.0D);

        for (String mob : MobSpawnGate.MANAGED_MOBS) {
            mobSpawnEnabled.putIfAbsent(mob, true);
        }

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
        structureConfigEntries.putIfAbsent("overrun_village", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("overrun_plains_village", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("overrun_savanna_village", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("overrun_taiga_village", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("overrun_snowy_village", StructureConfigEntry.of());
        structureConfigEntries.putIfAbsent("overrun_desert_village", StructureConfigEntry.of());

        mobConfigEntries.putIfAbsent("ghoul", MobConfigEntry.of(true, 25, 2, 5, true, false));
        mobConfigEntries.putIfAbsent("revenant", MobConfigEntry.of(true,25, 5, 8, true, false));
        mobConfigEntries.putIfAbsent("nightmare", MobConfigEntry.of(true,10, 1, 1, false, false));
        mobConfigEntries.putIfAbsent("skeleton_creeper", MobConfigEntry.of(true,25, 1, 4, true, false));
        mobConfigEntries.putIfAbsent("acolyte", MobConfigEntry.of(false, false, false));
        mobConfigEntries.putIfAbsent("reaper", MobConfigEntry.of(false, 5, 2, 3, true, false));
        mobConfigEntries.putIfAbsent("corrupted_vindicator", MobConfigEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("corrupted_pillager", MobConfigEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("corrupted_evoker", MobConfigEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("wraith", MobConfigEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("tormented_villager", MobConfigEntry.of(true, 0, 1, 1, false, false));
        mobConfigEntries.putIfAbsent("raven", MobConfigEntry.of(true, 8, 2, 4, false, false));
        mobConfigEntries.putIfAbsent("magpie", MobConfigEntry.of(true, 8, 2, 3, false, false));
        mobConfigEntries.putIfAbsent("crow", MobConfigEntry.of(true, 10, 4, 9, false, false));

        corruptedChampionConfigEntries.putIfAbsent("corrupted_champion", defaultChampion());

        structureConfigEntries.replaceAll((key, entry) -> switch (key) {
            case "crypt" -> entry.sanitized(-40, -10);
            case "lich_prison" -> entry.sanitized(210, 210);
            default -> entry.sanitized();
        });
        particleConfigEntries.replaceAll((key, entry) -> entry.sanitized());
        hordeConfigEntries.replaceAll((key, entry) -> entry.sanitized());
        mobConfigEntries.replaceAll((key, entry) -> entry.sanitized());
        corruptedChampionConfigEntries.replaceAll((key, entry) -> entry.sanitized(defaultChampion()));
    }

    private static CorruptedChampionEntry defaultChampion() {
        return CorruptedChampionEntry.of(
                400.0F, 200.0F, 30.0D, 40.0D, 14.0D, 12.0D, 0.15D,
                800, 400,  700, 150, 5,
                30, 5, 6000, List.of("entity.minecraft.villager"), List.of("item.minecraft.debug_stick"),
                true, true, true, 400, 600, 400, -15
        );
    }

}


