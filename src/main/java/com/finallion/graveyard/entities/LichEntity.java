package com.finallion.graveyard.entities;

import com.finallion.graveyard.blocks.AltarBlock;
import com.finallion.graveyard.entities.ai.goals.LichMeleeGoal;
import com.finallion.graveyard.entities.projectiles.SkullEntity;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGParticles;
import com.finallion.graveyard.init.TGSounds;
import com.finallion.graveyard.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;
import java.util.function.Predicate;

public class LichEntity extends HostileEntity implements IAnimatable {
    private final ServerBossBar bossBar;
    private AnimationFactory factory = new AnimationFactory(this);
    protected static final TargetPredicate HEAD_TARGET_PREDICATE;
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE;
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final UUID ATTACKING_DMG_BOOST_ID = UUID.fromString("120E0DFB-87AE-4653-9776-831010E291A1");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST;
    private static final EntityAttributeModifier CRAWL_SPEED_BOOST;
    private static final EntityAttributeModifier DMG_BOOST;
    // animation
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    private final AnimationBuilder CORPSE_SPELL_ANIMATION = new AnimationBuilder().addAnimation("corpse_spell", true);
    private final AnimationBuilder START_PHASE_2_ANIMATION = new AnimationBuilder().addAnimation("phase_two", false);
    private final AnimationBuilder PHASE_2_IDLE_ANIMATION = new AnimationBuilder().addAnimation("phase_two_idle", true);
    private final AnimationBuilder PHASE_2_ATTACK_ANIMATION = new AnimationBuilder().addAnimation("phase_two_attack", true);
    private final AnimationBuilder START_PHASE_3_ANIMATION = new AnimationBuilder().addAnimation("phase_three", false);
    private final AnimationBuilder PHASE_3_ATTACK_ANIMATION = new AnimationBuilder().addAnimation("crawl", true);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder SHOOT_SKULL_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    private final AnimationBuilder SUMMON_ANIMATION = new AnimationBuilder().addAnimation("summon", true);
    private final AnimationBuilder CONJURE_FANG_ANIMATION = new AnimationBuilder().addAnimation("corpse_spell", true);
    private final AnimationBuilder STUNNED_ANIMATION = new AnimationBuilder().addAnimation("stunned", true);
    private final AnimationBuilder CRAWL_IDLE_ANIMATION = new AnimationBuilder().addAnimation("crawl_idle", true);
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
    private static final TrackedData<Integer> INVUL_TIMER; // spawn invul timer
    private static final TrackedData<Integer> PHASE_INVUL_TIMER; // other invul timer
    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> FIGHT_DURATION_TIMER;
    private static final TrackedData<Integer> PHASE_TWO_START_ANIM_TIMER; // main phase one to two
    private static final TrackedData<Integer> PHASE_THREE_START_ANIM_TIMER; // main phase two to three
    private static final TrackedData<Integer> PHASE; // divides fight into three main phases and two transitions, animations are named after the main phase
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Boolean> CAN_HUNT_START;
    private static final TrackedData<Boolean> CAN_MOVE;

    // spell timer data tracker
    private static final TrackedData<Integer> CONJURE_FANG_TIMER;
    private static final TrackedData<Integer> HEAL_DURATION_TIMER;
    private static final TrackedData<Integer> CORPSE_SPELL_DURATION_TIMER;
    private static final TrackedData<Integer> LEVITATION_DURATION_TIMER;

    // constants
    private static final int SPAWN_INVUL_TIMER = 420;
    private static final int DEFAULT_INVUL_TIMER = 200;
    private final float HEALTH_PHASE_01 = 400.0F;
    private final float HEALTH_PHASE_02 = 200.0F;
    public final int ATTACK_ANIMATION_DURATION = 40;
    private final int START_PHASE_TWO_ANIMATION_DURATION = 121;
    private final int START_PHASE_THREE_ANIMATION_DURATION = 220;
    private final int START_PHASE_TWO_PARTICLES = 80;
    private final int CORPSE_SPELL_DURATION = 400;
    private final int HUNT_COOLDOWN = 600;
    private final int HUNT_DURATION = 800;
    protected static final EntityDimensions CRAWL_DIMENSIONS;
    // variables
    private int huntCooldownTicker = 100; // initial cooldown from spawn time until first spell, will be set in goal
    private BlockPos homePos;
    private Direction spawnDirection;
    private int phaseThreeAttackSoundAge = 120;
    private int idleSoundAge = 0;

    public LichEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar) (new ServerBossBar(this.getDisplayName(), BossBar.Color.WHITE, BossBar.Style.PROGRESS)).setDarkenSky(true).setThickenFog(true);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (getAnimationState() == ANIMATION_SPAWN && getInvulnerableTimer() >= 0) {
            event.getController().setAnimation(SPAWN_ANIMATION);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    // animation handler
    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {

        // set from the respawn method, stops all animations from previous phases
        if (getAnimationState() == ANIMATION_STOP) {
            return PlayState.STOP;
        }

        /* PHASE 1 */
        // takes one tick to get to this method (from mobtick)
        // do not attack when: the spawn invul timer is active, the phase is incorrect or the phase invul timer was set from a spell
        if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01) && canMeeleAttack()) {
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
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && getInvulnerableTimer() <= 0) {
                event.getController().setAnimation(IDLE_ANIMATION);
                return PlayState.CONTINUE;
            } else if (getAnimationState() != ANIMATION_SHOOT_SKULL || getAnimationState() != ANIMATION_SUMMON || getAnimationState() != ANIMATION_CONJURE_FANG || getAnimationState() != ANIMATION_CORPSE_SPELL) {
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
        if (!event.isMoving() && getPhase() == 5 && !(this.isDead() || this.getHealth() < 0.01)) {
            event.getController().setAnimation(CRAWL_IDLE_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (getAnimationState() == ANIMATION_PHASE_3_ATTACK && getPhase() == 5 && !(this.isDead() || this.getHealth() < 0.01)) {
            event.getController().setAnimation(PHASE_3_ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }

        /* DEATH */
        if (this.isDead() || this.getHealth() < 0.01) {
            event.getController().setAnimation(DEATH_ANIMATION);
            return PlayState.CONTINUE;
        }


        /* STOPPERS */
        // stops attack animation from looping
        if (getPhase() == 1) {
            if (getAttackAnimTimer() <= 0) {
                setAnimationState(ANIMATION_IDLE);
                return PlayState.STOP;
            }

            // stops idle animation from looping
            if (getAttackAnimTimer() > 0 && getAnimationState() == ANIMATION_IDLE) {
                return PlayState.STOP;
            }
        }


        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
        animationData.addAnimationController(new AnimationController(this, "controller2", 0, this::predicate2));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(2, new LichMeleeGoal(this, 1.0D, false));
        this.goalSelector.add(4, new SummonFallenCorpsesGoal(this));
        this.goalSelector.add(5, new ConjureFangsGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new ShootSkullGoal(this));
        this.goalSelector.add(8, new TeleportAndHealGoal(this));
        this.goalSelector.add(9, new LevitationGoal(this));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    public static DefaultAttributeContainer.Builder createLichAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 22.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 18.0D)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 14.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 5.0D);
    }


    /* PARTICLE HANDLING */
    @Override
    public void tickMovement() {
        randomDisplayTick(this.getWorld(), this.getBlockPos(), this.getRandom());

        int phaseTwoTimer = getStartPhaseTwoAnimTimer();
        if (phaseTwoTimer < START_PHASE_TWO_PARTICLES && phaseTwoTimer > (START_PHASE_TWO_PARTICLES / 2)) {
            float offset = 0.0F;
            for (int i = 0; i < 25; i++) {
                if (i < 7) {
                    offset += 0.15F;
                } else if (i > 12) {
                    offset -= 0.15F;
                }
                MathUtil.createParticleDisk(this.getWorld(), this.getX(), this.getY() + ((float) i / 10), this.getZ(), 0.0D, 0.3D, 0.0D, 2 * offset, ParticleTypes.SOUL_FIRE_FLAME, this.getRandom());
            }
        }

        if (getInvulnerableTimer() > 80 && random.nextInt(6) == 0 && getPhase() == 1) {
            MathUtil.createParticleFlare(this.getWorld(), this.getX() - 1.0D, this.getY() - 1.0D + 3.5D - (float) getInvulnerableTimer() / 100, this.getZ() - 1.0D, random.nextInt(300) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, false);
        }

        if (getInvulnerableTimer() > 80 && getPhase() == 1) {
            MathUtil.createParticleCircle(this.getWorld(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 5, ParticleTypes.SOUL_FIRE_FLAME, this.getRandom(), 1.0F);
        }

        if (this.deathTime > 0 && this.deathTime <= 100 && random.nextInt(4) == 0) {
            MathUtil.createParticleFlare(this.getWorld(), this.getX() - 1.0D, this.getY() - 1, this.getZ() - 1.0D, random.nextInt(300) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, false);
        }

        if (getPhaseInvulnerableTimer() > 0 && getInvulnerableTimer() <= 0 && getPhase() == 1) {
            MathUtil.createParticleCircle(this.getWorld(), this.getX(), this.getY() + 1.5D, this.getZ(), 0.0D, 0.0D, 0.0D, 2.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, this.getRandom(), 0.5F);
        }

        if (!canHuntStart() && getPhase() == 3 && getAnimationState() == ANIMATION_STUNNED && random.nextInt(7) == 0) {
            MathUtil.createParticleFlare(this.getWorld(), this.getX() - 0.75D, this.getY() + 2.5D, this.getZ() - 0.75D, random.nextInt(100) + 150, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME, random, true);
        }

        Vec3d rotation = this.getRotationVec(1.0F);
        if (getHealTimer() > 0 && getPhase() == 1) {
            MathUtil.createParticleSpiral(this.getWorld(), this.getX() + rotation.x * 3.5, this.getY() - 0.5D, this.getZ() + rotation.z * 3.5, 0.0D, 0.0D, 0.0D, 350, ParticleTypes.SOUL_FIRE_FLAME, random);
        }

        if (getHealTimer() == 1 && getPhase() == 1) {
            getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 3.0F, -1.0F);
            for (int i = 0; i < 20; i++) {
                MathUtil.createParticleSpiral(this.getWorld(), this.getX() + rotation.x * 3.5, this.getY() - 0.5D, this.getZ() + rotation.z * 3.5, random.nextDouble() - random.nextDouble(), random.nextDouble() - random.nextDouble(), random.nextDouble() - random.nextDouble(), 350, ParticleTypes.SOUL_FIRE_FLAME, random);
            }
        }

        if (!this.isAlive() && homePos != null) {
            BlockState altar = getWorld().getBlockState(homePos.down());
            if (altar.getBlock() instanceof AltarBlock) {
                getWorld().setBlockState(homePos.down(), altar.with(AltarBlock.BLOODY, false), 3);
            }
        }

        super.tickMovement();
    }

    protected void mobTick() {
        if (homePos == null) {
            homePos = new BlockPos(this.getBlockX() + 0.5D, this.getY(), this.getBlockZ() + 0.5D);
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
            if (duration < 6000 && duration % 400 == 0) {
                summonMob(false);
            } else if (duration >= 6000 && duration < 10000 && duration % 600 == 0) {
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

            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            setCanMove(true);
            setAnimationState(ANIMATION_PHASE_3_ATTACK);
            if (!entityAttributeInstance.hasModifier(CRAWL_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(CRAWL_SPEED_BOOST);
            }
        }
        /* END PHASE 5 FIGHT LOGIC */

        /* PHASE 3 FIGHT LOGIC */
        if (getPhase() == 3) {
            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            EntityAttributeInstance entityAttributeDmgInstance = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);

            if (getHuntCooldownTicker() > 0) {
                setHuntCooldownTicker(getHuntCooldownTicker() - 1);
            } else {
                setHuntStart(true);
            }

            if (canHuntStart()) {
                List<PlayerEntity> playersInRange = getPlayersInRange(30.0D);
                Iterator<PlayerEntity> iterator = playersInRange.iterator();
                while (iterator.hasNext()) {
                    PlayerEntity player = iterator.next();
                    if (getPhaseInvulnerableTimer() == 0 || getPhaseInvulnerableTimer() % 300 == 0) {
                        for (int i = 0; i <= 3; i++) {
                            BlockPos targetPos = new BlockPos(this.getX() + random.nextInt(15) + 3.0D, this.getY(), this.getZ() + random.nextInt(15) + 3.0D);
                            if (world.getBlockState(targetPos).isAir() && world.getBlockState(targetPos.up()).isAir()) {
                                player.teleport(this.getX() + random.nextInt(15) + 3.0D, this.getY(), this.getZ() + random.nextInt(15) + 3.0D);
                                break;
                            }
                        }
                    }
                    if (getPhaseInvulnerableTimer() % 50 == 0) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 80, 2));
                    }
                }

                setCanMove(true);
                if (getPhaseInvulnerableTimer() == 0) {
                    playHuntSound();
                    this.setPhaseInvulTimer(HUNT_DURATION); // acts as hunt duration counter
                }

                if (getPhaseInvulnerableTimer() == 400) {
                    playHuntSound();
                }
                setAnimationState(ANIMATION_PHASE_2_ATTACK);

                if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                    entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
                }

                if (!entityAttributeDmgInstance.hasModifier(DMG_BOOST)) {
                    entityAttributeDmgInstance.addTemporaryModifier(DMG_BOOST);
                }
            }

            // if the invul (= hunt duration) runs out, set cooldown
            if (getPhaseInvulnerableTimer() == 1 && getHuntCooldownTicker() == 0 && canHuntStart()) {
                this.teleport(homePos.getX() + 0.5D, homePos.getY() + 1.1D, homePos.getZ() + 0.5D);
                this.setHuntCooldownTicker(HUNT_COOLDOWN);
                setAnimationState(ANIMATION_STUNNED);
                setHuntStart(false);
                setCanMove(false);
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
                entityAttributeDmgInstance.removeModifier(DMG_BOOST);
            }
        }
        /* END PHASE 3 FIGHT LOGIC */

        /* TRANSITION MAIN PHASE ONE to TWO, == PHASE TWO */
        if (getPhase() == 2) {
            int phaseTwoTimer = getStartPhaseTwoAnimTimer();
            if (getPhaseInvulnerableTimer() == 0) {
                this.teleport(homePos.getX() + 0.5D, homePos.getY() + 1.1D, homePos.getZ() + 0.5D);
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
            List<PlayerEntity> playersInRange = getPlayersInRange(30.0D);
            Iterator<PlayerEntity> iterator = playersInRange.iterator();
            while (iterator.hasNext()) {
                PlayerEntity player = iterator.next();
                if (getHealTimer() == 400) {
                    player.teleport(this.getX(), this.getY() - 15.0D, this.getZ());
                }
                if (!player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 1));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 50, 1));
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

            this.bossBar.setPercent(1.0F - (float) i / timer);
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

            super.mobTick();

            this.bossBar.setPercent(this.getHealth() / this.getMaxHealthPerPhase());
        }
    }

    private float getMaxHealthPerPhase() {
        if (getPhase() == 1) {
            return HEALTH_PHASE_01;
        } else {
            return HEALTH_PHASE_02;
        }
    }


    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    // called by Vial of Blood item
    public void onSummoned(Direction direction, BlockPos altarPos) {
        this.setAnimationState(ANIMATION_SPAWN);
        applyInvulAndResetBossBar(SPAWN_INVUL_TIMER);
        this.homePos = new BlockPos(altarPos.getX() + 0.5D, altarPos.getY(), altarPos.getZ() + 0.5D);
        this.spawnDirection = direction;
        playSpawnSound();
    }

    private void applyInvulAndResetBossBar(int invul) {
        this.setInvulTimer(invul);
        this.bossBar.setPercent(0.0F);
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;

        if (this.deathTime == 5) {
            playDeathSound();
        }

        if (this.deathTime == 160 && !this.world.isClient()) {
            this.world.sendEntityStatus(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        if (pose == EntityPose.CROUCHING) {
            return 2.0F;
        } else {
            return 4.0F;
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (pose == EntityPose.CROUCHING) {
            setPose(EntityPose.CROUCHING);
            return CRAWL_DIMENSIONS;
        }
        return super.getDimensions(pose);
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    public boolean canUsePortals() {
        return false;
    }

    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if ((this.getInvulnerableTimer() > 0 || this.getPhaseInvulnerableTimer() > 0) && source != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (amount > this.getHealth() && getPhase() < 5 && source != DamageSource.OUT_OF_WORLD) {
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


                return super.damage(source, amount);
            }
        }
    }

    private void respawn() {
        this.setPhase(getPhase() + 1);
        setAnimationState(ANIMATION_STOP);
        this.clearStatusEffects();
        applyInvulAndResetBossBar(DEFAULT_INVUL_TIMER);
        setHealth(HEALTH_PHASE_02);
        setAttackAnimTimer(0);
        if (getPhase() == 4 || getPhase() == 5) {
            getDimensions(EntityPose.CROUCHING);
        }
    }

    public void randomDisplayTick(World world, BlockPos pos, Random random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        double d = (double) i + random.nextInt(3) - random.nextInt(3);
        double e = (double) j + 4.2D;
        double f = (double) k + random.nextInt(3) - random.nextInt(3);
        world.addParticle(ParticleTypes.ASH, d, e, f, 0.0D, 0.0D, 0.0D);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        mutable.set(i + MathHelper.nextInt(random, -17, 17), j + random.nextInt(10), k + MathHelper.nextInt(random, -17, 17));
        BlockState blockState = world.getBlockState(mutable);
        if (!blockState.isFullCube(world, mutable)) {
            world.addParticle(ParticleTypes.SMOKE, (double) mutable.getX() + random.nextDouble(), (double) mutable.getY() + random.nextDouble(), (double) mutable.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

    }

    public List<PlayerEntity> getPlayersInRange(double range) {
        Box box = (new Box(this.getBlockPos())).expand(range);
        return world.getNonSpectatingEntities(PlayerEntity.class, box);
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
    public boolean isImmuneToExplosion() {
        return true;
    }

    private boolean summonMob(boolean hard) {
        Box box = (new Box(this.getBlockPos())).expand(35.0D);
        List<HostileGraveyardEntity> mobs = getWorld().getNonSpectatingEntities(HostileGraveyardEntity.class, box);
        if (mobs.size() > 30) {
            return false;
        }

        for (int i = 10; i > 0; i--) {
            BlockPos pos = new BlockPos(this.getBlockX() + random.nextInt(15) + 5, this.getBlockY(), this.getBlockZ() + random.nextInt(15) + 5);
            int amount = random.nextInt(5) + 1;
            if (world.getBlockState(pos).isAir() && world.getBlockState(pos.up()).isAir() && world.getBlockState(pos.up().up()).isAir()) {
                if (!hard) {
                    for (int ii = 0; ii < amount; ++ii) {
                        RevenantEntity revenant = TGEntities.REVENANT.create(world);
                        revenant.setPosition(pos.getX(), pos.getY(), pos.getZ());
                        world.spawnEntity(revenant);
                    }
                } else {
                    for (int ii = 0; ii < amount - 1; ++ii) {
                        GhoulEntity ghoul = TGEntities.GHOUL.create(world);
                        ghoul.setVariant((byte) 10);
                        ghoul.setPosition(pos.getX(), pos.getY(), pos.getZ());
                        world.spawnEntity(ghoul);
                    }
                }
                return true;

            }
        }
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    ///////////////////////
    /* GETTER AND SETTER */
    ///////////////////////
    public boolean canHuntStart() {
        return this.dataTracker.get(CAN_HUNT_START);
    }

    public void setHuntStart(boolean bool) {
        this.dataTracker.set(CAN_HUNT_START, bool);
    }

    public void setCanMove(boolean bool) {
        this.dataTracker.set(CAN_MOVE, bool);
    }

    public boolean canMove() {
        return this.dataTracker.get(CAN_MOVE);
    }

    public int getInvulnerableTimer() {
        return (Integer) this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    public int getFightDurationTimer() {
        return (Integer) this.dataTracker.get(FIGHT_DURATION_TIMER);
    }

    public void setFightDurationTimer(int ticks) {
        this.dataTracker.set(FIGHT_DURATION_TIMER, ticks);
    }

    public int getLevitationDurationTimer() {
        return (Integer) this.dataTracker.get(LEVITATION_DURATION_TIMER);
    }

    public void setLevitationDurationTimer(int ticks) {
        this.dataTracker.set(LEVITATION_DURATION_TIMER, ticks);
    }

    public int getConjureFangTimer() {
        return (Integer) this.dataTracker.get(CONJURE_FANG_TIMER);
    }

    public void setConjureFangTimer(int ticks) {
        this.dataTracker.set(CONJURE_FANG_TIMER, ticks);
    }

    public int getCorpseSpellTimer() {
        return (Integer) this.dataTracker.get(CORPSE_SPELL_DURATION_TIMER);
    }

    public void setCorpseSpellTimer(int ticks) {
        this.dataTracker.set(CORPSE_SPELL_DURATION_TIMER, ticks);
    }

    public int getHealTimer() {
        return (Integer) this.dataTracker.get(HEAL_DURATION_TIMER);
    }

    public void setHealTimer(int ticks) {
        this.dataTracker.set(HEAL_DURATION_TIMER, ticks);
    }

    public int getPhaseInvulnerableTimer() {
        return (Integer) this.dataTracker.get(PHASE_INVUL_TIMER);
    }

    public void setPhaseInvulTimer(int ticks) {
        this.dataTracker.set(PHASE_INVUL_TIMER, ticks);
    }

    public int getAnimationState() {
        return this.dataTracker.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.dataTracker.set(ANIMATION, state);
    }

    public int getPhase() {
        return (Integer) this.dataTracker.get(PHASE);
    }

    public void setPhase(int phase) {
        this.dataTracker.set(PHASE, phase);
    }

    public int getStartPhaseTwoAnimTimer() {
        return (Integer) this.dataTracker.get(PHASE_TWO_START_ANIM_TIMER);
    }

    public void setStartPhaseTwoAnimTimer(int startPhaseTwoAnimTimer) {
        this.dataTracker.set(PHASE_TWO_START_ANIM_TIMER, startPhaseTwoAnimTimer);
    }

    public int getStartPhaseThreeAnimTimer() {
        return (Integer) this.dataTracker.get(PHASE_THREE_START_ANIM_TIMER);
    }

    public void setStartPhaseThreeAnimTimer(int startPhaseThreeAnimTimer) {
        this.dataTracker.set(PHASE_THREE_START_ANIM_TIMER, startPhaseThreeAnimTimer);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.dataTracker.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.dataTracker.set(ATTACK_ANIM_TIMER, time);
    }

    public int getHuntCooldownTicker() {
        return huntCooldownTicker;
    }

    public void setHuntCooldownTicker(int huntCooldownTicker) {
        this.huntCooldownTicker = huntCooldownTicker;
    }

    /* END GETTER AND SETTER */

    ///////////////////////
    /* SOUNDS */
    ///////////////////////
    private void playSpawnSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_SPAWN, SoundCategory.HOSTILE, 15.0F, 1.0F);
    }

    public void playAttackSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_MELEE, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    public void playHealSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_CAST_TELEPORT, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playCorpseSpellSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_CORPSE_SPELL, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseTwoSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_PHASE_02, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseThreeSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_PHASE_03, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseThreeAttackSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_PHASE_03_ATTACK, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playDeathSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_DEATH, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    public void playScareSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_SCARE, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playHuntSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_HUNT, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playShootSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_CAST_SKULL, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playLevitationSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_CAST_LEVITATION, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_IDLE, SoundCategory.HOSTILE, 15.0F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return TGSounds.LICH_HURT;
    }
    /* SOUNDS END */

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CAN_MOVE, false);
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(FIGHT_DURATION_TIMER, 0);
        this.dataTracker.startTracking(HEAL_DURATION_TIMER, 0);
        this.dataTracker.startTracking(LEVITATION_DURATION_TIMER, 0);
        this.dataTracker.startTracking(CORPSE_SPELL_DURATION_TIMER, 0);
        this.dataTracker.startTracking(PHASE_INVUL_TIMER, 0);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(CONJURE_FANG_TIMER, 0);
        this.dataTracker.startTracking(PHASE_TWO_START_ANIM_TIMER, START_PHASE_TWO_ANIMATION_DURATION);
        this.dataTracker.startTracking(PHASE_THREE_START_ANIM_TIMER, START_PHASE_THREE_ANIMATION_DURATION);
        this.dataTracker.startTracking(PHASE, 1);
        this.dataTracker.startTracking(CAN_HUNT_START, false);
    }

    // on game stop
    public void writeCustomDataToNbt(NbtCompound nbt) {
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
        super.writeCustomDataToNbt(nbt);
    }


    // on game load
    public void readCustomDataFromNbt(NbtCompound nbt) {
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

        super.readCustomDataFromNbt(nbt);
    }

    static {
        INVUL_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE_INVUL_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ATTACK_ANIM_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE_TWO_START_ANIM_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE_THREE_START_ANIM_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        FIGHT_DURATION_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HEAL_DURATION_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CONJURE_FANG_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CORPSE_SPELL_DURATION_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LEVITATION_DURATION_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CAN_HUNT_START = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_MOVE = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_ATTACK_PREDICATE = Entity::isPlayer;
        HEAD_TARGET_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(20.0D).setPredicate(CAN_ATTACK_PREDICATE);
        CRAWL_DIMENSIONS = EntityDimensions.fixed(1.8F, 2.0F);
        CRAWL_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.18D, EntityAttributeModifier.Operation.ADDITION);
        ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15D, EntityAttributeModifier.Operation.ADDITION);
        DMG_BOOST = new EntityAttributeModifier(ATTACKING_DMG_BOOST_ID, "Damage speed boost", 40.0D, EntityAttributeModifier.Operation.ADDITION);
    }

    public class SummonFallenCorpsesGoal extends Goal {
        protected final LichEntity lich;
        private final int FALL_HEIGHT = 15;
        private final int SQUARE_SIZE = 20;
        private final int CORPSE_SPAWN_RARITY_PLAYER = 9;
        private BlockPos pos;
        private List<PlayerEntity> list;
        private List<BlockPos> positions = new ArrayList<>();

        public SummonFallenCorpsesGoal(LichEntity lich) {
            this.lich = lich;
        }

        @Override
        public boolean canStart() {
            return getCorpseSpellTimer() <= -400 // cooldown
                    && random.nextInt(75) == 0
                    && canSpellCast();
        }

        @Override
        public void start() {
            // make invulnerable during spell, prevents lich melee attack
            setPhaseInvulTimer(CORPSE_SPELL_DURATION);
            setCorpseSpellTimer(400);
            playCorpseSpellSound();
            this.lich.teleport(this.lich.homePos.getX() + 0.5D, this.lich.homePos.getY() + 1.1D, this.lich.homePos.getZ() + 0.5D);

            pos = this.lich.getBlockPos();
            list = getPlayersInRange(35.0D);

            for (int i = -SQUARE_SIZE; i < SQUARE_SIZE; i++) {
                for (int ii = -SQUARE_SIZE; ii < SQUARE_SIZE; ii++) {
                    positions.add(pos.add(i, FALL_HEIGHT, ii));
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
        public boolean shouldContinue() {
            if (getCorpseSpellTimer() > 0) {
                return true;
            }
            return super.shouldContinue();
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

            ServerWorld serverWorld = (ServerWorld) LichEntity.this.world;

            FallingCorpse corpse = (FallingCorpse) TGEntities.FALLING_CORPSE.create(serverWorld);
            BlockPos blockPos = positions.get(random.nextInt(positions.size()));
            corpse.setPos((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.55D, (double) blockPos.getZ() + 0.5D);
            serverWorld.spawnEntity(corpse);

            if (random.nextInt(CORPSE_SPAWN_RARITY_PLAYER) == 0 && list.size() > 0) {
                FallingCorpse corpse2 = (FallingCorpse) TGEntities.FALLING_CORPSE.create(serverWorld);
                PlayerEntity target = list.get(random.nextInt(list.size()));
                if (target != null) {
                    BlockPos blockPos2 = target.getBlockPos().add(0, FALL_HEIGHT, 0);
                    corpse2.setPos((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.55D, (double) blockPos2.getZ() + 0.5D);
                    serverWorld.spawnEntity(corpse2);
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

        public boolean canStart() {
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
                if (livingEntity.squaredDistanceTo(this.mob) < 8000.0D && livingEntity.squaredDistanceTo(this.mob) > 16.0D) {
                    World world = this.mob.world;
                    ++this.cooldown;

                    if (this.cooldown == 15) {
                        playShootSound();
                        setAnimationState(ANIMATION_SHOOT_SKULL);
                    }

                    if (this.cooldown == 20) {
                        Vec3d vec3d3 = this.mob.getRotationVec(1.0F);
                        double d = this.mob.squaredDistanceTo(livingEntity) * 2;
                        double h = Math.sqrt(Math.sqrt(d)) * 0.5D;
                        double e = livingEntity.getX() - this.mob.getX();
                        double f = livingEntity.getBodyY(0.5D) - this.mob.getBodyY(0.5D) - 1.25D;
                        double g = livingEntity.getZ() - this.mob.getZ();
                        SkullEntity skull = new SkullEntity(this.mob.world, this.mob, e, f, g);
                        skull.setPosition(this.mob.getX() - vec3d3.x * 0.5, this.mob.getBodyY(0.5D) + 1.25D, this.mob.getZ() - vec3d3.z * 0.5D);
                        world.spawnEntity(skull);

                        int amount = random.nextInt(5) + 2;
                        for (int i = 0; i < amount; ++i) {
                            SkullEntity devSkull = new SkullEntity(this.mob.world, this.mob, this.mob.getRandom().nextTriangular(e, 2.297D * h), f, this.mob.getRandom().nextTriangular(g, 2.297D * h));
                            devSkull.setPosition(this.mob.getX() - vec3d3.x * 0.5, this.mob.getBodyY(0.5D) + 1.25D, this.mob.getZ() - vec3d3.z * 0.5D);
                            world.spawnEntity(devSkull);
                        }

                        this.mob.getLookControl().lookAt(livingEntity, 10.0F, 10.0F);
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

        public boolean canStart() {
            return this.mob.getHealth() <= 300.0F
                    && random.nextInt(20) == 0
                    && this.mob.getHealTimer() <= -200
                    && this.mob.getLevitationDurationTimer() <= -60
                    && canSpellCast();
        }

        public void start() {
            setHealTimer(400);
            this.mob.teleport(homePos.getX() + 0.5D, homePos.getY() + 1.1D, homePos.getZ() + 0.5D);
            playHealSound();
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
        public boolean shouldContinue() {
            if (getHealTimer() > 0) {
                return true;
            }
            return super.shouldContinue();
        }

        public void tick() {
            if (getHealTimer() > 1) {
                setAnimationState(ANIMATION_SUMMON);
                this.mob.heal(0.35F);

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

        public boolean canStart() {
            return canSpellCast() && random.nextInt(125) == 0 && this.mob.getLevitationDurationTimer() <= -400; // cooldown
        }

        public void start() {
            setLevitationDurationTimer(150);

            List<PlayerEntity> player = getPlayersInRange(30.0D);
            Iterator<PlayerEntity> it = player.iterator();
            PlayerEntity playerEntity;

            while (it.hasNext()) {
                playerEntity = (PlayerEntity) it.next();
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 150, 1));
            }

            playLevitationSound();
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
        public boolean shouldContinue() {
            if (getLevitationDurationTimer() > 0) {
                return true;
            }
            return super.shouldContinue();
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
        public boolean canStart() {
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
        public boolean shouldContinue() {
            if (getConjureFangTimer() > 0) {
                return true;
            }
            return super.shouldContinue();
        }

        public void start() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                double d = Math.min(livingEntity.getY(), this.mob.getY());
                double e = Math.max(livingEntity.getY(), this.mob.getY()) + 1.0D;
                float f = (float) MathHelper.atan2(livingEntity.getZ() - this.mob.getZ(), livingEntity.getX() - this.mob.getX());
                int i;

                if (this.mob.squaredDistanceTo(livingEntity) <= 64.0D) {
                    setConjureFangTimer(40);

                    for (i = 0; i < 16; ++i) {
                        double h = 1.25D * (double) (i + 1);
                        int j = 1 * i;
                        this.conjureFangs(this.mob.getX() + (double) MathHelper.cos(f) * h, this.mob.getZ() + (double) MathHelper.sin(f) * h, d, e, f, j);
                        this.conjureFangs(this.mob.getX() + (double) MathHelper.cos(-f) * h, this.mob.getZ() + (double) MathHelper.sin(-f) * h, d, e, -f, j);
                        this.conjureFangs(this.mob.getX() + (double) MathHelper.sin(f) * h, this.mob.getZ() + (double) MathHelper.cos(f) * h, d, e, f, j);
                        this.conjureFangs(this.mob.getX() + (double) MathHelper.sin(-f) * h, this.mob.getZ() + (double) MathHelper.cos(-f) * h, d, e, -f, j);
                    }
                }
            }
        }

        public void tick() {
            if (getConjureFangTimer() > 1) {
                setAnimationState(ANIMATION_CONJURE_FANG);

                if (getLevitationDurationTimer() == 1) {
                    setAnimationState(ANIMATION_IDLE);
                }
            }
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos blockPos = new BlockPos(x, y, z);
            boolean bl = false;
            double d = 0.0D;

            do {
                BlockPos blockPos2 = blockPos.down();
                BlockState blockState = this.mob.world.getBlockState(blockPos2);
                if (blockState.isSideSolidFullSquare(this.mob.world, blockPos2, Direction.UP)) {
                    if (!this.mob.world.isAir(blockPos)) {
                        BlockState blockState2 = this.mob.world.getBlockState(blockPos);
                        VoxelShape voxelShape = blockState2.getCollisionShape(this.mob.world, blockPos);
                        if (!voxelShape.isEmpty()) {
                            d = voxelShape.getMax(Direction.Axis.Y);
                        }
                    }

                    bl = true;
                    break;
                }

                blockPos = blockPos.down();
            } while (blockPos.getY() >= MathHelper.floor(maxY) - 1);

            if (bl) {
                this.mob.world.spawnEntity(new EvokerFangsEntity(this.mob.world, x, (double) blockPos.getY() + d, z, yaw, warmup, this.mob));
            }
        }
    }

}
