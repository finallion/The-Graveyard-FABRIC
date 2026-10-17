package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.util.Scarecrows;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FlockStartleGoal extends Goal {
    private static final double SENTINEL_RANGE = 6.0D;
    private static final double MEMBER_RANGE = 3.0D;
    private static final double SNEAK_FACTOR = 0.5D;
    private static final int SCAN_INTERVAL = 5;
    private static final int SCARECROW_SCAN_INTERVAL = 20;
    private static final int SCARE_FLOCK_SIZE = 6;
    private static final double SCARECROW_WITNESS_RANGE = 16.0D;
    private static final int ESCAPE_DISTANCE = 14;
    private static final int ESCAPE_HEIGHT = 12;
    private static final double ESCAPE_SPEED = 1.3D;

    private final CrowEntity crow;
    @Nullable
    private Vec3d target;

    public FlockStartleGoal(CrowEntity crow) {
        this.crow = crow;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.crow.isAlarmed() && this.crow.age % SCAN_INTERVAL == 0) {
            this.watch();
        }

        if (!this.crow.isAlarmed() && this.crow.isSentinel() && this.crow.age % SCARECROW_SCAN_INTERVAL == 0) {
            this.watchForScarecrows();
        }

        if (!this.crow.isAlarmed()) {
            return false;
        }

        this.target = this.findEscape();
        return this.target != null;
    }

    private void watch() {
        double range = this.crow.isSentinel() ? SENTINEL_RANGE : MEMBER_RANGE;
        PlayerEntity player = this.crow.getWorld().getClosestPlayer(this.crow, range);

        if (player == null || player.isSpectator()) {
            return;
        }

        if (player.isSneaking()) {
            double sneakRange = range * SNEAK_FACTOR;

            if (this.crow.squaredDistanceTo(player) > sneakRange * sneakRange) {
                return;
            }
        }

        this.crow.raiseAlarm();

        if (player instanceof ServerPlayerEntity serverPlayer && this.crow.getFlockSize() >= SCARE_FLOCK_SIZE) {
            TGAdvancements.SCARE_CROWS.trigger(serverPlayer);
        }
    }

    private void watchForScarecrows() {
        if (Scarecrows.isNear(this.crow.getWorld(), this.crow.getBlockPos(), Scarecrows.RADIUS)) {
            this.crow.raiseAlarm();

            if (this.crow.getWorld() instanceof ServerWorld world) {
                TGAdvancements.triggerNearby(world, this.crow.getPos(), SCARECROW_WITNESS_RANGE, TGAdvancements.SCARECROW);
            }
        }
    }

    @Nullable
    private Vec3d findEscape() {
        PlayerEntity player = this.crow.getWorld().getClosestPlayer(this.crow, 16.0D);
        Vec3d away = player == null
                ? this.crow.getRotationVec(1.0F).negate()
                : this.crow.getPos().subtract(player.getPos()).normalize();

        Vec3d escape = AboveGroundTargeting.find(this.crow, ESCAPE_DISTANCE, ESCAPE_HEIGHT, away.x, away.z, (float) (Math.PI / 2), 10, 6);

        if (escape == null && player != null) {
            escape = NoPenaltyTargeting.findFrom(this.crow, ESCAPE_DISTANCE, ESCAPE_HEIGHT, player.getPos());
        }

        return escape;
    }

    @Override
    public boolean shouldContinue() {
        return this.crow.isAlarmed() && !this.crow.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.crow.setCircling(false);
        this.crow.setGliding(false);
        this.crow.takeOff();
        this.crow.getNavigation().startMovingTo(this.target.x, this.target.y, this.target.z, ESCAPE_SPEED);
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
