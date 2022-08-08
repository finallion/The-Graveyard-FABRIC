package com.finallion.graveyard.entities;

import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGParticles;
import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class LichEntity extends HostileEntity implements IAnimatable {
    private final ServerBossBar bossBar;
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    protected static final TargetPredicate HEAD_TARGET_PREDICATE;
    private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE;
    // animation
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    private final AnimationBuilder CORPSE_SPELL_ANIMATION = new AnimationBuilder().addAnimation("corpse_spell", true);
    private final AnimationBuilder START_PHASE_2_ANIMATION = new AnimationBuilder().addAnimation("phase_two", false);
    private final AnimationBuilder PHASE_2_IDLE_ANIMATION = new AnimationBuilder().addAnimation("phase_two_idle", true);
    private final AnimationBuilder PHASE_2_ATTACK_ANIMATION = new AnimationBuilder().addAnimation("phase_two_attack", true);
    protected static final int ANIMATION_SPAWN = 0;
    protected static final int ANIMATION_IDLE = 1;
    protected static final int ANIMATION_MELEE = 2;
    protected static final int ANIMATION_CORPSE_SPELL = 3;
    protected static final int ANIMATION_START_PHASE_2 = 4;
    protected static final int ANIMATION_PHASE_2_IDLE = 5;
    protected static final int ANIMATION_PHASE_2_ATTACK = 6;
    protected static final int ANIMATION_STOP = 10;
    // data tracker
    private static final TrackedData<Integer> INVUL_TIMER; // spawn invul timer
    private static final TrackedData<Integer> PHASE_INVUL_TIMER; // other invul timer
    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> PHASE_TWO_START_ANIM_TIMER;
    private static final TrackedData<Integer> PHASE; // divides fight into three main phases and two transitions, animations are named after the main phase
    protected static final TrackedData<Integer> ANIMATION;
    protected static final TrackedData<Boolean> CAN_CORPSE_SPELL_START;
    // constants
    private static final int SPAWN_INVUL_TIMER = 420;
    private static final int DEFAULT_INVUL_TIMER = 200;
    private final float HEALTH_PHASE_01 = 400.0F;
    private final float HEALTH_PHASE_02 = 200.0F;
    private final int ATTACK_ANIMATION_DURATION = 40;
    private final int START_PHASE_TWO_ANIMATION_DURATION = 121;
    private final int START_PHASE_TWO_PARTICLES = 80;
    private final int CORPSE_SPELL_COOLDOWN = 800;
    // variables
    private int spellDuration = 300;
    private int corpseSpellCooldownTicker = 800;

    // fog effect from boss bar
    // rotate fallen corpses
    // add falling parts of skeletons
    // falling bone particles
    // make unpushable
    // teeth only one texture layer
    // in chase phase: screech when entering player sphere
    // save phase in nbt
    // apply mining fatigue
    // prevent effect from being cleared (with milk)
    // teleport to altar pos
    public LichEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar) (new ServerBossBar(this.getDisplayName(), BossBar.Color.WHITE, BossBar.Style.PROGRESS)).setDarkenSky(true);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (getAnimationState() == ANIMATION_SPAWN && getInvulnerableTimer() >= 0) {
            event.getController().setAnimation(SPAWN_ANIMATION);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    // attack handler
    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        // each anim in its own if-clause to avoid unpredictable behaviour between phases
        // anims that loop will loop forever until PlayState.STOPed, it doesn't care about internal animation state tracker

        // set from the respawn method, stops all animations from previous phases
        if (getAnimationState() == ANIMATION_STOP) {
            return PlayState.STOP;
        }

        /* MAIN PHASE 1 */
        // takes one tick to get to this method (from mobtick)
        // do not attack when: the spawn invul timer is active, the phase is incorrect or the invul timer was set from a spell
        if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01) && canMeeleAttack()) {
            setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2); // disables to call the animation immediately after (seems to be called multiple times per tick, per frame tick?)
            event.getController().setAnimation(ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (getAnimationState() == ANIMATION_CORPSE_SPELL && getPhase() == 1) {
            event.getController().setAnimation(CORPSE_SPELL_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (getPhase() == 1) {
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && getInvulnerableTimer() <= 0) {
                event.getController().setAnimation(IDLE_ANIMATION);
                return PlayState.CONTINUE;
            } else {
                setAnimationState(ANIMATION_MELEE);
            }
        }

        /* MAIN PHASE 2 */
        if (getAnimationState() == ANIMATION_START_PHASE_2 && getPhase() == 2) {
            event.getController().setAnimation(START_PHASE_2_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (getAnimationState() == ANIMATION_PHASE_2_IDLE && getPhase() == 3) {
            event.getController().setAnimation(PHASE_2_IDLE_ANIMATION);
            return PlayState.CONTINUE;
        }

        // takes one tick to get to this method (from mobtick)
        // do not attack when: the spawn invul timer is active, the phase is incorrect or the invul timer was set from a spell
        if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01) && canMeeleAttack()) {
            setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2); // disables to call the animation immediately after (seems to be called multiple times per tick, per frame tick?)
            event.getController().setAnimation(ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }


        /* MAIN PHASE 3 */

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
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false, 1.5D));
        this.goalSelector.add(2, new SummonFallenCorpsesGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 20.0F));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, HEALTH_PHASE_01)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                    .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 5.0D).build());
        }
        return attributeContainer;
    }

    @Override
    public void tickMovement() {
        randomDisplayTick(this.getWorld(), this.getBlockPos(), this.getRandom());
        int phaseTwoTimer = getStartPhaseTwoAnimTimer();

        if (phaseTwoTimer < START_PHASE_TWO_PARTICLES && phaseTwoTimer > (START_PHASE_TWO_PARTICLES / 2)) {
            for (int i = 0; i < 100; i++) {
                this.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + random.nextDouble() + random.nextInt(3) - random.nextInt(3), this.getY() + random.nextDouble(), this.getZ() + random.nextDouble() + random.nextInt(3) - random.nextInt(3), 0.0D, 0.4D, 0.0D);
            }
        }

        super.tickMovement();
    }

    protected void mobTick() {
        if (getCorpseSpellCooldownTicker() > 0) {
            setCorpseSpellCooldownTicker(getCorpseSpellCooldownTicker() - 1);
        }

        /* TRANSITION PHASE ONE to TWO */
        if (getPhase() == 2) {
            int phaseTwoTimer = getStartPhaseTwoAnimTimer();
            setPhaseInvulTimer(START_PHASE_TWO_ANIMATION_DURATION); // invul
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
        /* END TRANSITION PHASE ONE to TWO  */

        if (this.getPhaseInvulnerableTimer() > 0) {
            int timer = getPhaseInvulnerableTimer() - 1;
            this.setPhaseInvulTimer(timer);
        }

        int i;
        if (this.getInvulnerableTimer() > 0) {
            i = this.getInvulnerableTimer() - 1;

            int timer;
            if (getPhase() == 1) {
                timer = SPAWN_INVUL_TIMER;
            } else {
                timer = DEFAULT_INVUL_TIMER;
            }
            if (this.getInvulnerableTimer() == 1) {
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
            } else if (getCorpseSpellCooldownTicker() <= 0) {
                setCorpseSpellStart(true);
            }


            super.mobTick();

            //if (this.age % 20 == 0) {
            //    this.heal(1.0F);
            //}

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


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(PHASE_INVUL_TIMER, 0);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(PHASE_TWO_START_ANIM_TIMER, START_PHASE_TWO_ANIMATION_DURATION);
        this.dataTracker.startTracking(PHASE, 1);
        this.dataTracker.startTracking(CAN_CORPSE_SPELL_START, false);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Invul", this.getInvulnerableTimer());
        nbt.putInt("PhaseInvul", this.getPhaseInvulnerableTimer());
        //nbt.putInt("Phase", this.getPhase());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setInvulTimer(nbt.getInt("Invul"));
        this.setPhaseInvulTimer(nbt.getInt("PhaseInvul"));
        //this.setPhase(nbt.getInt("Phase"));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
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
        playSpawnSound();
    }

    private void applyInvulAndResetBossBar(int invul) {
        this.setInvulTimer(invul);
        this.bossBar.setPercent(0.0F);
    }


    private void playSpawnSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_SPAWN, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playAttackSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_MELEE, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playCorpseSpellSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_CORPSE_SPELL, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    private void playStartPhaseTwoSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_CORPSE_SPELL, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    public boolean canCorpseSpellStart() {
        return this.dataTracker.get(CAN_CORPSE_SPELL_START);
    }

    public void setCorpseSpellStart(boolean bool) {
        this.dataTracker.set(CAN_CORPSE_SPELL_START, bool);
    }

    public int getInvulnerableTimer() {
        return (Integer) this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
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

    public int getAttackAnimTimer() {
        return (Integer) this.dataTracker.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.dataTracker.set(ATTACK_ANIM_TIMER, time);
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
                    respawn();
                    return false;
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
        //this.world.sendEntityStatus(this, (byte)35);
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

        if (random.nextInt(2) == 0) {
            mutable.set(i + MathHelper.nextInt(random, -10, 10), j + random.nextInt(10), k + MathHelper.nextInt(random, -10, 10));
            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.isFullCube(world, mutable)) {
                world.addParticle(ParticleTypes.SMOKE, (double) mutable.getX() + random.nextDouble(), (double) mutable.getY() + random.nextDouble(), (double) mutable.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }


    public boolean canMeeleAttack() {
        return getPhase() == 1 && getPhaseInvulnerableTimer() <= 0 && getInvulnerableTimer() <= 0 && this.getHealth() > 35.0F;
    }

    public boolean canHunt() {
        return getPhase() == 3 && getPhaseInvulnerableTimer() <= 0 && getInvulnerableTimer() <= 0;
    }

    public int getCorpseSpellCooldownTicker() {
        return corpseSpellCooldownTicker;
    }

    public void setCorpseSpellCooldownTicker(int corpseSpellCooldownTicker) {
        this.corpseSpellCooldownTicker = corpseSpellCooldownTicker;
    }

    /*
    @Override
    public boolean isPushable() {
        return getPhase() == 1;
    }

    @Override
    protected boolean isImmobile() {
        if (getPhase() == 1) {
            return true;
        }
        return super.isImmobile();
    }

     */

    static {
        INVUL_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE_INVUL_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ATTACK_ANIM_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE_TWO_START_ANIM_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CAN_CORPSE_SPELL_START = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_ATTACK_PREDICATE = Entity::isPlayer;
        HEAD_TARGET_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(20.0D).setPredicate(CAN_ATTACK_PREDICATE);
    }

    public class SummonFallenCorpsesGoal extends Goal {
        protected final LichEntity lich;
        private final int FALL_HEIGHT = 15;
        private final int SQUARE_SIZE = 10;
        private final int CORPSE_SPAWN_RARITY = 5;
        private final int CORPSE_SPAWN_RARITY_PLAYER = 12;
        private final int SPELL_DURATION = 300;

        public SummonFallenCorpsesGoal(LichEntity lich) {
            this.lich = lich;
        }

        @Override
        public boolean canStart() {
            return lich.getPhase() == 1 && canCorpseSpellStart();
        }

        public boolean shouldContinue() {
            return spellDuration > 0 && this.lich.getPhase() == 1;
        }

        public void tick() {
            if (canCorpseSpellStart() && getCorpseSpellCooldownTicker() == 0) {
                // make invulnerable during spell, prevents lich melee attack
                this.lich.setPhaseInvulTimer(SPELL_DURATION);
                this.lich.setCorpseSpellStart(false);
                this.lich.setCorpseSpellCooldownTicker(CORPSE_SPELL_COOLDOWN);
                playCorpseSpellSound();
            }


            if (getPhaseInvulnerableTimer() <= 0) {
                setAnimationState(ANIMATION_MELEE);
                stop();
            } else {
                setAnimationState(ANIMATION_CORPSE_SPELL);

                BlockPos pos = this.lich.getBlockPos();
                List<PlayerEntity> list = this.lich.world.getPlayers(HEAD_TARGET_PREDICATE, this.lich, lich.getBoundingBox().expand(30.0D));
                List<BlockPos> positions = new ArrayList<>();
                for (int i = -SQUARE_SIZE; i < SQUARE_SIZE; i++) {
                    for (int ii = -SQUARE_SIZE; ii < SQUARE_SIZE; ii++) {
                        positions.add(pos.add(i, FALL_HEIGHT, ii));
                    }
                }

                if (random.nextInt(CORPSE_SPAWN_RARITY) == 0) {
                    FallingCorpse corpse = (FallingCorpse) TGEntities.FALLING_CORPSE.create(world);
                    BlockPos blockPos = positions.get(random.nextInt(positions.size()));
                    corpse.refreshPositionAndAngles((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.55D, (double) blockPos.getZ() + 0.5D, Direction.random(this.lich.getRandom()).asRotation(), 0.0F);
                    world.spawnEntity(corpse);
                }

                if (random.nextInt(CORPSE_SPAWN_RARITY_PLAYER) == 0 && list.size() > 0) {
                    FallingCorpse corpse = (FallingCorpse) TGEntities.FALLING_CORPSE.create(world);
                    PlayerEntity target = list.get(random.nextInt(list.size()));
                    if (target != null) {
                        BlockPos blockPos = target.getBlockPos().add(0, FALL_HEIGHT, 0);
                        corpse.refreshPositionAndAngles((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.55D, (double) blockPos.getZ() + 0.5D, 90, 0.0F);
                        world.spawnEntity(corpse);
                    }
                }
            }

        }


    }


    public class MeleeAttackGoal extends Goal {
        protected final LichEntity mob;
        private final double speed;
        private final double knockback;
        private final boolean pauseWhenMobIdle;
        private Path path;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int updateCountdownTicks;
        private int cooldown;
        private long lastUpdateTime;
        private final int DAMAGE_START_IN_ANIM = 16;
        boolean canFinishAttack = false;

        public MeleeAttackGoal(LichEntity mob, double speed, boolean pauseWhenMobIdle, double knockback) {
            this.mob = mob;
            this.speed = speed;
            this.knockback = knockback;
            this.pauseWhenMobIdle = pauseWhenMobIdle;
            this.cooldown = getAttackAnimTimer();
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        public boolean canStart() {
            if (canMeeleAttack()) {
                long l = this.mob.world.getTime();
                if (l - this.lastUpdateTime < 20L) {
                    return false;
                } else {
                    this.lastUpdateTime = l;
                    LivingEntity livingEntity = this.mob.getTarget();
                    if (livingEntity == null) {
                        return false;
                    } else if (!livingEntity.isAlive()) {
                        return false;
                    } else {
                        this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                        if (this.path != null) {
                            return true;
                        } else {
                            return this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                        }
                    }
                }
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (!this.pauseWhenMobIdle) {
                return !this.mob.getNavigation().isIdle();
            } else if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
                return false;
            } else {
                return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity) livingEntity).isCreative();
            }
        }

        public void start() {
            this.mob.getNavigation().startMovingAlong(this.path, this.speed);
            this.mob.setAttacking(true);
            this.updateCountdownTicks = 0;
            this.cooldown = 0;
        }

        public void stop() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                this.mob.setTarget((LivingEntity) null);
            }

            this.mob.setAttacking(false);
            this.mob.getNavigation().stop();
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                this.mob.getLookControl().lookAt(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
                this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
                if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.mob.getRandom().nextFloat() < 0.05F)) {
                    this.targetX = livingEntity.getX();
                    this.targetY = livingEntity.getY();
                    this.targetZ = livingEntity.getZ();
                    this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                    if (d > 1024.0D) {
                        this.updateCountdownTicks += 10;
                    } else if (d > 256.0D) {
                        this.updateCountdownTicks += 5;
                    }

                    if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
                        this.updateCountdownTicks += 15;
                    }

                    this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
                }

                this.cooldown = Math.max(this.cooldown - 1, 0);
                this.attack(livingEntity, d);
            }
        }

        protected void attack(LivingEntity target, double squaredDistance) {
            double distance = this.getSquaredMaxAttackDistance(target);
            if (canMeeleAttack()) {
                if (squaredDistance <= distance && this.cooldown <= 0) {
                    this.resetCooldown();
                    // set timer to start animation, after timer runs out, possibility to set again
                    if (getAttackAnimTimer() == 0) {
                        setAttackAnimTimer(ATTACK_ANIMATION_DURATION);
                    }

                    // sound on start anim
                    if (getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
                        playAttackSound();
                    }

                    canFinishAttack = true;
                }

                // outside of previous method because animation can play, but player will be out of reach in meantime -> still damage
                if (this.cooldown <= 0 && canFinishAttack) {
                    // x ticks after start of anim -> damage and knockback
                    if (getAttackAnimTimer() == DAMAGE_START_IN_ANIM && this.mob.tryAttack(target)) {
                        // warden sonic boom logic
                        Vec3d vec3d = mob.getPos().add(0.0D, 1.600000023841858D, 0.0D);
                        Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
                        Vec3d vec3d3 = vec3d2.normalize();

                        for (int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                            Vec3d vec3d4 = vec3d.add(vec3d3.multiply((double) i));
                            ((ServerWorld) mob.getWorld()).spawnParticles(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, vec3d4.x, vec3d4.y + 1.0D, vec3d4.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                        }

                        double d = 1.5D * (1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        double e = 2.5D * (1.0D - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
                        canFinishAttack = false;
                    }
                }
            }


        }

        protected void resetCooldown() {
            this.cooldown = this.getTickCount(cooldown);
        }

        protected boolean isCooledDown() {
            return this.cooldown <= 0;
        }

        protected int getCooldown() {
            return this.cooldown;
        }

        protected int getMaxCooldown() {
            return this.getTickCount(cooldown);
        }

        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return (double) (this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F + entity.getWidth());
        }
    }


}
