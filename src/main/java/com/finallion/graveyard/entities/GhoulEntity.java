package com.finallion.graveyard.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GhoulEntity extends HostileEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder WALK_ANIMATION = new AnimationBuilder().addAnimation("walk", true);
    private final AnimationBuilder RAGE_ANIMATION = new AnimationBuilder().addAnimation("rage", false);
    private final AnimationBuilder RUNNING_ANIMATION = new AnimationBuilder().addAnimation("running", true);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    protected static final byte ANIMATION_IDLE = 0;
    protected final byte ANIMATION_WALK = 1;
    protected static final byte ANIMATION_RAGE = 2;
    protected final byte ANIMATION_DEATH = 3;
    protected static final byte ANIMATION_RUNNING = 4;
    protected static final byte ANIMATION_ATTACK = 5;
    public static final int ANIMATION_ATTACK_LENGTH = 15;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> VARIANT = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
    private AnimationFactory factory = new AnimationFactory(this);
    private int animationTimer = 0;
    private int lock = 0;

    public GhoulEntity(EntityType<? extends GhoulEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        // selects one of four skins for the ghoul
        byte variant = (byte) random.nextInt(4);
        this.dataTracker.startTracking(VARIANT, variant);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
    }

    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    @Override
    public double squaredAttackRange(LivingEntity target) {
        return 4.0D;
    }

    protected void initCustomGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new GhoulMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
    }


    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.33D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D).add(EntityAttributes.GENERIC_ARMOR, 3.0D).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D).build());
        }
        return attributeContainer;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        lock--;

        if (animationTimer > 0) {
            animationTimer--;
            if (animationTimer == 0) {
                setAnimation(ANIMATION_RUNNING);
            }
        }
    }


    @SuppressWarnings("rawtypes")
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);
        boolean isDying = this.isDead();

        if (isDying) {
            controller.setAnimation(DEATH_ANIMATION);
            return PlayState.CONTINUE;
        }

        byte currentAnimation = getAnimation();
        switch (currentAnimation) {
            case ANIMATION_ATTACK:
                controller.setAnimation(ATTACK_ANIMATION);
                animationTimer = 15;
                // TODO:
                // target switches between null and actual target
                // to prevent a sudden stop in action, this will lock the idle animation for a time
                // long enough, so the getTarget-call can return the target
                lock = 25;
                break;
            case ANIMATION_RAGE:
                controller.setAnimation(RAGE_ANIMATION);
                break;
            default:
                if (isAttacking()) {
                    controller.setAnimation(RUNNING_ANIMATION);
                    return PlayState.CONTINUE;
                }
                if (lock == 20) {
                    controller.setAnimation(RAGE_ANIMATION);
                    return PlayState.CONTINUE;
                }

                if (lock <= 0) {
                    controller.setAnimation(isMoving ? WALK_ANIMATION : IDLE_ANIMATION);
                }

                break;
        }

        return PlayState.CONTINUE;

    }

    public float getBrightnessAtEyes() {
        return 2.0F;
    }


    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putByte("ghoulVariant", getVariant());
    }

    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        setVariant(tag.getByte("ghoulVariant"));
    }


    public byte getAnimation() {
        return dataTracker.get(ANIMATION);
    }


    public byte getVariant() {
        return dataTracker.get(VARIANT);
    }

    public void setVariant(byte variant) {
        dataTracker.set(VARIANT, variant);
    }

    public void setAnimation(byte animation) {
        dataTracker.set(ANIMATION, animation);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }


    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_HUSK_AMBIENT, 2.5F, -5.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_HUSK_HURT, 2.5F, -5.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_HUSK_DEATH, 2.5F, -5.0F);
    }



    class GhoulMeleeAttackGoal extends MeleeAttackGoal {
        private final GhoulEntity ghoul;

        public GhoulMeleeAttackGoal(GhoulEntity ghoulEntity, double speed, boolean pauseWhenIdle) {
            super(ghoulEntity, speed, pauseWhenIdle);
            this.ghoul = ghoulEntity;
        }

        private static final int ANIMATION_LENGTH = GhoulEntity.ANIMATION_ATTACK_LENGTH;
        private static final int ANIMATION_ATTACK_START = 14;
        private int animationTimer = 0;

        @Override
        public boolean canStart() {
            return super.canStart();
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = 4.0D; // distance from where the target can be hit
            System.out.println("1: " + animationTimer);
            if (squaredDistance <= d && animationTimer <= 0) {
                ghoul.setAnimation(GhoulEntity.ANIMATION_ATTACK);
                animationTimer = ANIMATION_LENGTH;
            }
            System.out.println("2: " + animationTimer);
            if (animationTimer > 0) {
                animationTimer--;
                System.out.println("3: " + animationTimer);
                if (animationTimer == ANIMATION_ATTACK_START) {
                    System.out.println("4: " + animationTimer);
                    this.mob.tryAttack(target);
                }

                if (animationTimer == 0) {
                    ghoul.setAnimation(GhoulEntity.ANIMATION_RUNNING);
                }


            }
        }

        @Override
        public void stop() {
            super.stop();
            ghoul.setAnimation(GhoulEntity.ANIMATION_RUNNING);
            animationTimer = 0;
        }
    }


}
