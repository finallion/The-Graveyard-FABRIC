package com.lion.graveyard.config;

import com.lion.graveyard.config.entries.*;
import java.util.*;
import net.minecraft.resources.Identifier;

public class GraveyardConfig {
    private static final String CHAMPION_ID = "corrupted_champion";
    private static final String HORDE_ID = "horde_spawn";
    private static final String FOG_ID = "graveyard_fog_particle";

    private static final String[][] BOOLEAN_KEYS = {
            {"urnHasDoubleInventory", "behaviour.urnDoubleInventory"},
            {"disableWitherSkeletonSpawner", "behaviour.replaceWitherSkeletonSpawner"},
            {"enableBossMusic", "general.bossMusic"},
            {"chroniclePagesInLoot", "chronicle.pagesInLoot"},
            {"ashParticles", "general.ashParticles"},
            {"structuresOnly", "general.structuresOnly"},
            {"chronicleInventoryButton", "chronicle.inventoryButton"},
            {"chronicleToasts", "chronicle.toasts"},
            {"corvidsStealItems", "behaviour.magpiesStealItems"},
            {"ghoulingCollectsItems", "behaviour.ghoulingCollectsItems"},
            {"crowsEatCrops", "behaviour.crowsEatCrops"},
            {"smolderingBlocksInVillages", "behaviour.smolderingBlocksInVillages"},
            {"skeletonCreeperDestroysBlocks", "behaviour.skeletonCreeperDestroysBlocks"},
            {"wraithGriefing", "behaviour.wraithGriefing"}};

    private static final String[][] NUMBER_KEYS = {
            {"skeletonCreeperExplosionRadius", "behaviour.skeletonCreeperExplosionRadius"},
            {"corvidTheftChance", "behaviour.magpieTheftChance"}};

    public final Map<String, StructureConfigEntry> structureConfigEntries = new HashMap<>();
    public final Map<String, MobConfigEntry> mobConfigEntries = new HashMap<>();
    public final Map<String, HordeConfigEntry> hordeConfigEntries = new HashMap<>();
    public final Map<String, CorruptedChampionEntry> corruptedChampionConfigEntries = new HashMap<>();
    public final Map<String, Boolean> booleanEntries = new HashMap<>();
    public final Map<String, Double> numberEntries = new HashMap<>();

    private final Map<String, ParticleConfigEntry> particleEntries = new HashMap<>();
    private final Map<String, Boolean> mobSwitches = new HashMap<>();

    public GraveyardConfig(ConfigValues values) {
        for (String id : ConfigSchema.STRUCTURE_IDS) {
            structureConfigEntries.put(id, structure(values, "structures." + id + "."));
        }

        for (String id : ConfigSchema.FULL_MOB_IDS) {
            mobConfigEntries.put(id, mob(values, "mobs." + id + "."));
        }

        for (String id : ConfigSchema.ALL_MOB_IDS) {
            mobSwitches.put(id, values.bool("mobs." + id + ".enabled"));
        }

        particleEntries.put(FOG_ID, ParticleConfigEntry.of(values.bool("behaviour.fogParticles.enabled"), values.integer("behaviour.fogParticles.rarity")));
        hordeConfigEntries.put(HORDE_ID, horde(values));
        corruptedChampionConfigEntries.put(CHAMPION_ID, champion(values));

        for (String[] key : BOOLEAN_KEYS) {
            booleanEntries.put(key[0], values.bool(key[1]));
        }

        for (String[] key : NUMBER_KEYS) {
            numberEntries.put(key[0], values.real(key[1]));
        }

        numberEntries.put("namelessHangedMinOffers", (double) values.integer("behaviour.namelessHangedOffers.min"));
        numberEntries.put("namelessHangedMaxOffers", (double) values.integer("behaviour.namelessHangedOffers.max"));
    }

    public boolean enabled(Identifier id) {
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

    public double structureChance(Identifier id) {
        double chance = getStructure(id).spawnChance();
        return isOverrunVariant(id.getPath()) ? chance * getStructure(Identifier.fromNamespaceAndPath(id.getNamespace(), "overrun_village")).spawnChance() : chance;
    }

    public boolean flag(String key, boolean fallback) {
        Boolean value = booleanEntries.get(key);
        return value == null ? fallback : value;
    }

    public boolean structuresOnly() {
        return flag("structuresOnly", false);
    }

    public boolean mobMayAppear(String mobName) {
        Boolean value = mobSwitches.get(mobName);
        return value == null || value;
    }

    public double number(String key, double fallback) {
        Double value = numberEntries.get(key);
        return value == null ? fallback : value;
    }

    public MobConfigEntry mobEntry(String mobName) {
        MobConfigEntry entry = mobConfigEntries.get(mobName);
        return entry == null ? MobConfigEntry.of(true, 0, 0, 0, false, false) : entry;
    }

    public boolean fogSpawn(Identifier id) {
        return getParticle(id).canGenerate;
    }

    public StructureConfigEntry getStructure(Identifier id) {
        StructureConfigEntry entry = structureConfigEntries.get(id.getPath());
        return entry == null ? StructureConfigEntry.of() : entry;
    }

    public ParticleConfigEntry getParticle(Identifier id) {
        ParticleConfigEntry entry = particleEntries.get(id.getPath());
        return entry == null ? ParticleConfigEntry.of(50) : entry;
    }

    public HordeConfigEntry getHorde(Identifier id) {
        HordeConfigEntry entry = hordeConfigEntries.get(id.getPath());
        return entry == null ? HordeConfigEntry.of(40, 24000, 1200) : entry;
    }

    private static StructureConfigEntry structure(ConfigValues values, String prefix) {
        boolean ranged = values.has(prefix + "minY");
        return StructureConfigEntry.of(
                values.bool(prefix + "enabled"),
                values.real(prefix + "chance"),
                ranged ? values.integer(prefix + "minY") : null,
                ranged ? values.integer(prefix + "maxY") : null
        );
    }

    private static MobConfigEntry mob(ConfigValues values, String prefix) {
        return MobConfigEntry.of(
                values.bool(prefix + "enabled"),
                values.integer(prefix + "spawn.weight"),
                values.integer(prefix + "spawn.minGroup"),
                values.integer(prefix + "spawn.maxGroup"),
                values.bool(prefix + "burnsInSunlight"),
                values.bool(prefix + "canBeWithered"),
                values.real(prefix + "health"),
                values.real(prefix + "damage"),
                values.real(prefix + "speed")
        );
    }

    private static HordeConfigEntry horde(ConfigValues values) {
        return HordeConfigEntry.of(
                values.bool("hordes.enabled"),
                values.integer("hordes.spawnAttempts"),
                values.integer("hordes.intervalTicks"),
                values.integer("hordes.randomExtraTicks"),
                values.real("hordes.chancePerCheck"),
                values.integer("hordes.minDistance"),
                values.integer("hordes.maxDistance"),
                values.bool("hordes.nightOnly")
        );
    }

    private static CorruptedChampionEntry champion(ConfigValues values) {
        String c = "corruptedChampion.";
        CorruptedChampionEntry entry = CorruptedChampionEntry.of(
                values.floating(c + "stats.castingHealth"),
                values.floating(c + "stats.huntingHealth"),
                values.real(c + "stats.castingDamage"),
                values.real(c + "stats.huntingDamageBonus"),
                values.real(c + "stats.armor"),
                values.real(c + "stats.armorToughness"),
                values.real(c + "stats.huntingSpeedBonus"),
                values.integer(c + "spells.huntDuration"),
                values.integer(c + "spells.corpseDuration"),
                values.integer(c + "spells.healingDuration"),
                values.integer(c + "spells.levitationDuration"),
                values.integer(c + "spells.maxSkulls"),
                values.integer(c + "fight.maxSummonedMobs"),
                values.integer(c + "fight.maxSummonGroupSize"),
                values.integer(c + "fight.ghoulSpawnTimer"),
                values.strings(c + "summoning.bloodEntities"),
                values.strings(c + "summoning.summonItems"),
                values.bool(c + "fight.multiphase"),
                values.bool(c + "fight.invulnerableDuringSpells"),
                values.bool(c + "summoning.needsStaffFragments"),
                values.integer(c + "spells.corpseCooldown"),
                values.integer(c + "spells.healingCooldown"),
                values.integer(c + "spells.levitationCooldown"),
                values.integer(c + "spells.playerTeleportYOffset")
        );
        entry.bossBarDarkenSky = values.bool(c + "effects.darkenSky");
        entry.bossBarThickenFog = values.bool(c + "effects.thickenFog");
        entry.bossMusicVolume = values.floating(c + "effects.musicVolume");
        entry.spawnInvulnerableTicks = values.integer(c + "summoning.spawnInvulnerableTicks");
        entry.phaseInvulnerableTicks = values.integer(c + "fight.phaseInvulnerableTicks");
        entry.cooldownHunt = values.integer(c + "fight.huntCooldown");
        entry.cooldownCharge = values.integer(c + "fight.chargeCooldown");
        entry.cooldownPounce = values.integer(c + "fight.pounceCooldown");
        entry.healThresholdHealth = values.floating(c + "spells.healThresholdHealth");
        entry.skullExplosionRadius = values.floating(c + "spells.skullExplosionRadius");
        entry.skullsDestroyBlocks = values.bool(c + "spells.skullsDestroyBlocks");
        return entry;
    }
}
