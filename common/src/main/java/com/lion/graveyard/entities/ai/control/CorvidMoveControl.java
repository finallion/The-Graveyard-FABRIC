package com.lion.graveyard.entities.ai.control;

import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.phys.Vec3;

/// Vanilla `FlightMoveControl` sets full up or down speed at any height error, so the flight waves.
/// This control sets the vertical speed along a straight line to the target.
public class CorvidMoveControl extends FlyingMoveControl {
    private static final float MAX_PITCH_CHANGE = 10.0F;
    private static final double VERTICAL_DEADZONE = 0.15D;
    private static final double VERTICAL_GAIN = 0.2D;
    private static final double CRUISE_SPEED = 0.25D;
    private static final double MAX_SINK = 0.3D;
    private static final double MAX_CLIMB = 0.2D;
    private static final double COAST_THRUST = 0.6D;

    private final CorvidEntity corvid;

    public CorvidMoveControl(CorvidEntity entity) {
        super(entity, 10, false);
        this.corvid = entity;
    }

    @Override
    public void tick() {
        if (this.operation != Operation.MOVE_TO) {
            this.coast();
            return;
        }

        this.operation = Operation.WAIT;
        // Keep gravity on at ground contact. With zero vertical speed, isOnGround becomes false.
        this.mob.setNoGravity(!this.mob.onGround());

        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (distanceSq < 2.5E-7D) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
        }

        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, 90.0F));

        if (this.corvid.isPreparingTakeoff()) {
            this.mob.setSpeed(0.0F);
            this.mob.setZza(0.0F);
            return;
        }

        double attribute = this.mob.getAttributeValue(this.mob.onGround()
                ? Attributes.MOVEMENT_SPEED : Attributes.FLYING_SPEED);
        this.mob.setSpeed((float) (this.speedModifier * attribute));

        double horizontal = Math.sqrt(dx * dx + dz * dz);
        Vec3 velocity = this.mob.getDeltaMovement();
        double desired;

        if (Math.abs(dy) < VERTICAL_DEADZONE) {
            desired = 0.0D;
        } else if (horizontal < 1.0D) {
            desired = Mth.clamp(dy * VERTICAL_GAIN, -MAX_SINK, MAX_CLIMB);
        } else {
            double cruise = Math.max(Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z), CRUISE_SPEED);
            desired = Mth.clamp(cruise * dy / horizontal, -MAX_SINK, MAX_CLIMB);
        }

        this.mob.setDeltaMovement(velocity.x, Mth.lerp(0.3D, velocity.y, desired), velocity.z);
        this.mob.setYya(0.0F);

        float pitch = (float) -(Mth.atan2(dy, horizontal) * Mth.RAD_TO_DEG);
        this.mob.setXRot(this.rotlerp(this.mob.getXRot(), pitch, MAX_PITCH_CHANGE));
    }

    private void coast() {
        if (this.corvid.isFlying() && !this.corvid.isHopping() && !this.mob.onGround() && this.corvid.isAlive()) {
            this.mob.setNoGravity(true);
            double attribute = this.mob.getAttributeValue(Attributes.FLYING_SPEED);
            this.mob.setSpeed((float) (COAST_THRUST * attribute));
            Vec3 velocity = this.mob.getDeltaMovement();
            double targetVy = this.mob.isInWater() ? 0.25D
                    : this.mob.horizontalCollision ? 0.12D : -0.04D;
            this.mob.setDeltaMovement(velocity.x, Mth.lerp(0.2D, velocity.y, targetVy), velocity.z);
            this.mob.setYya(0.0F);
            this.mob.setXRot(this.rotlerp(this.mob.getXRot(), 0.0F, MAX_PITCH_CHANGE));
        } else {
            this.mob.setNoGravity(false);
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }
}
