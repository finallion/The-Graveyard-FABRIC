package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.entities.animation.AnimationClock;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Supplier;

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

    private static final TrackedData<Byte> DECAY = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> SPAWN_TIMER = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ATTACK_TIMER = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> ATTACK_KIND = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> GLITCH_TIMER = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> CLAWS = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> WANDERING = DataTracker.registerData(AnimatedCorruptedIllager.class, TrackedDataHandlerRegistry.BOOLEAN);

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

    protected AnimatedCorruptedIllager(EntityType<? extends CorruptedIllager> entityType, World world, String name) {
        super(entityType, world, name);
    }

    protected boolean hasSpawnAnimation() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DECAY, (byte) 0);
        this.dataTracker.startTracking(SPAWN_TIMER, 0);
        this.dataTracker.startTracking(ATTACK_TIMER, 0);
        this.dataTracker.startTracking(ATTACK_KIND, ATTACK_BITE);
        this.dataTracker.startTracking(GLITCH_TIMER, 0);
        this.dataTracker.startTracking(CLAWS, (byte) -1);
        this.dataTracker.startTracking(WANDERING, false);
    }

    public int getDecay() {
        byte decay = this.dataTracker.get(DECAY);

        if (decay >= 1 && decay <= DECAY_LEVELS) {
            return decay;
        }

        return Math.floorMod(this.getUuid().hashCode(), DECAY_LEVELS) + 1;
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
        byte claws = this.dataTracker.get(CLAWS);

        if (claws >= 0 && claws <= (LEFT_CLAW | RIGHT_CLAW)) {
            return claws;
        }

        return (int) (this.getUuid().getMostSignificantBits() >>> 62);
    }

    public int getSpawnTimer() {
        return this.dataTracker.get(SPAWN_TIMER);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        EntityData result = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        this.dataTracker.set(DECAY, (byte) (this.random.nextInt(DECAY_LEVELS) + 1));
        this.dataTracker.set(CLAWS, (byte) this.random.nextInt((LEFT_CLAW | RIGHT_CLAW) + 1));
        boolean unseenSpawn = spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.STRUCTURE;

        if (this.hasSpawnAnimation() && !unseenSpawn) {
            this.dataTracker.set(SPAWN_TIMER, SPAWN_TICKS);
        }

        return result;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("Decay", (byte) this.getDecay());
        nbt.putByte("Claws", (byte) this.getClaws());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("Decay", NbtElement.NUMBER_TYPE)) {
            byte decay = nbt.getByte("Decay");

            if (decay >= 1 && decay <= DECAY_LEVELS) {
                this.dataTracker.set(DECAY, decay);
            }
        }

        if (nbt.contains("Claws", NbtElement.NUMBER_TYPE)) {
            this.dataTracker.set(CLAWS, nbt.getByte("Claws"));
        }
    }

    @Override
    public void tick() {
        // The AI does not tick while immobile. The timers run here.
        if (!this.getWorld().isClient()) {
            tickDown(SPAWN_TIMER);
            tickDown(ATTACK_TIMER);
            tickDown(GLITCH_TIMER);
            resolvePendingHit();
        }

        super.tick();

        if (!this.getWorld().isClient() && this.isAiDisabled()) {
            setChasing(false);
            this.dataTracker.set(WANDERING, false);
        }

        if (this.getWorld().isClient() && AnimationPreview.forcedAnimation(this) == null) {
            tickAnimationSound();
        }
    }

    private AnimationSound currentAnimationSound() {
        boolean moving = this.limbAnimator.getSpeed() > MOVING_LIMB_SPEED;

        if (this.isDead() || this.getHealth() < 0.01F) {
            return AnimationSound.BLOW_APART;
        }

        if (this.getSpawnTimer() > 0) {
            return AnimationSound.SPAWN;
        }

        if (this.dataTracker.get(ATTACK_TIMER) > 0) {
            return this.dataTracker.get(ATTACK_KIND) == ATTACK_STRIKE ? AnimationSound.STRIKE : AnimationSound.BITE;
        }

        if (this.dataTracker.get(GLITCH_TIMER) > 0) {
            return AnimationSound.SILENT;
        }

        if (this.isSwimmingInWater()) {
            return AnimationSound.IDLE;
        }

        if (this.isChasing() && moving) {
            return AnimationSound.ATTACK_RUN;
        }

        return moving || this.dataTracker.get(WANDERING) ? AnimationSound.WANDER : AnimationSound.IDLE;
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
        if (sound == this.playingSound && this.age - this.soundStartAge < sound.lengthTicks - SOUND_RESTART_SLACK) {
            return;
        }

        this.currentSound = sound;
        this.soundCountdown = sound.repeatTicks;
        startSound(sound);
    }

    private void startSound(AnimationSound sound) {
        this.playingSound = sound;
        this.soundStartAge = this.age;
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

        boolean strike = this.dataTracker.get(ATTACK_KIND) == ATTACK_STRIKE;
        int hitTimer = strike ? STRIKE_TICKS - STRIKE_HIT_TICKS : BITE_TICKS - BITE_HIT_TICKS;

        if (this.dataTracker.get(ATTACK_TIMER) != hitTimer) {
            return;
        }

        LivingEntity target = this.pendingTarget;
        this.pendingTarget = null;
        float reach = this.getWidth() * 2.0F;

        if (target.isAlive() && this.squaredDistanceTo(target) <= reach * reach + target.getWidth() + 1.0D) {
            super.tryAttack(target);
        }
    }

    private boolean isSwimmingInWater() {
        return this.isTouchingWater() && !this.isOnGround();
    }

    private void tickDown(TrackedData<Integer> timer) {
        int ticks = this.dataTracker.get(timer);

        if (ticks > 0) {
            this.dataTracker.set(timer, ticks - 1);
        }
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        boolean navigating = !ChaseTarget.isLive(this) && !this.getNavigation().isIdle();
        this.stuckTicks = navigating && this.limbAnimator.getSpeed() <= MOVING_LIMB_SPEED ? this.stuckTicks + 1 : 0;
        this.dataTracker.set(WANDERING, navigating && this.stuckTicks < WANDER_STUCK_TICKS);
        boolean resting = !ChaseTarget.isLive(this) && this.getNavigation().isIdle() && this.getSpawnTimer() == 0;
        int glitch = this.dataTracker.get(GLITCH_TIMER);

        if (glitch > 0 && !resting) {
            this.dataTracker.set(GLITCH_TIMER, 0);
        } else if (glitch == 0 && resting && this.glitchCooldown == 0 && this.random.nextInt(GLITCH_CHANCE) == 0) {
            this.dataTracker.set(GLITCH_TIMER, GLITCH_TICKS);
            this.glitchCooldown = GLITCH_COOLDOWN_TICKS;
        }

        if (this.glitchCooldown > 0) {
            this.glitchCooldown--;
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!this.getWorld().isClient()) {
            byte next = this.dataTracker.get(ATTACK_KIND) == ATTACK_STRIKE ? ATTACK_BITE : ATTACK_STRIKE;
            this.dataTracker.set(ATTACK_KIND, next);
            this.dataTracker.set(ATTACK_TIMER, next == ATTACK_STRIKE ? STRIKE_TICKS : BITE_TICKS);
        }

        this.pendingTarget = target instanceof LivingEntity living ? living : null;
        return true;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getSpawnTimer() > 0;
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_TICKS && !this.getWorld().isClient()) {
            this.getWorld().sendEntityStatus(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationTable.<AnimatedCorruptedIllager>create()
                .transitionTicks(() -> this.getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS)
                .when(() -> this.isDead() || this.getHealth() < 0.01F, DEATH)
                .when(() -> this.getSpawnTimer() > 0, SPAWN)
                .when(() -> this.dataTracker.get(ATTACK_TIMER) > 0 && this.dataTracker.get(ATTACK_KIND) == ATTACK_STRIKE, STRIKE)
                .when(() -> this.dataTracker.get(ATTACK_TIMER) > 0, BITE)
                .when(() -> this.dataTracker.get(GLITCH_TIMER) > 0, GLITCH)
                .when(this::isSwimmingInWater, IDLE)
                .when(state -> this.isChasing() && AnimationTable.isMoving(state), ATTACK_RUN)
                // WANDERING covers walking below the limb swing threshold.
                .when(state -> AnimationTable.isMoving(state) || this.dataTracker.get(WANDERING), WANDER)
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
