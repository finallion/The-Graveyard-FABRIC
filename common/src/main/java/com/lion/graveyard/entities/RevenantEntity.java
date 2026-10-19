package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.entities.ai.goals.RevenantMeleeAttackGoal;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.lion.graveyard.entities.animation.AnimationClock;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RevenantEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final Identifier SLOWNESS_ID = Graveyard.id("slowness_id");
    private static final AttributeModifier SLOWNESS_EFFECT;

    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> REANIMATE_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Boolean> CAN_REANIMATE;
    private static final EntityDataAccessor<Boolean> CHASING;
    private static final int CHASE_END_TICKS = 20;

    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("death");
    private final RawAnimation FAKE_DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("fake_death");
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlayAndHold("attack");
    private final RawAnimation RUNNING_ANIMATION = RawAnimation.begin().then("running", LoopType.LOOP);
    private final RawAnimation REANIMATE_ANIMATION = RawAnimation.begin().thenPlayAndHold("reanimate");

    protected static final int ANIMATION_IDLE = 0;
    protected static final int ANIMATION_WALK = 1;
    protected static final int ANIMATION_DEATH = 2;
    protected static final int ANIMATION_RUNNING = 3;
    protected static final int ANIMATION_MELEE = 4;
    protected static final int ANIMATION_REANIMATE = 5;
    // An animation cannot hold its last frame. A fake death state holds it.
    protected static final int ANIMATION_FAKE_DEATH = 6;

    public final int ATTACK_ANIMATION_DURATION = 15;
    public final int ATTACK_ANIM_SYNC_TICKS = ATTACK_ANIMATION_DURATION + AnimationTable.TRANSITION_TICKS;
    private int lastAttackAnimTimer;
    private int ticksWithoutTarget;

    // Reanimation animation 40 ticks, death animation 30 ticks, wait 30 ticks.
    public final int REANIMATE_DURATION = 120;

    public RevenantEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, "revenant");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION, ANIMATION_IDLE);
        builder.define(ATTACK_ANIM_TIMER, 0);
        builder.define(REANIMATE_ANIM_TIMER, 0);
        builder.define(CAN_REANIMATE, true);
        builder.define(CHASING, false);
    }

    @Override
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

    public static AttributeSupplier.Builder createRevenantAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.155D)
                .add(Attributes.FOLLOW_RANGE, 25.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            restartAttackClipOnRearm();
        }
    }

    // The controller does not restart a running clip.
    private void restartAttackClipOnRearm() {
        int timer = this.getAttackAnimTimer();

        if (timer > this.lastAttackAnimTimer + 1) {
            AnimationController<?> controller = this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get("controller");

            if (controller != null) {
                AnimationClock.restart(controller);
            }
        }

        this.lastAttackAnimTimer = timer;
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        trackChase();

        if (this.getAttackAnimTimer() == ATTACK_ANIM_SYNC_TICKS) {
            setAnimationState(ANIMATION_MELEE);
        }

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

        super.customServerAiStep(serverLevel);
    }

    /// The vanilla melee goal stops at each path end.
    private void trackChase() {
        if (ChaseTarget.isLive(this)) {
            this.ticksWithoutTarget = 0;
            this.entityData.set(CHASING, true);
        } else if (this.entityData.get(CHASING) && ++this.ticksWithoutTarget > CHASE_END_TICKS) {
            this.entityData.set(CHASING, false);
        }
    }

    @Override
    public void aiStep() {
        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (getReanimateAnimTimer() > 0) {
            this.getLookControl().setLookAt(this.getX(), this.getY(), this.getZ());

            if (!entityAttributeInstance.hasModifier(SLOWNESS_ID)) {
                entityAttributeInstance.addTransientModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_ID)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT);
            }
        }

        super.aiStep();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == 35 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_RESISTANCE) || source.is(DamageTypeTags.IS_FIRE)) {
            int lyingTicks = getReanimateAnimTimer();
            boolean lyingDead = getAnimationState() == ANIMATION_FAKE_DEATH && lyingTicks > 40;
            setCanReanimate(false);
            setReanimateAnimTimer(0);

            if (lyingDead) {
                boolean hurt = super.hurtServer(serverLevel, source, Float.MAX_VALUE);

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

        if (getReanimateAnimTimer() > 0) {
            return false;
        }

        return super.hurtServer(serverLevel, source, amount);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(AnimationTable.<RevenantEntity>create()
                .when(() -> (isDeadOrDying() || getHealth() < 0.01F) && getAnimationState() == ANIMATION_FAKE_DEATH, FAKE_DEATH_ANIMATION)
                .when(() -> isDeadOrDying() || getHealth() < 0.01F, DEATH_ANIMATION)
                .when(() -> getAnimationState() == ANIMATION_FAKE_DEATH && getReanimateAnimTimer() > 39, FAKE_DEATH_ANIMATION)
                .when(() -> getReanimateAnimTimer() > 0, REANIMATE_ANIMATION)
                .when(() -> getAttackAnimTimer() > 0, ATTACK_ANIMATION)
                .when(this::isRunning, RUNNING_ANIMATION)
                .when(AnimationTable::isMoving, WALK_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .build("controller"));
    }

    private boolean isRunning(AnimationTest<RevenantEntity> state) {
        return AnimationTable.isMoving(state) && this.entityData.get(CHASING) && !isInWaterOrRain();
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
    public void die(DamageSource source) {
        super.die(source);
        this.playSound(TGSounds.REVENANT_DEATH.get(), 1.0F, 1.0F);
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

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("canReanimate", canReanimate());
        output.putInt("ReanimateTimer", getReanimateAnimTimer());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setCanReanimate(input.getBooleanOr("canReanimate", false));

        input.getInt("ReanimateTimer").ifPresent(timer -> {
            setReanimateAnimTimer(timer);

            if (getReanimateAnimTimer() > 40) {
                setAnimationState(ANIMATION_FAKE_DEATH);
            }
        });
    }

    static {
        ATTACK_ANIM_TIMER = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);
        REANIMATE_ANIM_TIMER = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);
        ANIMATION = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.INT);
        CAN_REANIMATE = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.BOOLEAN);
        CHASING = SynchedEntityData.defineId(RevenantEntity.class, EntityDataSerializers.BOOLEAN);
        SLOWNESS_EFFECT = new AttributeModifier(SLOWNESS_ID, -0.3D, AttributeModifier.Operation.ADD_VALUE);
    }

}
