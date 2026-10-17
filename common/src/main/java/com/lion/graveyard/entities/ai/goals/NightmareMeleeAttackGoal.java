package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.NightmareEntity;
import net.minecraft.entity.LivingEntity;

public class NightmareMeleeAttackGoal extends AbstractGraveyardMeleeGoal<NightmareEntity> {
    private final int DAMAGE_START_IN_ANIM = 10;

    public NightmareMeleeAttackGoal(NightmareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle, 20);
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.cooldown <= 0) {
            this.resetCooldown(20);
            if (this.mob.getAttackAnimTimer() == 0) {
                this.mob.setAttackAnimTimer(this.mob.ATTACK_ANIMATION_DURATION);
                animationTicker = this.mob.ATTACK_ANIMATION_DURATION;
                canFinishAttack = true;
            }
        }

        if (canFinishAttack && animationTicker == DAMAGE_START_IN_ANIM) {
            this.mob.tryAttack(target);
            canFinishAttack = false;
        }
    }
}
