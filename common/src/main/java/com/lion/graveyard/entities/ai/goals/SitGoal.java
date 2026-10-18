package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GraveyardMinionEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class SitGoal extends Goal {
    private final GraveyardMinionEntity tameable;

    public SitGoal(GraveyardMinionEntity tameable) {
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return this.tameable.isSitting();
    }

    @Override
    public boolean canUse() {
        if (this.tameable.isInWater()) {
            return false;
        } else if (!this.tameable.onGround()) {
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

    @Override
    public void start() {
        this.tameable.getNavigation().stop();
        this.tameable.setInSittingPose(true);
    }

    @Override
    public void stop() {
        this.tameable.setInSittingPose(false);
    }
}