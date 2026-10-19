package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.MagpieEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    protected Vec3 target;
    private int hopsLeft;
    private int timer;
    private int timeout;

    public HopGoal(MagpieEntity magpie) {
        this.magpie = magpie;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.magpie.isPecking() || !this.magpie.onGround() || this.magpie.getRandom().nextInt(60) != 0) {
            return false;
        }

        this.target = this.findTarget();
        return this.target != null;
    }

    @Nullable
    protected Vec3 findTarget() {
        for (int i = 0; i < FIND_ATTEMPTS; i++) {
            float angle = this.magpie.getRandom().nextFloat() * ((float) Math.PI * 2.0F);
            Vec3 direction = new Vec3(Mth.cos(angle), 0.0D, Mth.sin(angle));

            if (this.isHopPathClear(direction)) {
                double distance = MIN_DISTANCE + this.magpie.getRandom().nextDouble() * DISTANCE_SPREAD;
                return new Vec3(this.magpie.getX() + direction.x * distance, this.magpie.getY(), this.magpie.getZ() + direction.z * distance);
            }
        }
        return null;
    }

    protected boolean isHopPathClear(Vec3 direction) {
        BlockPos feet = this.magpie.blockPosition();
        // A diagonal hop touches both walls of a corner, so check both axes.
        boolean diagonal = Math.abs(direction.x) > 0.35D && Math.abs(direction.z) > 0.35D;
        boolean checkX = diagonal || Math.abs(direction.x) >= Math.abs(direction.z);
        boolean checkZ = diagonal || Math.abs(direction.z) >= Math.abs(direction.x);

        if (checkX && !this.isColumnClear(feet.relative(direction.x > 0.0D ? Direction.EAST : Direction.WEST))) {
            return false;
        }

        return !checkZ || this.isColumnClear(feet.relative(direction.z > 0.0D ? Direction.SOUTH : Direction.NORTH));
    }

    private boolean isColumnClear(BlockPos pos) {
        return (this.isEmptyShape(pos) || this.isLowHalf(pos)) && this.isEmptyShape(pos.above());
    }

    private boolean isEmptyShape(BlockPos pos) {
        Level world = this.magpie.level();
        return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
    }

    private boolean isLowHalf(BlockPos pos) {
        Level world = this.magpie.level();
        return world.getBlockState(pos).getCollisionShape(world, pos).max(Direction.Axis.Y) <= 0.5D;
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
    public boolean requiresUpdateEveryTick() {
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

        if (!this.magpie.onGround() || this.target == null) {
            // Skip the beat. A late hop breaks the sync with the animation loop.
            this.timer = this.getHopInterval();
            return;
        }

        Vec3 direction = new Vec3(this.target.x - this.magpie.getX(), 0.0D, this.target.z - this.magpie.getZ()).normalize();

        if (!this.isHopPathClear(direction)) {
            this.hopsLeft = 0;
            return;
        }

        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        this.magpie.setYRot(yaw);
        this.magpie.setYBodyRot(yaw);
        this.magpie.setYHeadRot(yaw);
        this.magpie.setDeltaMovement(direction.x * HOP_FORWARD, HOP_UP, direction.z * HOP_FORWARD);
        this.magpie.needsSync = true;
        this.hopsLeft--;
        this.timer = this.getHopInterval();
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.magpie.onGround() && !this.magpie.isNearGround()) {
            return false;
        }

        return this.timeout > 0 && (this.hopsLeft > 0 || !this.magpie.onGround());
    }

    @Override
    public void stop() {
        this.magpie.setHopping(false);
    }
}
