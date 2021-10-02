package com.finallion.graveyard.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.world.World;

public class Acolyte extends VindicatorEntity {
    private AttributeContainer attributeContainer;

    public Acolyte(EntityType<? extends VindicatorEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null)
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3499999940395355D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D).build());
        return attributeContainer;
    }



}
