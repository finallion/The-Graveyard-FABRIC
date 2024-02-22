package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blocks.AltarBlock;
import com.lion.graveyard.entities.ai.goals.LichMeleeGoal;
import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGParticles;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.sounds.BossMusicPlayer;
import com.lion.graveyard.util.MathUtil;;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Predicate;

public class LichEntity extends Monster implements GeoEntity {
    private final ServerBossEvent bossBar;
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    protected static final TargetingConditions HEAD_TARGET_PREDICATE;
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE;
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final UUID ATTACKING_DMG_BOOST_ID = UUID.fromString("120E0DFB-87AE-4653-9776-831010E291A1");
    private static final UUID CRAWL_SPEED_BOOST_ID = UUID.fromString("120E0DFB-87AE-1978-9776-831010E291A2");
    private static final AttributeModifier ATTACKING_SPEED_BOOST;
    private static final AttributeModifier CRAWL_SPEED_BOOST;
    private static final AttributeModifier DMG_BOOST;
    // animation
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation CORPSE_SPELL_ANIMATION = RawAnimation.begin().then("corpse_spell", Animation.LoopType.LOOP);
    private final RawAnimation START_PHASE_2_ANIMATION = RawAnimation.begin().then("phase_two", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation PHASE_2_IDLE_ANIMATION = RawAnimation.begin().then("phase_two_idle", Animation.LoopType.LOOP);
    private final RawAnimation PHASE_2_ATTACK_ANIMATION = RawAnimation.begin().then("phase_two_attack", Animation.LoopType.LOOP);
    private final RawAnimation START_PHASE_3_ANIMATION = RawAnimation.begin().then("phase_three", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation PHASE_3_ATTACK_ANIMATION = RawAnimation.begin().then("crawl", Animation.LoopType.LOOP);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation SHOOT_SKULL_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation SUMMON_ANIMATION = RawAnimation.begin().then("summon", Animation.LoopType.LOOP);
    private final RawAnimation CONJURE_FANG_ANIMATION = RawAnimation.begin().then("corpse_spell", Animation.LoopType.LOOP);
    private final RawAnimation STUNNED_ANIMATION = RawAnimation.begin().then("stunned", Animation.LoopType.LOOP);
    private final RawAnimation CRAWL_IDLE_ANIMATION = RawAnimation.begin().then("crawl_idle", Animation.LoopType.LOOP);
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
    // data tracker
    private static final EntityDataAccessor<Integer> INVUL_TIMER; // spawn invul timer
    private static final EntityDataAccessor<Integer> PHASE_INVUL_TIMER; // other invul timer
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> FIGHT_DURATION_TIMER;
    private static final EntityDataAccessor<Integer> PHASE_TWO_START_ANIM_TIMER; // main phase one to two
    private static final EntityDataAccessor<Integer> PHASE_THREE_START_ANIM_TIMER; // main phase two to three
    private static final EntityDataAccessor<Integer> PHASE; // divides fight into three main phases and two transitions, animations are named after the main phase
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Integer> HUNT_TIMER;
    private static final EntityDataAccessor<Boolean> CAN_HUNT_START;
    private static final EntityDataAccessor<Boolean> CAN_MOVE;
    private static final EntityDataAccessor<Integer> MUSIC_DELAY;

    // spell timer data tracker
    private static final EntityDataAccessor<Integer> CONJURE_FANG_TIMER;
    private static final EntityDataAccessor<Integer> HEAL_DURATION_TIMER;
    private static final EntityDataAccessor<Integer> CORPSE_SPELL_DURATION_TIMER;
    private static final EntityDataAccessor<Integer> LEVITATION_DURATION_TIMER;

    // constants
    private static final byte MUSIC_PLAY_ID = 67;
    private static final byte MUSIC_STOP_ID = 68;
    private static final int SPAWN_INVUL_TIMER = 490;
    private static final int DEFAULT_INVUL_TIMER = 200;
    private final float HEALTH_PHASE_01 = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").healthInCastingPhase;
    private final float HEALTH_PHASE_02 = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").healthInHuntingPhase;
    public final int ATTACK_ANIMATION_DURATION = 40;
    private final int START_PHASE_TWO_ANIMATION_DURATION = 121;
    private final int START_PHASE_THREE_ANIMATION_DURATION = 220;
    private final int START_PHASE_TWO_PARTICLES = 80;
    private final int CORPSE_SPELL_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationFallingCorpseSpell;
    private final int HUNT_COOLDOWN = 600;
    private final int HUNT_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationHuntingPhase;
    private final int HEALING_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationHealingSpell;
    private final int LEVITATION_DURATION = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").durationLevitationSpell;
    protected static final EntityDimensions CRAWL_DIMENSIONS;
    // variables
    private int huntCooldownTicker = 100; // initial cooldown from spawn time until first spell, will be set in goal
    private BlockPos homePos;
    private Direction spawnDirection;
    private int phaseThreeAttackSoundAge = 120;
    private int idleSoundAge = 0;

    public LichEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.bossBar = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true).setCreateWorldFog(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController(this, "controller", 0, event -> {
            if (getAnimationState() == ANIMATION_SPAWN && getInvulnerableTimer() >= 0) {
                event.getController().setAnimation(SPAWN_ANIMATION);
                return PlayState.CONTINUE;
            }

            return PlayState.CONTINUE;
        }));
        animationData.add(new AnimationController(this, "controller2", 0, event -> {
            // set from the respawn method, stops all animations from previous phases
            if (getAnimationState() == ANIMATION_STOP) {
                return PlayState.STOP;
            }

            /* PHASE 1 */
            // takes one tick to get to this method (from mobtick)
            // do not attack when: the spawn invul timer is active, the phase is incorrect or the phase invul timer was set from a spell
            if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAggressive() && !(this.isDeadOrDying() || this.getHealth() < 0.01) && canMeeleAttack() && getAnimationState() != ANIMATION_SPAWN) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2); // disables to call the animation immediately after (seems to be called multiple times per tick, per frame tick?)
                event.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_CORPSE_SPELL && getPhase() == 1) {
                event.getController().setAnimation(CORPSE_SPELL_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_SHOOT_SKULL && getPhase() == 1) {
                event.getController().setAnimation(SHOOT_SKULL_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_SUMMON && getPhase() == 1) {
                event.getController().setAnimation(SUMMON_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_CONJURE_FANG && getPhase() == 1) {
                event.getController().setAnimation(CONJURE_FANG_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getPhase() == 1) {
                if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && getInvulnerableTimer() <= 0 && getAnimationState() != ANIMATION_SHOOT_SKULL) {
                    event.getController().setAnimation(IDLE_ANIMATION);
                    return PlayState.CONTINUE;
                } else if ((getAnimationState() != ANIMATION_SHOOT_SKULL || getAnimationState() != ANIMATION_SUMMON || getAnimationState() != ANIMATION_CONJURE_FANG || getAnimationState() != ANIMATION_CORPSE_SPELL) && getAttackAnimTimer() > 0) {
                    setAnimationState(ANIMATION_MELEE);
                }
            }

            /* TRANSITION PHASE 2 */
            if (getAnimationState() == ANIMATION_START_PHASE_2 && getPhase() == 2) {
                event.getController().setAnimation(START_PHASE_2_ANIMATION);
                return PlayState.CONTINUE;
            }
            /* PHASE 3 */
            if (getAnimationState() == ANIMATION_STUNNED && getPhase() == 3) {
                event.getController().setAnimation(STUNNED_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_PHASE_2_IDLE && getPhase() == 3) {
                event.getController().setAnimation(PHASE_2_IDLE_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_PHASE_2_ATTACK && getPhase() == 3) {
                event.getController().setAnimation(PHASE_2_ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }


            /* PHASE 4 */
            if (getAnimationState() == ANIMATION_START_PHASE_3 && getPhase() == 4) {
                event.getController().setAnimation(START_PHASE_3_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* PHASE 5 */
            if (!event.isMoving() && getPhase() == 5 && !(this.isDeadOrDying() || this.getHealth() < 0.01)) {
                event.getController().setAnimation(CRAWL_IDLE_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_PHASE_3_ATTACK && getPhase() == 5 && !(this.isDeadOrDying() || this.getHealth() < 0.01)) {
                event.getController().setAnimation(PHASE_3_ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* DEATH */
            if (this.isDeadOrDying() || this.getHealth() < 0.01) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }


            /* STOPPERS */
            // stops attack animation from looping
            if (getPhase() == 1) {
                if (getAttackAnimTimer() <= 0 && getInvulnerableTimer() <= 0) {
                    setAnimationState(ANIMATION_IDLE);
                    return PlayState.STOP;
                }

                // stops idle animation from looping
                if (getAttackAnimTimer() > 0 && getAnimationState() == ANIMATION_IDLE) {
                    return PlayState.STOP;
                }
            }


            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

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
        if (!level().isClientSide() && getBossMusic() != null) {
            if (canPlayMusic() && deathTime == 0) {
                this.level().broadcastEntityEvent(this, MUSIC_PLAY_ID);
            } else {
                this.level().broadcastEntityEvent(this, MUSIC_STOP_ID);
            }
        }
        super.tick();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == MUSIC_PLAY_ID && getMusicDelay() == 78) BossMusicPlayer.playBossMusic(this);
        else if (id == MUSIC_STOP_ID) BossMusicPlayer.stopBossMusic(this);
        else super.handleEntityEvent(id);
    }


    /* PARTICLE HANDLING */
    @Override
    public void aiStep() {
        if (homePos == null) {
            homePos = BlockPos.containing(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
        }

        animateTick(this.level(), this.blockPosition(), this.getRandom());

        int phaseTwoTimer = getStartPhaseTwoAnimTimer();
        if (phaseTwoTimer < START_PHASE_TWO_PARTICLES && phaseTwoTimer > (START_PHASE_TWO_PARTICLES / 2)) {
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

        if (getInvulnerableTimer() > 60 && random.nextInt(6) == 0 && getPhase() == 1) {
            MathUtil.createParticleFlare(this.level(), this.getX() - 0.75D, this.getY() - 1.0D + 3.5D - (float) getInvulnerableTimer() / 100, this.getZ() - 0.75D, random.nextInt(300) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, false);
        }

        if (getInvulnerableTimer() > 20 && getPhase() == 1) {
            MathUtil.createParticleCircle(this.level(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 5, ParticleTypes.SOUL_FIRE_FLAME, this.getRandom(), 1.0F);
        }

        Vec3 rotation = this.getViewVector(1.0F);
        if (this.deathTime > 0 && this.deathTime <= 100 && random.nextInt(4) == 0) {
            MathUtil.createParticleFlare(this.level(), this.getX() - 0.75D, this.getY() - 1, this.getZ() - 0.75D, random.nextInt(300) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, false);
        }

        if (getPhaseInvulnerableTimer() > 0 && getInvulnerableTimer() <= 0 && getPhase() == 1) {
            MathUtil.createParticleCircle(this.level(), this.getX(), this.getY() + 1.5D, this.getZ(), 0.0D, 0.0D, 0.0D, 2.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, this.getRandom(), 0.5F);
        }

        if (!canHuntStart() && getPhase() == 3 && getAnimationState() == ANIMATION_STUNNED && random.nextInt(7) == 0) {
            MathUtil.createParticleFlare(this.level(), this.getX() - 0.75D, this.getY() + 2.5D, this.getZ() - 0.75D, random.nextInt(100) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, true);
        }

        if (getHealTimer() > 0 && getPhase() == 1) {
            MathUtil.createParticleSpiral(this.level(), this.getX() + rotation.x * 3.5, this.getY() - 0.5D, this.getZ() + rotation.z * 3.5, 0.0D, 0.0D, 0.0D, 350, ParticleTypes.SOUL_FIRE_FLAME, random);
        }

        if (getHealTimer() == 1 && getPhase() == 1) {
            level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, -1.0F);
            for (int i = 0; i < 20; i++) {
                MathUtil.createParticleSpiral(this.level(), this.getX() + rotation.x * 3.5, this.getY() - 0.5D, this.getZ() + rotation.z * 3.5, random.nextDouble() - random.nextDouble(), random.nextDouble() - random.nextDouble(), random.nextDouble() - random.nextDouble(), 350, ParticleTypes.SOUL_FIRE_FLAME, random);
            }
        }

        if (!this.isAlive() && homePos != null) {
            BlockState altar = level().getBlockState(homePos.below());
            if (altar.getBlock() instanceof AltarBlock) {
                level().setBlock(homePos.below(), altar.setValue(AltarBlock.BLOODY, false), 3);
            }
        }

        if (canHuntStart()) {
            List<Player> playersInRange = getPlayersInRange(30.0D);
            Iterator<Player> iterator = playersInRange.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if ((getHuntTimer() == 0 || getHuntTimer() % 300 == 0) && !player.isCreative() && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                    player.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 2.5F, -5.0F);
                    for (int i = 0; i < 10; i++) {
                        MathUtil.createParticleCircle(this.level(), player.getX(), player.getY(), player.getZ(), 0.0D, 0.01D, 0.0D, 1.0F, TGParticles.GRAVEYARD_SOUL_PARTICLE, this.getRandom(), 0.5F);
                    }
                }
            }
        }

        if (!canHuntStart() && random.nextInt(5) == 0) {
            level().playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 4.0F, -10.0F);
        }

        if (getMusicDelay() < 78) {
            setMusicDelay(getMusicDelay() + 1);
        }

        super.aiStep();
    }

    @Override
    public void baseTick() {
        if (homePos == null) {
            homePos = BlockPos.containing(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
        }

        super.baseTick();
    }

    protected void customServerAiStep() {
        if (homePos == null) {
            homePos = BlockPos.containing(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
        }

        if (idleSoundAge <= 0) {
            playAmbientSound();
            idleSoundAge = 100;
        }
        idleSoundAge--;

        /* SUMMON MOB ATTACK */
        int duration = getFightDurationTimer();
        setFightDurationTimer(duration + 1);
        if (duration > 400) {
            if (duration < Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").ghoulSpawnTimerInFight && duration % 400 == 0 && random.nextBoolean()) {
                summonMob(false);
            } else if (duration >= Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").ghoulSpawnTimerInFight && duration < Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").ghoulSpawnTimerInFight * 2 && duration % 600 == 0 && random.nextBoolean()) {
                summonMob(true);
            }
        }

        /* PHASE 5 FIGHT LOGIC */
        if (getPhase() == 5) {
            if (phaseThreeAttackSoundAge == 120) {
                playStartPhaseThreeAttackSound();
            }

            if (phaseThreeAttackSoundAge == 20) {
                phaseThreeAttackSoundAge = 120;
            } else {
                phaseThreeAttackSoundAge--;
            }

            AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            setCanMove(true);
            setAnimationState(ANIMATION_PHASE_3_ATTACK);
            if (!entityAttributeInstance.hasModifier(CRAWL_SPEED_BOOST)) {
                entityAttributeInstance.addTransientModifier(CRAWL_SPEED_BOOST);
            }
        }
        /* END PHASE 5 FIGHT LOGIC */

        /* PHASE 3 FIGHT LOGIC */
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
                Iterator<Player> iterator = playersInRange.iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    if ((getHuntTimer() == 0 || getHuntTimer() % 300 == 0) && !player.isCreative() && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                        for (int i = 0; i <= 5; i++) {
                            BlockPos targetPos = BlockPos.containing(this.getX() + Mth.nextInt(random, -10, 10), this.getY(), this.getZ() + Mth.nextInt(random, -10, 10));

                            if (level().getBlockState(targetPos).isAir() && level().getBlockState(targetPos.above()).isAir() && level().getBlockState(targetPos.below()).isSolidRender(level(), targetPos.below())) {
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
                setAnimationState(ANIMATION_PHASE_2_ATTACK);

                if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                    entityAttributeInstance.addTransientModifier(ATTACKING_SPEED_BOOST);
                }

                if (!entityAttributeDmgInstance.hasModifier(DMG_BOOST)) {
                    entityAttributeDmgInstance.addTransientModifier(DMG_BOOST);
                }
            }

            // if the hunt runs out, set cooldown
            if (getHuntTimer() == 1 && getHuntCooldownTicker() == 0 && canHuntStart()) {
                this.teleportTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D);
                this.setHuntCooldownTicker(HUNT_COOLDOWN);
                setAnimationState(ANIMATION_STUNNED);
                setHuntStart(false);
                setCanMove(false);
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST.getId());
                entityAttributeDmgInstance.removeModifier(DMG_BOOST.getId());
            }

            if (this.getHuntTimer() > 0) {
                int timer = getHuntTimer() - 1;
                this.setHuntTimer(timer);
            }
        }
        /* END PHASE 3 FIGHT LOGIC */

        /* TRANSITION MAIN PHASE ONE to TWO, == PHASE TWO */
        if (getPhase() == 2) {
            int phaseTwoTimer = getStartPhaseTwoAnimTimer();
            if (getPhaseInvulnerableTimer() == 0) {
                this.teleportTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D);
                setPhaseInvulTimer(START_PHASE_TWO_ANIMATION_DURATION); // invul
            }
            setAnimationState(ANIMATION_START_PHASE_2);
            if (phaseTwoTimer > 0) {
                if (phaseTwoTimer == START_PHASE_TWO_ANIMATION_DURATION) {
                    playStartPhaseTwoSound();
                }
                setStartPhaseTwoAnimTimer(phaseTwoTimer - 1);
            }

            // if the transition animation phase has played advance phase
            if (phaseTwoTimer == 1) {
                setAnimationState(ANIMATION_PHASE_2_IDLE);
                setPhase(getPhase() + 1);
            }
        }
        /* END TRANSITION MAIN PHASE ONE to TWO  */

        /* TRANSITION MAIN PHASE TWO to THREE, == PHASE FOUR */
        if (getPhase() == 4) {
            // config adaption
            if (!Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells && canHuntStart()) {
                AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                AttributeInstance entityAttributeDmgInstance = this.getAttribute(Attributes.ATTACK_DAMAGE);

                this.teleportTo(homePos.getX() + 0.75D, homePos.getY() + 0.5D, homePos.getZ() + 0.75D);
                this.setHuntCooldownTicker(HUNT_COOLDOWN);
                setAnimationState(ANIMATION_STUNNED);
                setHuntStart(false);
                setCanMove(false);
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST.getId());
                entityAttributeDmgInstance.removeModifier(DMG_BOOST.getId());
            }

            int phaseThreeTimer = getStartPhaseThreeAnimTimer();
            if (getPhaseInvulnerableTimer() == 0) {
                setPhaseInvulTimer(START_PHASE_THREE_ANIMATION_DURATION); // invul
            }
            setAnimationState(ANIMATION_START_PHASE_3);
            if (phaseThreeTimer > 0) {
                if (phaseThreeTimer == START_PHASE_THREE_ANIMATION_DURATION) {
                    playStartPhaseThreeSound();
                }
                setStartPhaseThreeAnimTimer(phaseThreeTimer - 1);
            }

            // if the transition animation phase has played advance phase
            if (phaseThreeTimer == 1) {
                setAnimationState(ANIMATION_PHASE_3_ATTACK);
                setPhase(getPhase() + 1);
            }
        }
        /* END TRANSITION MAIN PHASE TWO TO THREE */

        if (this.getPhaseInvulnerableTimer() > 0) {
            int timer = getPhaseInvulnerableTimer() - 1;
            this.setPhaseInvulTimer(timer);
        }

        // during healing phase, damage player and hinder to escape room where they were teleported to
        if (getHealTimer() > 0) {
            List<Player> playersInRange = getPlayersInRange(30.0D);
            Iterator<Player> iterator = playersInRange.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (getHealTimer() == HEALING_DURATION && !player.isCreative()) {
                    player.teleportTo(this.getX(), this.getY() + Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").playerTeleportYOffset, this.getZ());
                }
                if (!player.hasEffect(MobEffects.DIG_SLOWDOWN) && !player.isCreative()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 50, 1));
                }
            }
        }

        /* SPELL TIMER */
        setHealTimer(getHealTimer() - 1);
        setLevitationDurationTimer(getLevitationDurationTimer() - 1);
        setConjureFangTimer(getConjureFangTimer() - 1);
        setCorpseSpellTimer(getCorpseSpellTimer() - 1);

        int i;
        if (this.getInvulnerableTimer() > 0) {
            i = this.getInvulnerableTimer() - 1;

            int timer;
            if (getPhase() == 1) {
                timer = SPAWN_INVUL_TIMER;
            } else {
                timer = DEFAULT_INVUL_TIMER;
            }
            if (this.getInvulnerableTimer() == 1 && getPhase() == 1) {
                setAnimationState(ANIMATION_IDLE);
            }

            this.bossBar.setProgress(1.0F - (float) i / timer);
            this.setInvulTimer(i);
        } else {
            // ATTACK TIMER
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

    private float getMaxHealthPerPhase() {
        if (getPhase() == 1) {
            return HEALTH_PHASE_01;
        } else {
            return HEALTH_PHASE_02;
        }
    }

    public void setCustomName(Component p_31476_) {
        super.setCustomName(p_31476_);
        this.bossBar.setName(this.getDisplayName());
    }

    // called by Vial of Blood item
    public void onSummoned(Direction direction, BlockPos altarPos) {
        this.setAnimationState(ANIMATION_SPAWN);
        applyInvulAndResetBossBar(SPAWN_INVUL_TIMER);
        this.homePos = BlockPos.containing(altarPos.getX() + 0.5D, altarPos.getY(), altarPos.getZ() + 0.5D);
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

        if (this.deathTime == 160 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        if (pose == Pose.CROUCHING) {
            return 2.0F;
        } else {
            return 4.0F;
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (pose == Pose.CROUCHING) {
            setPose(Pose.CROUCHING);
            return CRAWL_DIMENSIONS;
        }
        return super.getDimensions(pose);
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected boolean canRide(Entity p_20339_) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer p_31483_) {
        super.startSeenByPlayer(p_31483_);
        this.bossBar.addPlayer(p_31483_);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
        this.bossBar.removePlayer(p_31488_);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if ((this.getInvulnerableTimer() > 0 || this.getPhaseInvulnerableTimer() > 0) && !source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
                return false;
            } else {
                if (amount > this.getHealth() && getPhase() < 5 && !source.is(DamageTypeTags.BYPASSES_RESISTANCE) && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isMultiphaseFight) {
                    //amount = this.getHealth() - 1;
                    respawn();
                    return false;
                }

                // damage stops healing, set to one to add particle effect
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
        setAnimationState(ANIMATION_STOP);
        this.removeAllEffects();
        applyInvulAndResetBossBar(DEFAULT_INVUL_TIMER);
        setHealth(HEALTH_PHASE_02);
        setAttackAnimTimer(0);
        if (getPhase() == 4 || getPhase() == 5) {
            getDimensions(Pose.CROUCHING);
        }
    }

    public void animateTick(Level world, BlockPos pos, RandomSource random) {
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
        if (!blockState.isSolidRender(world, mutable)) {
            world.addParticle(ParticleTypes.SMOKE, (double) mutable.getX() + random.nextDouble(), (double) mutable.getY() + random.nextDouble(), (double) mutable.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }

    public List<Player> getPlayersInRange(double range) {
        AABB box = (new AABB(this.blockPosition())).inflate(range);
        return level().getEntitiesOfClass(Player.class, box);
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

    private boolean summonMob(boolean hard) {
        if (this.getHuntTimer() <= 1) { // do not summon mobs when lich is hunting
            AABB box = (new AABB(this.blockPosition())).inflate(35.0D);
            List<HostileGraveyardEntity> mobs = level().getEntitiesOfClass(HostileGraveyardEntity.class, box);
            if (mobs.size() >= Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").maxSummonedMobs) {
                return false;
            }

            for (int i = 10; i > 0; i--) {
                BlockPos pos = new BlockPos(this.getBlockX() + Mth.nextInt(random, -15, 15), this.getBlockY(), this.getBlockZ() + Mth.nextInt(random, -15, 15));
                int amount = random.nextInt(Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").maxGroupSizeSummonedMobs) + 1;
                if (level().getBlockState(pos).isAir() && level().getBlockState(pos.above()).isAir() && level().getBlockState(pos.above().above()).isAir() && !level().getBlockState(pos.below()).isAir()) {
                    if (!hard) {
                        for (int ii = 0; ii < amount; ++ii) {
                            RevenantEntity revenant = TGEntities.REVENANT.get().create(level());
                            revenant.moveTo(pos.getX(), pos.getY(), pos.getZ());
                            revenant.setCanBurnInSunlight(false);
                            level().addFreshEntity(revenant);
                        }
                    } else {
                        for (int ii = 0; ii < amount - 1; ++ii) {
                            GhoulEntity ghoul = TGEntities.GHOUL.get().create(level());
                            ghoul.setVariant((byte) 9);
                            ghoul.moveTo(pos.getX(), pos.getY(), pos.getZ());
                            ghoul.setCanBurnInSunlight(false);
                            level().addFreshEntity(ghoul);
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

    ///////////////////////
    /* GETTER AND SETTER */
    ///////////////////////
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

    /* END GETTER AND SETTER */

    ///////////////////////
    /* SOUNDS */
    ///////////////////////
    private void playSpawnSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_SPAWN.get(), SoundSource.HOSTILE, 15.0F, 1.0F);
    }

    public void playAttackSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_MELEE.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    public void playHealSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_CAST_TELEPORT.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playCorpseSpellSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_CORPSE_SPELL.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseTwoSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_PHASE_02.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseThreeSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_PHASE_03.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseThreeAttackSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_PHASE_03_ATTACK.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playDeathSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_DEATH.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    public void playScareSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_SCARE.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playHuntSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_HUNT.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playShootSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_CAST_SKULL.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    private void playLevitationSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_CAST_LEVITATION.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        this.level().playSound(null, this.blockPosition(), TGSounds.LICH_IDLE.get(), SoundSource.HOSTILE, 15.0F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return TGSounds.LICH_HURT.get();
    }

    public SoundEvent getBossMusic() {
        return TGSounds.LICH_THEME_01.get();
    }

    protected boolean canPlayMusic() {
        return !isSilent() /*&& getTarget() instanceof Player*/;
    }

    public boolean canPlayerHearMusic(Player player) {
        return player != null /*&& canAttack(player)*/ && distanceTo(player) < 2500;
    }
    /* SOUNDS END */

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAN_MOVE, false);
        this.entityData.define(INVUL_TIMER, 0);
        this.entityData.define(MUSIC_DELAY, 0);
        this.entityData.define(HUNT_TIMER, 0);
        this.entityData.define(FIGHT_DURATION_TIMER, 0);
        this.entityData.define(HEAL_DURATION_TIMER, 0);
        this.entityData.define(LEVITATION_DURATION_TIMER, 0);
        this.entityData.define(CORPSE_SPELL_DURATION_TIMER, 0);
        this.entityData.define(PHASE_INVUL_TIMER, 0);
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
        this.entityData.define(ATTACK_ANIM_TIMER, 0);
        this.entityData.define(CONJURE_FANG_TIMER, 0);
        this.entityData.define(PHASE_TWO_START_ANIM_TIMER, START_PHASE_TWO_ANIMATION_DURATION);
        this.entityData.define(PHASE_THREE_START_ANIM_TIMER, START_PHASE_THREE_ANIMATION_DURATION);
        this.entityData.define(PHASE, 1);
        this.entityData.define(CAN_HUNT_START, false);
    }

    // on game stop
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
        super.addAdditionalSaveData(nbt);
    }


    // on game load
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (!nbt.contains("Invul")) { // if tag is empty, set to default phase 1
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
        }

        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }

        super.readAdditionalSaveData(nbt);
    }

    static {
        INVUL_TIMER = SynchedEntityData.defineId(LichEntity.class, EntityDataSerializers.INT);
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
        CRAWL_DIMENSIONS = EntityDimensions.fixed(1.8F, 2.0F);
        CRAWL_SPEED_BOOST = new AttributeModifier(CRAWL_SPEED_BOOST_ID, "Crawl speed boost", 0.18D, AttributeModifier.Operation.ADDITION);
        ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").speedInHuntPhase, AttributeModifier.Operation.ADDITION);
        DMG_BOOST = new AttributeModifier(ATTACKING_DMG_BOOST_ID, "Damage speed boost", Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").damageHuntingPhaseAddition, AttributeModifier.Operation.ADDITION);
    }

    public class SummonFallenCorpsesGoal extends Goal {
        protected final LichEntity lich;
        private final int FALL_HEIGHT = 10;
        private final int SQUARE_SIZE = 30;
        private final int CORPSE_SPAWN_RARITY_PLAYER = 9;
        private BlockPos pos;
        private List<Player> list;
        private List<BlockPos> positions = new ArrayList<>();

        public SummonFallenCorpsesGoal(LichEntity lich) {
            this.lich = lich;
        }

        @Override
        public boolean canUse() {
            return getCorpseSpellTimer() <= -Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").cooldownCorpseSpell // cooldown
                    && random.nextInt(75) == 0
                    && canSpellCast();
        }

        @Override
        public void start() {
            // make invulnerable during spell, prevents lich melee attack
            if (Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isInvulnerableDuringSpells) {
                setPhaseInvulTimer(CORPSE_SPELL_DURATION);
            }
            setCorpseSpellTimer(CORPSE_SPELL_DURATION);
            playCorpseSpellSound();
            this.lich.teleportTo(this.lich.homePos.getX() + 0.5D, this.lich.homePos.getY() + 0.5D, this.lich.homePos.getZ() + 0.5D);

            pos = this.lich.blockPosition();
            list = getPlayersInRange(35.0D);

            for (int i = -SQUARE_SIZE; i < SQUARE_SIZE; i++) {
                for (int ii = -SQUARE_SIZE; ii < SQUARE_SIZE; ii++) {
                    BlockPos position = this.lich.homePos.below().below().offset(i, 0, ii);
                    if (level().getBlockState(position).isSolidRender(level(), position)) {
                        positions.add(pos.offset(i, FALL_HEIGHT, ii));
                    }
                }
            }

            super.start();
        }

        @Override
        public void stop() {
            setAnimationState(ANIMATION_IDLE);
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getCorpseSpellTimer() > 0) {
                return true;
            }
            return super.canContinueToUse();
        }

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

            ServerLevel serverWorld = (ServerLevel) LichEntity.this.level();
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

        public boolean canUse() {
            return this.mob.getTarget() != null && canSpellCast();
        }

        public void start() {
            this.cooldown = 0;
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            setAnimationState(ANIMATION_IDLE);
            super.stop();
        }

        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                if (livingEntity.distanceToSqr(this.mob) < 8000.0D && livingEntity.distanceToSqr(this.mob) > 16.0D) {
                    Level world = this.mob.level();
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
                        skull.moveTo(this.mob.getX() - vec3d3.x * 0.5, this.mob.getY(0.5D) + 1.25D, this.mob.getZ() - vec3d3.z * 0.5D);
                        world.addFreshEntity(skull);

                        int amount = random.nextInt(Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").maxAmountSkullsInShootSkullSpell) + 2;
                        for (int i = 0; i < amount; ++i) {
                            SkullEntity devSkull = new SkullEntity(this.mob.level(), this.mob, this.mob.getRandom().triangle(e, 2.297D * h), f, this.mob.getRandom().triangle(g, 2.297D * h));
                            devSkull.moveTo(this.mob.getX() - vec3d3.x * 0.5, this.mob.getY(0.5D) + 1.25D, this.mob.getZ() - vec3d3.z * 0.5D);
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

        public boolean canUse() {
            return this.mob.getHealth() <= 300.0F
                    && random.nextInt(40) == 0
                    && this.mob.getHealTimer() <= -Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").cooldownTeleportPlayerAndHeal
                    && canSpellCast();
        }

        public void start() {
            setHealTimer(HEALING_DURATION);
            this.mob.teleportTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D);
            playHealSound();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return super.requiresUpdateEveryTick();
        }

        @Override
        public void stop() {
            setAnimationState(ANIMATION_IDLE);
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getHealTimer() > 0) {
                return true;
            }
            return super.canContinueToUse();
        }

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

        public boolean canUse() {
            return canSpellCast()
                    && random.nextInt(125) == 0
                    && this.mob.getLevitationDurationTimer() <= -Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").cooldownLevitationSpell; // cooldown
        }

        public void start() {
            setLevitationDurationTimer(LEVITATION_DURATION);

            List<Player> player = getPlayersInRange(30.0D);
            Iterator<Player> it = player.iterator();
            Player playerEntity;

            while (it.hasNext()) {
                playerEntity = (Player) it.next();
                if (!playerEntity.isCreative()) {
                    playerEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, 1));
                }
            }

            playLevitationSound();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return super.requiresUpdateEveryTick();
        }


        @Override
        public void stop() {
            setAnimationState(ANIMATION_IDLE);
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getLevitationDurationTimer() > 0) {
                return true;
            }
            return super.canContinueToUse();
        }

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
            return canSpellCast() && random.nextInt(25) == 0 && this.mob.getConjureFangTimer() <= -100; // cooldown
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            setAnimationState(ANIMATION_IDLE);
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (getConjureFangTimer() > 0) {
                return true;
            }
            return super.canContinueToUse();
        }

        public void start() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
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
            boolean bl = false;
            double d = 0.0D;

            do {
                BlockPos blockPos2 = blockPos.below();
                BlockState blockState = this.mob.level().getBlockState(blockPos2);
                if (blockState.isFaceSturdy(this.mob.level(), blockPos2, Direction.UP)) {
                    if (!this.mob.level().isEmptyBlock(blockPos)) {
                        BlockState blockState2 = this.mob.level().getBlockState(blockPos);
                        VoxelShape voxelShape = blockState2.getCollisionShape(this.mob.level(), blockPos);
                        if (!voxelShape.isEmpty()) {
                            d = voxelShape.max(Direction.Axis.Y);
                        }
                    }

                    bl = true;
                    break;
                }

                blockPos = blockPos.below();
            } while (blockPos.getY() >= Mth.floor(maxY) - 1);

            if (bl) {
                this.mob.level().addFreshEntity(new EvokerFangs(this.mob.level(), x, (double) blockPos.getY() + d, z, yaw, warmup, this.mob));
            }
        }
    }

}
