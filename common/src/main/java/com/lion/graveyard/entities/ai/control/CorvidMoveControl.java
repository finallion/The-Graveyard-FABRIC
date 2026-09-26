package com.lion.graveyard.entities.ai.control;

import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/// Vanilla `FlightMoveControl` sets full up or down speed at any height error, so the flight waves.
/// This control sets the vertical speed along a straight line to the target.
public class CorvidMoveControl extends FlightMoveControl {
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
        if (this.state != State.MOVE_TO) {
            this.coast();
            return;
        }
        this.state = State.WAIT;
        // Keep gravity on at ground contact. With zero vertical speed, isOnGround becomes false.
        this.entity.setNoGravity(!this.entity.isOnGround());

        double dx = this.targetX - this.entity.getX();
        double dy = this.targetY - this.entity.getY();
        double dz = this.targetZ - this.entity.getZ();
        double distanceSq = dx * dx + dy * dy + dz * dz;
        if (distanceSq < 2.5E-7D) {
            this.entity.setUpwardSpeed(0.0F);
            this.entity.setForwardSpeed(0.0F);
            return;
        }

        float yaw = (float) (MathHelper.atan2(dz, dx) * MathHelper.DEGREES_PER_RADIAN) - 90.0F;
        this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), yaw, 90.0F));

        if (this.corvid.isPreparingTakeoff()) {
            this.entity.setMovementSpeed(0.0F);
            this.entity.setForwardSpeed(0.0F);
            return;
        }

        double attribute = this.entity.getAttributeValue(this.entity.isOnGround()
                ? EntityAttributes.GENERIC_MOVEMENT_SPEED : EntityAttributes.GENERIC_FLYING_SPEED);
        this.entity.setMovementSpeed((float) (this.speed * attribute));

        double horizontal = Math.sqrt(dx * dx + dz * dz);
        Vec3d velocity = this.entity.getVelocity();
        double desired;
        if (Math.abs(dy) < VERTICAL_DEADZONE) {
            desired = 0.0D;
        } else if (horizontal < 1.0D) {
            desired = MathHelper.clamp(dy * VERTICAL_GAIN, -MAX_SINK, MAX_CLIMB);
        } else {
            double cruise = Math.max(Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z), CRUISE_SPEED);
            desired = MathHelper.clamp(cruise * dy / horizontal, -MAX_SINK, MAX_CLIMB);
        }
        this.entity.setVelocity(velocity.x, MathHelper.lerp(0.3D, velocity.y, desired), velocity.z);
        this.entity.setUpwardSpeed(0.0F);

        float pitch = (float) -(MathHelper.atan2(dy, horizontal) * MathHelper.DEGREES_PER_RADIAN);
        this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), pitch, MAX_PITCH_CHANGE));
    }

    private void coast() {
        if (this.corvid.isFlying() && !this.corvid.isHopping() && !this.entity.isOnGround() && this.corvid.isAlive()) {
            this.entity.setNoGravity(true);
            double attribute = this.entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED);
            this.entity.setMovementSpeed((float) (COAST_THRUST * attribute));
            Vec3d velocity = this.entity.getVelocity();
            double targetVy = this.entity.isTouchingWater() ? 0.25D
                    : this.entity.horizontalCollision ? 0.12D : -0.04D;
            this.entity.setVelocity(velocity.x, MathHelper.lerp(0.2D, velocity.y, targetVy), velocity.z);
            this.entity.setUpwardSpeed(0.0F);
            this.entity.setPitch(this.wrapDegrees(this.entity.getPitch(), 0.0F, MAX_PITCH_CHANGE));
        } else {
            this.entity.setNoGravity(false);
            this.entity.setUpwardSpeed(0.0F);
            this.entity.setForwardSpeed(0.0F);
        }
    }
}
