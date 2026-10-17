package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GraveyardMinionEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class AttackWithOwnerGoal extends TargetGoal {
    private final GraveyardMinionEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public AttackWithOwnerGoal(GraveyardMinionEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.tameable.isSitting()) {
            LivingEntity livingEntity = this.tameable.getOwner();

            if (livingEntity == null) {
                return false;
            } else {
                this.attacking = livingEntity.getLastHurtMob();
                int i = livingEntity.getLastHurtMobTimestamp();
                return i != this.lastAttackTime && this.canAttack(this.attacking, TargetingConditions.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tameable.getOwner();

        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}