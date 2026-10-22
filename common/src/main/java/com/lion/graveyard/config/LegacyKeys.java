package com.lion.graveyard.config;

import java.util.ArrayList;
import java.util.List;

public final class LegacyKeys {
    public enum Rule {
        COPY,
        FLOOR,
        MOB_SWITCH,
        SKIP
    }

    public record Entry(String oldPath, String newPath, Rule rule) {
    }

    private static final String[][] BOOLEANS = {
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

    private static final String[][] NUMBERS = {
            {"skeletonCreeperExplosionRadius", "behaviour.skeletonCreeperExplosionRadius"},
            {"corvidTheftChance", "behaviour.magpieTheftChance"}};

    private static final String[][] MOB_FIELDS = {
            {"weight", "spawn.weight"},
            {"minGroup", "spawn.minGroup"},
            {"maxGroup", "spawn.maxGroup"},
            {"canBurnInSunlight", "burnsInSunlight"},
            {"canBeWithered", "canBeWithered"},
            {"healthMultiplier", "health"},
            {"damageMultiplier", "damage"},
            {"speedMultiplier", "speed"}};

    private static final String[][] HORDE_FIELDS = {
            {"enabled", "enabled"},
            {"mobSpawnAttempts", "spawnAttempts"},
            {"ticksUntilNextSpawn", "intervalTicks"},
            {"additionalRandomizedTicks", "randomExtraTicks"},
            {"chancePerCheck", "chancePerCheck"},
            {"minDistance", "minDistance"},
            {"maxDistance", "maxDistance"},
            {"nightOnly", "nightOnly"}};

    private static final String[][] CHAMPION_FIELDS = {
            {"summoningNeedsStaffFragments", "summoning.needsStaffFragments"},
            {"isBossSummonableItem", "summoning.summonItems"},
            {"isBloodCollectableEntity", "summoning.bloodEntities"},
            {"spawnInvulnerableTicks", "summoning.spawnInvulnerableTicks"},
            {"healthInCastingPhase", "stats.castingHealth"},
            {"healthInHuntingPhase", "stats.huntingHealth"},
            {"damageCastingPhase", "stats.castingDamage"},
            {"damageHuntingPhaseAddition", "stats.huntingDamageBonus"},
            {"armor", "stats.armor"},
            {"armorToughness", "stats.armorToughness"},
            {"speedInHuntPhase", "stats.huntingSpeedBonus"},
            {"durationHuntingPhase", "spells.huntDuration"},
            {"durationFallingCorpseSpell", "spells.corpseDuration"},
            {"cooldownCorpseSpell", "spells.corpseCooldown"},
            {"durationHealingSpell", "spells.healingDuration"},
            {"cooldownTeleportPlayerAndHeal", "spells.healingCooldown"},
            {"healThresholdHealth", "spells.healThresholdHealth"},
            {"durationLevitationSpell", "spells.levitationDuration"},
            {"cooldownLevitationSpell", "spells.levitationCooldown"},
            {"playerTeleportYOffset", "spells.playerTeleportYOffset"},
            {"maxAmountSkullsInShootSkullSpell", "spells.maxSkulls"},
            {"skullExplosionRadius", "spells.skullExplosionRadius"},
            {"skullsDestroyBlocks", "spells.skullsDestroyBlocks"},
            {"isMultiphaseFight", "fight.multiphase"},
            {"isInvulnerableDuringSpells", "fight.invulnerableDuringSpells"},
            {"phaseInvulnerableTicks", "fight.phaseInvulnerableTicks"},
            {"cooldownHunt", "fight.huntCooldown"},
            {"cooldownCharge", "fight.chargeCooldown"},
            {"cooldownPounce", "fight.pounceCooldown"},
            {"maxSummonedMobs", "fight.maxSummonedMobs"},
            {"maxGroupSizeSummonedMobs", "fight.maxSummonGroupSize"},
            {"ghoulSpawnTimerInFight", "fight.ghoulSpawnTimer"},
            {"bossBarDarkenSky", "effects.darkenSky"},
            {"bossBarThickenFog", "effects.thickenFog"},
            {"bossMusicVolume", "effects.musicVolume"}};

    private static final List<Entry> ENTRIES = build();

    private LegacyKeys() {}

    public static List<Entry> entries() {
        return ENTRIES;
    }

    private static List<Entry> build() {
        List<Entry> entries = new ArrayList<>();

        for (String id : ConfigSchema.STRUCTURE_IDS) {
            copy(entries, "structureConfigEntries." + id + ".enabled", "structures." + id + ".enabled");
            copy(entries, "structureConfigEntries." + id + ".chance", "structures." + id + ".chance");

            if (id.equals("crypt") || id.equals("lich_prison")) {
                copy(entries, "structureConfigEntries." + id + ".minY", "structures." + id + ".minY");
                copy(entries, "structureConfigEntries." + id + ".maxY", "structures." + id + ".maxY");
            }
        }

        copy(entries, "particleConfigEntries.graveyard_fog_particle.canGenerate", "behaviour.fogParticles.enabled");
        copy(entries, "particleConfigEntries.graveyard_fog_particle.spawnChance", "behaviour.fogParticles.rarity");

        for (String mob : ConfigSchema.FULL_MOB_IDS) {
            entries.add(new Entry("mobConfigEntries." + mob + ".enabled", "mobs." + mob + ".enabled", Rule.MOB_SWITCH));

            for (String[] field : MOB_FIELDS) {
                copy(entries, "mobConfigEntries." + mob + "." + field[0], "mobs." + mob + "." + field[1]);
            }
        }

        for (String[] field : HORDE_FIELDS) {
            copy(entries, "hordeConfigEntries.horde_spawn." + field[0], "hordes." + field[1]);
        }

        for (String[] field : CHAMPION_FIELDS) {
            copy(entries, "corruptedChampionConfigEntries.corrupted_champion." + field[0], "corruptedChampion." + field[1]);
        }

        entries.add(new Entry("booleanEntries.tormentedVillagerLight", "", Rule.SKIP));

        for (String[] key : BOOLEANS) {
            copy(entries, "booleanEntries." + key[0], key[1]);
        }

        for (String mob : ConfigSchema.ALL_MOB_IDS) {
            entries.add(new Entry("mobSpawnEnabled." + mob, "mobs." + mob + ".enabled", Rule.MOB_SWITCH));
        }

        for (String[] key : NUMBERS) {
            copy(entries, "numberEntries." + key[0], key[1]);
        }

        entries.add(new Entry("numberEntries.namelessHangedMinOffers", "behaviour.namelessHangedOffers.min", Rule.FLOOR));
        entries.add(new Entry("numberEntries.namelessHangedMaxOffers", "behaviour.namelessHangedOffers.max", Rule.FLOOR));
        return List.copyOf(entries);
    }

    private static void copy(List<Entry> entries, String oldPath, String newPath) {
        entries.add(new Entry(oldPath, newPath, Rule.COPY));
    }
}
