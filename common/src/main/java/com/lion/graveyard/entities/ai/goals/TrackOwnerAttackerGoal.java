package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GraveyardMinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;

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
        if (!this.tameable.isSitting()) {
            LivingEntity livingEntity = this.tameable.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                this.attacker = livingEntity.getAttacker();
                int i = livingEntity.getLastAttackedTime();
                return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacker, livingEntity);
            }
        } else {
            return false;
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