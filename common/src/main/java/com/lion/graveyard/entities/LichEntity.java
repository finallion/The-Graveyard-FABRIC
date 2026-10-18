package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blocks.AltarBlock;
import com.lion.graveyard.config.entries.CorruptedChampionEntry;
import com.lion.graveyard.entities.ai.goals.LichMeleeGoal;
import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGParticles;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.sounds.BossMusicPlayer;
import com.lion.graveyard.util.MathUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Predicate;

public class LichEntity extends Monster implements GeoEntity {
    private final ServerBossEvent bossBar;
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    protected static final TargetingConditions HEAD_TARGET_PREDICATE;
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE;
    private static final ResourceLocation ATTACKING_SPEED_BOOST_ID = Graveyard.id("attacking_speed_boost_id");
    private static final ResourceLocation ATTACKING_DMG_BOOST_ID = Graveyard.id("attacking_dmg_boost_id");
    private static final ResourceLocation CRAWL_SPEED_BOOST_ID = Graveyard.id("crawl_speed_boost_id");
    private static final AttributeModifier ATTACKING_SPEED_BOOST;
    private static final AttributeModifier CRAWL_SPEED_BOOST;
    private static final AttributeModifier DMG_BOOST;
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation CORPSE_SPELL_ANIMATION = RawAnimation.begin().then("corpse_spell", Animation.LoopType.LOOP);
    private final RawAnimation START_PHASE_2_ANIMATION = RawAnimation.begin().then("phase_two", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation PHASE_2_IDLE_ANIMATION = RawAnimation.begin().then("phase_two_idle", Animation.LoopType.LOOP);
    private final RawAnimation PHASE_2_ATTACK_ANIMATION = RawAnimation.begin().then("phase_two_attack", Animation.LoopType.LOOP);
    private final RawAnimation START_PHASE_3_ANIMATION = RawAnimation.begin().then("phase_three", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation PHASE_3_ATTACK_ANIMATION = RawAnimation.begin().then("crawl", Animation.LoopType.LOOP);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation SHOOT_SKULL_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation SUMMON_ANIMATION = RawAnimation.begin().then("summon", Animation.LoopType.LOOP);
    private final RawAnimation CONJURE_FANG_ANIMATION = RawAnimation.begin().then("corpse_spell", Animation.LoopType.LOOP);
    private final RawAnimation STUNNED_ANIMATION = RawAnimation.begin().then("stunned", Animation.LoopType.LOOP);
    private final RawAnimation CRAWL_IDLE_ANIMATION = RawAnimation.begin().then("crawl_idle", Animation.LoopType.LOOP);
    private final RawAnimation PHASE_2_CHARGE_ANIMATION = RawAnimation.begin().then("phase_two_charge", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation CRAWL_ATTACK_ANIMATION = RawAnimation.begin().then("crawl_attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation CRAWL_POUNCE_ANIMATION = RawAnimation.begin().then("crawl_pounce", Animation.LoopType.HOLD_ON_LAST_FRAME);
    protected static final int ANIMATION_SPAWN = 0;
    protected static final int ANIMATION_IDLE = 1;
    protected static final int ANIMATION_MELEE = 2;
    protected static final int ANIMATION_CORPSE_SPELL = 3;
    protected static final int ANIMATION_START_PHASE_2 = 4;
    protected static final int ANIMATION_PHASE_2_IDLE = 5;
    protected static final int ANIMATION_PHASE_2_ATTACK = 6;
    protected static final int ANIMATION_SHOOT_SKULL = 7;
    protected static final int ANIMATION_START_PHASE_3 = 8;
    protected static final int ANIMATION_PHASE_3_ATTACK = 9;
    protected static final int ANIMATION_STOP = 10;
    protected static final int ANIMATION_SUMMON = 11;
    protected static final int ANIMATION_CONJURE_FANG = 12;
    protected static final int ANIMATION_STUNNED = 13;
    protected static final int ANIMATION_CRAWL_IDLE = 14;
    protected static final int ANIMATION_PHASE_2_CHARGE = 15;
    protected static final int ANIMATION_CRAWL_POUNCE = 16;
    protected static final int ANIMATION_CRAWL_ATTACK = 17;
    private static final EntityDataAccessor<Integer> INVUL_TIMER;
    private static final EntityDataAccessor<Integer> SPAWN_DURATION;
    private static final EntityDataAccessor<Integer> PHASE_INVUL_TIMER;
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> FIGHT_DURATION_TIMER;
    private static final EntityDataAccessor<Integer> PHASE_TWO_START_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> PHASE_THREE_START_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> PHASE;
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Integer> HUNT_TIMER;
    private static final EntityDataAccessor<Boolean> CAN_HUNT_START;
    private static final EntityDataAccessor<Boolean> CAN_MOVE;
    private static final EntityDataAccessor<Integer> MUSIC_DELAY;

    private static final EntityDataAccessor<Integer> CONJURE_FANG_TIMER;
    private static final EntityDataAccessor<Integer> HEAL_DURATION_TIMER;
    private static final EntityDataAccessor<Integer> CORPSE_SPELL_DURATION_TIMER;
    private static final EntityDataAccessor<Integer> LEVITATION_DURATION_TIMER;

    private static final byte MUSIC_PLAY_ID = 67;
    private static final byte MUSIC_STOP_ID = 68;
    private static CorruptedChampionEntry champion() {
        return Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion");
    }

    private static final int SPAWN_CLIP_TICKS = 490;

    private static int spawnInvulTicks() {
        return champion().spawnInvulnerableTicks();
    }

    private static int phaseInvulTicks() {
        return champion().phaseInvulnerableTicks();
    }
    private final float HEALTH_PHASE_01 = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").healthInCastingPhase;
    private final float HEALTH_PHASE_02 = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").healthInHuntingPhase;
    public final int ATTACK_ANIMATION_DURATION = 40;
    private final int START_PHASE_TWO_ANIMATION_DURATION = 120 + AnimationTable.TRANSITION_TICKS;
    private final int START_PHASE_THREE_ANIMATION_DURATION = 220 + AnimationTable.TRANSITION_TICKS;
    private final int START_PHASE_TWO_PARTICLES = 80 + AnimationTable.TRANSITION_TICKS;
    private final int CORPSE_SPELL_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationFallingCorpseSpell;
    private final int HUNT_COOLDOWN = champion().cooldownHunt();
    private final int HUNT_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationHuntingPhase;
    private final int HEALING_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationHealingSpell;
    private final int LEVITATION_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationLevitationSpell;
    protected static final EntityDimensions CRAWL_DIMENSIONS;
    private int huntCooldownTicker = 100;
    private BlockPos homePos;
    private boolean homeKnown;
    private Direction spawnDirection;
    private int phaseThreeAttackSoundAge;
    private int lastHurtSoundAge = -100;
    private int phaseTwoStrikeTicks;
    private int crawlStrikeTicks;
    private float pounceYaw;
    private int idleSoundAge = 0;
    private int chargeTicks;
    private boolean chargeContact;
    private int openingTicks;
    private int lastAttackAnimTimer;
    private static final int DEATH_REMOVAL_TICKS = 160 + AnimationTable.TRANSITION_TICKS;
    private int chargeCooldown = 100;
    private int pounceTicks;
    private int pounceCooldown = 100;
    private boolean pounceAirborne;

    public LichEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.bossBar = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(champion().bossBarDarkenSky()).setCreateWorldFog(champion().bossBarThickenFog());

        if (!world.isClientSide()) {
            this.entityData.set(SPAWN_DURATION, Math.max(1, spawnInvulTicks()));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<LichEntity> controller = AnimationTable.<LichEntity>create()
                .transitionTicks(this::blendTicks)
                // Set from the respawn method: drops every animation of the phase before.
                .stopWhen(() -> getAnimationState() == ANIMATION_STOP)
                .when(this::isSpawning, SPAWN_ANIMATION)
                .startAt(SPAWN_ANIMATION, lich -> lich.isSpawning() && lich.getPhase() == 1 && AnimationPreview.forcedAnimation(lich) == null
                        ? (double) (lich.getSpawnDuration() - lich.getInvulnerableTimer()) * SPAWN_CLIP_TICKS / Math.max(1, lich.getSpawnDuration()) : 0)
                .when(() -> isDeadOrDying(), DEATH_ANIMATION)

                .when(() -> getPhase() == 1 && getAnimationState() == ANIMATION_CORPSE_SPELL, CORPSE_SPELL_ANIMATION)
                .when(() -> getPhase() == 1 && getAnimationState() == ANIMATION_SHOOT_SKULL, SHOOT_SKULL_ANIMATION)
                .when(() -> getPhase() == 1 && getAnimationState() == ANIMATION_SUMMON, SUMMON_ANIMATION)
                .when(() -> getPhase() == 1 && getAnimationState() == ANIMATION_CONJURE_FANG, CONJURE_FANG_ANIMATION)
                .when(() -> getPhase() == 1 && getAttackAnimTimer() > 0 && canMeeleAttack(), ATTACK_ANIMATION)

                .when(() -> getPhase() == 2, START_PHASE_2_ANIMATION)

                .when(() -> getPhase() == 3 && getAnimationState() == ANIMATION_STUNNED, STUNNED_ANIMATION)
                .when(() -> getPhase() == 3 && getAnimationState() == ANIMATION_PHASE_2_CHARGE, PHASE_2_CHARGE_ANIMATION)
                .when(() -> getPhase() == 3 && getAnimationState() == ANIMATION_PHASE_2_ATTACK, PHASE_2_ATTACK_ANIMATION)
                .when(() -> getPhase() == 3, PHASE_2_IDLE_ANIMATION)

                .when(() -> getPhase() == 4, START_PHASE_3_ANIMATION)

                .when(() -> getPhase() == 5 && getAnimationState() == ANIMATION_CRAWL_POUNCE, CRAWL_POUNCE_ANIMATION)
                .when(() -> getPhase() == 5 && getAnimationState() == ANIMATION_CRAWL_ATTACK, CRAWL_ATTACK_ANIMATION)
                .when(state -> getPhase() == 5 && AnimationTable.isMoving(state), PHASE_3_ATTACK_ANIMATION)
                .when(() -> getPhase() == 5, CRAWL_IDLE_ANIMATION)

                .otherwise(IDLE_ANIMATION)
                .build(this, "controller", isSpawning() ? 0 : AnimationTable.TRANSITION_TICKS);
        controller.setAnimationSpeedHandler(lich -> lich.isSpawning() && lich.getPhase() == 1 ? (double) SPAWN_CLIP_TICKS / Math.max(1, lich.getSpawnDuration()) : 1.0D);
        animationData.add(controller);
    }

    private int blendTicks() {
        AnimationController<?> controller = getAnimatableInstanceCache().getManagerForId(getId()).getAnimationControllers().get("controller");
        String current = controller == null || controller.getCurrentAnimation() == null ? null : controller.getCurrentAnimation().animation().name();
        boolean hardCut = isSpawning() || "spawn".equals(current) || "phase_three".equals(current);
        return hardCut ? 0 : AnimationTable.TRANSITION_TICKS;
    }

    private boolean isSpawning() {
        return getAnimationState() == ANIMATION_SPAWN && getInvulnerableTimer() >= 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new LichMeleeGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new SummonFallenCorpsesGoal(this));
        this.goalSelector.addGoal(5, new ConjureFangsGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new ShootSkullGoal(this));
        this.goalSelector.addGoal(8, new TeleportAndHealGoal(this));
        this.goalSelector.addGoal(9, new LevitationGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createLichAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").healthInCastingPhase)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").damageCastingPhase)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.ARMOR, Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").armor)
                .add(Attributes.ARMOR_TOUGHNESS, Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").armorToughness)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void tick() {
        if (!getCommandSenderWorld().isClientSide && getBossMusic() != null && AnimationPreview.forcedAnimation(this) == null) {
            if (canPlayMusic() && deathTime == 0) {
                this.getCommandSenderWorld().broadcastEntityEvent(this, MUSIC_PLAY_ID);
            } else {
                this.getCommandSenderWorld().broadcastEntityEvent(this, MUSIC_STOP_ID);
            }
        }

        super.tick();

        if (getCommandSenderWorld().isClientSide()) {
            restartAttackClipOnRearm();
        }
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    private void restartAttackClipOnRearm() {
        int timer = getAttackAnimTimer();

        if (timer > lastAttackAnimTimer + 1) {
            AnimationController<?> controller = getAnimatableInstanceCache().getManagerForId(getId()).getAnimationControllers().get("controller");

            if (controller != null) {
                controller.forceAnimationReset();
            }
        }

        lastAttackAnimTimer = timer;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == MUSIC_PLAY_ID && getMusicDelay() == 78) {
            BossMusicPlayer.playBossMusic(this);
        } else if (id == MUSIC_STOP_ID) {
            BossMusicPlayer.stopBossMusic(this);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void aiStep() {
        if (homePos == null) {
            homePos = BlockPos.containing(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
        }

        randomDisplayTick(this.level(), this.blockPosition(), this.getRandom());

        int phaseTwoTimer = getStartPhaseTwoAnimTimer();

        if (phaseTwoTimer < START_PHASE_TWO_PARTICLES && phaseTwoTimer > START_PHASE_TWO_PARTICLES - 40) {
            float offset = 0.0F;
            for (int i = 0; i < 25; i++) {
                if (i < 7) {
                    offset += 0.15F;
                } else if (i > 12) {
                    offset -= 0.15F;
                }

                MathUtil.createParticleDisk(this.level(), this.getX(), this.getY() + ((float) i / 10), this.getZ(), 0.0D, 0.3D, 0.0D, 2 * offset, ParticleTypes.SOUL_FIRE_FLAME, this.getRandom());
            }
        }

        if (getInvulnerableTimer() > 0 && random.nextInt(6) == 0 && getPhase() == 1) {
            MathUtil.createParticleFlare(this.level(), this.getX() - 0.75D, this.getY() - 1.0D + 3.5D - (float) getInvulnerableTimer() / 100, this.getZ() - 0.75D, random.nextInt(300) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, false);
        }

        if (getInvulnerableTimer() > 0 && getPhase() == 1) {
            MathUtil.createParticleCircle(this.level(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 5, ParticleTypes.SOUL_FIRE_FLAME, this.getRandom(), 1.0F);
        }

        if (this.deathTime > 0 && this.deathTime <= 100 && random.nextInt(4) == 0) {
            MathUtil.createParticleFlare(this.level(), this.getX() - 0.75D, this.getY() - 1, this.getZ() - 0.75D, random.nextInt(300) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, false);
        }

        if (getPhase() == 1 ? getPhaseInvulnerableTimer() > 0 && getInvulnerableTimer() <= 0 : getPhase() == 3 && (getPhaseInvulnerableTimer() > 0 || getInvulnerableTimer() > 0)) {
            MathUtil.createParticleCircle(this.level(), this.getX(), this.getY() + 1.5D, this.getZ(), 0.0D, 0.0D, 0.0D, 2.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, this.getRandom(), 0.5F);
        }

        if (!canHuntStart() && getPhase() == 3 && getAnimationState() == ANIMATION_STUNNED && random.nextInt(7) == 0) {
            MathUtil.createParticleFlare(this.level(), this.getX() - 0.75D, this.getY() + 2.5D, this.getZ() - 0.75D, random.nextInt(100) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, true);
        }

        if (level().isClientSide()) {
            spawnTransitionAndDeathParticles();
        }

        if (getHealTimer() > 0 && getPhase() == 1) {
            spawnHealHelix(false);
        }

        if (getHealTimer() == 1 && getPhase() == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, -1.0F);
            spawnHealHelix(true);
        }

        if (!this.isAlive()) {
            this.releaseAltar();
        }

        if (canHuntStart()) {
            List<Player> playersInRange = getPlayersInRange(30.0D);
            for (Player player : playersInRange) {
                if ((getHuntTimer() == 0 || getHuntTimer() % 300 == 0) && !player.isCreative() && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                    player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 2.5F, -5.0F);
                    for (int i = 0; i < 10; i++) {
                        MathUtil.createParticleCircle(this.level(), player.getX(), player.getY(), player.getZ(), 0.0D, 0.01D, 0.0D, 1.0F, TGParticles.GRAVEYARD_SOUL_PARTICLE, this.getRandom(), 0.5F);
                    }
                }
            }
        }

        if (!canHuntStart() && random.nextInt(5) == 0) {
            getCommandSenderWorld().playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 4.0F, -10.0F);
        }

        if (getMusicDelay() < 78) {
            setMusicDelay(getMusicDelay() + 1);
        }

        super.aiStep();
    }

    private void spawnHealHelix(boolean burst) {
        if (burst) {
            for (int strand = 0; strand < 2; strand++) {
                for (int i = 0; i < HELIX_POINTS; i += 2) {
                    double progress = (double) i / HELIX_POINTS;
                    double angle = 2.0D * Math.PI * HELIX_TURNS * progress + strand * Math.PI;
                    double cos = Math.cos(angle);
                    double sin = Math.sin(angle);
                    level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX() + HELIX_RADIUS * cos, getY() + 0.1D + progress * HELIX_HEIGHT, getZ() + HELIX_RADIUS * sin, cos * 0.35D, 0.12D, sin * 0.35D);
                }
            }
            MathUtil.createParticleCircle(level(), getX(), getY() + 0.2D, getZ(), 0.0D, 0.05D, 0.0D, 3.0F, ParticleTypes.SOUL_FIRE_FLAME, random, 0.5F);
            return;
        }

        int step = tickCount % HELIX_POINT_SPACING;
        for (int strand = 0; strand < 2; strand++) {
            for (int i = step; i < HELIX_POINTS; i += HELIX_POINT_SPACING) {
                double progress = (double) i / HELIX_POINTS;
                double angle = 2.0D * Math.PI * HELIX_TURNS * progress + strand * Math.PI + tickCount * HELIX_SPIN_PER_TICK;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double x = getX() + HELIX_RADIUS * cos;
                double y = getY() + 0.1D + progress * HELIX_HEIGHT;
                double z = getZ() + HELIX_RADIUS * sin;

                if ((i / HELIX_POINT_SPACING) % 3 == 0) {
                    level().addParticle(ParticleTypes.SOUL, x, y, z, -sin * HELIX_SWIRL_SPEED, HELIX_SOUL_RISE, cos * HELIX_SWIRL_SPEED);
                } else {
                    level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, -sin * HELIX_SWIRL_SPEED, HELIX_FLAME_RISE, cos * HELIX_SWIRL_SPEED);
                }
            }
        }
    }

    private void spawnTransitionAndDeathParticles() {
        int phaseTwoTimer = getStartPhaseTwoAnimTimer();

        if (getPhase() == 2 && phaseTwoTimer > 0 && phaseTwoTimer < START_PHASE_TWO_ANIMATION_DURATION) {
            for (int i = 0; i < 8; i++) {
                double angle = tickCount * 0.4D + i * Math.PI / 4.0D;
                double height = (tickCount * 0.15D + i * 0.5D) % 4.0D;
                level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX() + 1.8D * Math.cos(angle), getY() + height, getZ() + 1.8D * Math.sin(angle), 0.0D, 0.05D, 0.0D);
            }

            if (phaseTwoTimer == 2) {
                spawnOutwardBurst(ParticleTypes.SOUL, 60, 0.3D);
            }
        }

        int phaseThreeTimer = getStartPhaseThreeAnimTimer();

        if (getPhase() == 4 && phaseThreeTimer > 0 && phaseThreeTimer < START_PHASE_THREE_ANIMATION_DURATION) {
            for (int i = 0; i < 4; i++) {
                spawnInHitbox(ParticleTypes.ASH, 0.0D);
            }
            for (int i = 0; i < 2; i++) {
                spawnInHitbox(ParticleTypes.SOUL, 0.0D);
            }

            if (phaseThreeTimer == 2) {
                spawnOutwardBurst(ParticleTypes.SOUL_FIRE_FLAME, 60, 0.35D);
                spawnOutwardBurst(ParticleTypes.LARGE_SMOKE, 30, 0.35D);
            }
        }

        if (deathTime > 0 && deathTime <= 140) {
            for (int i = 0; i < 6; i++) {
                spawnInHitbox(ParticleTypes.SOUL, 0.05D + random.nextDouble() * 0.1D);
            }
            for (int i = 0; i < 4; i++) {
                spawnInHitbox(ParticleTypes.SOUL_FIRE_FLAME, 0.05D + random.nextDouble() * 0.1D);
            }

            if (deathTime % 10 == 0) {
                MathUtil.createParticleCircle(level(), getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D, 2.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, random, 0.5F);
            }
        }

        if (deathTime == 150) {
            spawnOutwardBurst(ParticleTypes.SOUL_FIRE_FLAME, 80, 0.4D);
            spawnOutwardBurst(ParticleTypes.LARGE_SMOKE, 40, 0.4D);
        }
    }

    private void spawnInHitbox(ParticleOptions type, double velocityY) {
        level().addParticle(type, getX() + (random.nextDouble() - 0.5D) * 0.9D, getY() + random.nextDouble() * 4.0D, getZ() + (random.nextDouble() - 0.5D) * 0.9D, 0.0D, velocityY, 0.0D);
    }

    private void spawnOutwardBurst(ParticleOptions type, int count, double maxSpeed) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2.0D * Math.PI;
            double speed = maxSpeed * random.nextDouble();
            level().addParticle(type, getX(), getY() + 2.0D, getZ(), Math.cos(angle) * speed, (random.nextDouble() - 0.3D) * 0.2D, Math.sin(angle) * speed);
        }
    }

    @Override
    public void baseTick() {
        if (homePos == null) {
            homePos = BlockPos.containing(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
        }

        super.baseTick();
    }

    @Override
    protected void customServerAiStep() {
        if (homePos == null) {
            homePos = BlockPos.containing(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
        }

        if (idleSoundAge <= 0) {
            playAmbientSound();
            idleSoundAge = 100;
        }

        idleSoundAge--;

        int duration = getFightDurationTimer();
        setFightDurationTimer(duration + 1);

        if (duration > 400) {
            if (duration < Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").ghoulSpawnTimerInFight && duration % 400 == 0 && random.nextBoolean()) {
                summonMob(false);
            } else if (duration >= Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").ghoulSpawnTimerInFight && duration < Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").ghoulSpawnTimerInFight * 2 && duration % 600 == 0 && random.nextBoolean()) {
                summonMob(true);
            }
        }

        if (getPhase() == 5) {
            if (phaseThreeAttackSoundAge <= 0) {
                playStartPhaseThreeAttackSound();
                phaseThreeAttackSoundAge = 160 + random.nextInt(141);
            } else {
                phaseThreeAttackSoundAge--;
            }

            AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            setCanMove(true);
            tickCrawlStrike();

            if (!entityAttributeInstance.hasModifier(CRAWL_SPEED_BOOST_ID)) {
                entityAttributeInstance.addTransientModifier(CRAWL_SPEED_BOOST);
            }

            tickPounce();
        }

        if (getPhase() == 3) {
            AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance entityAttributeDmgInstance = this.getAttribute(Attributes.ATTACK_DAMAGE);

            if (getHuntCooldownTicker() > 0) {
                setHuntCooldownTicker(getHuntCooldownTicker() - 1);
            } else {
                setHuntStart(true);
            }

            if (canHuntStart()) {
                List<Player> playersInRange = getPlayersInRange(30.0D);
                for (Player player : playersInRange) {
                    if ((getHuntTimer() == 0 || getHuntTimer() % 300 == 0) && !player.isCreative() && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                        for (int i = 0; i <= 5; i++) {
                            BlockPos targetPos = BlockPos.containing(this.getX() + Mth.nextInt(random, -10, 10), this.getY(), this.getZ() + Mth.nextInt(random, -10, 10));

                            if (getCommandSenderWorld().getBlockState(targetPos).isAir() && getCommandSenderWorld().getBlockState(targetPos.above()).isAir() && getCommandSenderWorld().getBlockState(targetPos.below()).isRedstoneConductor(getCommandSenderWorld(), targetPos.below())) {
                                player.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                                break;
                            }
                        }
                    }

                    if (getHuntTimer() % 50 == 0 && !player.isCreative()) {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 2));
                    }
                }

                setCanMove(true);

                if (getHuntTimer() == 0) {
                    playHuntSound();
                    setHuntTimer(HUNT_DURATION);

                    if (Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                        this.setPhaseInvulTimer(HUNT_DURATION);
                    }
                }

                if (getHuntTimer() == 400) {
                    playHuntSound();
                }

                tickPhaseTwoStrike();
                tickCharge();

                if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST_ID)) {
                    entityAttributeInstance.addTransientModifier(ATTACKING_SPEED_BOOST);
                }

                if (!entityAttributeDmgInstance.hasModifier(ATTACKING_DMG_BOOST_ID)) {
                    entityAttributeDmgInstance.addTransientModifier(DMG_BOOST);
                }
            }

            if (getHuntTimer() == 1 && getHuntCooldownTicker() == 0 && canHuntStart()) {
                this.teleportTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D);
                stopRush();
                this.setHuntCooldownTicker(HUNT_COOLDOWN);
                enterStunned();
                setHuntStart(false);
                setCanMove(false);
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
                entityAttributeDmgInstance.removeModifier(DMG_BOOST);
            }

            if (this.getHuntTimer() > 0) {
                int timer = getHuntTimer() - 1;
                this.setHuntTimer(timer);
            }
        }

        if (getPhase() == 2) {
            int phaseTwoTimer = getStartPhaseTwoAnimTimer();

            if (getPhaseInvulnerableTimer() == 0) {
                this.teleportTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D);
                setPhaseInvulTimer(START_PHASE_TWO_ANIMATION_DURATION);
            }

            setAnimationState(ANIMATION_START_PHASE_2);

            if (phaseTwoTimer > 0) {
                if (phaseTwoTimer == START_PHASE_TWO_ANIMATION_DURATION) {
                    playStartPhaseTwoSound();
                }

                setStartPhaseTwoAnimTimer(phaseTwoTimer - 1);
            }

            if (phaseTwoTimer == 1) {
                setAnimationState(ANIMATION_PHASE_2_IDLE);
                setPhase(getPhase() + 1);
            }
        }

        if (getPhase() == 4) {
            if (!Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells && canHuntStart()) {
                AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                AttributeInstance entityAttributeDmgInstance = this.getAttribute(Attributes.ATTACK_DAMAGE);

                this.teleportTo(homePos.getX() + 0.75D, homePos.getY() + 0.5D, homePos.getZ() + 0.75D);
                stopRush();
                this.setHuntCooldownTicker(HUNT_COOLDOWN);
                setAnimationState(ANIMATION_STUNNED);
                setHuntStart(false);
                setCanMove(false);
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
                entityAttributeDmgInstance.removeModifier(DMG_BOOST);
            }

            int phaseThreeTimer = getStartPhaseThreeAnimTimer();

            if (getPhaseInvulnerableTimer() == 0) {
                setPhaseInvulTimer(START_PHASE_THREE_ANIMATION_DURATION);
            }

            setAnimationState(ANIMATION_START_PHASE_3);

            if (phaseThreeTimer > 0) {
                if (phaseThreeTimer == START_PHASE_THREE_ANIMATION_DURATION) {
                    playStartPhaseThreeSound();
                }

                int elapsed = START_PHASE_THREE_ANIMATION_DURATION - phaseThreeTimer;

                if (isBoneBreakTick(elapsed)) {
                    ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BONE_BLOCK.defaultBlockState()), getX(), getY() + 2.5D, getZ(), 25, 0.5D, 1.0D, 0.5D, 0.15D);
                }

                setStartPhaseThreeAnimTimer(phaseThreeTimer - 1);
            }

            if (phaseThreeTimer == 1) {
                setAnimationState(ANIMATION_PHASE_3_ATTACK);
                setPhase(getPhase() + 1);
            }
        }

        if (this.getPhaseInvulnerableTimer() > 0) {
            int timer = getPhaseInvulnerableTimer() - 1;
            this.setPhaseInvulTimer(timer);
        }

        if (getHealTimer() > 0) {
            List<Player> playersInRange = getPlayersInRange(30.0D);
            for (Player player : playersInRange) {
                if (getHealTimer() == HEALING_DURATION && !player.isCreative()) {
                    player.teleportTo(this.getX(), this.getY() + Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").playerTeleportYOffset, this.getZ());
                }

                if (!player.hasEffect(MobEffects.DIG_SLOWDOWN) && !player.isCreative()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 50, 1));
                }
            }
        }

        setHealTimer(getHealTimer() - 1);
        setLevitationDurationTimer(getLevitationDurationTimer() - 1);
        setConjureFangTimer(getConjureFangTimer() - 1);
        setCorpseSpellTimer(getCorpseSpellTimer() - 1);
        openingTicks = Math.max(openingTicks - 1, 0);

        int i;

        if (this.getInvulnerableTimer() > 0) {
            i = this.getInvulnerableTimer() - 1;

            int timer;

            if (getPhase() == 1) {
                timer = Math.max(1, spawnInvulTicks());
            } else {
                timer = Math.max(1, phaseInvulTicks());
            }

            if (this.getInvulnerableTimer() == 1 && getPhase() == 1) {
                setAnimationState(ANIMATION_IDLE);
                awakenBurst();
                openFight();
            }

            this.bossBar.setProgress(1.0F - (float) i / timer);
            this.setInvulTimer(i);
        } else {
            if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
                setAnimationState(ANIMATION_MELEE);
            }

            if (this.getAttackAnimTimer() > 0) {
                int animTimer = this.getAttackAnimTimer() - 1;
                this.setAttackAnimTimer(animTimer);
            }

            super.customServerAiStep();

            this.bossBar.setProgress(this.getHealth() / this.getMaxHealthPerPhase());
        }
    }

    private static final int[][] SUMMON_OFFSETS = {{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int HELIX_POINT_SPACING = 4;
    private static final double HELIX_SWIRL_SPEED = 0.08D;
    private static final double HELIX_FLAME_RISE = 0.03D;
    private static final double HELIX_SOUL_RISE = 0.06D;
    private static final int HELIX_POINTS = 40;
    private static final double HELIX_SPIN_PER_TICK = 0.12D;
    private static final double HELIX_TURNS = 3.0D;
    private static final double HELIX_RADIUS = 2.4D;
    private static final double HELIX_HEIGHT = 6.0D;
    private static final int CRAWL_STRIKE_TICKS = 23;
    private static final int CRAWL_STRIKE_HIT_TICK = 13;
    private static final int PHASE_TWO_STRIKE_TICKS = 40;
    private static final int PHASE_TWO_STRIKE_HIT_TICK = 19;
    private static final double POUNCE_AIR_FACTOR = 3.93D;
    private static final int CHARGE_TICKS = 53;
    private static final int CHARGE_LUNGE_TICK = 15;
    private static final int CHARGE_LUNGE_END_TICK = 31;
    private static final int CHARGE_HIT_TICK = 33;
    private static final int POUNCE_TICKS = 53;
    // The controller blends for five ticks. The leap waits for the dive pose.
    private static final int POUNCE_LEAP_TICK = 15;

    /// Elapsed ticks of the bone break keyframes. Keyframes start six ticks after the state change.
    private static boolean isBoneBreakTick(int elapsed) {
        return elapsed == 34 || elapsed == 44 || elapsed == 53 || elapsed == 70 || elapsed == 132 || elapsed == 144;
    }

    private void enterStunned() {
        if (getAnimationState() != ANIMATION_STUNNED) {
            level().playSound(null, getX(), getY(), getZ(), TGSounds.LICH_STUNNED.get(), SoundSource.HOSTILE, 4.0F, 0.5F);
        }

        setAnimationState(ANIMATION_STUNNED);
    }

    private void tickCharge() {
        LivingEntity target = getTarget();

        if (chargeTicks == 0) {
            chargeCooldown = Math.max(chargeCooldown - 1, 0);

            if (target == null || !target.isAlive() || chargeCooldown > 0 || !getSensing().hasLineOfSight(target)) {
                return;
            }

            double distance = distanceTo(target);

            if (distance < 6.0D || distance > 12.0D) {
                return;
            }

            chargeTicks = CHARGE_TICKS;
            chargeContact = false;
            phaseTwoStrikeTicks = 0;
        }

        if (target == null || !target.isAlive()) {
            endCharge();
            return;
        }

        chargeTicks--;
        setAnimationState(ANIMATION_PHASE_2_CHARGE);
        int elapsed = CHARGE_TICKS - chargeTicks;
        getLookControl().setLookAt(target, 60.0F, 60.0F);

        if (elapsed < CHARGE_LUNGE_TICK || elapsed > CHARGE_LUNGE_END_TICK || chargeContact) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            getNavigation().stop();
            getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0D);
        } else {
            double speed = Mth.clamp((CHARGE_LUNGE_END_TICK - elapsed) / 16.0D, 0.3D, 0.8D);
            Vec3 direction = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
            setDeltaMovement(direction.x * speed, getDeltaMovement().y, direction.z * speed);
        }

        if (elapsed >= CHARGE_LUNGE_TICK && elapsed <= CHARGE_LUNGE_END_TICK && getBoundingBox().inflate(0.6D).intersects(target.getBoundingBox())) {
            chargeContact = true;
        }

        if (chargeContact && elapsed == CHARGE_HIT_TICK) {
            double reach = getBbWidth() * 3.0D + 1.0D;

            if (distanceToSqr(target) <= reach * reach) {
                doHurtTarget(target);
            }
        }

        if (chargeTicks == 0) {
            endCharge();
        }
    }

    public boolean startCrawlStrike() {
        if (crawlStrikeTicks > 0 || pounceTicks > 0) {
            return false;
        }

        crawlStrikeTicks = CRAWL_STRIKE_TICKS;
        return true;
    }

    private void tickCrawlStrike() {
        if (crawlStrikeTicks <= 0) {
            setAnimationState(ANIMATION_PHASE_3_ATTACK);
            return;
        }

        setAnimationState(ANIMATION_CRAWL_ATTACK);

        if (CRAWL_STRIKE_TICKS - crawlStrikeTicks == CRAWL_STRIKE_HIT_TICK) {
            LivingEntity target = getTarget();
            double reach = getBbWidth() * 2.0D + 1.0D;

            if (target != null && target.isAlive() && distanceToSqr(target) <= reach * reach) {
                doHurtTarget(target);
            }
        }

        crawlStrikeTicks--;
    }

    public boolean isCharging() {
        return chargeTicks > 0;
    }

    public boolean startPhaseTwoStrike() {
        if (phaseTwoStrikeTicks > 0 || chargeTicks > 0) {
            return false;
        }

        phaseTwoStrikeTicks = PHASE_TWO_STRIKE_TICKS;
        return true;
    }

    private void tickPhaseTwoStrike() {
        if (phaseTwoStrikeTicks <= 0) {
            setAnimationState(ANIMATION_PHASE_2_IDLE);
            return;
        }

        setAnimationState(ANIMATION_PHASE_2_ATTACK);

        if (PHASE_TWO_STRIKE_TICKS - phaseTwoStrikeTicks == PHASE_TWO_STRIKE_HIT_TICK) {
            LivingEntity target = getTarget();
            double reach = getBbWidth() * 3.0D + 1.0D;

            if (target != null && target.isAlive() && distanceToSqr(target) <= reach * reach) {
                doHurtTarget(target);
            }
        }

        phaseTwoStrikeTicks--;
    }

    private void stopRush() {
        chargeTicks = 0;
        chargeContact = false;
        phaseTwoStrikeTicks = 0;
        pounceTicks = 0;
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
    }

    private void endCharge() {
        chargeTicks = 0;
        chargeContact = false;
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        chargeCooldown = champion().cooldownCharge() + random.nextInt(81);
        setAnimationState(ANIMATION_PHASE_2_IDLE);
    }

    private void tickPounce() {
        LivingEntity target = getTarget();

        if (pounceTicks == 0) {
            pounceCooldown = Math.max(pounceCooldown - 1, 0);

            if (target == null || !target.isAlive() || pounceCooldown > 0 || !onGround() || !getSensing().hasLineOfSight(target)) {
                return;
            }

            double distance = distanceTo(target);

            if (distance < 3.0D || distance > 9.0D) {
                return;
            }

            pounceTicks = POUNCE_TICKS;
            pounceAirborne = false;
            pounceYaw = getYRot();
            crawlStrikeTicks = 0;
        }

        pounceTicks--;
        setAnimationState(ANIMATION_CRAWL_POUNCE);
        int elapsed = POUNCE_TICKS - pounceTicks;

        if (elapsed < POUNCE_LEAP_TICK && target != null) {
            float targetYaw = (float) (Mth.atan2(target.getZ() - getZ(), target.getX() - getX()) * 57.2957763671875D) - 90.0F;
            pounceYaw += Mth.clamp(Mth.wrapDegrees(targetYaw - pounceYaw), -30.0F, 30.0F);
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        }

        if (elapsed == POUNCE_LEAP_TICK && target != null && onGround()) {
            Vec3 direction = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
            double reach = Math.max(0.0D, direction.length() - (getBbWidth() + target.getBbWidth()) / 2.0D + 0.3D);
            double speed = Mth.clamp(reach / POUNCE_AIR_FACTOR, 0.15D, 0.9D);
            direction = direction.normalize();
            setDeltaMovement(direction.x * speed, 0.3D, direction.z * speed);
            hasImpulse = true;
            pounceAirborne = true;
        } else if (pounceAirborne && elapsed > POUNCE_LEAP_TICK + 2 && onGround()) {
            pounceAirborne = false;

            if (target != null && target.isAlive() && getBoundingBox().inflate(1.0D).intersects(target.getBoundingBox())) {
                doHurtTarget(target);
            }
        }

        if (pounceTicks == 0) {
            pounceAirborne = false;
            pounceCooldown = champion().cooldownPounce() + random.nextInt(61);
        }

        if (isPouncing()) {
            getNavigation().stop();
            getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0D);
            setZza(0.0F);
            setXxa(0.0F);
            setYRot(pounceYaw);
            setYBodyRot(pounceYaw);
            setYHeadRot(pounceYaw);
        }
    }

    public boolean isPouncing() {
        return pounceTicks > 0 && (POUNCE_TICKS - pounceTicks <= POUNCE_LEAP_TICK || pounceAirborne);
    }

    private float getMaxHealthPerPhase() {
        if (getPhase() == 1) {
            return HEALTH_PHASE_01;
        } else {
            return HEALTH_PHASE_02;
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    private void releaseAltar() {
        if (this.homePos == null || this.level().isClientSide) {
            return;
        }

        BlockState altar = this.level().getBlockState(this.homePos.below());

        if (altar.getBlock() instanceof AltarBlock) {
            this.level().setBlock(this.homePos.below(), altar.setValue(AltarBlock.BLOODY, false), 3);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            this.releaseAltar();
        }

        super.remove(reason);
    }

    public BlockPos getHomePos() {
        return this.homePos;
    }

    public boolean isHomeKnown() {
        return this.homeKnown;
    }

    public void onSummoned(Direction direction, BlockPos altarPos) {
        this.setAnimationState(ANIMATION_SPAWN);
        this.entityData.set(SPAWN_DURATION, spawnInvulTicks());
        applyInvulAndResetBossBar(spawnInvulTicks());
        this.homePos = BlockPos.containing(altarPos.getX() + 0.5D, altarPos.getY(), altarPos.getZ() + 0.5D);
        this.homeKnown = true;
        this.spawnDirection = direction;
        playSpawnSound();
    }

    private void applyInvulAndResetBossBar(int invul) {
        this.setInvulTimer(invul);
        this.bossBar.setProgress(0.0F);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == 5) {
            playDeathSound();
        }

        if (this.deathTime == DEATH_REMOVAL_TICKS && !this.getCommandSenderWorld().isClientSide()) {
            this.getCommandSenderWorld().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (pose == Pose.CROUCHING) {
            return CRAWL_DIMENSIONS;
        }

        return super.getDefaultDimensions(pose);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);

        if (AnimationPreview.forcedAnimation(this) == null) {
            this.bossBar.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if ((this.getInvulnerableTimer() > 0 || this.getPhaseInvulnerableTimer() > 0) && !source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
                return false;
            } else {
                if (amount > this.getHealth() && getPhase() < 5 && !source.is(DamageTypeTags.BYPASSES_RESISTANCE) && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isMultiphaseFight) {
                    respawn();
                    return false;
                }

                // Damage stops the healing. The timer value 1 triggers the particle effect.
                if (getHealTimer() > 0) {
                    setHealTimer(1);
                } else {
                    setHealTimer(0);
                }

                return super.hurt(source, amount);
            }
        }
    }

    private void respawn() {
        this.setPhase(getPhase() + 1);
        setAnimationState(getPhase() == 4 ? ANIMATION_START_PHASE_3 : ANIMATION_STOP);
        this.removeAllEffects();
        applyInvulAndResetBossBar(phaseInvulTicks());
        setHealth(HEALTH_PHASE_02);
        setAttackAnimTimer(0);

        if (getPhase() == 4 || getPhase() == 5) {
            setPose(Pose.CROUCHING);
        }
    }

    public void randomDisplayTick(Level world, BlockPos pos, RandomSource random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        double d = (double) i + random.nextInt(3) - random.nextInt(3);
        double e = (double) j + 4.2D;
        double f = (double) k + random.nextInt(3) - random.nextInt(3);
        world.addParticle(ParticleTypes.ASH, d, e, f, 0.0D, 0.0D, 0.0D);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        mutable.set(i + Mth.nextInt(random, -17, 17), j + random.nextInt(10), k + Mth.nextInt(random, -17, 17));
        BlockState blockState = world.getBlockState(mutable);

        if (!blockState.isCollisionShapeFullBlock(world, mutable)) {
            world.addParticle(ParticleTypes.SMOKE, (double) mutable.getX() + random.nextDouble(), (double) mutable.getY() + random.nextDouble(), (double) mutable.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }

    public List<Player> getPlayersInRange(double range) {
        AABB box = (new AABB(this.blockPosition())).inflate(range);
        return getCommandSenderWorld().getEntitiesOfClass(Player.class, box);
    }

    public boolean canMeeleAttack() {
        return getPhase() == 1
                && getPhaseInvulnerableTimer() <= 0
                && getInvulnerableTimer() <= 0
                && getHealTimer() <= 0
                && getLevitationDurationTimer() <= 0
                && getConjureFangTimer() <= 0
                && getCorpseSpellTimer() <= 0
                && this.getHealth() > 35.0F;
    }

    public boolean canSpellCast() {
        return getPhase() == 1
                && getAttackAnimTimer() <= 0
                && getPhaseInvulnerableTimer() <= 0
                && getInvulnerableTimer() <= 0
                && getHealTimer() <= 0
                && getLevitationDurationTimer() <= 0
                && getConjureFangTimer() <= 0
                && getCorpseSpellTimer() <= 0;
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    private boolean isSummonCell(BlockPos pos) {
        Level world = getCommandSenderWorld();
        return world.getBlockState(pos).isAir() && world.getBlockState(pos.above()).isAir() && world.getBlockState(pos.above().above()).isAir() && !world.getBlockState(pos.below()).isAir();
    }

    private BlockPos summonCell(BlockPos center, int index) {
        int[] offset = SUMMON_OFFSETS[index % SUMMON_OFFSETS.length];
        BlockPos cell = center.offset(offset[0], 0, offset[1]);
        return isSummonCell(cell) ? cell : center;
    }

    private boolean summonMob(boolean hard) {
        if (this.getHuntTimer() <= 1) {
            AABB box = (new AABB(this.blockPosition())).inflate(35.0D);
            List<HostileGraveyardEntity> mobs = level().getEntitiesOfClass(HostileGraveyardEntity.class, box);

            if (mobs.size() >= Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").maxSummonedMobs) {
                return false;
            }

            for (int i = 10; i > 0; i--) {
                BlockPos pos = new BlockPos(this.getBlockX() + Mth.nextInt(random, -15, 15), this.getBlockY(), this.getBlockZ() + Mth.nextInt(random, -15, 15));
                int amount = random.nextInt(Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").maxGroupSizeSummonedMobs) + 1;

                if (isSummonCell(pos)) {
                    if (!hard) {
                        for (int ii = 0; ii < amount; ++ii) {
                            RevenantEntity revenant = TGEntities.REVENANT.get().create(getCommandSenderWorld());
                            BlockPos cell = summonCell(pos, ii);
                            revenant.setPos(cell.getX() + 0.5D, cell.getY(), cell.getZ() + 0.5D);
                            revenant.setCanBurnInSunlight(false);
                            getCommandSenderWorld().addFreshEntity(revenant);
                        }
                    } else {
                        for (int ii = 0; ii < amount - 1; ++ii) {
                            GhoulEntity ghoul = TGEntities.GHOUL.get().create(getCommandSenderWorld());
                            ghoul.setVariant((byte) 9);
                            BlockPos cell = summonCell(pos, ii);
                            ghoul.setPos(cell.getX() + 0.5D, cell.getY(), cell.getZ() + 0.5D);
                            ghoul.setCanBurnInSunlight(false);
                            getCommandSenderWorld().addFreshEntity(ghoul);
                        }
                    }

                    return true;

                }
            }
        }

        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    public boolean canHuntStart() {
        return this.entityData.get(CAN_HUNT_START);
    }

    public void setHuntStart(boolean bool) {
        this.entityData.set(CAN_HUNT_START, bool);
    }

    public void setCanMove(boolean bool) {
        this.entityData.set(CAN_MOVE, bool);
    }

    public boolean canMove() {
        return this.entityData.get(CAN_MOVE);
    }

    public int getInvulnerableTimer() {
        return (Integer) this.entityData.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.entityData.set(INVUL_TIMER, ticks);
    }

    public int getFightDurationTimer() {
        return (Integer) this.entityData.get(FIGHT_DURATION_TIMER);
    }

    public void setFightDurationTimer(int ticks) {
        this.entityData.set(FIGHT_DURATION_TIMER, ticks);
    }

    public int getLevitationDurationTimer() {
        return (Integer) this.entityData.get(LEVITATION_DURATION_TIMER);
    }

    public void setLevitationDurationTimer(int ticks) {
        this.entityData.set(LEVITATION_DURATION_TIMER, ticks);
    }

    public int getConjureFangTimer() {
        return (Integer) this.entityData.get(CONJURE_FANG_TIMER);
    }

    public void setConjureFangTimer(int ticks) {
        this.entityData.set(CONJURE_FANG_TIMER, ticks);
    }

    /// Zero until the server value arrives.
    public int getSpawnDuration() {
        int duration = this.entityData.get(SPAWN_DURATION);
        return duration > 0 ? duration : SPAWN_CLIP_TICKS;
    }

    public int getCorpseSpellTimer() {
        return (Integer) this.entityData.get(CORPSE_SPELL_DURATION_TIMER);
    }

    public void setCorpseSpellTimer(int ticks) {
        this.entityData.set(CORPSE_SPELL_DURATION_TIMER, ticks);
    }

    public int getHealTimer() {
        return (Integer) this.entityData.get(HEAL_DURATION_TIMER);
    }

    public void setHealTimer(int ticks) {
        this.entityData.set(HEAL_DURATION_TIMER, ticks);
    }

    public int getPhaseInvulnerableTimer() {
        return (Integer) this.entityData.get(PHASE_INVUL_TIMER);
    }

    public void setPhaseInvulTimer(int ticks) {
        this.entityData.set(PHASE_INVUL_TIMER, ticks);
    }

    public int getHuntTimer() {
        return (Integer) this.entityData.get(HUNT_TIMER);
    }

    public void setHuntTimer(int ticks) {
        this.entityData.set(HUNT_TIMER, ticks);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION, state);
    }

    public int getPhase() {
        return (Integer) this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getStartPhaseTwoAnimTimer() {
        return (Integer) this.entityData.get(PHASE_TWO_START_ANIM_TIMER);
    }

    public void setStartPhaseTwoAnimTimer(int startPhaseTwoAnimTimer) {
        this.entityData.set(PHASE_TWO_START_ANIM_TIMER, startPhaseTwoAnimTimer);
    }

    public int getStartPhaseThreeAnimTimer() {
        return (Integer) this.entityData.get(PHASE_THREE_START_ANIM_TIMER);
    }

    public void setStartPhaseThreeAnimTimer(int startPhaseThreeAnimTimer) {
        this.entityData.set(PHASE_THREE_START_ANIM_TIMER, startPhaseThreeAnimTimer);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.entityData.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.entityData.set(ATTACK_ANIM_TIMER, time);
    }

    public int getHuntCooldownTicker() {
        return huntCooldownTicker;
    }

    public void setHuntCooldownTicker(int huntCooldownTicker) {
        this.huntCooldownTicker = huntCooldownTicker;
    }

    public int getMusicDelay() {
        return (Integer) this.entityData.get(MUSIC_DELAY);
    }

    public void setMusicDelay(int time) {
        this.entityData.set(MUSIC_DELAY, time);
    }

    private void openFight() {
        if (getTarget() == null) {
            double range = getAttributeValue(Attributes.FOLLOW_RANGE);
            Player nearest = null;
            for (Player player : getPlayersInRange(range)) {
                if (player.isAlive() && !player.isCreative() && !player.isSpectator()
                        && (nearest == null || distanceToSqr(player) < distanceToSqr(nearest))) {
                    nearest = player;
                }
            }
            setTarget(nearest);
        }

        setCorpseSpellTimer(Math.min(getCorpseSpellTimer(), -champion().cooldownCorpseSpell));
        setLevitationDurationTimer(Math.min(getLevitationDurationTimer(), -champion().cooldownLevitationSpell));
        openingTicks = 20;
    }

    private boolean isAtHome() {
        return distanceToSqr(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D) <= 1.0D;
    }

    private void awakenBurst() {
        if (this.getCommandSenderWorld() instanceof ServerLevel world) {
            world.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.5D, this.getZ(), 160, 1.4D, 1.6D, 1.4D, 0.12D);
            world.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.0D, this.getZ(), 60, 1.0D, 1.2D, 1.0D, 0.05D);
            world.playSound(null, this.blockPosition(), TGSounds.LICH_SCARE.get(), SoundSource.HOSTILE, 6.0F, 0.8F);
        }
    }

    private void playSpawnSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_SPAWN.get(), SoundSource.HOSTILE, 15.0F, 1.0F);
    }

    public void playAttackSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_MELEE.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    public void playHealSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_CAST_TELEPORT.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playCorpseSpellSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_CORPSE_SPELL.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseTwoSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_PHASE_02.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseThreeSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_PHASE_03.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseThreeAttackSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_PHASE_03_ATTACK.get(), SoundSource.HOSTILE, 7.0F, 0.85F + random.nextFloat() * 0.2F);
    }

    private void playDeathSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_DEATH.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    public void playScareSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_SCARE.get(), SoundSource.HOSTILE, 10.0F, 0.9F + random.nextFloat() * 0.15F);
    }

    private void playHuntSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_HUNT.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playShootSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_CAST_SKULL.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playLevitationSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_CAST_LEVITATION.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        this.getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.LICH_IDLE.get(), SoundSource.HOSTILE, 15.0F, 1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        if (tickCount - lastHurtSoundAge < 15) {
            return;
        }

        lastHurtSoundAge = tickCount;
        playSound(getHurtSound(source), getSoundVolume(), 0.85F + random.nextFloat() * 0.25F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return TGSounds.LICH_HURT.get();
    }

    public SoundEvent getBossMusic() {
        return TGSounds.LICH_THEME_01.get();
    }

    protected boolean canPlayMusic() {
        return !isSilent();
    }

    public boolean canPlayerHearMusic(Player player) {
        return player != null && distanceTo(player) < 2500;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CAN_MOVE, false);
        builder.define(INVUL_TIMER, 0);
        builder.define(SPAWN_DURATION, 0);
        builder.define(MUSIC_DELAY, 0);
        builder.define(HUNT_TIMER, 0);
        builder.define(FIGHT_DURATION_TIMER, 0);
        builder.define(HEAL_DURATION_TIMER, 0);
        builder.define(LEVITATION_DURATION_TIMER, 0);
        builder.define(CORPSE_SPELL_DURATION_TIMER, 0);
        builder.define(PHASE_INVUL_TIMER, 0);
        builder.define(ANIMATION, ANIMATION_IDLE);
        builder.define(ATTACK_ANIM_TIMER, 0);
        builder.define(CONJURE_FANG_TIMER, 0);
        builder.define(PHASE_TWO_START_ANIM_TIMER, START_PHASE_TWO_ANIMATION_DURATION);
        builder.define(PHASE_THREE_START_ANIM_TIMER, START_PHASE_THREE_ANIMATION_DURATION);
        builder.define(PHASE, 1);
        builder.define(CAN_HUNT_START, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Invul", getInvulnerableTimer());
        nbt.putInt("PhaseInvul", getPhaseInvulnerableTimer());
        nbt.putInt("Phase", Math.max(getPhase(), 1));
        nbt.putInt("AttackTimer", getAttackAnimTimer());
        nbt.putInt("FightTimer", getFightDurationTimer());
        nbt.putInt("LevTimer", getLevitationDurationTimer());
        nbt.putInt("HealTimer", getHealTimer());
        nbt.putInt("CorpseSpellTimer", getCorpseSpellTimer());
        nbt.putInt("ConjureTimer", getConjureFangTimer());
        nbt.putInt("Anim", getAnimationState());
        nbt.putFloat("Health", getHealth());
        nbt.putBoolean("CanMove", canMove());
        nbt.putBoolean("CanHunt", canHuntStart());
        nbt.putInt("HuntTimer", getHuntTimer());
        nbt.putInt("HuntCooldown", huntCooldownTicker);
        nbt.putInt("PhaseTwoTimer", getStartPhaseTwoAnimTimer());
        nbt.putInt("PhaseThreeTimer", getStartPhaseThreeAnimTimer());

        if (homePos != null && this.homeKnown) {
            nbt.putLong("HomePos", homePos.asLong());
        }

        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (!nbt.contains("Invul")) {
            this.setInvulTimer(0);
            this.setPhaseInvulTimer(0);
            this.setPhase(1);
            this.setAnimationState(ANIMATION_IDLE);
            this.setHealth(HEALTH_PHASE_01);
            this.setAttackAnimTimer(0);
            this.setHealTimer(0);
            this.setLevitationDurationTimer(0);
            this.setCorpseSpellTimer(0);
            this.setConjureFangTimer(0);
            this.setFightDurationTimer(0);
            this.setCanMove(false);
            this.setHuntStart(false);
        } else {
            this.setInvulTimer(nbt.getInt("Invul"));
            this.setPhaseInvulTimer(nbt.getInt("PhaseInvul"));
            this.setPhase(nbt.getInt("Phase"));
            this.setAnimationState(nbt.getInt("Anim"));
            this.setHealth(nbt.getFloat("Health"));
            this.setAttackAnimTimer(nbt.getInt("AttackTimer"));
            this.setHealTimer(nbt.getInt("HealTimer"));
            this.setLevitationDurationTimer(nbt.getInt("LevTimer"));
            this.setCorpseSpellTimer(nbt.getInt("CorpseSpellTimer"));
            this.setConjureFangTimer(nbt.getInt("ConjureTimer"));
            this.setFightDurationTimer(nbt.getInt("FightTimer"));
            this.setCanMove(nbt.getBoolean("CanMove"));
            this.setHuntStart(nbt.getBoolean("CanHunt"));

            if (nbt.contains("HuntTimer")) {
                this.setHuntTimer(nbt.getInt("HuntTimer"));
            }

            if (nbt.contains("HuntCooldown")) {
                this.huntCooldownTicker = nbt.getInt("HuntCooldown");
            }

            if (nbt.contains("PhaseTwoTimer")) {
                this.setStartPhaseTwoAnimTimer(nbt.getInt("PhaseTwoTimer"));
            }

            if (nbt.contains("PhaseThreeTimer")) {
                this.setStartPhaseThreeAnimTimer(nbt.getInt("PhaseThreeTimer"));
            }
        }

        if (nbt.contains("HomePos", Tag.TAG_LONG)) {
            this.homePos = BlockPos.of(nbt.getLong("HomePos"));
            this.homeKnown = true;
        }

        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }

        super.readAdditionalSaveData(nbt);
    }

    static {
        INVUL_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        SPAWN_DURATION = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        PHASE_INVUL_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        HUNT_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        ATTACK_ANIM_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        PHASE_TWO_START_ANIM_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        PHASE_THREE_START_ANIM_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        ANIMATION = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        FIGHT_DURATION_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        HEAL_DURATION_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        CONJURE_FANG_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        CORPSE_SPELL_DURATION_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        LEVITATION_DURATION_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        PHASE = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        CAN_HUNT_START = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.BOOLEAN);
        CAN_MOVE = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.BOOLEAN);
        MUSIC_DELAY = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
        CAN_ATTACK_PREDICATE = Entity::isAlwaysTicking;
        HEAD_TARGET_PREDICATE = TargetingConditions.forCombat().range(20.0D).selector(CAN_ATTACK_PREDICATE);
        CRAWL_DIMENSIONS = EntityDimensions.fixed(1.8F, 2.0F).withEyeHeight(2.0F);
        CRAWL_SPEED_BOOST = new AttributeModifier(CRAWL_SPEED_BOOST_ID, 0.18D, AttributeModifier.Operation.ADD_VALUE);
        ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").speedInHuntPhase, AttributeModifier.Operation.ADD_VALUE);
        DMG_BOOST = new AttributeModifier(ATTACKING_DMG_BOOST_ID, Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").damageHuntingPhaseAddition, AttributeModifier.Operation.ADD_VALUE);
    }

    public class SummonFallenCorpsesGoal extends Goal {
        protected final LichEntity lich;
        private final int FALL_HEIGHT = 10;
        private final int SQUARE_SIZE = 30;
        private final int MIN_FREE_BLOCKS = 4;
        private final int CORPSE_SPAWN_RARITY_PLAYER = 9;
        private BlockPos pos;
        private List<Player> list;
        private List<BlockPos> positions = new ArrayList<>();

        public SummonFallenCorpsesGoal(LichEntity lich) {
            this.lich = lich;
        }

        @Override
        public boolean canUse() {
            if (getPhase() == 1 && getCorpseSpellTimer() > 1) {
                return true;
            }

            return getCorpseSpellTimer() <= -Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").cooldownCorpseSpell
                    && (openingTicks > 0 && getTarget() == null || random.nextInt(75) == 0)
                    && canSpellCast();
        }

        @Override
        public void start() {
            if (getCorpseSpellTimer() <= 0) {
                // The invulnerability during the spell stops the melee attack.
                if (Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                    setPhaseInvulTimer(CORPSE_SPELL_DURATION);
                }

                setCorpseSpellTimer(CORPSE_SPELL_DURATION);
                openingTicks = 0;
                playCorpseSpellSound();

                if (!isAtHome()) {
                    this.lich.teleportTo(this.lich.homePos.getX() + 0.5D, this.lich.homePos.getY() + 0.5D, this.lich.homePos.getZ() + 0.5D);
                }
            }

            pos = this.lich.blockPosition();
            list = getPlayersInRange(35.0D);
            positions.clear();

            Level world = getCommandSenderWorld();
            BlockPos.MutableBlockPos cell = new BlockPos.MutableBlockPos();
            for (int i = -SQUARE_SIZE; i <= SQUARE_SIZE; i++) {
                for (int ii = -SQUARE_SIZE; ii <= SQUARE_SIZE; ii++) {
                    int x = pos.getX() + i;
                    int z = pos.getZ() + ii;
                    for (int y = pos.getY() + 3; y >= pos.getY() - 6; y--) {
                        cell.set(x, y, z);

                        if (!world.getBlockState(cell).getCollisionShape(world, cell).isEmpty()) {
                            int top = y;
                            while (top - y < FALL_HEIGHT && world.getBlockState(cell.set(x, top + 1, z)).getCollisionShape(world, cell).isEmpty()) {
                                top++;
                            }

                            if (top - y >= MIN_FREE_BLOCKS) {
                                positions.add(new BlockPos(x, top, z));
                            }

                            break;
                        }
                    }
                }
            }

            super.start();
        }

        @Override
        public void stop() {
            if (getAnimationState() == ANIMATION_CORPSE_SPELL) {
                setAnimationState(ANIMATION_IDLE);
            }

            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getCorpseSpellTimer() > 0) {
                return true;
            }

            return super.canContinueToUse();
        }

        @Override
        public void tick() {
            if (getCorpseSpellTimer() > 1) {
                setAnimationState(ANIMATION_CORPSE_SPELL);

                if (getCorpseSpellTimer() == 1) {
                    setAnimationState(ANIMATION_IDLE);
                }
            }

            if (getCorpseSpellTimer() % 200 == 0) {
                summonMob(true);
            }

            if (positions.size() <= 3) {
                setCorpseSpellTimer(0);
                setPhaseInvulTimer(0);
                stop();
                return;
            }

            ServerLevel serverWorld = (ServerLevel) LichEntity.this.getCommandSenderWorld();
            FallingCorpse corpse = (FallingCorpse) TGEntities.FALLING_CORPSE.get().create(serverWorld);
            BlockPos blockPos = positions.get(random.nextInt(positions.size()));
            corpse.setPos((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.55D, (double) blockPos.getZ() + 0.5D);
            serverWorld.addFreshEntity(corpse);

            if (random.nextInt(CORPSE_SPAWN_RARITY_PLAYER) == 0 && list.size() > 0) {
                FallingCorpse corpse2 = (FallingCorpse) TGEntities.FALLING_CORPSE.get().create(serverWorld);
                Player target = list.get(random.nextInt(list.size()));

                if (target != null && !target.isCreative()) {
                    BlockPos blockPos2 = target.blockPosition().offset(0, FALL_HEIGHT, 0);
                    corpse2.setPos((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.55D, (double) blockPos2.getZ() + 0.5D);
                    serverWorld.addFreshEntity(corpse2);
                }
            }
        }
    }

    public class ShootSkullGoal extends Goal {
        private final LichEntity mob;
        public int cooldown;

        public ShootSkullGoal(LichEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && canSpellCast();
        }

        @Override
        public void start() {
            this.cooldown = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            if (getAnimationState() == ANIMATION_SHOOT_SKULL) {
                setAnimationState(ANIMATION_IDLE);
            }

            super.stop();
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();

            if (livingEntity != null) {
                if (livingEntity.distanceToSqr(this.mob) < 8000.0D && livingEntity.distanceToSqr(this.mob) > 16.0D) {
                    Level world = this.mob.getCommandSenderWorld();
                    ++this.cooldown;

                    if (this.cooldown == 10) {
                        playShootSound();
                        setAnimationState(ANIMATION_SHOOT_SKULL);
                    }

                    if (this.cooldown == 20) {
                        Vec3 vec3d3 = this.mob.getViewVector(1.0F);
                        double d = this.mob.distanceToSqr(livingEntity) * 2;
                        double h = Math.sqrt(Math.sqrt(d)) * 0.5D;
                        double e = livingEntity.getX() - this.mob.getX();
                        double f = livingEntity.getY(0.5D) - this.mob.getY(0.5D) - 1.25D;
                        double g = livingEntity.getZ() - this.mob.getZ();
                        SkullEntity skull = new SkullEntity(this.mob.level(), this.mob, e, f, g);
                        skull.setPos(this.mob.getX() - vec3d3.x * 0.5, this.mob.getY(0.5D) + 1.25D, this.mob.getZ() - vec3d3.z * 0.5D);
                        world.addFreshEntity(skull);

                        int amount = random.nextInt(Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").maxAmountSkullsInShootSkullSpell) + 2;
                        for (int i = 0; i < amount; ++i) {
                            SkullEntity devSkull = new SkullEntity(this.mob.level(), this.mob, this.mob.getRandom().triangle(e, 2.297D * h),
                                    f, this.mob.getRandom().triangle(g, 2.297D * h));
                            devSkull.setPos(this.mob.getX() - vec3d3.x * 0.5, this.mob.getY(0.5D) + 1.25D, this.mob.getZ() - vec3d3.z * 0.5D);
                            world.addFreshEntity(devSkull);
                        }

                        this.mob.getLookControl().setLookAt(livingEntity, 10.0F, 10.0F);
                    }

                    if (this.cooldown == 45) {
                        this.cooldown = -40;
                        setAnimationState(ANIMATION_IDLE);
                    }
                } else if (this.cooldown > 0) {
                    --this.cooldown;
                }
            }
        }
    }

    public class TeleportAndHealGoal extends Goal {
        private final LichEntity mob;

        public TeleportAndHealGoal(LichEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (getPhase() == 1 && this.mob.getHealTimer() > 1) {
                return true;
            }

            return this.mob.getHealth() <= champion().healThresholdHealth()
                    && random.nextInt(40) == 0
                    && this.mob.getHealTimer() <= -Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").cooldownTeleportPlayerAndHeal
                    && canSpellCast();
        }

        @Override
        public void start() {
            if (getHealTimer() > 0) {
                return;
            }

            setHealTimer(HEALING_DURATION);
            openingTicks = 0;

            if (!isAtHome()) {
                this.mob.teleportTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D);
            }

            playHealSound();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            if (getAnimationState() == ANIMATION_SUMMON) {
                setAnimationState(ANIMATION_IDLE);
            }

            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getHealTimer() > 0) {
                return true;
            }

            return super.canContinueToUse();
        }

        @Override
        public void tick() {
            if (getHealTimer() > 1) {
                setAnimationState(ANIMATION_SUMMON);
                this.mob.heal(0.2F);

                if (getHealTimer() == 1) {
                    setAnimationState(ANIMATION_IDLE);
                }
            }
        }
    }

    public class LevitationGoal extends Goal {
        private final LichEntity mob;

        public LevitationGoal(LichEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (getPhase() == 1 && this.mob.getLevitationDurationTimer() > 1) {
                return true;
            }

            return canSpellCast()
                    && random.nextInt(125) == 0
                    && this.mob.getLevitationDurationTimer() <= -Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").cooldownLevitationSpell;
        }

        @Override
        public void start() {
            if (getLevitationDurationTimer() > 0) {
                return;
            }

            setLevitationDurationTimer(LEVITATION_DURATION);
            openingTicks = 0;

            List<Player> players = getPlayersInRange(30.0D);

            for (Player playerEntity : players) {
                if (!playerEntity.isCreative()) {
                    playerEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, 1));
                }
            }

            playLevitationSound();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            if (getAnimationState() == ANIMATION_SUMMON) {
                setAnimationState(ANIMATION_IDLE);
            }

            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getLevitationDurationTimer() > 0) {
                return true;
            }

            return super.canContinueToUse();
        }

        @Override
        public void tick() {
            if (getLevitationDurationTimer() > 1) {
                setAnimationState(ANIMATION_SUMMON);

                if (getLevitationDurationTimer() == 1) {
                    setAnimationState(ANIMATION_IDLE);
                }
            }
        }
    }

    public class ConjureFangsGoal extends Goal {
        private final LichEntity mob;

        public ConjureFangsGoal(LichEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (getPhase() == 1 && this.mob.getConjureFangTimer() > 1) {
                return true;
            }

            return canSpellCast() && random.nextInt(25) == 0 && this.mob.getConjureFangTimer() <= -100
                    && this.mob.getTarget() != null && this.mob.distanceToSqr(this.mob.getTarget()) <= 64.0D;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            if (getAnimationState() == ANIMATION_CONJURE_FANG) {
                setAnimationState(ANIMATION_IDLE);
            }

            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getConjureFangTimer() > 0) {
                return true;
            }

            return super.canContinueToUse();
        }

        @Override
        public void start() {
            LivingEntity livingEntity = this.mob.getTarget();

            if (getConjureFangTimer() <= 0 && livingEntity != null) {
                double d = Math.min(livingEntity.getY(), this.mob.getY());
                double e = Math.max(livingEntity.getY(), this.mob.getY()) + 1.0D;
                float f = (float) Mth.atan2(livingEntity.getZ() - this.mob.getZ(), livingEntity.getX() - this.mob.getX());
                int i;

                if (this.mob.distanceToSqr(livingEntity) <= 64.0D) {
                    setConjureFangTimer(40);

                    for (i = 0; i < 16; ++i) {
                        double h = 1.25D * (double) (i + 1);
                        int j = 1 * i;
                        this.conjureFangs(this.mob.getX() + (double) Mth.cos(f) * h, this.mob.getZ() + (double) Mth.sin(f) * h, d, e, f, j);
                        this.conjureFangs(this.mob.getX() + (double) Mth.cos(-f) * h, this.mob.getZ() + (double) Mth.sin(-f) * h, d, e, -f, j);
                        this.conjureFangs(this.mob.getX() + (double) Mth.sin(f) * h, this.mob.getZ() + (double) Mth.cos(f) * h, d, e, f, j);
                        this.conjureFangs(this.mob.getX() + (double) Mth.sin(-f) * h, this.mob.getZ() + (double) Mth.cos(-f) * h, d, e, -f, j);
                    }
                }
            }
        }

        @Override
        public void tick() {
            if (getConjureFangTimer() > 1) {
                setAnimationState(ANIMATION_CONJURE_FANG);

                if (getConjureFangTimer() == 1) {
                    setAnimationState(ANIMATION_IDLE);
                }
            }
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = BlockPos.containing(x, y, z);
            boolean foundGround = false;
            double d = 0.0D;

            do {
                BlockPos blockPos2 = blockPos.below();
                BlockState blockState = this.mob.getCommandSenderWorld().getBlockState(blockPos2);

                if (blockState.isFaceSturdy(this.mob.getCommandSenderWorld(), blockPos2, Direction.UP)) {
                    if (!this.mob.getCommandSenderWorld().isEmptyBlock(blockPos)) {
                        BlockState blockState2 = this.mob.getCommandSenderWorld().getBlockState(blockPos);
                        VoxelShape voxelShape = blockState2.getCollisionShape(this.mob.getCommandSenderWorld(), blockPos);

                        if (!voxelShape.isEmpty()) {
                            d = voxelShape.max(Direction.Axis.Y);
                        }
                    }

                    foundGround = true;
                    break;
                }

                blockPos = blockPos.below();
            } while (blockPos.getY() >= Mth.floor(maxY) - 1);

            if (foundGround) {
                this.mob.getCommandSenderWorld().addFreshEntity(new EvokerFangs(this.mob.getCommandSenderWorld(), x, (double) blockPos.getY() + d, z, yaw, warmup, this.mob));
            }
        }
    }

}
