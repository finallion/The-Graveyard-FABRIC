package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.HostileGraveyardEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class FleeGraveyardMobsGoal extends FleeEntityGoal<HostileGraveyardEntity> {

    public FleeGraveyardMobsGoal(PathAwareEntity mob, double slowSpeed, double fastSpeed) {
        super(mob, HostileGraveyardEntity.class, 10.0F, slowSpeed, fastSpeed);
    }
}
