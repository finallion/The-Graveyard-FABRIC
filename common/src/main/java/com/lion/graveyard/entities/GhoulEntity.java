package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.entities.ai.goals.GhoulMeleeAttackGoal;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import com.lion.graveyard.init.TGAdvancements;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import com.mojang.serialization.Codec;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GhoulEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final int EMERGE_WITNESS_INTERVAL = 10;
    private static final double EMERGE_WITNESS_RANGE = 4.0D;
    private static final Identifier SLOWNESS_ID = Graveyard.id("slowness_id");
    private static final AttributeModifier SLOWNESS_EFFECT;

    private static final EntityDataAccessor<Byte> VARIANT;
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> RAGE_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Integer> SPAWN_TIMER;
    private static final EntityDataAccessor<Boolean> IS_RAGING;
    private static final EntityDataAccessor<Boolean> RUN_CRAWLING;
    private static final EntityDataAccessor<Boolean> NAVIGATING;
    private static final EntityDataAccessor<Boolean> RUNNING;

    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("animation.ghoul.death");
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.ghoul.idle");
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("animation.ghoul.walk");
    private final RawAnimation RAGE_ANIMATION = RawAnimation.begin().thenPlayAndHold("animation.ghoul.rage");
    private final RawAnimation RUN_BIPED_ANIMATION = RawAnimation.begin().thenLoop("animation.ghoul.run_biped");
    private final RawAnimation RUN_CRAWL_ANIMATION = RawAnimation.begin().thenLoop("animation.ghoul.run_crawl");
    @Nullable
    private UUID chaseTargetId;
    private int ticksWithoutTarget;
    private int nextChaseSoundAge;
    private int lastAttackAnimTimer;
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlayAndHold("animation.ghoul.attack");
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().thenPlayAndHold("animation.ghoul.spawn");
    protected static final int ANIMATION_IDLE = 0;
    protected static final int ANIMATION_WALK = 1;
    protected static final int ANIMATION_RAGE = 2;
    protected static final int ANIMATION_DEATH = 3;
    protected static final int ANIMATION_RUNNING = 4;
    protected static final int ANIMATION_MELEE = 5;
    protected static final int ANIMATION_SPAWN = 6;

    private static final float RUN_START_LIMB_SWING = 0.55F;
    private static final float RUN_STOP_LIMB_SWING = 0.3F;

    public final int ATTACK_ANIMATION_DURATION = 22;
    public static final int ATTACK_ANIM_SYNC_TICKS = 22 + AnimationTable.TRANSITION_TICKS;
    public final int RAGE_ANIMATION_DURATION = 64 + AnimationTable.TRANSITION_TICKS;
    private static final int SPAWN_ANIMATION_DURATION = 92;
    private static final int CHASE_SOUND_COOLDOWN_TICKS = 200;
    private static final int CHASE_END_TICKS = 20;
    private static final Identifier BIPED_SPEED_ID = Graveyard.id("biped_speed_id");
    private static final AttributeModifier BIPED_SPEED_BOOST = new AttributeModifier(BIPED_SPEED_ID, 0.05D, AttributeModifier.Operation.ADD_VALUE);
    private static final float WALK_STEP_DISTANCE = 1.0F;
    private static final float CHASE_STEP_DISTANCE = 0.55F;
    private static final int DEATH_ANIMATION_DURATION = 44 + AnimationTable.TRANSITION_TICKS;
    public final int RAGE_ANIMATION_COOLDOWN = 300;

    public GhoulEntity(EntityType<? extends GhoulEntity> entityType, Level world) {
        super(entityType, world, "ghoul");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(VARIANT, (byte) 0);
        builder.define(ANIMATION, ANIMATION_IDLE);
        builder.define(ATTACK_ANIM_TIMER, 0);
        builder.define(RAGE_ANIM_TIMER, 0);
        builder.define(SPAWN_TIMER, SPAWN_ANIMATION_DURATION);
        builder.define(IS_RAGING, false);
        builder.define(RUN_CRAWLING, false);
        builder.define(NAVIGATING, false);
        builder.define(RUNNING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GhoulMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, LichEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createGhoulAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.175D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
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

    public int getSpawnTimer() {
        return (Integer) this.entityData.get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int ticks) {
        this.entityData.set(SPAWN_TIMER, ticks);
    }

    public int getRageAnimTimer() {
        return (Integer) this.entityData.get(RAGE_ANIM_TIMER);
    }

    public void setRageAnimTimer(int time) {
        this.entityData.set(RAGE_ANIM_TIMER, time);
    }

    public boolean isRaging() {
        return this.entityData.get(IS_RAGING);
    }

    public void setIsRaging(boolean raging) {
        this.entityData.set(IS_RAGING, raging);
    }

    public byte getVariant() {
        byte tracked = entityData.get(VARIANT);
        return tracked >= 1 && tracked <= 9 ? tracked : (byte) (Math.floorMod(getUUID().hashCode(), 8) + 1);
    }

    public void setVariant(byte variant) {
        entityData.set(VARIANT, variant);
    }

    /// The melee goal restarts at each path end.
    private void trackChase() {
        LivingEntity target = ChaseTarget.isLive(this) ? this.getTarget() : null;

        if (target == null) {
            if (this.chaseTargetId != null && ++this.ticksWithoutTarget > CHASE_END_TICKS) {
                this.chaseTargetId = null;
            }

            return;
        }

        this.ticksWithoutTarget = 0;

        if (target.getUUID().equals(this.chaseTargetId)) {
            return;
        }

        this.chaseTargetId = target.getUUID();
        this.entityData.set(RUN_CRAWLING, this.random.nextBoolean());

        if (this.tickCount >= this.nextChaseSoundAge) {
            this.playSound(TGSounds.GHOUL_AMBIENT.get(), 1.5F, 0.7F);
            this.nextChaseSoundAge = this.tickCount + CHASE_SOUND_COOLDOWN_TICKS;
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        trackChase();
        this.entityData.set(NAVIGATING, !this.getNavigation().isDone());
        this.entityData.set(RUNNING, isRunningAtSpeed(this.entityData.get(RUNNING)));

        if (this.getAttackAnimTimer() == ATTACK_ANIM_SYNC_TICKS) {
            setAnimationState(ANIMATION_MELEE);

            if (getRageAnimTimer() <= 0) {
                setRageAnimTimer(RAGE_ANIMATION_COOLDOWN);
            }
        }

        if (this.getRageAnimTimer() == RAGE_ANIMATION_DURATION) {
            this.playSound(TGSounds.GHOUL_ROAR.get(), 1.0F, 1.0F);
            setIsRaging(true);
            setAnimationState(ANIMATION_RAGE);
            aggroMobs();
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.getRageAnimTimer() > 0) {
            int animTimer = this.getRageAnimTimer() - 1;
            this.setRageAnimTimer(animTimer);
        }

        if (this.getRageAnimTimer() == 0) {
            setIsRaging(false);
        }

        super.customServerAiStep(serverLevel);
    }

    @Override
    public void aiStep() {
        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (isRaging()) {
            if (!entityAttributeInstance.hasModifier(SLOWNESS_ID)) {
                entityAttributeInstance.addTransientModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_ID)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT);
            }
        }

        if (!this.level().isClientSide()) {
            boolean biped = ChaseTarget.isLive(this) && this.chaseTargetId != null && !this.entityData.get(RUN_CRAWLING);

            if (biped != entityAttributeInstance.hasModifier(BIPED_SPEED_ID)) {
                if (biped) {
                    entityAttributeInstance.addTransientModifier(BIPED_SPEED_BOOST);
                } else {
                    entityAttributeInstance.removeModifier(BIPED_SPEED_BOOST);
                }
            }
        }

        super.aiStep();
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && level() != null) {
            RandomSource random = this.getRandom();
            BlockState blockState = this.getBlockStateOn();

            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
                for(int i = 0; i < 10; ++i) {
                    double d = this.getX() + (double) Mth.randomBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)Mth.randomBetween(random, -0.7F, 0.7F);
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        // The AI does not tick while the ghoul is immobile, so the timer cannot run in mobTick.
        if (!this.level().isClientSide() && getSpawnTimer() > 0) {
            if (getSpawnTimer() % EMERGE_WITNESS_INTERVAL == 0 && this.level() instanceof ServerLevel world) {
                TGAdvancements.triggerNearby(world, this.position(), EMERGE_WITNESS_RANGE, TGAdvancements.GHOUL_EMERGES);
            }

            this.setSpawnTimer(getSpawnTimer() - 1);
        }

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
                controller.forceAnimationReset();
            }
        }

        this.lastAttackAnimTimer = timer;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || getSpawnTimer() > 0;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_ANIMATION_DURATION && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putByte("ghoulVariant", getVariant());
        output.putInt("spawnTimer", getSpawnTimer());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        input.read("ghoulVariant", Codec.BYTE).ifPresent(this::setVariant);
        setSpawnTimer(input.getIntOr("spawnTimer", 0));
    }

    private void aggroMobs() {
        if (getTarget() != null) {
            List<HordeGraveyardEntity> monster = level().getEntitiesOfClass(HordeGraveyardEntity.class, new AABB(blockPosition()).inflate(45.0D), this::hasLineOfSight);
            for (HordeGraveyardEntity entity : monster) {
                entity.setTarget(getTarget());
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(AnimationTable.<GhoulEntity>create()
                // A transition into the spawn animation lifts the ghoul above the ground.
                .transitionTicks(() -> getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS)
                .when(this::isDying, DEATH_ANIMATION)
                .when(() -> getSpawnTimer() > 0, SPAWN_ANIMATION)
                .startAt(SPAWN_ANIMATION, ghoul -> ghoul.getSpawnTimer() > 0 && AnimationPreview.forcedAnimation(ghoul) == null ? Math.max(0, SPAWN_ANIMATION_DURATION - ghoul.getSpawnTimer()) : 0)
                .when(this::isRaging, RAGE_ANIMATION)
                .when(() -> getAttackAnimTimer() > 0, ATTACK_ANIMATION)
                .when(state -> isRunning(state) && this.entityData.get(RUN_CRAWLING), RUN_CRAWL_ANIMATION)
                .when(this::isRunning, RUN_BIPED_ANIMATION)
                .when(state -> AnimationTable.isMoving(state) || this.entityData.get(NAVIGATING), WALK_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .build(this, "controller", getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS));
    }

    private boolean isDying() {
        return this.isDeadOrDying() || this.getHealth() < 0.01F;
    }

    private boolean isRunningAtSpeed(boolean wasRunning) {
        float swing = this.walkAnimation.speed();
        return !isInWaterOrRain() && swing > (wasRunning ? RUN_STOP_LIMB_SWING : RUN_START_LIMB_SWING);
    }

    private boolean isRunning(AnimationTest<GhoulEntity> state) {
        return AnimationTable.isMoving(state) && !isInWaterOrRain() && this.entityData.get(RUNNING);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.GHOUL_AMBIENT.get(), 1.0F, -5.0F);
    }

    @Override
    protected float nextStep() {
        return this.moveDist + (this.chaseTargetId != null ? CHASE_STEP_DISTANCE : WALK_STEP_DISTANCE);
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.GHOUL_STEP.get(), 0.5F, -1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.GHOUL_HURT.get(), 1.0F, -5.0F);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.playSound(TGSounds.GHOUL_DEATH.get(), 1.0F, -5.0F);
    }

    static {
        VARIANT = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BYTE);
        ATTACK_ANIM_TIMER = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        RAGE_ANIM_TIMER = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        ANIMATION = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        SPAWN_TIMER = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        IS_RAGING = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BOOLEAN);
        RUN_CRAWLING = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BOOLEAN);
        NAVIGATING = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BOOLEAN);
        RUNNING = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BOOLEAN);
        SLOWNESS_EFFECT = new AttributeModifier(SLOWNESS_ID, -0.3D, AttributeModifier.Operation.ADD_VALUE);
    }

}
