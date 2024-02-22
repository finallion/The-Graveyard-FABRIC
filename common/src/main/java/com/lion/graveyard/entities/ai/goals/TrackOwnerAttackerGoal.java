package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GraveyardMinionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class TrackOwnerAttackerGoal extends TargetGoal {
    private final GraveyardMinionEntity tameAnimal;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public TrackOwnerAttackerGoal(GraveyardMinionEntity tameable) {
        super(tameable, false);
        this.tameAnimal = tameable;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        if (!this.tameAnimal.isSitting()) {
            LivingEntity livingentity = this.tameAnimal.getOwner();
            if (livingentity == null) {
                return false;
            } else {
                this.ownerLastHurtBy = livingentity.getLastHurtByMob();
                int i = livingentity.getLastHurtByMobTimestamp();
                return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = this.tameAnimal.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }

}