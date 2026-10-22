package com.lion.graveyard.compat.legacy;

import java.util.*;

public class HeadConfig {
    public Map<String, HeadStructureEntry> structureConfigEntries = new HashMap<>();
    public Map<String, HeadParticleEntry> particleConfigEntries = new HashMap<>(1);
    public Map<String, HeadMobEntry> mobConfigEntries = new HashMap<>();
    public Map<String, HeadHordeEntry> hordeConfigEntries = new HashMap<>();
    public Map<String, HeadChampionEntry> corruptedChampionConfigEntries = new HashMap<>();
    public Map<String, Boolean> booleanEntries = new HashMap<>();
    public Map<String, Boolean> mobSpawnEnabled = new HashMap<>();
    public Map<String, Double> numberEntries = new HashMap<>();

    private static final List<String> MANAGED_MOBS = List.of(
            "ghoul",
            "revenant",
            "nightmare",
            "skeleton_creeper",
            "acolyte",
            "reaper",
            "corrupted_vindicator",
            "corrupted_pillager",
            "corrupted_evoker",
            "wraith",
            "raven",
            "magpie",
            "crow",
            "tormented_villager",
            "nameless_hanged"
    );

    private static final Set<String> ENABLED_IGNORED = Set.of("acolyte", "corrupted_vindicator", "corrupted_pillager", "corrupted_evoker", "wraith");

    public boolean enabled(String id) {
        String path = id;

        if (!structureSwitch(path)) {
            return false;
        }

        return !isOverrunVariant(path) || structureSwitch("overrun_village");
    }

    private static boolean isOverrunVariant(String path) {
        return path.startsWith("overrun_") && path.endsWith("_village") && !path.equals("overrun_village");
    }

    private boolean structureSwitch(String path) {
        HeadStructureEntry entry = structureConfigEntries.get(path);
        return entry == null || entry.enabled;
    }

    public double structureChance(String id) {
        double chance = getStructure(id).spawnChance();
        return isOverrunVariant(id) ? chance * getStructure("overrun_village").spawnChance() : chance;
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

        HeadMobEntry entry = mobConfigEntries.get(mobName);
        return entry == null || entry.enabled || ENABLED_IGNORED.contains(mobName);
    }

    public double number(String key, double fallback) {
        Double value = numberEntries.get(key);
        return value == null ? fallback : value;
    }

    public HeadMobEntry mobEntry(String mobName) {
        HeadMobEntry entry = mobConfigEntries.get(mobName);
        return entry == null ? HeadMobEntry.of(true, 0, 0, 0, false, false) : entry;
    }
    public boolean fogSpawn(String id) {
        return getParticle(id).canGenerate;
    }

    public HeadStructureEntry getStructure(String id) {
        for (Map.Entry<String, HeadStructureEntry> entry : structureConfigEntries.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return HeadStructureEntry.of();
    }

    public HeadParticleEntry getParticle(String id) {
        for (Map.Entry<String, HeadParticleEntry> entry : particleConfigEntries.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return HeadParticleEntry.of(50);
    }

    public HeadMobEntry getMob(String id) {
        for (Map.Entry<String, HeadMobEntry> entry : mobConfigEntries.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return HeadMobEntry.of(true, 0, 0, 0, false, false);
    }

    public HeadHordeEntry getHorde(String id) {
        for (Map.Entry<String, HeadHordeEntry> entry : hordeConfigEntries.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return HeadHordeEntry.of(40, 24000, 1200);
    }

    public void save() {
        fillDefaults();
        HeadOmega.GSON.toJson(this);
    }

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

        for (String mob : MANAGED_MOBS) {
            mobSpawnEnabled.putIfAbsent(mob, true);
        }

        particleConfigEntries.putIfAbsent("graveyard_fog_particle", HeadParticleEntry.of(50));

        hordeConfigEntries.putIfAbsent("horde_spawn", HeadHordeEntry.of(40, 24000, 1200));

        structureConfigEntries.putIfAbsent("haunted_house", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("large_graveyard", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("altar", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("crypt", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("giant_mushroom", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("medium_graveyard", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("memorial_tree", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("mushroom_grave", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("small_desert_graveyard", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("small_graveyard", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("small_desert_grave", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("small_grave", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("small_mountain_grave", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("small_savanna_grave", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("lich_prison", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("ruins", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("dead_tree", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("overrun_village", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("overrun_plains_village", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("overrun_savanna_village", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("overrun_taiga_village", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("overrun_snowy_village", HeadStructureEntry.of());
        structureConfigEntries.putIfAbsent("overrun_desert_village", HeadStructureEntry.of());

        mobConfigEntries.putIfAbsent("ghoul", HeadMobEntry.of(true, 25, 2, 5, true, false));
        mobConfigEntries.putIfAbsent("revenant", HeadMobEntry.of(true,25, 5, 8, true, false));
        mobConfigEntries.putIfAbsent("nightmare", HeadMobEntry.of(true,10, 1, 1, false, false));
        mobConfigEntries.putIfAbsent("skeleton_creeper", HeadMobEntry.of(true,25, 1, 4, true, false));
        mobConfigEntries.putIfAbsent("acolyte", HeadMobEntry.of(false, false, false));
        mobConfigEntries.putIfAbsent("reaper", HeadMobEntry.of(false, 5, 2, 3, true, false));
        mobConfigEntries.putIfAbsent("corrupted_vindicator", HeadMobEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("corrupted_pillager", HeadMobEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("corrupted_evoker", HeadMobEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("wraith", HeadMobEntry.of(false,true, false));
        mobConfigEntries.putIfAbsent("tormented_villager", HeadMobEntry.of(true, 0, 1, 1, false, false));
        mobConfigEntries.putIfAbsent("raven", HeadMobEntry.of(true, 8, 2, 4, false, false));
        mobConfigEntries.putIfAbsent("magpie", HeadMobEntry.of(true, 8, 2, 3, false, false));
        mobConfigEntries.putIfAbsent("crow", HeadMobEntry.of(true, 10, 4, 9, false, false));

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

    private static HeadChampionEntry defaultChampion() {
        return HeadChampionEntry.of(
                400.0F,
                200.0F,
                30.0D,
                40.0D,
                14.0D,
                12.0D,
                0.15D,
                800,
                400,
                700,
                150,
                5,
                30,
                5,
                6000,
                List.of("entity.minecraft.villager"),
                List.of("item.minecraft.debug_stick"),
                true,
                true,
                true,
                400,
                600,
                400,
                -15
        );
    }

}


