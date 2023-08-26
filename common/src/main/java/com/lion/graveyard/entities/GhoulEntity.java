package main.java.com.lion.graveyard.entities;

import main.java.com.lion.graveyard.entities.ai.goals.GhoulMeleeAttackGoal;
import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
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
    private static final EntityAttributeModifier SLOWNESS_EFFECT;

    private static final TrackedData<Byte> VARIANT;
    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> RAGE_ANIM_TIMER;
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Integer> SPAWN_TIMER;
    private static final TrackedData<Boolean> IS_RAGING;

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

    public GhoulEntity(EntityType<? extends GhoulEntity> entityType, World world) {
        super(entityType, world, "ghoul");
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        // selects one of eight skins for the ghoul (in BaseGhoulModel)
        byte variant = (byte) ((byte) random.nextInt(8) + (byte)1);

        this.dataTracker.startTracking(VARIANT, variant);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(RAGE_ANIM_TIMER, 0);
        this.dataTracker.startTracking(SPAWN_TIMER, 32);
        this.dataTracker.startTracking(IS_RAGING, false);
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
        return dataTracker.get(VARIANT);
    }

    public void setVariant(byte variant) {
        dataTracker.set(VARIANT, variant);
    }

    protected void mobTick() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimationState(ANIMATION_MELEE);
            if (getRageAnimTimer() <= 0) {
                setRageAnimTimer(RAGE_ANIMATION_COOLDOWN);
            }
        }

        // RAGE TIMER
        if (this.getRageAnimTimer() == RAGE_ANIMATION_DURATION) {
            this.playSound(TGSounds.GHOUL_ROAR, 1.0F, 1.0F);
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

        super.tickMovement();
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && getEntityWorld() != null) {
            //MinecraftClient.getInstance().particleManager.addBlockBreakParticles(this.getBlockPos().down(), getEntityWorld().getBlockState(this.getBlockPos().down()));
            Random random = this.getRandom();
            BlockState blockState = this.getSteppingBlockState();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double) MathHelper.nextBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
                    this.getEntityWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        super.tick();
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("ghoulVariant", getVariant());
        nbt.putInt("spawnTimer", getSpawnTimer());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setVariant(nbt.getByte("ghoulVariant"));
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
        data.add(new AnimationController(this, "controller", 0, event -> {
            float limbSwingAmount = event.getLimbSwingAmount();
            boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);

            /* SPAWN */
            if (getSpawnTimer() > 0) {
                event.getController().setAnimation(SPAWN_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* DEATH */
            if (this.isDead() || this.getHealth() < 0.01) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01)) {
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
                if (isAttacking() && !isWet()) {
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
            if (getAttackAnimTimer() <= 0 && !(this.isDead() || this.getHealth() < 0.01) && getAnimationState() != ANIMATION_RAGE) {
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
        this.playSound(TGSounds.GHOUL_AMBIENT, 1.0F, -5.0F);
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.GHOUL_STEP, 0.5F, -1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.GHOUL_HURT, 1.0F, -5.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(TGSounds.GHOUL_DEATH, 1.0F, -5.0F);
    }

    static {
        VARIANT = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BYTE);
        ATTACK_ANIM_TIMER = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        RAGE_ANIM_TIMER = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SPAWN_TIMER = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.INTEGER);
        IS_RAGING = DataTracker.registerData(GhoulEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SLOWNESS_EFFECT = new EntityAttributeModifier(SLOWNESS_ID, "Slowness effect", -0.3D, EntityAttributeModifier.Operation.ADDITION);
    }

}
