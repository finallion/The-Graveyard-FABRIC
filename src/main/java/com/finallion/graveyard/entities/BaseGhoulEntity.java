package com.finallion.graveyard.entities;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

public class BaseGhoulEntity extends AnimatedGraveyardEntity implements IAnimatable {
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
    protected static final TrackedData<Byte> VARIANT = DataTracker.registerData(BaseGhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
    private AnimationFactory factory = new AnimationFactory(this);
    private static boolean isInRange = false;
    private static boolean canAttack = false;
    private static final double ATTACK_RANGE = 4.5D;
    private static int timeSinceLastAttack = 0;
    private TargetPredicate targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(25.0D).ignoreVisibility();

    public BaseGhoulEntity(EntityType<? extends BaseGhoulEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        // selects one of eight skins for the ghoul
        byte variant = (byte) random.nextInt(8);
        this.dataTracker.startTracking(VARIANT, variant);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0D)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.155D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                    .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D).build());
        }
        return attributeContainer;
    }

    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putByte("ghoulVariant", getVariant());
    }

    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        setVariant(tag.getByte("ghoulVariant"));
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public double squaredAttackRange(LivingEntity target) {
        return ATTACK_RANGE;
    }

    private boolean isInRageDistance() {
        Box box = new Box(new BlockPos(this.getX(), this.getY(), this.getZ())).expand(15.0, 5.0, 15.0);
        PlayerEntity player = this.world.getClosestPlayer(targetPredicate, this);
        LivingEntity villager = this.world.getClosestEntity(MerchantEntity.class, targetPredicate, this, this.getX(), this.getY(), this.getZ(), box);
        LivingEntity ironGolem = this.world.getClosestEntity(IronGolemEntity.class, targetPredicate, this, this.getX(), this.getY(), this.getZ(), box);

        if (player != null) {
            return !(this.distanceTo(player) < ATTACK_RANGE);
        } else if (villager != null) {
            return !(this.distanceTo(villager) < ATTACK_RANGE);
        } else if (ironGolem != null) {
            return !(this.distanceTo(ironGolem) < ATTACK_RANGE + 4.0D);
        }

        return true;
    }

    // to play the rage animation when the mob is spawned in and disengages
    private void stopAttackAnimation() {
        if (!this.hasAngerTime()) {
            setState(ANIMATION_RAGE);
        }
    }

    @Override
    public void tickMovement() {
        // stops attack animation when anger time is 0 and sets rage animation to play
        stopAttackAnimation();

        timeSinceLastAttack--;

        if (!this.world.isClient()) {
            if (this.getTarget() != null) {
                canAttack = this.getTarget().squaredDistanceTo(this) <= ATTACK_RANGE;
            }
        }


        super.tickMovement();
    }

    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if (isAttacking() && canAttack && !(this.isDead() || this.getHealth() < 0.01)) {
            timeSinceLastAttack = 35;
            canAttack = false;
            event.getController().setAnimation(ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (getAnimationState() == ANIMATION_RAGE  && !(this.isDead() || this.getHealth() < 0.01) && isInRageDistance()) {
            event.getController().setAnimation(RAGE_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (timeSinceLastAttack < 0) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);

        if (isDead()) {
            event.getController().setAnimation(DEATH_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (event.isMoving() || isMoving) {
            if (isWet()) {
                event.getController().setAnimation(WALK_ANIMATION);
            } else if (isAttacking() && timeSinceLastAttack < 0) {
                event.getController().setAnimation(RUNNING_ANIMATION);
            } else {
                event.getController().setAnimation(WALK_ANIMATION);
            }
        } else {
            event.getController().setAnimation(IDLE_ANIMATION);
        }
        return PlayState.CONTINUE;

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
        data.addAnimationController(new AnimationController(this, "controller2", 5, this::predicate2));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_HUSK_AMBIENT, 1.0F, -5.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_HUSK_HURT, 1.0F, -5.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.ENTITY_HUSK_DEATH, 1.0F, -5.0F);
    }


    /*
    static class GhoulMeleeAttackGoal extends MeleeAttackGoal {
        private final BaseGhoulEntity ghoul;
        private static final int ATTACK_DURATION = 7;
        private int attackTimer = 0;

        public GhoulMeleeAttackGoal(BaseGhoulEntity ghoulEntity, double speed, boolean pauseWhenIdle) {
            super(ghoulEntity, speed, pauseWhenIdle);
            this.ghoul = ghoulEntity;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            //isInRange = false;
            if (livingEntity != null) {
                double squaredDistance = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());

                // if target is in range, allow attack animation to actually happen
                isInRange = squaredDistance < ATTACK_RANGE;
            }
            super.tick();
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = ATTACK_RANGE;

            if (squaredDistance <= d && attackTimer <= 0) {
                ghoul.setState(ANIMATION_ATTACK);
                attackTimer = ATTACK_DURATION;
                this.mob.tryAttack(target);
                attackTimer--;
            }


            if (attackTimer > 0) {
                attackTimer--;

                if (attackTimer == 0) {
                    ghoul.setState(ANIMATION_RUNNING);
                }
            }

        }

        @Override
        public void stop() {
            super.stop();
            attackTimer = 0;
        }
    }

     */

}
