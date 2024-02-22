package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.NightmareMeleeAttackGoal;
import com.lion.graveyard.init.TGCriteria;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

public class NightmareEntity extends HostileGraveyardEntity implements GeoEntity, NeutralMob {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_WALK = 1;
    protected static final byte ANIMATION_DEATH = 3;
    protected static final byte ANIMATION_ATTACK = 5;
    protected static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.INT);
    public final int ATTACK_ANIMATION_DURATION = 20;


    private static final EntityDataAccessor<Boolean> ANGRY;
    private static final EntityDataAccessor<Boolean> PROVOKED;
    private static final UniformInt ANGER_TIME;
    private UUID target;
    private int angerTime;
    private int ageWhenTargetSet;

    public NightmareEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, "nightmare");
        this.setMaxUpStep(1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
        this.entityData.define(PROVOKED, false);
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
        this.entityData.define(ATTACK_ANIM_TIMER, 0);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION);
    }

    public void setState(byte time) {
        this.entityData.set(ANIMATION, time);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChasePlayerGoal(this));
        this.goalSelector.addGoal(2, new NightmareMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new TeleportTowardsPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    protected void customServerAiStep() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimation(ANIMATION_ATTACK);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.level().isDay() && this.tickCount >= this.ageWhenTargetSet + 600) {
            float f = this.getLightLevelDependentMagicValue();
            if (f > 0.5F && this.level().canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((LivingEntity)null);
                this.teleport();
            }
        }

        super.customServerAiStep();
    }


    public void aiStep() {
        this.jumping = false;

        if (!this.level().isClientSide()) {
            if (this.getTarget() != null) {
                if (this.getTarget().distanceToSqr(this) >= 1024.0D) {
                    stopBeingAngry();
                }
            }
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

        super.aiStep();
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.addPersistentAngerSaveData(nbt);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.readPersistentAngerSaveData(this.level(), nbt);
    }


    public static AttributeSupplier.Builder createNightmareAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.19D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 28 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            return false;
        } else {
            return super.hurt(source, amount);
        }
    }


    public byte getAnimation() {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(byte animation) {
        entityData.set(ANIMATION, animation);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.entityData.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.entityData.set(ATTACK_ANIM_TIMER, time);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 2, event -> {
            if (isDeadOrDying()) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_ATTACK && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAggressive() && !(this.isDeadOrDying() || this.getHealth() < 0.01)) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2);
                event.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* WALK */
            if (((getAnimationState() == ANIMATION_WALK || event.isMoving()) && getAttackAnimTimer() <= 0)) {
                event.getController().setAnimation(WALK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* IDLE */
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && !event.isMoving()) {
                event.getController().setAnimation(IDLE_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* STOPPERS */
            // stops idle animation from looping
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() > 0) {
                setAnimation(ANIMATION_ATTACK);
                return PlayState.STOP;
            }

            // stops attack animation from looping
            if (getAttackAnimTimer() <= 0 && !(this.isDeadOrDying() || this.getHealth() < 0.01)) {
                setAnimation(ANIMATION_IDLE);
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
        this.playSound(TGSounds.NIGHTMARE_AMBIENT.get(), 1.0F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.NIGHTMARE_HURT.get(), 1.0F, -10.0F);
    }

    protected SoundEvent getDeathSound() {
        return TGSounds.NIGHTMARE_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return -10.0F;
    }

    boolean isPlayerStaring(Player player) {
        ItemStack itemStack = (ItemStack)player.getInventory().armor.get(3);
        if (itemStack.is(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        } else {
            Vec3 vec3d = player.getViewVector(1.0F).normalize();
            Vec3 vec3d2 = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dot(vec3d2);

            if (e > 1.0D - 0.025D / d) {
                return player.hasLineOfSight(this);
            } else {
                return false;
            }
        }
    }

    protected float getStandingEyeHeight(Pose p_32517_, EntityDimensions p_32518_) {
        return 2.55F;
    }


    boolean teleportTowards(Entity entity) {
        Vec3 vec3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double e = this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D - vec3d.x * 16.0D;
        double f = this.getY() + (double)(this.random.nextInt(4) - 2) - vec3d.y * 16.0D;
        double g = this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D - vec3d.z * 16.0D;
        return this.teleport(e, f, g);
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y, z);

        while(mutable.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(mutable).blocksMotion()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.level().getBlockState(mutable);
        boolean bl = blockState.blocksMotion();
        boolean bl2 = blockState.getFluidState().is(FluidTags.WATER);
        if (bl && !bl2) {
            boolean bl3 = this.randomTeleport(x, y, z, false);
            if (bl3 && !this.isSilent()) {
                //this.level.playSound((Player)null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, -10.0F);
            }

            return bl3;
        } else {
            return false;
        }
    }

    protected boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double e = this.getY() + (double)(this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleport(d, e, f);
        } else {
            return false;
        }
    }

    public void setRemainingPersistentAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    public void setProvoked() {
        this.entityData.set(PROVOKED, true);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.target;
    }


    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.target = uuid;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME.sample(this.random));
    }

    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.entityData.set(ANGRY, false);
            this.setRemainingPersistentAngerTime(0);
            this.entityData.set(PROVOKED, false);
        } else {
            this.ageWhenTargetSet = this.tickCount;
            this.entityData.set(ANGRY, true);
        }

    }

    @Override
    protected void createWitherRose(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayer player) {
            if (player.hasEffect(MobEffects.BLINDNESS)) {
                TGCriteria.KILL_WHILE_BLINDED.get().trigger(player);
            }
        }

        super.setLastHurtByMob(adversary);
    }

    static {
        ANGRY = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BOOLEAN);
        PROVOKED = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BOOLEAN);
        ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }


    static class ChasePlayerGoal extends Goal {
        private final NightmareEntity nightmare;
        @Nullable
        private LivingEntity target;

        public ChasePlayerGoal(NightmareEntity nightmare) {
            this.nightmare = nightmare;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            this.target = this.nightmare.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d = this.target.distanceToSqr(this.nightmare);
                return d > 256.0D ? false : this.nightmare.isPlayerStaring((Player)this.target);
            }
        }

        public void start() {
            this.nightmare.getNavigation().stop();
        }

        public void tick() {
            this.nightmare.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class TeleportTowardsPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final NightmareEntity nightmare;
        @Nullable
        private Player targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetingConditions staringPlayerPredicate;
        private final TargetingConditions validTargetPredicate = TargetingConditions.forCombat().ignoreLineOfSight();

        public TeleportTowardsPlayerGoal(NightmareEntity nightmare, @Nullable Predicate<LivingEntity> targetPredicate) {
            super(nightmare, Player.class, 10, false, false, targetPredicate);
            this.nightmare = nightmare;
            this.staringPlayerPredicate = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((playerEntity) -> {
                return nightmare.isPlayerStaring((Player)playerEntity);
            });
        }

        public boolean canUse() {
            this.targetPlayer = this.nightmare.level().getNearestPlayer(this.staringPlayerPredicate, this.nightmare);
            return this.targetPlayer != null;
        }

        public void start() {
            this.lookAtPlayerWarmup = this.adjustedTickDelay(5);
            this.ticksSinceUnseenTeleport = 0;
            this.nightmare.setProvoked();
        }

        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        public boolean canContinueToUse() {
            if (this.targetPlayer != null) {
                if (!this.nightmare.isPlayerStaring(this.targetPlayer)) {
                    return false;
                } else {
                    this.nightmare.lookAt(this.targetPlayer, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.target != null && this.validTargetPredicate.test(this.nightmare, this.target) ? true : super.canContinueToUse();
            }
        }

        public void tick() {
            if (this.nightmare.getTarget() == null) {
                super.setTarget((LivingEntity)null);
            }

            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.target = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.target != null && !this.nightmare.isPassenger()) {
                    // custom teleport injection
                    if (this.nightmare.isPlayerStaring((Player)this.target)) {
                        if (target.distanceToSqr(this.nightmare) <= 384.0D && target != null) {
                            if (target.distanceToSqr(this.nightmare) > 24.0D) {
                                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                                this.nightmare.teleportTowards(target);
                            }
                        }
                        this.ticksSinceUnseenTeleport = 0;
                    // custom stop
                    } else if (this.target.distanceToSqr(this.nightmare) > 256.0D && this.ticksSinceUnseenTeleport++ >= this.adjustedTickDelay(80) && this.nightmare.teleportTowards(this.target)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }

                super.tick();
            }

        }
    }


}
