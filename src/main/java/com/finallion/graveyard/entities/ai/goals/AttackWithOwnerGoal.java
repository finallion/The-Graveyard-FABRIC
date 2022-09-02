package com.finallion.graveyard.entities.ai.goals;

import com.finallion.graveyard.entities.GraveyardMinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class AttackWithOwnerGoal extends TrackTargetGoal {
    private final GraveyardMinionEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public AttackWithOwnerGoal(GraveyardMinionEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacking = livingEntity.getAttacking();
            int i = livingEntity.getLastAttackTime();
            return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
        }
    }

    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }

        super.start();
    }
}