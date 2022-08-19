package com.finallion.graveyard.entities;

import com.finallion.graveyard.init.TGEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.UUID;

public class GhoulEntity extends AngerableGraveyardEntity implements IAnimatable {
    protected static final TrackedData<Byte> ANIMATION_MOVE_STATE = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder WALK_ANIMATION = new AnimationBuilder().addAnimation("walk", true);
    private final AnimationBuilder RAGE_ANIMATION = new AnimationBuilder().addAnimation("rage", false);
    private final AnimationBuilder RUNNING_ANIMATION = new AnimationBuilder().addAnimation("running", true);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_WALK = 1;
    protected static final byte ANIMATION_RAGE = 2;
    protected static final byte ANIMATION_DEATH = 3;
    protected static final byte ANIMATION_RUNNING = 4;
    protected static final byte ANIMATION_ATTACK = 5;
    protected static final byte ANIMATION_SPAWN = 6;
    protected static final TrackedData<Byte> VARIANT = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
    private AnimationFactory factory = new AnimationFactory(this);
    private static boolean canRage = false;
    private static boolean canAttack = false;
    private static final double ATTACK_RANGE = 4.5D;
    private static int timeSinceLastAttack = 0;
    private boolean spawned = false;
    private int spawnTimer;

    //TODO: total rework of the class, this is a mess: replace attributes with DataTrackers, rework animation logic.
    public GhoulEntity(EntityType<? extends GhoulEntity> entityType, World world) {
        super(entityType, world, "ghoul");
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        // selects one of eight skins for the ghoul
        byte variant = (byte) random.nextInt(8);

        this.dataTracker.startTracking(VARIANT, variant);
        this.dataTracker.startTracking(ANIMATION_MOVE_STATE, (byte) 0);

        spawnTimer = 30;
        setState((byte) ANIMATION_SPAWN);
    }

    public int getAnimationState() {
        return this.dataTracker.get(ANIMATION_MOVE_STATE);
    }

    public void setState(byte time) {
        this.dataTracker.set(ANIMATION_MOVE_STATE, time);
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


    public static DefaultAttributeContainer.Builder createGhoulAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.155D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
    }


    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public double squaredAttackRange(LivingEntity target) {
        return ATTACK_RANGE;
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
        spawnTimer--;

        if (world.isClient() && spawnTimer >= 0 && spawned) {
            MinecraftClient.getInstance().particleManager.addBlockBreakParticles(this.getBlockPos().down(), world.getBlockState(this.getBlockPos().down()));
        }


        if (!this.world.isClient()) {
            if (this.getTarget() != null) {
                canAttack = this.getTarget().squaredDistanceTo(this) <= ATTACK_RANGE;
                canRage = this.getTarget().squaredDistanceTo(this) > ATTACK_RANGE * 6;
            }
        }

        super.tickMovement();
    }


    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        // new AnimationBuilder().addAnimation(first_anim, false (this is mandatory)).addAnimation(second_anim, false)
        if (isAttacking() && canAttack && !(this.isDead() || this.getHealth() < 0.01)) {
            timeSinceLastAttack = 25;
            canAttack = false;
            event.getController().setAnimation(ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (getAnimationState() == ANIMATION_RAGE  && !(this.isDead() || this.getHealth() < 0.01) && canRage) {
            event.getController().setAnimation(RAGE_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (timeSinceLastAttack < 0) {
            return PlayState.STOP;
        }

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicate3(AnimationEvent<E> event) {
        if (getAnimationState() == 6) {
            event.getController().setAnimation(SPAWN_ANIMATION);
            spawned = true;

            return PlayState.CONTINUE;
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

        if (spawnTimer < 0) {
            if (event.isMoving() || isMoving) {
                if (isWet()) {
                    event.getController().setAnimation(WALK_ANIMATION);
                } else if (isAttacking() && timeSinceLastAttack < 0) {
                    event.getController().setAnimation(RUNNING_ANIMATION);
                } else if (timeSinceLastAttack < 0) {
                    event.getController().setAnimation(WALK_ANIMATION);
                }
            } else if (timeSinceLastAttack < 0) {
                event.getController().setAnimation(IDLE_ANIMATION);
            }
        }
        return PlayState.CONTINUE;

    }

    public byte getVariant() {
        return dataTracker.get(VARIANT);
    }

    public void setVariant(byte variant) {
        dataTracker.set(VARIANT, variant);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("ghoulVariant", getVariant());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setVariant(nbt.getByte("ghoulVariant"));
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 3, this::predicate));
        data.addAnimationController(new AnimationController(this, "controller2", 3, this::predicate2));
        data.addAnimationController(new AnimationController(this, "controller3", 0, this::predicate3));
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

}
