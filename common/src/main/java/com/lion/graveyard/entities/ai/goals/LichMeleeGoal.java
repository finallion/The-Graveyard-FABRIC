package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.init.TGParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class LichMeleeGoal extends AbstractGraveyardMeleeGoal<LichEntity> {
    private final int DAMAGE_START_IN_ANIM = 18;
    private int scareSoundAge = 40;
    private final double chaseSpeed;

    public LichMeleeGoal(LichEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle, 40);
        this.chaseSpeed = speed;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();

        if (target != null && target.isAlive() && (this.mob.isPouncing() || this.mob.isCharging() || isDirectChase(target) || isPhaseOneHold(target))) {
            return true;
        }

        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        if (this.mob.isPouncing() || this.mob.isCharging()) {
            return;
        }

        LivingEntity target = this.mob.getTarget();

        if (target != null && this.mob.getPhase() == 1) {
            // The body stands still in phase 1. A path makes the move control turn the body away from the head.
            if (!isStraightBelowOrAbove(target)) {
                this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            // The body control turns a pushed body to yRot.
            this.mob.setYRot(this.mob.yHeadRot);
            this.mob.getNavigation().stop();
            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.animationTicker--;
            onTargetTick();
            attack(target, this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ()));
            return;
        }

        if (target != null && isDirectChase(target)) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.mob.getNavigation().stop();
            this.mob.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), this.chaseSpeed);
            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.animationTicker--;
            onTargetTick();
            attack(target, this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ()));
            return;
        }

        super.tick();
    }

    private boolean isPhaseOneHold(LivingEntity target) {
        return this.mob.getPhase() == 1 && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target);
    }

    private boolean isStraightBelowOrAbove(LivingEntity target) {
        double dx = target.getX() - this.mob.getX();
        double dz = target.getZ() - this.mob.getZ();
        return dx * dx + dz * dz < 1.0D && Math.abs(target.getY() - this.mob.getY()) > 2.0D;
    }

    private boolean isDirectChase(LivingEntity target) {
        return this.mob.getPhase() == 5
                && this.mob.distanceToSqr(target) <= 36.0D
                && this.mob.getSensing().hasLineOfSight(target);
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
                        this.mob.playMeleeSwingSound();
                    }

                    canFinishAttack = true;
                }

                if (canFinishAttack && animationTicker == DAMAGE_START_IN_ANIM && this.mob.distanceToSqr(target) <= d + 1.0D && this.mob.doHurtTarget(getServerLevel(this.mob), target)) {
                    Vec3 vec3d = mob.position().add(0.0D, 1.600000023841858D, 0.0D);
                    Vec3 vec3d2 = target.getEyePosition().subtract(vec3d);
                    Vec3 vec3d3 = vec3d2.normalize();

                    for (int i = 1; i < Mth.floor(vec3d2.length()) + 7; ++i) {
                        Vec3 vec3d4 = vec3d.add(vec3d3.scale((double) i));
                        ((ServerLevel) mob.level()).sendParticles(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, vec3d4.x, vec3d4.y + 1.0D, vec3d4.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }

                    double e = 2.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    double f = 1.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    target.push(vec3d3.x() * e, vec3d3.y() * f, vec3d3.z() * e);
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
            return (double) (this.mob.getBbWidth() * 3.0F * this.mob.getBbWidth() * 3.0F + entity.getBbWidth());
        }

        return (double) (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + entity.getBbWidth());
    }
}
