package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

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
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
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
    public boolean canContinueToUse() {
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
