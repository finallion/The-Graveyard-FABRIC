package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.RevenantMeleeAttackGoal;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class RevenantEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final UUID SLOWNESS_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A4");
    private static final AttributeModifier SLOWNESS_EFFECT;

    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> REANIMATE_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Boolean> CAN_REANIMATE;

    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation FAKE_DEATH_ANIMATION = RawAnimation.begin().then("fake_death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation RUNNING_ANIMATION = RawAnimation.begin().then("running", Animation.LoopType.LOOP);
    private final RawAnimation REANIMATE_ANIMATION = RawAnimation.begin().then("reanimate", Animation.LoopType.PLAY_ONCE);


    protected static final int ANIMATION_IDLE = 0;
    protected static final int ANIMATION_WALK = 1;
    protected static final int ANIMATION_DEATH = 2;
    protected static final int ANIMATION_RUNNING = 3;
    protected static final int ANIMATION_MELEE = 4;
    protected static final int ANIMATION_REANIMATE = 5;
    protected static final int ANIMATION_FAKE_DEATH = 6; // necessary because anim can't be hold on last frame

    public final int ATTACK_ANIMATION_DURATION = 9;
    public final int REANIMATE_DURATION = 120; // duration reanimation anim: 40 + duration death animation: 30 + waiting time: 30

    public RevenantEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, "revenant");
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
        this.entityData.define(ATTACK_ANIM_TIMER, 0);
        this.entityData.define(REANIMATE_ANIM_TIMER, 0);
        this.entityData.define(CAN_REANIMATE, true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new RevenantMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, LichEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public static AttributeSupplier.Builder createRevenantAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.155D)
                .add(Attributes.FOLLOW_RANGE, 25.0D);
    }



    protected void customServerAiStep() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
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

        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (getReanimateAnimTimer() > 0) {
            this.getLookControl().setLookAt(this.getX(), this.getY(), this.getZ());
            if (!entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.addTransientModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT.getId());
            }
        }

        super.aiStep();
    }


    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 30 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_RESISTANCE) || source.is(DamageTypeTags.IS_FIRE)) {
            setCanReanimate(false);
            setReanimateAnimTimer(0);
        }

        if (amount >= getHealth() && canReanimate() && random.nextInt(4) == 0) {
            setCanReanimate(false);
            setReanimateAnimTimer(REANIMATE_DURATION);
            return false;
        }

        if (getReanimateAnimTimer() > 0) { // negate damage while reanimating
            return false;
        }

        return super.hurt(source, amount);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 0, event -> {
            /* DEATH */
            if (this.isDeadOrDying() || this.getHealth() < 0.01) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* REANIMATION */
            if (getAnimationState() == ANIMATION_FAKE_DEATH && getReanimateAnimTimer() > 39) { // one tick delay
                event.getController().setAnimation(FAKE_DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_REANIMATE && getReanimateAnimTimer() == 39) { // one tick delay
                event.getController().setAnimation(REANIMATE_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAggressive() && !(this.isDeadOrDying() || this.getHealth() < 0.01) && getReanimateAnimTimer() <= 0) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2);
                event.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* WALK */
            if (((getAnimationState() == ANIMATION_WALK || event.isMoving()) && getAttackAnimTimer() <= 0) && getReanimateAnimTimer() <= 0) {
                if (isAggressive() && !isInWater()) {
                    event.getController().setAnimation(RUNNING_ANIMATION);
                } else {
                    event.getController().setAnimation(WALK_ANIMATION);
                }
                return PlayState.CONTINUE;
            }

            /* IDLE */
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && !event.isMoving() && getReanimateAnimTimer() <= 0) {
                event.getController().setAnimation(IDLE_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* STOPPERS */
            // stops idle animation from looping
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() > 0) {
                setAnimationState(ANIMATION_MELEE);
                return PlayState.STOP;
            }

            // stops attack animation from looping
            if (getAttackAnimTimer() <= 0 && !(this.isDeadOrDying() || this.getHealth() < 0.01) && getAnimationState() != ANIMATION_REANIMATE) {
                setAnimationState(ANIMATION_IDLE);
                return PlayState.STOP;
            }

            return PlayState.CONTINUE;
        }));
    }


    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.REVENANT_AMBIENT.get(), 1.0F, 1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.REVENANT_HURT.get(), 1.0F, 1.0F);
    }

    protected SoundEvent getDeathSound() {
        return TGSounds.REVENANT_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return 1.0F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.REVENANT_STEP.get(), 0.15F, 1.0F);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION, state);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.entityData.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.entityData.set(ATTACK_ANIM_TIMER, time);
    }

    public int getReanimateAnimTimer() {
        return (Integer) this.entityData.get(REANIMATE_ANIM_TIMER);
    }

    public void setReanimateAnimTimer(int time) {
        this.entityData.set(REANIMATE_ANIM_TIMER, time);
    }

    public boolean canReanimate() {
        return this.entityData.get(CAN_REANIMATE);
    }

    public void setCanReanimate(boolean bool) {
        this.entityData.set(CAN_REANIMATE, bool);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("canReanimate", canReanimate());
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setCanReanimate(nbt.getBoolean("canReanimate"));
    }

    static {
        ATTACK_ANIM_TIMER = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);
        REANIMATE_ANIM_TIMER = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);
        ANIMATION = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);
        CAN_REANIMATE = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.BOOLEAN);
        SLOWNESS_EFFECT = new AttributeModifier(SLOWNESS_ID, "Slowness effect", -0.3D, AttributeModifier.Operation.ADDITION);
    }


}
