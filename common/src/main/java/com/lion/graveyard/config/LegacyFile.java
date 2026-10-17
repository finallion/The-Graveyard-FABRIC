package com.lion.graveyard.config;

import java.util.List;
import java.util.Map;

final class LegacyFile {
    Map<String, Structure> structureConfigEntries;
    Map<String, Particle> particleConfigEntries;
    Map<String, Mob> mobConfigEntries;
    Map<String, Horde> hordeConfigEntries;
    Map<String, Champion> corruptedChampionConfigEntries;
    Map<String, Boolean> booleanEntries;
    Map<String, Boolean> mobSpawnEnabled;
    Map<String, Double> numberEntries;

    static final class Structure {
        boolean enabled;
        Double chance;
        Integer minY;
        Integer maxY;
    }

    static final class Particle {
        boolean canGenerate;
        int spawnChance;
    }

    static final class Mob {
        boolean enabled;
        int weight;
        int minGroup;
        int maxGroup;
        boolean canBurnInSunlight;
        boolean canBeWithered;
        Double healthMultiplier;
        Double damageMultiplier;
        Double speedMultiplier;
    }

    static final class Horde {
        boolean enabled;
        int mobSpawnAttempts;
        int ticksUntilNextSpawn;
        int additionalRandomizedTicks;
        Double chancePerCheck;
        Integer minDistance;
        Integer maxDistance;
        Boolean nightOnly;
    }

    static final class Champion {
        float healthInCastingPhase;
        float healthInHuntingPhase;
        double damageCastingPhase;
        double damageHuntingPhaseAddition;
        double armor;
        double armorToughness;
        double speedInHuntPhase;
        int durationHuntingPhase;
        int durationFallingCorpseSpell;
        int durationHealingSpell;
        int durationLevitationSpell;
        int maxAmountSkullsInShootSkullSpell;
        int maxSummonedMobs;
        int maxGroupSizeSummonedMobs;
        int ghoulSpawnTimerInFight;
        List<String> isBloodCollectableEntity;
        List<String> isBossSummonableItem;
        boolean summoningNeedsStaffFragments;
        boolean isMultiphaseFight;
        boolean isInvulnerableDuringSpells;
        int cooldownCorpseSpell;
        int cooldownTeleportPlayerAndHeal;
        int cooldownLevitationSpell;
        int playerTeleportYOffset;
        Boolean bossBarDarkenSky;
        Boolean bossBarThickenFog;
        Float bossMusicVolume;
        Integer spawnInvulnerableTicks;
        Integer phaseInvulnerableTicks;
        Integer cooldownHunt;
        Integer cooldownCharge;
        Integer cooldownPounce;
        Float healThresholdHealth;
        Float skullExplosionRadius;
        Boolean skullsDestroyBlocks;
    }
}
