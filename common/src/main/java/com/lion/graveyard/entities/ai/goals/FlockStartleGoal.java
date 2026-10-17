package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.util.Scarecrows;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
    private Vec3 target;

    public FlockStartleGoal(CrowEntity crow) {
        this.crow = crow;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.crow.isAlarmed() && this.crow.tickCount % SCAN_INTERVAL == 0) {
            this.watch();
        }

        if (!this.crow.isAlarmed() && this.crow.isSentinel() && this.crow.tickCount % SCARECROW_SCAN_INTERVAL == 0) {
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
        Player player = this.crow.level().getNearestPlayer(this.crow, range);

        if (player == null || player.isSpectator()) {
            return;
        }

        if (player.isShiftKeyDown()) {
            double sneakRange = range * SNEAK_FACTOR;

            if (this.crow.distanceToSqr(player) > sneakRange * sneakRange) {
                return;
            }
        }

        this.crow.raiseAlarm();

        if (player instanceof ServerPlayer serverPlayer && this.crow.getFlockSize() >= SCARE_FLOCK_SIZE) {
            TGAdvancements.SCARE_CROWS.trigger(serverPlayer);
        }
    }

    private void watchForScarecrows() {
        if (Scarecrows.isNear(this.crow.level(), this.crow.blockPosition(), Scarecrows.RADIUS)) {
            this.crow.raiseAlarm();

            if (this.crow.level() instanceof ServerLevel world) {
                TGAdvancements.triggerNearby(world, this.crow.position(), SCARECROW_WITNESS_RANGE, TGAdvancements.SCARECROW);
            }
        }
    }

    @Nullable
    private Vec3 findEscape() {
        Player player = this.crow.level().getNearestPlayer(this.crow, 16.0D);
        Vec3 away = player == null
                ? this.crow.getViewVector(1.0F).reverse()
                : this.crow.position().subtract(player.position()).normalize();

        Vec3 escape = HoverRandomPos.getPos(this.crow, ESCAPE_DISTANCE, ESCAPE_HEIGHT, away.x, away.z, (float) (Math.PI / 2), 10, 6);

        if (escape == null && player != null) {
            escape = DefaultRandomPos.getPosAway(this.crow, ESCAPE_DISTANCE, ESCAPE_HEIGHT, player.position());
        }

        return escape;
    }

    @Override
    public boolean canContinueToUse() {
        return this.crow.isAlarmed() && !this.crow.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.crow.setCircling(false);
        this.crow.setGliding(false);
        this.crow.takeOff();
        this.crow.getNavigation().moveTo(this.target.x, this.target.y, this.target.z, ESCAPE_SPEED);
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
