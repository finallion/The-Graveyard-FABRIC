package com.finallion.graveyard.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GhoulEntityTest extends AnimatedGraveyardEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    protected static final TrackedData<Byte> VARIANT = DataTracker.registerData(GhoulEntityTest.class, TrackedDataHandlerRegistry.BYTE);


    public GhoulEntityTest(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new GhoulMeleeAttackGoal(this, 1.0D, false, 1));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        // selects one of four skins for the ghoul
        byte variant = (byte) random.nextInt(4);
        this.dataTracker.startTracking(VARIANT, variant);
    }

    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putByte("ghoulVariant", getVariant());
    }

    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        setVariant(tag.getByte("ghoulVariant"));
    }

    public byte getVariant() {
        return dataTracker.get(VARIANT);
    }

    public void setVariant(byte variant) {
        dataTracker.set(VARIANT, variant);
    }


    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.33D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D).add(EntityAttributes.GENERIC_ARMOR, 3.0D).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D).build());
        }
        return attributeContainer;
    }


    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<GhoulEntityTest>(this, "controller", 0, this::predicate));
        animationData.addAnimationController(new AnimationController<GhoulEntityTest>(this, "controller1", 0, this::predicate1));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() || this.isWet()) {
            if (this.hasAngerTime()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("running", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", true));
            }
            return PlayState.CONTINUE;
        }

        if (this.isDead()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", false));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicate1(AnimationEvent<E> event) {
        if (this.dataTracker.get(ANIMATION_STATE) == 1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("attack", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_HUSK_AMBIENT, 1.5F, -5.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_HUSK_HURT, 1.5F, -5.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_HUSK_DEATH, 1.5F, -5.0F);
    }


    static class GhoulMeleeAttackGoal extends MeleeAttackGoal {
        private final GhoulEntityTest ghoul;
        private final int stateChecker;
        private int ticksUntilNextAttack;
        private final int ANIMATION_LENGTH = 22;
        private final int ANIMATION_START = 5;

        public GhoulMeleeAttackGoal(GhoulEntityTest ghoulEntity, double speed, boolean pauseWhenIdle, int state) {
            super(ghoulEntity, speed, pauseWhenIdle);
            this.ghoul = ghoulEntity;
            this.stateChecker = state;
            this.ticksUntilNextAttack = 0;
        }


        @Override
        public boolean canStart() {
            return super.canStart();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.ghoul.getTarget();
            if (livingentity != null) {
                double distance = this.mob.squaredDistanceTo(livingentity.getX(), livingentity.getY(), livingentity.getZ());

                attack(livingentity, distance);
            }

        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);

            if (squaredDistance <= d && this.ticksUntilNextAttack <= 0) {
               // this.ghoul.setAttackingState(stateChecker);
                ticksUntilNextAttack = ANIMATION_LENGTH;
            }

            if (ticksUntilNextAttack > 0) {
                this.ticksUntilNextAttack--;
                if (ticksUntilNextAttack == ANIMATION_START) {
                    this.mob.tryAttack(target);
                }

                if (ticksUntilNextAttack == 0) {
                }
            }

        }

        @Override
        public void stop() {
            super.stop();
            this.ghoul.stopAnger();
            ticksUntilNextAttack = 0;
        }
    }
}
