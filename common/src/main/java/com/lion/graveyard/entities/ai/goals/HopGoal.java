package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.MagpieEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/// Sets the velocity directly, because the pathfinder cannot make hops.
public class HopGoal extends Goal {
    /// Must equal the length of `animation.magpie.hop`.
    private static final int HOP_INTERVAL = 12;
    private static final double HOP_UP = 0.32D;
    private static final double HOP_FORWARD = 0.22D;
    private static final int TIMEOUT = 120;
    private static final int FIND_ATTEMPTS = 4;
    private static final double MIN_DISTANCE = 2.0D;
    private static final double DISTANCE_SPREAD = 2.0D;

    protected final MagpieEntity magpie;
    @Nullable
    protected Vec3d target;
    private int hopsLeft;
    private int timer;
    private int timeout;

    public HopGoal(MagpieEntity magpie) {
        this.magpie = magpie;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.magpie.isPecking() || !this.magpie.isOnGround() || this.magpie.getRandom().nextInt(60) != 0) {
            return false;
        }

        this.target = this.findTarget();
        return this.target != null;
    }

    @Nullable
    protected Vec3d findTarget() {
        for (int i = 0; i < FIND_ATTEMPTS; i++) {
            float angle = this.magpie.getRandom().nextFloat() * ((float) Math.PI * 2.0F);
            Vec3d direction = new Vec3d(MathHelper.cos(angle), 0.0D, MathHelper.sin(angle));

            if (this.isHopPathClear(direction)) {
                double distance = MIN_DISTANCE + this.magpie.getRandom().nextDouble() * DISTANCE_SPREAD;
                return new Vec3d(this.magpie.getX() + direction.x * distance, this.magpie.getY(), this.magpie.getZ() + direction.z * distance);
            }
        }
        return null;
    }

    protected boolean isHopPathClear(Vec3d direction) {
        BlockPos feet = this.magpie.getBlockPos();
        // A diagonal hop touches both walls of a corner, so check both axes.
        boolean diagonal = Math.abs(direction.x) > 0.35D && Math.abs(direction.z) > 0.35D;
        boolean checkX = diagonal || Math.abs(direction.x) >= Math.abs(direction.z);
        boolean checkZ = diagonal || Math.abs(direction.z) >= Math.abs(direction.x);

        if (checkX && !this.isColumnClear(feet.offset(direction.x > 0.0D ? Direction.EAST : Direction.WEST))) {
            return false;
        }

        return !checkZ || this.isColumnClear(feet.offset(direction.z > 0.0D ? Direction.SOUTH : Direction.NORTH));
    }

    private boolean isColumnClear(BlockPos pos) {
        return (this.isEmptyShape(pos) || this.isLowHalf(pos)) && this.isEmptyShape(pos.up());
    }

    private boolean isEmptyShape(BlockPos pos) {
        World world = this.magpie.getWorld();
        return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
    }

    private boolean isLowHalf(BlockPos pos) {
        World world = this.magpie.getWorld();
        return world.getBlockState(pos).getCollisionShape(world, pos).getMax(Direction.Axis.Y) <= 0.5D;
    }

    protected int getHopInterval() {
        return HOP_INTERVAL;
    }

    protected int rollHopCount() {
        return 3 + this.magpie.getRandom().nextInt(4);
    }

    @Override
    public void start() {
        this.magpie.getNavigation().stop();
        this.magpie.setHopping(true);
        this.hopsLeft = this.rollHopCount();
        this.timer = 0;
        this.timeout = TIMEOUT;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.timeout--;

        if (this.magpie.horizontalCollision) {
            this.hopsLeft = 0;
        }

        this.timer--;

        if (this.timer > 0) {
            return;
        }

        if (!this.magpie.isOnGround() || this.target == null) {
            // Skip the beat. A late hop breaks the sync with the animation loop.
            this.timer = this.getHopInterval();
            return;
        }

        Vec3d direction = new Vec3d(this.target.x - this.magpie.getX(), 0.0D, this.target.z - this.magpie.getZ()).normalize();

        if (!this.isHopPathClear(direction)) {
            this.hopsLeft = 0;
            return;
        }

        float yaw = (float) (MathHelper.atan2(direction.z, direction.x) * MathHelper.DEGREES_PER_RADIAN) - 90.0F;
        this.magpie.setYaw(yaw);
        this.magpie.setBodyYaw(yaw);
        this.magpie.setHeadYaw(yaw);
        this.magpie.setVelocity(direction.x * HOP_FORWARD, HOP_UP, direction.z * HOP_FORWARD);
        this.magpie.velocityDirty = true;
        this.hopsLeft--;
        this.timer = this.getHopInterval();
    }

    @Override
    public boolean shouldContinue() {
        if (!this.magpie.isOnGround() && !this.magpie.isNearGround()) {
            return false;
        }

        return this.timeout > 0 && (this.hopsLeft > 0 || !this.magpie.isOnGround());
    }

    @Override
    public void stop() {
        this.magpie.setHopping(false);
    }
}
