package com.lion.graveyard.entities;

import com.lion.graveyard.entities.animation.AnimationClock;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

/// An entity cannot emit block light. Thus the mob carries a light block with it.
public class TormentedVillagerEntity extends PathAwareEntity implements GeoEntity {

    private static final TrackedData<Integer> MOOD = DataTracker.registerData(TormentedVillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SPAWN_TICKS_LEFT = DataTracker.registerData(TormentedVillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int BELL_RANGE = 48;
    private static final int REST_CHANCE = 350;
    private static final float STEP_DISTANCE = 0.6F;
    private static final float STEP_VOLUME = 0.35F;
    private static final int AMBIENT_SOUND_DELAY = 160;
    private static final int RAGE_SOUND_TICKS = 60;
    private static final int MIN_AGE_TO_FADE = 100;
    private static final double REDEMPTION_WITNESS_RANGE = 16.0D;
    private static final double DAWN_WITNESS_RANGE = 12.0D;
    private static final int LIGHT_LEVEL = 5;
    private static final int LIGHT_INTERVAL = 4;
    private static final double LIGHT_REACH = 4.0D;
    private static final int REDEMPTION_EXPERIENCE = 5;
    private static final String LIGHT_KEY = "LightPos";
    private static final int MOOD_TRANSITION_TICKS = 6;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int moodTicks;
    private BlockPos lightPos;

    public TormentedVillagerEntity(EntityType<? extends TormentedVillagerEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 0;
    }

    public static DefaultAttributeContainer.Builder createTormentedVillagerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    public static boolean canSpawn(EntityType<TormentedVillagerEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return world.toServerWorld().isNight() && MobEntity.canMobSpawn(type, world, reason, pos, random);
    }

    public static void onBellRung(ServerWorld world, BlockPos bell) {
        for (TormentedVillagerEntity villager : world.getEntitiesByClass(TormentedVillagerEntity.class, new Box(bell).expand(BELL_RANGE), entity -> true)) {
            villager.redeem();
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MOOD, Mood.WANDERING.ordinal());
        this.dataTracker.startTracking(SPAWN_TICKS_LEFT, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.6D) {
            @Override
            public boolean canStart() {
                return TormentedVillagerEntity.this.getMood() == Mood.WANDERING && super.canStart();
            }
        });
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setMood(Mood.SPAWNING, Mood.SPAWNING.ticks);
        this.dataTracker.set(SPAWN_TICKS_LEFT, Mood.SPAWNING.ticks);
        this.playSound(TGSounds.TORMENTED_VILLAGER_SPAWN.get(), 1.0F, 1.0F);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public Mood getMood() {
        return Mood.values()[this.dataTracker.get(MOOD)];
    }

    private void setMood(Mood mood, int ticks) {
        this.dataTracker.set(MOOD, mood.ordinal());
        this.moodTicks = ticks;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        int spawnTicksLeft = this.dataTracker.get(SPAWN_TICKS_LEFT);
        if (spawnTicksLeft > 0) {
            this.dataTracker.set(SPAWN_TICKS_LEFT, spawnTicksLeft - 1);
        }
        if (this.age % LIGHT_INTERVAL == 0 && Graveyard.getConfig().flag("tormentedVillagerLight", true)) {
            this.carryLight();
        }

        Mood mood = this.getMood();
        if (mood != Mood.REDEEMING && mood != Mood.SPAWNING && this.age > MIN_AGE_TO_FADE && this.getWorld().isDay()) {
            this.vanish(false);
            return;
        }
        if (mood == Mood.WANDERING) {
            if (this.random.nextInt(REST_CHANCE) == 0) {
                this.startRandomMood();
            }
            return;
        }

        this.getNavigation().stop();
        if (--this.moodTicks > 0) {
            boolean cryLoops = mood == Mood.CRYING && this.moodTicks % Mood.CRYING.ticks == 0;
            boolean rageSoundEnds = mood == Mood.RAGING && this.moodTicks == Mood.RAGING.ticks - RAGE_SOUND_TICKS;
            if (cryLoops || rageSoundEnds) {
                this.playSound(TGSounds.TORMENTED_VILLAGER_CRYING.get(), 1.0F, 1.0F);
            }
            return;
        }
        if (mood == Mood.REDEEMING) {
            this.vanish(true);
        } else {
            this.setMood(Mood.WANDERING, 0);
        }
    }

    private void startRandomMood() {
        int roll = this.random.nextInt(10);
        if (roll < 6) {
            this.setMood(Mood.CRYING, Mood.CRYING.ticks * (1 + this.random.nextInt(3)));
            this.playSound(TGSounds.TORMENTED_VILLAGER_CRYING.get(), 1.0F, 1.0F);
        } else if (roll < 8) {
            this.setMood(Mood.GLITCHING, Mood.GLITCHING.ticks);
            this.playSound(TGSounds.TORMENTED_VILLAGER_GLITCH.get(), 0.2F, 1.0F);
        } else if (roll < 9) {
            this.setMood(Mood.RAGING, Mood.RAGING.ticks);
            this.playSound(TGSounds.TORMENTED_VILLAGER_RAGE.get(), 1.0F, 1.0F);
        } else {
            this.setMood(Mood.BREAKING_DOWN, Mood.BREAKING_DOWN.ticks);
            this.playSound(TGSounds.TORMENTED_VILLAGER_BREAKDOWN.get(), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && super.damage(source, amount);
    }

    public void redeem() {
        if (this.getMood() != Mood.REDEEMING) {
            this.setMood(Mood.REDEEMING, Mood.REDEEMING.ticks);
            this.playSound(TGSounds.TORMENTED_VILLAGER_REDEMPTION.get(), 1.0F, 1.0F);
        }
    }

    private void vanish(boolean redeemed) {
        if (this.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(ParticleTypes.SOUL, this.getX(), this.getBodyY(0.5D), this.getZ(), 20, 0.3D, 0.6D, 0.3D, 0.02D);
            if (redeemed) {
                ExperienceOrbEntity.spawn(world, this.getPos(), REDEMPTION_EXPERIENCE);
                this.rewardRedemption(world);
            } else {
                TGAdvancements.triggerNearby(world, this.getPos(), DAWN_WITNESS_RANGE, TGAdvancements.TORMENTED_VILLAGER_DAWN);
            }
        }
        this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 1.0F, 1.0F);
        this.discard();
    }

    private void rewardRedemption(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers(candidate -> !candidate.isSpectator() && candidate.squaredDistanceTo(this) <= REDEMPTION_WITNESS_RANGE * REDEMPTION_WITNESS_RANGE)) {
            TGAdvancements.REDEEM_TORMENTED_VILLAGER.trigger(player);
            TGAdvancements.grantNextStep(player, TGAdvancements.REDEEM_TEN_ADVANCEMENT, TGAdvancements.REDEEM_TEN_STEPS);
        }
    }

    private void carryLight() {
        BlockPos target = this.getBlockPos().up();
        if (target.equals(this.lightPos)) {
            return;
        }
        this.clearLight();
        if (this.getWorld().getBlockState(target).isAir()) {
            this.getWorld().setBlockState(target, Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, LIGHT_LEVEL), Block.NOTIFY_LISTENERS);
            this.lightPos = target;
        }
    }

    private void clearLight() {
        // A saved position far from the mob is stale. A read there can load a chunk.
        if (this.lightPos != null && this.lightPos.isWithinDistance(this.getBlockPos(), LIGHT_REACH)) {
            BlockState state = this.getWorld().getBlockState(this.lightPos);
            if (state.isOf(Blocks.LIGHT) && state.get(LightBlock.LEVEL_15) == LIGHT_LEVEL) {
                this.getWorld().setBlockState(this.lightPos, state.getFluidState().getBlockState(), Block.NOTIFY_LISTENERS);
            }
        }
        this.lightPos = null;
    }

    /// An unload keeps the light block. The saved position lets the mob remove it after the next load.
    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient && reason.shouldDestroy()) {
            this.clearLight();
        }
        super.remove(reason);
    }

    @Override
    public void copyFrom(Entity original) {
        if (original instanceof TormentedVillagerEntity villager && !villager.getWorld().isClient) {
            villager.clearLight();
        }
        super.copyFrom(original);
        this.lightPos = null;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.lightPos != null) {
            nbt.putLong(LIGHT_KEY, this.lightPos.asLong());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(LIGHT_KEY)) {
            this.lightPos = BlockPos.fromLong(nbt.getLong(LIGHT_KEY));
        }
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getMood() != Mood.WANDERING) {
            return null;
        }
        return this.getNavigation().isIdle() ? TGSounds.TORMENTED_VILLAGER_IDLE.get() : TGSounds.TORMENTED_VILLAGER_LOST.get();
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return AMBIENT_SOUND_DELAY;
    }

    private boolean isWalking() {
        double dx = this.getX() - this.prevX;
        double dz = this.getZ() - this.prevZ;
        return dx * dx + dz * dz > 1.0E-5D;
    }

    @Override
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + STEP_DISTANCE;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        BlockSoundGroup group = state.getSoundGroup();
        this.playSound(group.getStepSound(), group.getVolume() * STEP_VOLUME, group.getPitch());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationClock.controller(this, "controller", this.getMood() == Mood.SPAWNING ? 0 : MOOD_TRANSITION_TICKS, state -> {
            RawAnimation preview = AnimationPreview.frame(state, Mood::animationNames);
            if (preview != null) {
                return state.setAndContinue(preview);
            }
            Mood mood = this.getMood();
            // A blend from the bind pose shows a pose the mob never has.
            boolean cut = mood == Mood.SPAWNING || state.getController().getCurrentAnimation() == null;
            state.getController().transitionLength(cut ? 0 : MOOD_TRANSITION_TICKS);
            if (mood == Mood.WANDERING) {
                return state.setAndContinue(this.isWalking() ? Mood.WALK : mood.animation);
            }
            return state.setAndContinue(mood.animation);
        }, Mood.SPAWNING.animation.getAnimationStages().get(0).animationName(), villager -> villager.spawnClipOffset()));
    }

    private double spawnClipOffset() {
        int left = this.dataTracker.get(SPAWN_TICKS_LEFT);
        return left > 0 && AnimationPreview.forcedAnimation(this) == null ? Math.max(0, Mood.SPAWNING.ticks - left) : 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public enum Mood {
        WANDERING("idle", Animation.LoopType.LOOP, 0),
        SPAWNING("spawn", Animation.LoopType.HOLD_ON_LAST_FRAME, 160),
        CRYING("crying", Animation.LoopType.LOOP, 80),
        GLITCHING("glitch", Animation.LoopType.HOLD_ON_LAST_FRAME, 48 + MOOD_TRANSITION_TICKS),
        RAGING("rage", Animation.LoopType.HOLD_ON_LAST_FRAME, 104 + MOOD_TRANSITION_TICKS),
        BREAKING_DOWN("breakdown", Animation.LoopType.HOLD_ON_LAST_FRAME, 160 + MOOD_TRANSITION_TICKS),
        REDEEMING("redemption", Animation.LoopType.HOLD_ON_LAST_FRAME, 150 + MOOD_TRANSITION_TICKS);

        private static final RawAnimation WALK = RawAnimation.begin().then("animation.tormented_villager.lost", Animation.LoopType.LOOP);

        private final RawAnimation animation;
        private final int ticks;

        private static List<String> animationNames() {
            List<String> names = new ArrayList<>();
            for (Mood mood : values()) {
                names.add(mood.animation.getAnimationStages().get(0).animationName());
            }
            return names;
        }

        Mood(String name, Animation.LoopType loop, int ticks) {
            this.animation = RawAnimation.begin().then("animation.tormented_villager." + name, loop);
            this.ticks = ticks;
        }
    }
}
