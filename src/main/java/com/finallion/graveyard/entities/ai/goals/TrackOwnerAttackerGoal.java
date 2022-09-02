package com.finallion.graveyard.entities.ai.goals;

import com.finallion.graveyard.entities.GraveyardMinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class TrackOwnerAttackerGoal extends TrackTargetGoal {
    private final GraveyardMinionEntity tameable;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public TrackOwnerAttackerGoal(GraveyardMinionEntity tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {

        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        } else {
            this.attacker = livingEntity.getAttacker();
            int i = livingEntity.getLastAttackedTime();
            return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacker, livingEntity);
        }

    }

    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }

        super.start();
    }
}