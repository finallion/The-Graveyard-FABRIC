package com.lion.graveyard.compat.legacy;

public class HeadMobEntry {
    public final boolean enabled;
    public final int weight;
    public final int minGroup;
    public final int maxGroup;
    public final boolean canBurnInSunlight;
    public final boolean canBeWithered;
    public final Double healthMultiplier;
    public final Double damageMultiplier;
    public final Double speedMultiplier;

    private HeadMobEntry(
            boolean enabled,
            int weight,
            int minGroup,
            int maxGroup,
            boolean canBurnInSunlight,
            boolean canBeWithered,
            Double healthMultiplier,
            Double damageMultiplier,
            Double speedMultiplier
    ) {
        this.enabled = enabled;
        this.weight = weight;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.canBurnInSunlight = canBurnInSunlight;
        this.canBeWithered = canBeWithered;
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    public double health() {
        return multiplier(healthMultiplier);
    }

    public double damage() {
        return multiplier(damageMultiplier);
    }

    public double speed() {
        return multiplier(speedMultiplier);
    }

    private static double multiplier(Double value) {
        return value == null || !Double.isFinite(value) || value < 0.0D ? 1.0D : Math.max(0.05D, value);
    }

    public HeadMobEntry sanitized() {
        int safeWeight = Math.max(0, weight);
        int safeMin = Math.max(safeWeight > 0 ? 1 : 0, minGroup);
        return new HeadMobEntry(
                enabled,
                safeWeight,
                safeMin,
                Math.max(safeMin, maxGroup),
                canBurnInSunlight,
                canBeWithered,
                health(),
                damage(),
                speed()
        );
    }

    public static HeadMobEntry of(boolean enabled, boolean canBurnInSunlight, boolean canBeWithered) {
        return of(enabled, 0, 0, 0, canBurnInSunlight, canBeWithered);
    }

    public static HeadMobEntry of(
            boolean enabled,
            int weight,
            int minGroup,
            int maxGroup,
            boolean canBurnInSunlight,
            boolean canBeWithered
    ) {
        return new HeadMobEntry(enabled, weight, minGroup, maxGroup, canBurnInSunlight, canBeWithered, 1.0D, 1.0D, 1.0D);
    }
}
