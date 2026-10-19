package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.MagpieEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/// Ground navigation makes the bird take off, so this goal sets the velocity directly.
public class WalkGoal extends HopGoal {
    private static final double WALK_SPEED = 0.07D;
    private static final double ARRIVE_DISTANCE_SQ = 0.36D;
    private static final int TIMEOUT = 100;

    private int timeout;
    private boolean arrived;

    public WalkGoal(MagpieEntity magpie) {
        super(magpie);
    }

    @Override
    public boolean canUse() {
        if (this.magpie.isPecking() || !this.magpie.onGround() || this.magpie.getRandom().nextInt(40) != 0) {
            return false;
        }

        this.target = this.findTarget();
        return this.target != null;
    }

    @Override
    public void start() {
        this.magpie.getNavigation().stop();
        this.magpie.setWalking(true);
        this.timeout = TIMEOUT;
        this.arrived = false;
    }

    @Override
    public void tick() {
        this.timeout--;

        if (this.target == null || this.magpie.horizontalCollision) {
            this.arrived = true;
            return;
        }

        Vec3 offset = new Vec3(this.target.x - this.magpie.getX(), 0.0D, this.target.z - this.magpie.getZ());

        if (offset.lengthSqr() < ARRIVE_DISTANCE_SQ) {
            this.arrived = true;
            return;
        }

        Vec3 direction = offset.normalize();

        if (!this.isHopPathClear(direction)) {
            this.arrived = true;
            return;
        }

        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        this.magpie.setYRot(yaw);
        this.magpie.setYBodyRot(yaw);
        this.magpie.setYHeadRot(yaw);
        Vec3 velocity = this.magpie.getDeltaMovement();
        this.magpie.setDeltaMovement(direction.x * WALK_SPEED, velocity.y, direction.z * WALK_SPEED);
        this.magpie.needsSync = true;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.arrived && this.timeout > 0 && this.magpie.onGround();
    }

    @Override
    public void stop() {
        this.magpie.setWalking(false);
    }
}
