package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.RevenantEntity;
import net.minecraft.world.entity.LivingEntity;

public class RevenantMeleeAttackGoal extends AbstractGraveyardMeleeGoal<RevenantEntity> {
    private final int DAMAGE_START_IN_ANIM = 1;

    public RevenantMeleeAttackGoal(RevenantEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle, 15);
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);

        if (squaredDistance <= d && this.cooldown <= 0 && this.mob.getReanimateAnimTimer() <= 0) {
            this.resetCooldown(20);

            if (this.mob.getAttackAnimTimer() <= this.mob.ATTACK_ANIM_SYNC_TICKS - this.mob.ATTACK_ANIMATION_DURATION) {
                this.mob.setAttackAnimTimer(this.mob.ATTACK_ANIM_SYNC_TICKS);
                animationTicker = this.mob.ATTACK_ANIMATION_DURATION;
                canFinishAttack = true;
            }
        }

        if (canFinishAttack && animationTicker == DAMAGE_START_IN_ANIM) {
            if (this.mob.distanceToSqr(target) <= d + 1.0D) {
                this.mob.doHurtTarget(getServerLevel(this.mob), target);
            }

            canFinishAttack = false;
        }
    }
}
