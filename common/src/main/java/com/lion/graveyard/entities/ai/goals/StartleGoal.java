package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CorvidEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class StartleGoal extends Goal {
    private final CorvidEntity corvid;
    private final double distance;
    @Nullable
    private Vec3 target;

    public StartleGoal(CorvidEntity corvid, double distance) {
        this.corvid = corvid;
        this.distance = distance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.corvid.isFlying()) {
            return false;
        }

        Player player = this.corvid.level().getNearestPlayer(this.corvid, this.distance);

        if (player == null || player.isShiftKeyDown() || player.isSpectator()) {
            return false;
        }

        Vec3 away = this.corvid.position().subtract(player.position()).normalize();
        this.target = HoverRandomPos.getPos(this.corvid, 12, 10, away.x, away.z, (float) (Math.PI / 2), 10, 6);

        if (this.target == null) {
            this.target = DefaultRandomPos.getPosAway(this.corvid, 8, 7, player.position());
        }

        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.corvid.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.corvid.takeOff();
        this.corvid.getNavigation().moveTo(this.target.x, this.target.y, this.target.z, 1.2D);
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
