package com.finallion.graveyard.entites;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.world.World;

public class Acolyte extends VindicatorEntity {
    private AttributeContainer attributeContainer;

    public Acolyte(EntityType<? extends VindicatorEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public AttributeContainer getAttributes() {
        return attributeContainer;
    }
}
