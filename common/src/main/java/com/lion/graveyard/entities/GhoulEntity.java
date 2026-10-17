package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.entities.ai.goals.GhoulMeleeAttackGoal;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.block.BlockRenderType;
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
import com.lion.graveyard.init.TGAdvancements;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GhoulEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final int EMERGE_WITNESS_INTERVAL = 10;
    private static final double EMERGE_WITNESS_RANGE = 4.0D;
    private static final UUID SLOWNESS_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A1");
    private static final EntityAttributeModifier SLOWNESS_EFFECT;

    private static final TrackedData<Byte> VARIANT;
    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> RAGE_ANIM_TIMER;
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Integer> SPAWN_TIMER;
    private static final TrackedData<Boolean> IS_RAGING;
    private static final TrackedData<Boolean> RUN_CRAWLING;
    private static final TrackedData<Boolean> NAVIGATING;
    private static final TrackedData<Boolean> RUNNING;

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
    private static final UUID BIPED_SPEED_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A2");
    private static final EntityAttributeModifier BIPED_SPEED_BOOST = new EntityAttributeModifier(BIPED_SPEED_ID, "Biped run boost", 0.05D, EntityAttributeModifier.Operation.ADDITION);
    private static final float WALK_STEP_DISTANCE = 1.0F;
    private static final float CHASE_STEP_DISTANCE = 0.55F;
    private static final int DEATH_ANIMATION_DURATION = 44 + AnimationTable.TRANSITION_TICKS;
    public final int RAGE_ANIMATION_COOLDOWN = 300;


    public GhoulEntity(EntityType<? extends GhoulEntity> entityType, World world) {
        super(entityType, world, "ghoul");
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(VARIANT, (byte) 0);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(RAGE_ANIM_TIMER, 0);
        this.dataTracker.startTracking(SPAWN_TIMER, SPAWN_ANIMATION_DURATION);
        this.dataTracker.startTracking(IS_RAGING, false);
        this.dataTracker.startTracking(RUN_CRAWLING, false);
        this.dataTracker.startTracking(NAVIGATING, false);
        this.dataTracker.startTracking(RUNNING, false);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new GhoulMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, LichEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }


    public static DefaultAttributeContainer.Builder createGhoulAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
    }


    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
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

    public int getSpawnTimer() {
        return (Integer) this.dataTracker.get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int ticks) {
        this.dataTracker.set(SPAWN_TIMER, ticks);
    }

    public int getRageAnimTimer() {
        return (Integer) this.dataTracker.get(RAGE_ANIM_TIMER);
    }

    public void setRageAnimTimer(int time) {
        this.dataTracker.set(RAGE_ANIM_TIMER, time);
    }

    public boolean isRaging() {
        return this.dataTracker.get(IS_RAGING);
    }

    public void setIsRaging(boolean raging) {
        this.dataTracker.set(IS_RAGING, raging);
    }

    public byte getVariant() {
        byte tracked = dataTracker.get(VARIANT);
        return tracked >= 1 && tracked <= 9 ? tracked : (byte) (Math.floorMod(getUuid().hashCode(), 8) + 1);
    }

    public void setVariant(byte variant) {
        dataTracker.set(VARIANT, variant);
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

        if (target.getUuid().equals(this.chaseTargetId)) {
            return;
        }

        this.chaseTargetId = target.getUuid();
        this.dataTracker.set(RUN_CRAWLING, this.random.nextBoolean());

        if (this.age >= this.nextChaseSoundAge) {
            this.playSound(TGSounds.GHOUL_AMBIENT.get(), 1.5F, 0.7F);
            this.nextChaseSoundAge = this.age + CHASE_SOUND_COOLDOWN_TICKS;
        }
    }

    protected void mobTick() {
        trackChase();
        this.dataTracker.set(NAVIGATING, !this.getNavigation().isIdle());
        this.dataTracker.set(RUNNING, isRunningAtSpeed(this.dataTracker.get(RUNNING)));

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

        super.mobTick();
    }

    @Override
    public void tickMovement() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (isRaging()) {
            if (!entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.addTemporaryModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT);
            }
        }

        if (!this.getEntityWorld().isClient()) {
            boolean biped = ChaseTarget.isLive(this) && this.chaseTargetId != null && !this.dataTracker.get(RUN_CRAWLING);

            if (biped != entityAttributeInstance.hasModifier(BIPED_SPEED_BOOST)) {
                if (biped) {
                    entityAttributeInstance.addTemporaryModifier(BIPED_SPEED_BOOST);
                } else {
                    entityAttributeInstance.removeModifier(BIPED_SPEED_BOOST);
                }
            }
        }

        super.tickMovement();
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && getEntityWorld() != null) {
            Random random = this.getRandom();
            BlockState blockState = this.getSteppingBlockState();

            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for(int i = 0; i < 10; ++i) {
                    double d = this.getX() + (double) MathHelper.nextBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
                    this.getEntityWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        // The AI does not tick while the ghoul is immobile, so the timer cannot run in mobTick.
        if (!this.getEntityWorld().isClient() && getSpawnTimer() > 0) {
            if (getSpawnTimer() % EMERGE_WITNESS_INTERVAL == 0 && this.getEntityWorld() instanceof ServerWorld world) {
                TGAdvancements.triggerNearby(world, this.getPos(), EMERGE_WITNESS_RANGE, TGAdvancements.GHOUL_EMERGES);
            }

            this.setSpawnTimer(getSpawnTimer() - 1);
        }

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

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || getSpawnTimer() > 0;
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_ANIMATION_DURATION && !this.getEntityWorld().isClient()) {
            this.getEntityWorld().sendEntityStatus(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("ghoulVariant", getVariant());
        nbt.putInt("spawnTimer", getSpawnTimer());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("ghoulVariant")) {
            setVariant(nbt.getByte("ghoulVariant"));
        }

        setSpawnTimer(nbt.getInt("spawnTimer"));
    }

    private void aggroMobs() {
        if (getTarget() != null) {
            List<HordeGraveyardEntity> monster = getEntityWorld().getEntitiesByClass(HordeGraveyardEntity.class, new Box(getBlockPos()).expand(45.0D), this::canSee);
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
                .when(state -> isRunning(state) && this.dataTracker.get(RUN_CRAWLING), RUN_CRAWL_ANIMATION)
                .when(this::isRunning, RUN_BIPED_ANIMATION)
                .when(state -> AnimationTable.isMoving(state) || this.dataTracker.get(NAVIGATING), WALK_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .build(this, "controller", getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS));
    }

    private boolean isDying() {
        return this.isDead() || this.getHealth() < 0.01F;
    }

    private boolean isRunningAtSpeed(boolean wasRunning) {
        float swing = this.limbAnimator.getSpeed();
        return !isWet() && swing > (wasRunning ? RUN_STOP_LIMB_SWING : RUN_START_LIMB_SWING);
    }

    private boolean isRunning(AnimationState<GhoulEntity> state) {
        return AnimationTable.isMoving(state) && !isWet() && this.dataTracker.get(RUNNING);
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
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + (this.chaseTargetId != null ? CHASE_STEP_DISTANCE : WALK_STEP_DISTANCE);
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
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(TGSounds.GHOUL_DEATH.get(), 1.0F, -5.0F);
    }

    static {
        VARIANT = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
        ATTACK_ANIM_TIMER = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        RAGE_ANIM_TIMER = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SPAWN_TIMER = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        IS_RAGING = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        RUN_CRAWLING = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        NAVIGATING = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        RUNNING = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SLOWNESS_EFFECT = new EntityAttributeModifier(SLOWNESS_ID, "Slowness effect", -0.3D, EntityAttributeModifier.Operation.ADDITION);
    }

}
