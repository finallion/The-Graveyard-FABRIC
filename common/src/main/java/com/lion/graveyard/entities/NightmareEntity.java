package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.NightmareMeleeAttackGoal;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.UUID;

public class NightmareEntity extends HostileGraveyardEntity implements GeoEntity, NeutralMob {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("death");
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlayAndHold("attack");
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_WALK = 1;
    protected static final byte ANIMATION_DEATH = 3;
    protected static final byte ANIMATION_ATTACK = 5;
    protected static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER = SynchedEntityData.defineId(NightmareEntity.class, EntityDataSerializers.INT);
    public final int ATTACK_ANIMATION_DURATION = 15;
    public final int ATTACK_ANIM_SYNC_TICKS = ATTACK_ANIMATION_DURATION + AnimationTable.TRANSITION_TICKS;
    private int lastAttackAnimTimer;


    private static final EntityDataAccessor<Boolean> ANGRY;
    private static final EntityDataAccessor<Boolean> PROVOKED;
    private static final UniformInt ANGER_TIME;
    private UUID target;
    private int angerTime;
    private int ageWhenTargetSet;

    public NightmareEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, "nightmare");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGRY, false);
        builder.define(PROVOKED, false);
        builder.define(ANIMATION, ANIMATION_IDLE);
        builder.define(ATTACK_ANIM_TIMER, 0);
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION);
    }

    public void setState(byte time) {
        this.entityData.set(ANIMATION, time);
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChasePlayerGoal(this));
        this.goalSelector.addGoal(2, new NightmareMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new TeleportTowardsPlayerGoal(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal(this, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            restartAttackClipOnRearm();
        }
    }

    // The controller does not restart a running clip.
    private void restartAttackClipOnRearm() {
        int timer = this.getAttackAnimTimer();

        if (timer > this.lastAttackAnimTimer + 1) {
            AnimationController<?> controller = this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get("controller");

            if (controller != null) {
                controller.forceAnimationReset();
            }
        }

        this.lastAttackAnimTimer = timer;
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        if (this.getAttackAnimTimer() == ATTACK_ANIM_SYNC_TICKS) {
            setAnimation(ANIMATION_ATTACK);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.level().isBrightOutside() && this.tickCount >= this.ageWhenTargetSet + 600) {
            float f = this.getLightLevelDependentMagicValue();

            if (f > 0.5F && this.level().canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((LivingEntity)null);
                this.teleportRandomly();
            }
        }

        super.customServerAiStep(serverLevel);
    }


    @Override
    public void aiStep() {
        this.jumping = false;

        if (!this.level().isClientSide) {
            if (this.getTarget() != null) {
                if (this.getTarget().distanceToSqr(this) >= 1024.0D) {
                    stopBeingAngry();
                }
            }

            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

        super.aiStep();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addPersistentAngerSaveData(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readPersistentAngerSaveData(this.level(), input);
    }


    public static AttributeSupplier.Builder createNightmareAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.19D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == 31 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (this.isInvulnerableTo(serverLevel, source)) {
            return false;
        } else if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            return false;
        } else {
            return super.hurtServer(serverLevel, source, amount);
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
        data.add(AnimationTable.<NightmareEntity>create()
                .when(() -> isDeadOrDying() || getHealth() < 0.01F, DEATH_ANIMATION)
                .when(() -> getAttackAnimTimer() > 0, ATTACK_ANIMATION)
                .when(AnimationTable::isMoving, WALK_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .build(this, "controller"));
    }


    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
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

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.playSound(TGSounds.NIGHTMARE_DEATH.get(), 1.0F, -10.0F);
    }

    boolean isPlayerStaring(Player player) {
        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);

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

    boolean teleportTo(Entity entity) {
        Vec3 vec3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double e = this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D - vec3d.x * 16.0D;
        double f = this.getY() + (double)(this.random.nextInt(4) - 2) - vec3d.y * 16.0D;
        double g = this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D - vec3d.z * 16.0D;
        return this.teleportToPosition(e, f, g);
    }

    private boolean teleportToPosition(double x, double y, double z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y, z);

        while(mutable.getY() > this.level().getMinY() && !this.level().getBlockState(mutable).blocksMotion()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.level().getBlockState(mutable);
        boolean solid = blockState.blocksMotion();
        boolean inWater = blockState.getFluidState().is(FluidTags.WATER);

        if (solid && !inWater) {
            boolean teleported = this.randomTeleport(x, y, z, false);

            if (teleported && !this.isSilent()) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, -10.0F);
            }

            return teleported;
        } else {
            return false;
        }
    }

    protected boolean teleportRandomly() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double e = this.getY() + (double)(this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleportToPosition(d, e, f);
        } else {
            return false;
        }
    }

    @Override
    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    @Override
    public void setRemainingPersistentAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    @Override
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

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME.sample(this.random));
    }

    @Override
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
                TGAdvancements.KILL_WHILE_BLINDED.trigger(player);
            }
        }

        super.createWitherRose(adversary);
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
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            this.target = this.nightmare.getTarget();

            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d = this.target.distanceToSqr(this.nightmare);
                return d > 256.0D ? false : this.nightmare.isPlayerStaring((Player)this.target);
            }
        }

        @Override
        public void start() {
            this.nightmare.getNavigation().stop();
        }

        @Override
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

        public TeleportTowardsPlayerGoal(NightmareEntity nightmare, @Nullable TargetingConditions.Selector targetPredicate) {
            super(nightmare, Player.class, 10, false, false, targetPredicate);
            this.nightmare = nightmare;
            this.staringPlayerPredicate = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((playerEntity, level) -> {
                return nightmare.isPlayerStaring((Player)playerEntity);
            });
        }

        @Override
        public boolean canUse() {
            this.targetPlayer = getServerLevel(this.nightmare).getNearestPlayer(this.staringPlayerPredicate, this.nightmare);
            return this.targetPlayer != null;
        }

        @Override
        public void start() {
            this.lookAtPlayerWarmup = this.adjustedTickDelay(5);
            this.ticksSinceUnseenTeleport = 0;
            this.nightmare.setProvoked();
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.targetPlayer != null) {
                if (!this.nightmare.isPlayerStaring(this.targetPlayer)) {
                    return false;
                } else {
                    this.nightmare.lookAt(this.targetPlayer, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.target != null && this.validTargetPredicate.test(getServerLevel(this.nightmare), this.nightmare, this.target) ? true : super.canContinueToUse();
            }
        }

        @Override
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
                    if (this.nightmare.isPlayerStaring((Player)this.target)) {
                        if (target.distanceToSqr(this.nightmare) <= 384.0D && target != null) {
                            if (target.distanceToSqr(this.nightmare) > 24.0D) {
                                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                                this.nightmare.teleportTo(target);
                            }
                        }

                        this.ticksSinceUnseenTeleport = 0;
                    } else if (this.target.distanceToSqr(this.nightmare) > 256.0D && this.ticksSinceUnseenTeleport++ >= this.adjustedTickDelay(80) && this.nightmare.teleportTo(this.target)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }

                super.tick();
            }

        }
    }


}
