package com.lion.graveyard.config.entries;

import java.util.List;

public class CorruptedChampionEntry {
    public final float healthInCastingPhase;
    public final float healthInHuntingPhase;
    public final double damageCastingPhase;
    public final double damageHuntingPhaseAddition;
    public final double armor;
    public final double armorToughness;
    public final double speedInHuntPhase;
    public final int durationHuntingPhase;
    public final int durationFallingCorpseSpell;
    public final int durationHealingSpell;
    public final int durationLevitationSpell;
    public final int maxAmountSkullsInShootSkullSpell;
    public final int maxSummonedMobs;
    public final int maxGroupSizeSummonedMobs;
    public final int ghoulSpawnTimerInFight;
    public final List<String> isBloodCollectableEntity;
    public final List<String> isBossSummonableItem;
    public final boolean summoningNeedsStaffFragments;
    public final boolean isMultiphaseFight;
    public final boolean isInvulnerableDuringSpells;
    public final int cooldownCorpseSpell;
    public final int cooldownTeleportPlayerAndHeal;
    public final int cooldownLevitationSpell;
    public final int playerTeleportYOffset;
    public Boolean bossBarDarkenSky;
    public Boolean bossBarThickenFog;
    public Float bossMusicVolume;
    public Integer spawnInvulnerableTicks;
    public Integer phaseInvulnerableTicks;
    public Integer cooldownHunt;
    public Integer cooldownCharge;
    public Integer cooldownPounce;
    public Float healThresholdHealth;
    public Float skullExplosionRadius;
    public Boolean skullsDestroyBlocks;

    public boolean bossBarDarkenSky() {
        return bossBarDarkenSky == null || bossBarDarkenSky;
    }

    public boolean bossBarThickenFog() {
        return bossBarThickenFog == null || bossBarThickenFog;
    }

    public float bossMusicVolume() {
        return bossMusicVolume == null || !Float.isFinite(bossMusicVolume) ? 1.0F : Math.max(0.0F, Math.min(bossMusicVolume, 4.0F));
    }

    public int spawnInvulnerableTicks() {
        return spawnInvulnerableTicks == null ? 410 : Math.max(200, spawnInvulnerableTicks);
    }

    public int phaseInvulnerableTicks() {
        return phaseInvulnerableTicks == null ? 200 : Math.max(0, phaseInvulnerableTicks);
    }

    public int cooldownHunt() {
        return cooldownHunt == null ? 600 : Math.max(200, cooldownHunt);
    }

    public int cooldownCharge() {
        return cooldownCharge == null ? 160 : Math.max(40, cooldownCharge);
    }

    public int cooldownPounce() {
        return cooldownPounce == null ? 120 : Math.max(40, cooldownPounce);
    }

    public float healThresholdHealth() {
        return healThresholdHealth == null || !Float.isFinite(healThresholdHealth) ? 300.0F : healThresholdHealth;
    }

    public float skullExplosionRadius() {
        return skullExplosionRadius == null || !Float.isFinite(skullExplosionRadius) ? 2.0F : Math.max(0.0F, Math.min(skullExplosionRadius, 16.0F));
    }

    public boolean skullsDestroyBlocks() {
        return skullsDestroyBlocks != null && skullsDestroyBlocks;
    }


    private CorruptedChampionEntry(
            float healthInCastingPhase,
            float healthInHuntingPhase,
            double damageCastingPhase,
            double damageHuntingPhaseAddition,
            double armor,
            double armorToughness,
            double speedInHuntPhase,
            int durationHuntingPhase,
            int durationFallingCorpseSpell,
            int durationHealingSpell,
            int durationLevitationSpell,
            int maxAmountSkullsInShootSkullSpell,
            int maxSummonedMobs,
            int maxGroupSizeSummonedMobs,
            int ghoulSpawnTimerInFight,
            List<String> isBloodCollectableEntity,
            List<String> isBossSummonableItem,
            boolean isMultiphaseFight,
            boolean isInvulnerableDuringSpells,
            boolean summoningNeedsStaffFragments,
            int cooldownCorpseSpell,
            int cooldownTeleportPlayerAndHeal,
            int cooldownLevitationSpell,
            int playerTeleportYOffset
    ) {
        this.healthInCastingPhase = healthInCastingPhase;
        this.healthInHuntingPhase = healthInHuntingPhase;
        this.damageCastingPhase = damageCastingPhase;
        this.damageHuntingPhaseAddition = damageHuntingPhaseAddition;
        this.armor = armor;
        this.armorToughness = armorToughness;
        this.speedInHuntPhase = speedInHuntPhase;
        this.durationHuntingPhase = durationHuntingPhase;
        this.durationFallingCorpseSpell = durationFallingCorpseSpell;
        this.durationHealingSpell = durationHealingSpell;
        this.durationLevitationSpell = durationLevitationSpell;
        this.maxAmountSkullsInShootSkullSpell = maxAmountSkullsInShootSkullSpell;
        this.maxSummonedMobs = maxSummonedMobs;
        this.maxGroupSizeSummonedMobs = maxGroupSizeSummonedMobs;
        this.ghoulSpawnTimerInFight = ghoulSpawnTimerInFight;
        this.isBloodCollectableEntity = isBloodCollectableEntity;
        this.isBossSummonableItem = isBossSummonableItem;
        this.isMultiphaseFight = isMultiphaseFight;
        this.isInvulnerableDuringSpells = isInvulnerableDuringSpells;
        this.summoningNeedsStaffFragments = summoningNeedsStaffFragments;
        this.cooldownCorpseSpell = cooldownCorpseSpell;
        this.cooldownTeleportPlayerAndHeal = cooldownTeleportPlayerAndHeal;
        this.cooldownLevitationSpell = cooldownLevitationSpell;
        this.playerTeleportYOffset = playerTeleportYOffset;
    }

    public static CorruptedChampionEntry of(
            float healthInCastingPhase,
            float healthInHuntingPhase,
            double damageCastingPhase,
            double damageHuntingPhase,
            double armor,
            double armorToughness,
            double speedInHuntPhase,
            int durationHuntingPhase,
            int durationFallingCorpseSpell,
            int durationHealingSpell,
            int durationLevitationSpell,
            int amountShootSkullsSpell,
            int maxSummonedMobs,
            int chanceSummonedMobs,
            int ghoulSpawnTimerInFight,
            List<String> isBloodCollectableEntity,
            List<String> isBossSummonableItem,
            boolean isMultiphaseFight,
            boolean isInvulnerableDuringSpells,
            boolean summoningNeedsStaffFragments,
            int cooldownCorpseSpell,
            int cooldownTeleportPlayerAndHeal,
            int cooldownLevitationSpell,
            int playerTeleportYOffset
    ) {
        return new CorruptedChampionEntry(healthInCastingPhase, healthInHuntingPhase, damageCastingPhase, damageHuntingPhase, armor, armorToughness, speedInHuntPhase, durationHuntingPhase, durationFallingCorpseSpell, durationHealingSpell, durationLevitationSpell, amountShootSkullsSpell, maxSummonedMobs, chanceSummonedMobs, ghoulSpawnTimerInFight, isBloodCollectableEntity, isBossSummonableItem, isMultiphaseFight, isInvulnerableDuringSpells, summoningNeedsStaffFragments, cooldownCorpseSpell, cooldownTeleportPlayerAndHeal, cooldownLevitationSpell, playerTeleportYOffset);
    }
}
