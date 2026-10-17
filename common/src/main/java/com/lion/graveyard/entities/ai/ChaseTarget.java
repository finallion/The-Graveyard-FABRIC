package com.lion.graveyard.entities.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

/// Other mobs can set a target that nothing clears later.
public final class ChaseTarget {
    private ChaseTarget() {
    }

    public static boolean isLive(MobEntity mob) {
        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive() || target.isRemoved()) {
            return false;
        }

        if (target instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
            return false;
        }

        double range = mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        return mob.squaredDistanceTo(target) <= range * range;
    }
}
