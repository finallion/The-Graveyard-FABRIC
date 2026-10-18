package com.lion.graveyard.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/// Other mobs can set a target that nothing clears later.
public final class ChaseTarget {
    private ChaseTarget() {}

    public static boolean isLive(Mob mob) {
        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive() || target.isRemoved()) {
            return false;
        }

        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }

        double range = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        return mob.distanceToSqr(target) <= range * range;
    }
}
