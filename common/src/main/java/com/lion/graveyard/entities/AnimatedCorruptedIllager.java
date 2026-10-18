package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.entities.animation.AnimationClock;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import com.lion.graveyard.init.TGSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/// Animation predicates read these timers on the render thread.
public abstract class AnimatedCorruptedIllager extends CorruptedIllager implements GeoEntity {
    private static final String ANIMATION_PREFIX = "animation.corrupted_illager.";
    private static final int SPAWN_TICKS = 92;
    private static final int DEATH_TICKS = 49;
    private static final int GLITCH_TICKS = 49;
    private static final int STRIKE_TICKS = 27;
    private static final int BITE_TICKS = 21;
    private static final int STRIKE_HIT_TICKS = 16;
    private static final int BITE_HIT_TICKS = 13;
    private static final int GLITCH_CHANCE = 2400;
    private static final int GLITCH_COOLDOWN_TICKS = 600;
    private static final int SOUND_RESTART_SLACK = 2;
    private static final byte ATTACK_STRIKE = 0;
    private static final byte ATTACK_BITE = 1;
    private static final int DECAY_LEVELS = 2;
    private static final int LEFT_CLAW = 1;
    private static final int RIGHT_CLAW = 2;
    private static final double WANDER_SPEED = 0.27D;
    private static final float MOVING_LIMB_SPEED = 0.05F;
    private static final int WANDER_STUCK_TICKS = 20;

    private static final EntityDataAccessor<Byte> DECAY = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> ATTACK_KIND = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> GLITCH_TIMER = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> CLAWS = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> WANDERING = SynchedEntityData.defineId(AnimatedCorruptedIllager.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation SPAWN = RawAnimation.begin().thenPlayAndHold(ANIMATION_PREFIX + "spawn");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold(ANIMATION_PREFIX + "blow_apart");
    private static final RawAnimation STRIKE = RawAnimation.begin().thenPlayAndHold(ANIMATION_PREFIX + "strike");
    private static final RawAnimation BITE = RawAnimation.begin().thenPlayAndHold(ANIMATION_PREFIX + "bite");
    private static final RawAnimation GLITCH = RawAnimation.begin().thenPlayAndHold(ANIMATION_PREFIX + "glitch");
    private static final RawAnimation ATTACK_RUN = RawAnimation.begin().thenLoop(ANIMATION_PREFIX + "attack_run");
    private static final RawAnimation WANDER = RawAnimation.begin().thenLoop(ANIMATION_PREFIX + "wander");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop(ANIMATION_PREFIX + "idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private LivingEntity pendingTarget;
    private int glitchCooldown;
    private int stuckTicks;
    private AnimationSound currentSound = AnimationSound.SILENT;
    private int soundCountdown;
    private AnimationSound playingSound = AnimationSound.SILENT;
    private int soundStartAge;

    protected AnimatedCorruptedIllager(EntityType<? extends CorruptedIllager> entityType, Level world, String name) {
        super(entityType, world, name);
    }

    protected boolean hasSpawnAnimation() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DECAY, (byte) 0);
        builder.define(SPAWN_TIMER, 0);
        builder.define(ATTACK_TIMER, 0);
        builder.define(ATTACK_KIND, ATTACK_BITE);
        builder.define(GLITCH_TIMER, 0);
        builder.define(CLAWS, (byte) -1);
        builder.define(WANDERING, false);
    }

    public int getDecay() {
        byte decay = this.entityData.get(DECAY);

        if (decay >= 1 && decay <= DECAY_LEVELS) {
            return decay;
        }

        return Math.floorMod(this.getUUID().hashCode(), DECAY_LEVELS) + 1;
    }

    @Override
    protected double getWanderSpeed() {
        return WANDER_SPEED;
    }

    public boolean hasLeftClaw() {
        return (this.getClaws() & LEFT_CLAW) != 0;
    }

    public boolean hasRightClaw() {
        return (this.getClaws() & RIGHT_CLAW) != 0;
    }

    private int getClaws() {
        byte claws = this.entityData.get(CLAWS);

        if (claws >= 0 && claws <= (LEFT_CLAW | RIGHT_CLAW)) {
            return claws;
        }

        return (int) (this.getUUID().getMostSignificantBits() >>> 62);
    }

    public int getSpawnTimer() {
        return this.entityData.get(SPAWN_TIMER);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            MobSpawnType spawnReason,
            @Nullable SpawnGroupData entityData
    ) {
        SpawnGroupData result = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        this.entityData.set(DECAY, (byte) (this.random.nextInt(DECAY_LEVELS) + 1));
        this.entityData.set(CLAWS, (byte) this.random.nextInt((LEFT_CLAW | RIGHT_CLAW) + 1));
        boolean unseenSpawn = spawnReason == MobSpawnType.CHUNK_GENERATION || spawnReason == MobSpawnType.STRUCTURE;

        if (this.hasSpawnAnimation() && !unseenSpawn) {
            this.entityData.set(SPAWN_TIMER, SPAWN_TICKS);
        }

        return result;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("Decay", (byte) this.getDecay());
        nbt.putByte("Claws", (byte) this.getClaws());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("Decay", Tag.TAG_ANY_NUMERIC)) {
            byte decay = nbt.getByte("Decay");

            if (decay >= 1 && decay <= DECAY_LEVELS) {
                this.entityData.set(DECAY, decay);
            }
        }

        if (nbt.contains("Claws", Tag.TAG_ANY_NUMERIC)) {
            this.entityData.set(CLAWS, nbt.getByte("Claws"));
        }
    }

    @Override
    public void tick() {
        // The AI does not tick while immobile. The timers run here.
        if (!this.level().isClientSide()) {
            tickDown(SPAWN_TIMER);
            tickDown(ATTACK_TIMER);
            tickDown(GLITCH_TIMER);
            resolvePendingHit();
        }

        super.tick();

        if (!this.level().isClientSide() && this.isNoAi()) {
            setChasing(false);
            this.entityData.set(WANDERING, false);
        }

        if (this.level().isClientSide() && AnimationPreview.forcedAnimation(this) == null) {
            tickAnimationSound();
        }
    }

    private AnimationSound currentAnimationSound() {
        boolean moving = this.walkAnimation.speed() > MOVING_LIMB_SPEED;

        if (this.isDeadOrDying() || this.getHealth() < 0.01F) {
            return AnimationSound.BLOW_APART;
        }

        if (this.getSpawnTimer() > 0) {
            return AnimationSound.SPAWN;
        }

        if (this.entityData.get(ATTACK_TIMER) > 0) {
            return this.entityData.get(ATTACK_KIND) == ATTACK_STRIKE ? AnimationSound.STRIKE : AnimationSound.BITE;
        }

        if (this.entityData.get(GLITCH_TIMER) > 0) {
            return AnimationSound.SILENT;
        }

        if (this.isSwimmingInWater()) {
            return AnimationSound.IDLE;
        }

        if (this.isChasing() && moving) {
            return AnimationSound.ATTACK_RUN;
        }

        return moving || this.entityData.get(WANDERING) ? AnimationSound.WANDER : AnimationSound.IDLE;
    }

    private void tickAnimationSound() {
        boolean rendered = AnimationClock.isRendered(this);
        AnimationSound sound = currentAnimationSound();

        if (!rendered && sound != this.currentSound) {
            this.currentSound = sound;
            // GeckoLib starts the keyframes after the blend.
            this.soundCountdown = (sound == AnimationSound.SPAWN ? 0 : AnimationTable.TRANSITION_TICKS) + 1;
        }

        if (this.soundCountdown > 0 && --this.soundCountdown == 0) {
            if (!rendered) {
                startSound(this.currentSound);
            }

            this.soundCountdown = this.currentSound.repeatTicks;
        }
    }

    public void onAnimationKeyframeZero(String animation, double startOffsetTicks) {
        if (AnimationPreview.forcedAnimation(this) != null) {
            return;
        }

        AnimationSound sound = AnimationSound.of(animation);

        if (sound == AnimationSound.SPAWN && startOffsetTicks > 0) {
            return;
        }

        // One sound can span several loops of its animation.
        if (sound == this.playingSound && this.tickCount - this.soundStartAge < sound.lengthTicks - SOUND_RESTART_SLACK) {
            return;
        }

        this.currentSound = sound;
        this.soundCountdown = sound.repeatTicks;
        startSound(sound);
    }

    private void startSound(AnimationSound sound) {
        this.playingSound = sound;
        this.soundStartAge = this.tickCount;
        SoundEvent event = sound.event == null ? null : sound.event.get();

        if (sound == AnimationSound.BLOW_APART) {
            AnimationClock.playTracked(this, null);
            AnimationClock.playDetached(this, event);
        } else {
            AnimationClock.playTracked(this, event);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    private void resolvePendingHit() {
        if (this.pendingTarget == null) {
            return;
        }

        if (!this.isAlive()) {
            this.pendingTarget = null;
            return;
        }

        boolean strike = this.entityData.get(ATTACK_KIND) == ATTACK_STRIKE;
        int hitTimer = strike ? STRIKE_TICKS - STRIKE_HIT_TICKS : BITE_TICKS - BITE_HIT_TICKS;

        if (this.entityData.get(ATTACK_TIMER) != hitTimer) {
            return;
        }

        LivingEntity target = this.pendingTarget;
        this.pendingTarget = null;
        float reach = this.getBbWidth() * 2.0F;

        if (target.isAlive() && this.distanceToSqr(target) <= reach * reach + target.getBbWidth() + 1.0D) {
            super.doHurtTarget(target);
        }
    }

    private boolean isSwimmingInWater() {
        return this.isInWater() && !this.onGround();
    }

    private void tickDown(EntityDataAccessor<Integer> timer) {
        int ticks = this.entityData.get(timer);

        if (ticks > 0) {
            this.entityData.set(timer, ticks - 1);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        boolean navigating = !ChaseTarget.isLive(this) && !this.getNavigation().isDone();
        this.stuckTicks = navigating && this.walkAnimation.speed() <= MOVING_LIMB_SPEED ? this.stuckTicks + 1 : 0;
        this.entityData.set(WANDERING, navigating && this.stuckTicks < WANDER_STUCK_TICKS);
        boolean resting = !ChaseTarget.isLive(this) && this.getNavigation().isDone() && this.getSpawnTimer() == 0;
        int glitch = this.entityData.get(GLITCH_TIMER);

        if (glitch > 0 && !resting) {
            this.entityData.set(GLITCH_TIMER, 0);
        } else if (glitch == 0 && resting && this.glitchCooldown == 0 && this.random.nextInt(GLITCH_CHANCE) == 0) {
            this.entityData.set(GLITCH_TIMER, GLITCH_TICKS);
            this.glitchCooldown = GLITCH_COOLDOWN_TICKS;
        }

        if (this.glitchCooldown > 0) {
            this.glitchCooldown--;
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide()) {
            byte next = this.entityData.get(ATTACK_KIND) == ATTACK_STRIKE ? ATTACK_BITE : ATTACK_STRIKE;
            this.entityData.set(ATTACK_KIND, next);
            this.entityData.set(ATTACK_TIMER, next == ATTACK_STRIKE ? STRIKE_TICKS : BITE_TICKS);
        }

        this.pendingTarget = target instanceof LivingEntity living ? living : null;
        return true;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getSpawnTimer() > 0;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_TICKS && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationTable.<AnimatedCorruptedIllager>create()
                .transitionTicks(() -> this.getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS)
                .when(() -> this.isDeadOrDying() || this.getHealth() < 0.01F, DEATH)
                .when(() -> this.getSpawnTimer() > 0, SPAWN)
                .when(() -> this.entityData.get(ATTACK_TIMER) > 0 && this.entityData.get(ATTACK_KIND) == ATTACK_STRIKE, STRIKE)
                .when(() -> this.entityData.get(ATTACK_TIMER) > 0, BITE)
                .when(() -> this.entityData.get(GLITCH_TIMER) > 0, GLITCH)
                .when(this::isSwimmingInWater, IDLE)
                .when(state -> this.isChasing() && AnimationTable.isMoving(state), ATTACK_RUN)
                // WANDERING covers walking below the limb swing threshold.
                .when(state -> AnimationTable.isMoving(state) || this.entityData.get(WANDERING), WANDER)
                .otherwise(IDLE)
                .startAt(SPAWN, illager -> illager.getSpawnTimer() > 0 && AnimationPreview.forcedAnimation(illager) == null ? Math.max(0, SPAWN_TICKS - illager.getSpawnTimer()) : 0)
                .build(this, "controller", AnimationTable.TRANSITION_TICKS));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private enum AnimationSound {
        SILENT("", null, 0, 0),
        SPAWN("spawn", () -> TGSounds.CORRUPTED_ILLAGER_SPAWN.get(), 0, 92),
        IDLE("idle", () -> TGSounds.CORRUPTED_ILLAGER_IDLE.get(), 120, 120),
        WANDER("wander", () -> TGSounds.CORRUPTED_ILLAGER_WANDER.get(), 112, 112),
        ATTACK_RUN("attack_run", () -> TGSounds.CORRUPTED_ILLAGER_ATTACK_RUN.get(), 40, 40),
        STRIKE("strike", () -> TGSounds.CORRUPTED_ILLAGER_STRIKE.get(), 0, 22),
        BITE("bite", () -> TGSounds.CORRUPTED_ILLAGER_BITE.get(), 0, 16),
        BLOW_APART("blow_apart", () -> TGSounds.CORRUPTED_ILLAGER_DEATH.get(), 0, 44);

        private final String animation;
        private final Supplier<SoundEvent> event;
        private final int repeatTicks;
        private final int lengthTicks;

        AnimationSound(String animation, Supplier<SoundEvent> event, int repeatTicks, int lengthTicks) {
            this.animation = animation;
            this.event = event;
            this.repeatTicks = repeatTicks;
            this.lengthTicks = lengthTicks;
        }

        static AnimationSound of(String animationName) {
            for (AnimationSound sound : values()) {
                if ((ANIMATION_PREFIX + sound.animation).equals(animationName)) {
                    return sound;
                }
            }
            return SILENT;
        }
    }
}
