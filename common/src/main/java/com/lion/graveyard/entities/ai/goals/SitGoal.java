package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GraveyardMinionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;


import java.util.EnumSet;

public class SitGoal extends Goal {
    private final GraveyardMinionEntity tameable;

    public SitGoal(GraveyardMinionEntity tameable) {
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.tameable.isSitting();
    }

    public boolean canUse() {
        if (this.tameable.isInWaterOrBubble()) {
            return false;
        } else if (!this.tameable.isSitting()) {
            return false;
        } else {
            LivingEntity livingEntity = this.tameable.getOwner();
            if (livingEntity == null) {
                return true;
            } else {
                return (!(this.tameable.distanceToSqr(livingEntity) < 144.0D) || livingEntity.getLastHurtByMob() == null) && this.tameable.isSitting();
            }
        }
    }

    public void start() {
        this.tameable.getNavigation().stop();
        this.tameable.setInSittingPose(true);
    }

    public void stop() {
        this.tameable.setInSittingPose(false);
    }
}