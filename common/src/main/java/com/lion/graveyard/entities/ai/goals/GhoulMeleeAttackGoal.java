package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GhoulEntity;
import net.minecraft.world.entity.LivingEntity;

public class GhoulMeleeAttackGoal extends AbstractGraveyardMeleeGoal<GhoulEntity> {
    private final int DAMAGE_START_IN_ANIM = 7;

    public GhoulMeleeAttackGoal(GhoulEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle, 14);
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        if (this.mob.isRaging()) {
            canFinishAttack = false;
            return;
        }

        double d = this.getSquaredMaxAttackDistance(target);

        if (squaredDistance <= d && this.cooldown <= 0 && this.mob.getAttackAnimTimer() <= GhoulEntity.ATTACK_ANIM_SYNC_TICKS - this.mob.ATTACK_ANIMATION_DURATION) {
            this.resetCooldown(20);
            this.mob.setAttackAnimTimer(GhoulEntity.ATTACK_ANIM_SYNC_TICKS);
            animationTicker = this.mob.ATTACK_ANIMATION_DURATION;
            canFinishAttack = true;
        }

        if (canFinishAttack && animationTicker == DAMAGE_START_IN_ANIM) {
            if (this.mob.distanceToSqr(target) <= d + 1.0D) {
                this.mob.doHurtTarget(getServerLevel(this.mob), target);
            }

            canFinishAttack = false;
        }
    }
}
