package com.finallion.graveyard.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.function.Predicate;

public class GhoulEntity extends AnimatedGraveyardEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder WALK_ANIMATION = new AnimationBuilder().addAnimation("walk", true);
    private final AnimationBuilder RAGE_ANIMATION = new AnimationBuilder().addAnimation("rage", false);
    private final AnimationBuilder RUNNING_ANIMATION = new AnimationBuilder().addAnimation("running", true);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_WALK = 1;
    protected static final byte ANIMATION_RAGE = 2;
    protected static final byte ANIMATION_DEATH = 3;
    protected static final byte ANIMATION_RUNNING = 4;
    protected static final byte ANIMATION_ATTACK = 5;
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
    }

    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }


    protected void initCustomGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new GhoulMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
    }


    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D).add(EntityAttributes.GENERIC_ARMOR, 3.0D).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D).build());
        }
        return attributeContainer;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();


        if (animationTimer > 0) {
            animationTimer--;
            if (animationTimer == 0) {
                setIdleState(ANIMATION_IDLE);
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

        int currentAnimation = getAnimationState();
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
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
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



    class GhoulMeleeAttackGoal extends MeleeAttackGoal {
        private final GhoulEntity ghoul;
        private final int ANIMATION_ATTACK_LENGTH = 15;

        public GhoulMeleeAttackGoal(GhoulEntity ghoulEntity, double speed, boolean pauseWhenIdle) {
            super(ghoulEntity, speed, pauseWhenIdle);
            this.ghoul = ghoulEntity;
        }

        private static final int ANIMATION_ATTACK_START = 14;

        @Override
        public boolean canStart() {
            return super.canStart();
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity livingentity = this.ghoul.getTarget();
            if (livingentity != null) {
                this.ghoul.setAngryAt(livingentity.getUuid());
            }

        }


        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = 3.0D; // distance from where the target can be hit


            if (squaredDistance <= d && animationTimer <= 0) {
                ghoul.setAttackingState(ANIMATION_ATTACK);
                animationTimer = ANIMATION_ATTACK_LENGTH;
            }


            if (animationTimer > 0) {
                animationTimer--;
                if (animationTimer == ANIMATION_ATTACK_START) {
                    this.mob.tryAttack(target);
                }

                if (animationTimer == 0) {
                    ghoul.setIdleState(ANIMATION_IDLE);
                }


            }


        }

        @Override
        public void stop() {
            super.stop();
            ghoul.setIdleState(ANIMATION_RUNNING);
            animationTimer = 0;
        }
    }



}
