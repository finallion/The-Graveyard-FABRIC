package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GraveyardMinionEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class TrackOwnerAttackerGoal extends TargetGoal {
    private final GraveyardMinionEntity tameable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public TrackOwnerAttackerGoal(GraveyardMinionEntity tameable) {
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
                this.attacker = livingEntity.getLastHurtByMob();
                int i = livingEntity.getLastHurtByMobTimestamp();
                return i != this.lastAttackedTime && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && this.tameable.canAttackWithOwner(this.attacker, livingEntity);
            }
        } else {
            return false;
        }

    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.tameable.getOwner();

        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}