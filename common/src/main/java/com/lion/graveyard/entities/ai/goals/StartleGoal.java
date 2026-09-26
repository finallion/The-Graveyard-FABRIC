package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class StartleGoal extends Goal {
    private final CorvidEntity corvid;
    private final double distance;
    @Nullable
    private Vec3d target;

    public StartleGoal(CorvidEntity corvid, double distance) {
        this.corvid = corvid;
        this.distance = distance;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.corvid.isFlying()) {
            return false;
        }
        PlayerEntity player = this.corvid.getWorld().getClosestPlayer(this.corvid, this.distance);
        if (player == null || player.isSneaking() || player.isSpectator()) {
            return false;
        }
        Vec3d away = this.corvid.getPos().subtract(player.getPos()).normalize();
        this.target = AboveGroundTargeting.find(this.corvid, 12, 10, away.x, away.z, (float) (Math.PI / 2), 10, 6);
        if (this.target == null) {
            this.target = NoPenaltyTargeting.findFrom(this.corvid, 8, 7, player.getPos());
        }
        return this.target != null;
    }

    @Override
    public boolean shouldContinue() {
        return !this.corvid.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.corvid.takeOff();
        this.corvid.getNavigation().startMovingTo(this.target.x, this.target.y, this.target.z, 1.2D);
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
