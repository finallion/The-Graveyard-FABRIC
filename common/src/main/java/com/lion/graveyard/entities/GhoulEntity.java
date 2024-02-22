package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.GhoulMeleeAttackGoal;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class GhoulEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final UUID SLOWNESS_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A1");
    private static final AttributeModifier SLOWNESS_EFFECT;

    private static final EntityDataAccessor<Byte> VARIANT;
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> RAGE_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Integer> SPAWN_TIMER;
    private static final EntityDataAccessor<Boolean> IS_RAGING;

    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation RAGE_ANIMATION = RawAnimation.begin().then("rage", Animation.LoopType.LOOP);
    private final RawAnimation RUNNING_ANIMATION = RawAnimation.begin().then("running", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE);
    protected static final int ANIMATION_IDLE = 0;
    protected static final int ANIMATION_WALK = 1;
    protected static final int ANIMATION_RAGE = 2;
    protected static final int ANIMATION_DEATH = 3;
    protected static final int ANIMATION_RUNNING = 4;
    protected static final int ANIMATION_MELEE = 5;
    protected static final int ANIMATION_SPAWN = 6;

    public final int ATTACK_ANIMATION_DURATION = 14;
    public final int RAGE_ANIMATION_DURATION = 35;
    public final int RAGE_ANIMATION_COOLDOWN = 300;

    public GhoulEntity(EntityType<? extends GhoulEntity> entityType, Level world) {
        super(entityType, world, "ghoul");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        // selects one of eight skins for the ghoul (in BaseGhoulModel)
        byte variant = (byte) ((byte) random.nextInt(8) + (byte)1);

        this.entityData.define(VARIANT, variant);
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
        this.entityData.define(ATTACK_ANIM_TIMER, 0);
        this.entityData.define(RAGE_ANIM_TIMER, 0);
        this.entityData.define(SPAWN_TIMER, 32);
        this.entityData.define(IS_RAGING, false);
    }

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


    public MobType getMobType() {
        return MobType.UNDEAD;
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
        return entityData.get(VARIANT);
    }

    public void setVariant(byte variant) {
        entityData.set(VARIANT, variant);
    }

    protected void customServerAiStep() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimationState(ANIMATION_MELEE);
            if (getRageAnimTimer() <= 0) {
                setRageAnimTimer(RAGE_ANIMATION_COOLDOWN);
            }
        }

        // RAGE TIMER
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

        if (this.getSpawnTimer() > 0) {
            this.setSpawnTimer(getSpawnTimer() - 1);
        }

        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (isRaging()) {
            if (!entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.addTransientModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT.getId());
            }
        }

        super.aiStep();
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && level() != null) {
            //MinecraftClient.getInstance().particleManager.addBlockBreakParticles(this.getBlockPos().down(), level.getBlockState(this.getBlockPos().down()));
            RandomSource random = this.getRandom();
            BlockState blockstate = this.getBlockStateOn();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double) Mth.randomBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double) Mth.randomBetween(random, -0.7F, 0.7F);
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        super.tick();
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("ghoulVariant", getVariant());
        nbt.putInt("spawnTimer", getSpawnTimer());
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setVariant(nbt.getByte("ghoulVariant"));
        setSpawnTimer(nbt.getInt("spawnTimer"));
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
        data.add(new AnimationController(this, "controller", 0, event -> {
            float limbSwingAmount = event.getLimbSwingAmount();
            boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);

            /* SPAWN */
            if (getSpawnTimer() > 0) {
                event.getController().setAnimation(SPAWN_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* DEATH */
            if (this.isDeadOrDying() || this.getHealth() < 0.01) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAggressive() && !(this.isDeadOrDying() || this.getHealth() < 0.01)) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2);
                event.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* RAGE */
            if (getAnimationState() == ANIMATION_RAGE && getRageAnimTimer() == (RAGE_ANIMATION_DURATION - 1) && isRaging()) {
                event.getController().setAnimation(RAGE_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* WALK */
            if ((event.isMoving() || isMoving) && getAttackAnimTimer() <= 0 && !isRaging()) {
                if (isAggressive() && !isInWater()) {
                    event.getController().setAnimation(RUNNING_ANIMATION);
                } else {
                    event.getController().setAnimation(WALK_ANIMATION);
                }
                return PlayState.CONTINUE;
            }

            if (!event.isMoving() && !isRaging()) {
                event.getController().setAnimation(IDLE_ANIMATION);
            }

            /* IDLE */
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && !event.isMoving()) {
                setAnimationState(ANIMATION_IDLE);
                return PlayState.CONTINUE;
            }

            /* STOPPERS */
            // stops idle animation from looping
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() > 0) {
                setAnimationState(ANIMATION_MELEE);
                return PlayState.STOP;
            }

            // stops attack animation from looping
            if (getAttackAnimTimer() <= 0 && !(this.isDeadOrDying() || this.getHealth() < 0.01) && getAnimationState() != ANIMATION_RAGE) {
                setAnimationState(ANIMATION_IDLE);
                return PlayState.STOP;
            }

            return PlayState.CONTINUE;
        }));
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
    public void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.GHOUL_STEP.get(), 0.5F, -1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.GHOUL_HURT.get(), 1.0F, -5.0F);
    }

    protected SoundEvent getDeathSound() {
        return TGSounds.GHOUL_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return -5.0F;
    }

    static {
        VARIANT = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BYTE);
        ATTACK_ANIM_TIMER = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        RAGE_ANIM_TIMER = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        ANIMATION = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        SPAWN_TIMER = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.INT);
        IS_RAGING = SynchedEntityData.defineId(GhoulEntity.class, EntityDataSerializers.BOOLEAN);
        SLOWNESS_EFFECT = new AttributeModifier(SLOWNESS_ID, "Slowness effect", -0.3D, AttributeModifier.Operation.ADDITION);
    }
}
