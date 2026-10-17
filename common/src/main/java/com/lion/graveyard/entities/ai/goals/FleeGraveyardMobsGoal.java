package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.HostileGraveyardEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

public class FleeGraveyardMobsGoal extends AvoidEntityGoal<HostileGraveyardEntity> {

    public FleeGraveyardMobsGoal(PathfinderMob mob, double slowSpeed, double fastSpeed) {
        super(mob, HostileGraveyardEntity.class, 10.0F, slowSpeed, fastSpeed);
    }
}
