package com.lion.graveyard.entities.ai.goals;

import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public abstract class AbstractGraveyardMeleeGoal<E extends PathfinderMob> extends Goal {
    protected final E mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    protected int cooldown;
    private long lastUpdateTime;
    private UUID checkedTargetId;
    private double checkedTargetX;
    private double checkedTargetY;
    private double checkedTargetZ;
    protected int animationTicker;
    protected boolean canFinishAttack = false;

    protected AbstractGraveyardMeleeGoal(E mob, double speed, boolean pauseWhenMobIdle, int initialAnimationTicker) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.animationTicker = initialAnimationTicker;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long now = this.mob.level().getGameTime();
        LivingEntity target = this.mob.getTarget();

        if (target != null && (this.isNewOrFarMoved(target) || this.isInReach(target))) {
            this.lastUpdateTime = now - 20L;
        }

        if (now - this.lastUpdateTime < 20L) {
            return false;
        }

        this.lastUpdateTime = now;

        if (target == null || !target.isAlive()) {
            return false;
        }

        this.rememberChecked(target);
        this.path = this.mob.getNavigation().createPath(target, 0);

        if (this.path != null) {
            return true;
        }

        return this.isInReach(target);
    }

    private boolean isInReach(LivingEntity target) {
        return this.getSquaredMaxAttackDistance(target) >= this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
    }

    private boolean isNewOrFarMoved(LivingEntity target) {
        if (this.checkedTargetId == null || !target.getUUID().equals(this.checkedTargetId)) {
            return true;
        }

        double dx = target.getX() - this.checkedTargetX;
        double dy = target.getY() - this.checkedTargetY;
        double dz = target.getZ() - this.checkedTargetZ;

        return dx * dx + dy * dy + dz * dz >= 16.0D;
    }

    private void rememberChecked(LivingEntity target) {
        this.checkedTargetId = target.getUUID();
        this.checkedTargetX = target.getX();
        this.checkedTargetY = target.getY();
        this.checkedTargetZ = target.getZ();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isDone();
        } else if (!this.mob.isWithinHome(livingEntity.blockPosition())) {
            return false;
        } else {
            return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player) livingEntity).isCreative();
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speed);
        this.mob.setAggressive(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity) null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity != null) {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            double d = this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);

            if ((this.pauseWhenMobIdle || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingEntity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();
                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);

                if (d > 1024.0D) {
                    this.updateCountdownTicks += 10;
                } else if (d > 256.0D) {
                    this.updateCountdownTicks += 5;
                }

                if (!this.mob.getNavigation().moveTo(livingEntity, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.adjustedTickDelay(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.animationTicker--;
            this.onTargetTick();
            this.attack(livingEntity, d);
        }
    }

    protected void onTargetTick() {
    }

    protected abstract void attack(LivingEntity target, double squaredDistance);

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return (double) (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + entity.getBbWidth());
    }

    protected void resetCooldown(int ticks) {
        this.cooldown = this.adjustedTickDelay(ticks);
    }
}
