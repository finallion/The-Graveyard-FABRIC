package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.init.TGParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LichMeleeGoal extends AbstractGraveyardMeleeGoal<LichEntity> {
    private final int DAMAGE_START_IN_ANIM = 18;
    private int scareSoundAge = 40;
    private final double chaseSpeed;

    public LichMeleeGoal(LichEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle, 40);
        this.chaseSpeed = speed;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && target.isAlive() && (this.mob.isPouncing() || this.mob.isCharging() || isDirectChase(target))) {
            return true;
        }
        return super.shouldContinue();
    }

    @Override
    public void tick() {
        if (this.mob.isPouncing() || this.mob.isCharging()) {
            return;
        }
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.mob.getPhase() == 1) {
            // The body stands still in phase 1. A path makes the move control turn the body away from the head.
            this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);
            this.mob.getNavigation().stop();
            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.animationTicker--;
            onTargetTick();
            attack(target, this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ()));
            return;
        }
        if (target != null && isDirectChase(target)) {
            this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);
            this.mob.getNavigation().stop();
            this.mob.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), this.chaseSpeed);
            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.animationTicker--;
            onTargetTick();
            attack(target, this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ()));
            return;
        }
        super.tick();
    }

    private boolean isDirectChase(LivingEntity target) {
        return this.mob.getPhase() == 5
                && this.mob.squaredDistanceTo(target) <= 36.0D
                && this.mob.getVisibilityCache().canSee(target);
    }

    @Override
    protected void onTargetTick() {
        this.scareSoundAge--;
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);

        int phase = this.mob.getPhase();

        if (phase == 1) {
            if (this.mob.canMeeleAttack()) {
                if (squaredDistance <= d && this.cooldown <= 0) {
                    this.resetCooldown(20);
                    if (this.mob.getAttackAnimTimer() == 0) {
                        this.mob.setAttackAnimTimer(this.mob.ATTACK_ANIMATION_DURATION);
                        animationTicker = this.mob.ATTACK_ANIMATION_DURATION;
                    }

                    if (this.mob.getAttackAnimTimer() == this.mob.ATTACK_ANIMATION_DURATION) {
                        this.mob.playAttackSound();
                    }

                    canFinishAttack = true;
                }

                if (canFinishAttack && animationTicker == DAMAGE_START_IN_ANIM && this.mob.tryAttack(target)) {
                    Vec3d vec3d = mob.getPos().add(0.0D, 1.600000023841858D, 0.0D);
                    Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
                    Vec3d vec3d3 = vec3d2.normalize();

                    for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                        Vec3d vec3d4 = vec3d.add(vec3d3.multiply((double) i));
                        ((ServerWorld) mob.getWorld()).spawnParticles(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, vec3d4.x, vec3d4.y + 1.0D, vec3d4.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }

                    double e = 2.5D * (1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    double f = 1.5D * (1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * f, vec3d3.getZ() * e);
                    canFinishAttack = false;
                }
            }
        } else if ((phase == 3 && this.mob.canHuntStart()) || phase == 5) {
            if (phase == 3 && squaredDistance <= d + 11.0D && scareSoundAge <= 0) {
                this.mob.playScareSound();
                scareSoundAge = 240 + this.mob.getRandom().nextInt(200);
            }

            if (squaredDistance <= d && this.cooldown <= 0) {
                if (phase == 3) {
                    if (this.mob.startPhaseTwoStrike()) {
                        this.resetCooldown(40);
                    }
                } else if (this.mob.startCrawlStrike()) {
                    this.resetCooldown(30);
                }
            }
        }
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        if (this.mob.getPhase() == 1) {
            return 16.0D;
        } else if (this.mob.getPhase() == 3) {
            return (double) (this.mob.getWidth() * 3.0F * this.mob.getWidth() * 3.0F + entity.getWidth());
        }
        return (double) (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + entity.getWidth());
    }
}
