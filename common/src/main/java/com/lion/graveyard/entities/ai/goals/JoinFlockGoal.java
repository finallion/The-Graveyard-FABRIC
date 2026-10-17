package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

/// `FollowGroupLeaderGoal` accepts only a `SchoolingFishEntity`, so crows need their own goal.
public class JoinFlockGoal extends Goal {
    private static final int SEARCH_INTERVAL = 40;
    private static final int MOVE_INTERVAL = 10;

    private final CrowEntity crow;
    private int cooldown;
    private int moveDelay;

    public JoinFlockGoal(CrowEntity crow) {
        this.crow = crow;
        this.cooldown = crow.getRandom().nextInt(SEARCH_INTERVAL);
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.crow.isPecking()) {
            return false;
        }

        if (this.crow.hasLeader()) {
            return !this.crow.isCloseToLeader();
        }

        if (--this.cooldown > 0) {
            return false;
        }

        this.cooldown = SEARCH_INTERVAL;
        this.crow.findFlock();
        return this.crow.hasLeader() && !this.crow.isCloseToLeader();
    }

    @Override
    public boolean shouldContinue() {
        return this.crow.hasLeader() && !this.crow.isCloseToLeader();
    }

    @Override
    public void start() {
        this.moveDelay = 0;
    }

    @Override
    public void tick() {
        if (--this.moveDelay <= 0) {
            this.moveDelay = MOVE_INTERVAL;
            this.crow.moveTowardLeader();
        }
    }
}
