package com.finallion.graveyard.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class LichEntity extends HostileEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);

    public LichEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.155D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                    .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D).build());
        }
        return attributeContainer;
    }
}
