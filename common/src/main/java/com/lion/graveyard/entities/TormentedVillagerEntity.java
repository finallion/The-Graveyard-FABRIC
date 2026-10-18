package com.lion.graveyard.entities;

import com.lion.graveyard.entities.animation.AnimationClock;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TormentedVillagerEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Integer> MOOD = SynchedEntityData.defineId(TormentedVillagerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SPAWN_TICKS_LEFT = SynchedEntityData.defineId(TormentedVillagerEntity.class, EntityDataSerializers.INT);

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
    private static final int REDEMPTION_EXPERIENCE = 5;
    private static final String LIGHT_KEY = "LightPos";
    private static final int MOOD_TRANSITION_TICKS = 6;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int moodTicks;
    private BlockPos legacyLightPos;

    public TormentedVillagerEntity(EntityType<? extends TormentedVillagerEntity> type, Level world) {
        super(type, world);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createTormentedVillagerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    public static boolean canSpawn(EntityType<TormentedVillagerEntity> type, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return world.getLevel().isNight() && Mob.checkMobSpawnRules(type, world, reason, pos, random);
    }

    public static void onBellRung(ServerLevel world, BlockPos bell) {
        for (TormentedVillagerEntity villager : world.getEntitiesOfClass(TormentedVillagerEntity.class, new AABB(bell).inflate(BELL_RANGE), entity -> true)) {
            villager.redeem();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOOD, Mood.WANDERING.ordinal());
        builder.define(SPAWN_TICKS_LEFT, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.6D) {
            @Override
            public boolean canUse() {
                return TormentedVillagerEntity.this.getMood() == Mood.WANDERING && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        this.setMood(Mood.SPAWNING, Mood.SPAWNING.ticks);
        this.entityData.set(SPAWN_TICKS_LEFT, Mood.SPAWNING.ticks);
        this.playSound(TGSounds.TORMENTED_VILLAGER_SPAWN.get(), 1.0F, 1.0F);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    public Mood getMood() {
        return Mood.values()[this.entityData.get(MOOD)];
    }

    private void setMood(Mood mood, int ticks) {
        this.entityData.set(MOOD, mood.ordinal());
        this.moodTicks = ticks;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        int spawnTicksLeft = this.entityData.get(SPAWN_TICKS_LEFT);

        if (spawnTicksLeft > 0) {
            this.entityData.set(SPAWN_TICKS_LEFT, spawnTicksLeft - 1);
        }

        if (this.legacyLightPos != null && this.tickCount % 20 == 0) {
            this.releaseLegacyLight();
        }

        Mood mood = this.getMood();

        if (mood != Mood.REDEEMING && mood != Mood.SPAWNING && this.tickCount > MIN_AGE_TO_FADE && this.level().isDay()) {
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
    public boolean hurt(DamageSource source, float amount) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && super.hurt(source, amount);
    }

    public void redeem() {
        if (this.getMood() != Mood.REDEEMING) {
            this.setMood(Mood.REDEEMING, Mood.REDEEMING.ticks);
            this.playSound(TGSounds.TORMENTED_VILLAGER_REDEMPTION.get(), 1.0F, 1.0F);
        }
    }

    private void vanish(boolean redeemed) {
        if (this.level() instanceof ServerLevel world) {
            world.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY(0.5D), this.getZ(), 20, 0.3D, 0.6D, 0.3D, 0.02D);

            if (redeemed) {
                ExperienceOrb.award(world, this.position(), REDEMPTION_EXPERIENCE);
                this.rewardRedemption(world);
            } else {
                TGAdvancements.triggerNearby(world, this.position(), DAWN_WITNESS_RANGE, TGAdvancements.TORMENTED_VILLAGER_DAWN);
            }
        }

        this.playSound(SoundEvents.SOUL_ESCAPE.value(), 1.0F, 1.0F);
        this.discard();
    }

    private void rewardRedemption(ServerLevel world) {
        for (ServerPlayer player : world.getPlayers(candidate -> !candidate.isSpectator() && candidate.distanceToSqr(this) <= REDEMPTION_WITNESS_RANGE * REDEMPTION_WITNESS_RANGE)) {
            TGAdvancements.REDEEM_TORMENTED_VILLAGER.trigger(player);
            TGAdvancements.grantNextStep(player, TGAdvancements.REDEEM_TEN_ADVANCEMENT, TGAdvancements.REDEEM_TEN_STEPS);
        }
    }

    private void releaseLegacyLight() {
        if (this.legacyLightPos == null || this.level().isClientSide) {
            return;
        }

        if (!this.level().isLoaded(this.legacyLightPos)) {
            return;
        }

        BlockState state = this.level().getBlockState(this.legacyLightPos);

        if (state.is(Blocks.LIGHT) && state.getValue(LightBlock.LEVEL) == LIGHT_LEVEL) {
            this.level().setBlock(this.legacyLightPos, state.getFluidState().createLegacyBlock(), Block.UPDATE_CLIENTS);
        }

        this.legacyLightPos = null;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && reason.shouldDestroy()) {
            this.releaseLegacyLight();
        }

        super.remove(reason);
    }

    @Override
    public void restoreFrom(Entity original) {
        if (original instanceof TormentedVillagerEntity villager && !villager.level().isClientSide) {
            villager.releaseLegacyLight();
        }

        super.restoreFrom(original);
        this.legacyLightPos = null;
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        if (this.legacyLightPos != null) {
            nbt.putLong(LIGHT_KEY, this.legacyLightPos.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains(LIGHT_KEY)) {
            this.legacyLightPos = BlockPos.of(nbt.getLong(LIGHT_KEY));
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getMood() != Mood.WANDERING) {
            return null;
        }

        return this.getNavigation().isDone() ? TGSounds.TORMENTED_VILLAGER_IDLE.get() : TGSounds.TORMENTED_VILLAGER_LOST.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return AMBIENT_SOUND_DELAY;
    }

    private boolean isWalking() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 1.0E-5D;
    }

    @Override
    protected float nextStep() {
        return this.moveDist + STEP_DISTANCE;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundType group = state.getSoundType();
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
        int left = this.entityData.get(SPAWN_TICKS_LEFT);
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
