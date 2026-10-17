package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.MagpieEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
    public boolean canStart() {
        if (this.magpie.isPecking() || !this.magpie.isOnGround() || this.magpie.getRandom().nextInt(40) != 0) {
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
        Vec3d offset = new Vec3d(this.target.x - this.magpie.getX(), 0.0D, this.target.z - this.magpie.getZ());
        if (offset.lengthSquared() < ARRIVE_DISTANCE_SQ) {
            this.arrived = true;
            return;
        }
        Vec3d direction = offset.normalize();
        if (!this.isHopPathClear(direction)) {
            this.arrived = true;
            return;
        }
        float yaw = (float) (MathHelper.atan2(direction.z, direction.x) * MathHelper.DEGREES_PER_RADIAN) - 90.0F;
        this.magpie.setYaw(yaw);
        this.magpie.setBodyYaw(yaw);
        this.magpie.setHeadYaw(yaw);
        Vec3d velocity = this.magpie.getVelocity();
        this.magpie.setVelocity(direction.x * WALK_SPEED, velocity.y, direction.z * WALK_SPEED);
        this.magpie.velocityDirty = true;
    }

    @Override
    public boolean shouldContinue() {
        return !this.arrived && this.timeout > 0 && this.magpie.isOnGround();
    }

    @Override
    public void stop() {
        this.magpie.setWalking(false);
    }
}
