package com.finallion.graveyard.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ReaperEntity extends VexEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder WALK_ANIMATION = new AnimationBuilder().addAnimation("walk", true);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    protected final byte ANIMATION_IDLE = 0;
    protected final byte ANIMATION_WALK = 1;
    protected final byte ANIMATION_SPAWN = 2;
    protected final byte ANIMATION_DEATH = 3;
    protected final byte ANIMATION_ATTACK = 1;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
    public ReaperEntity(EntityType<? extends VexEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_SPAWN);
    }

    @SuppressWarnings("rawtypes")
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);
        boolean isDying = this.isDead();
        boolean isAttacking = this.isAttacking();

        if (isDying) {
            controller.setAnimation(DEATH_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (isAttacking) {
            controller.setAnimation(ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }

        byte currentAnimation = getAnimation();
        switch (currentAnimation) {
            case ANIMATION_ATTACK -> controller.setAnimation(ATTACK_ANIMATION);
            default -> controller.setAnimation(isMoving ? WALK_ANIMATION : IDLE_ANIMATION);
        }

        return PlayState.CONTINUE;
    }



    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D).build());
        }
        return attributeContainer;
    }

    public byte getAnimation() {
        return dataTracker.get(ANIMATION);
    }



    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 2, this::predicate));
    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
