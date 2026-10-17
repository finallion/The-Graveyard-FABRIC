package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.entities.ai.goals.RevenantMeleeAttackGoal;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class RevenantEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final UUID SLOWNESS_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A4");
    private static final EntityAttributeModifier SLOWNESS_EFFECT;

    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> REANIMATE_ANIM_TIMER;
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Boolean> CAN_REANIMATE;
    private static final TrackedData<Boolean> CHASING;
    private static final int CHASE_END_TICKS = 20;

    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("death");
    private final RawAnimation FAKE_DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("fake_death");
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlayAndHold("attack");
    private final RawAnimation RUNNING_ANIMATION = RawAnimation.begin().then("running", Animation.LoopType.LOOP);
    private final RawAnimation REANIMATE_ANIMATION = RawAnimation.begin().thenPlayAndHold("reanimate");


    protected static final int ANIMATION_IDLE = 0;
    protected static final int ANIMATION_WALK = 1;
    protected static final int ANIMATION_DEATH = 2;
    protected static final int ANIMATION_RUNNING = 3;
    protected static final int ANIMATION_MELEE = 4;
    protected static final int ANIMATION_REANIMATE = 5;
    protected static final int ANIMATION_FAKE_DEATH = 6; // necessary because anim can't be hold on last frame

    public final int ATTACK_ANIMATION_DURATION = 15;
    public final int ATTACK_ANIM_SYNC_TICKS = ATTACK_ANIMATION_DURATION + AnimationTable.TRANSITION_TICKS;
    private int lastAttackAnimTimer;
    private int ticksWithoutTarget;

    public final int REANIMATE_DURATION = 120; // duration reanimation anim: 40 + duration death animation: 30 + waiting time: 30

    public RevenantEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "revenant");
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(REANIMATE_ANIM_TIMER, 0);
        this.dataTracker.startTracking(CAN_REANIMATE, true);
        this.dataTracker.startTracking(CHASING, false);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(2, new RevenantMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.targetSelector.add(1, new RevengeGoal(this, LichEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    public static DefaultAttributeContainer.Builder createRevenantAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.5D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.155D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25.0D);
    }



    @Override
    public void tick() {
        super.tick();
        if (this.getEntityWorld().isClient()) {
            restartAttackClipOnRearm();
        }
    }

    // The controller does not restart a running clip.
    private void restartAttackClipOnRearm() {
        int timer = this.getAttackAnimTimer();
        if (timer > this.lastAttackAnimTimer + 1) {
            AnimationController<?> controller = this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get("controller");
            if (controller != null) {
                controller.forceAnimationReset();
            }
        }
        this.lastAttackAnimTimer = timer;
    }

    protected void mobTick() {
        trackChase();

        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIM_SYNC_TICKS) {
            setAnimationState(ANIMATION_MELEE);
        }

        // REANIMATE TIMER
        if (this.getReanimateAnimTimer() > 40) {
            setAnimationState(ANIMATION_FAKE_DEATH);
        }

        if (this.getReanimateAnimTimer() == 40) {
            setAnimationState(ANIMATION_REANIMATE);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.getReanimateAnimTimer() > 0) {
            int animTimer = this.getReanimateAnimTimer() - 1;
            this.setReanimateAnimTimer(animTimer);
        }

        super.mobTick();
    }

    /// The vanilla melee goal stops at each path end.
    private void trackChase() {
        if (ChaseTarget.isLive(this)) {
            this.ticksWithoutTarget = 0;
            this.dataTracker.set(CHASING, true);
        } else if (this.dataTracker.get(CHASING) && ++this.ticksWithoutTarget > CHASE_END_TICKS) {
            this.dataTracker.set(CHASING, false);
        }
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    @Override
    public void tickMovement() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (getReanimateAnimTimer() > 0) {
            this.getLookControl().lookAt(this.getX(), this.getY(), this.getZ());
            if (!entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.addTemporaryModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT);
            }
        }

        super.tickMovement();
    }


    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 35 && !this.getEntityWorld().isClient()) {
            this.getEntityWorld().sendEntityStatus(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE) || source.isIn(DamageTypeTags.IS_FIRE)) {
            int lyingTicks = getReanimateAnimTimer();
            boolean lyingDead = getAnimationState() == ANIMATION_FAKE_DEATH && lyingTicks > 40;
            setCanReanimate(false);
            setReanimateAnimTimer(0);
            if (lyingDead) {
                boolean hurt = super.damage(source, Float.MAX_VALUE);
                if (isAlive()) {
                    setReanimateAnimTimer(lyingTicks);
                }
                return hurt;
            }
        }

        if (amount >= getHealth() && canReanimate() && random.nextInt(4) == 0) {
            setCanReanimate(false);
            setReanimateAnimTimer(REANIMATE_DURATION);
            return false;
        }

        if (getReanimateAnimTimer() > 0) { // negate damage while reanimating
            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(AnimationTable.<RevenantEntity>create()
                .when(() -> (isDead() || getHealth() < 0.01F) && getAnimationState() == ANIMATION_FAKE_DEATH, FAKE_DEATH_ANIMATION)
                .when(() -> isDead() || getHealth() < 0.01F, DEATH_ANIMATION)
                .when(() -> getAnimationState() == ANIMATION_FAKE_DEATH && getReanimateAnimTimer() > 39, FAKE_DEATH_ANIMATION)
                .when(() -> getReanimateAnimTimer() > 0, REANIMATE_ANIMATION)
                .when(() -> getAttackAnimTimer() > 0, ATTACK_ANIMATION)
                .when(this::isRunning, RUNNING_ANIMATION)
                .when(AnimationTable::isMoving, WALK_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .build(this, "controller"));
    }

    private boolean isRunning(AnimationState<RevenantEntity> state) {
        return AnimationTable.isMoving(state) && this.dataTracker.get(CHASING) && !isWet();
    }


    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.REVENANT_AMBIENT.get(), 1.0F, 1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.REVENANT_HURT.get(), 1.0F, 1.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(TGSounds.REVENANT_DEATH.get(), 1.0F, 1.0F);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.REVENANT_STEP.get(), 0.15F, 1.0F);
    }

    public int getAnimationState() {
        return this.dataTracker.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.dataTracker.set(ANIMATION, state);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.dataTracker.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.dataTracker.set(ATTACK_ANIM_TIMER, time);
    }

    public int getReanimateAnimTimer() {
        return (Integer) this.dataTracker.get(REANIMATE_ANIM_TIMER);
    }

    public void setReanimateAnimTimer(int time) {
        this.dataTracker.set(REANIMATE_ANIM_TIMER, time);
    }

    public boolean canReanimate() {
        return this.dataTracker.get(CAN_REANIMATE);
    }

    public void setCanReanimate(boolean bool) {
        this.dataTracker.set(CAN_REANIMATE, bool);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("canReanimate", canReanimate());
        nbt.putInt("ReanimateTimer", getReanimateAnimTimer());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setCanReanimate(nbt.getBoolean("canReanimate"));
        if (nbt.contains("ReanimateTimer")) {
            setReanimateAnimTimer(nbt.getInt("ReanimateTimer"));
            if (getReanimateAnimTimer() > 40) {
                setAnimationState(ANIMATION_FAKE_DEATH);
            }
        }
    }

    static {
        ATTACK_ANIM_TIMER = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
        REANIMATE_ANIM_TIMER = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CAN_REANIMATE = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CHASING = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SLOWNESS_EFFECT = new EntityAttributeModifier(SLOWNESS_ID, "Slowness effect", -0.3D, EntityAttributeModifier.Operation.ADDITION);
    }


}
