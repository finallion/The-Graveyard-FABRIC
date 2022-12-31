package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.ai.goals.NightmareMeleeAttackGoal;
import com.finallion.graveyard.init.TGAdvancements;
import com.finallion.graveyard.item.DaggerItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
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
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class NightmareEntity extends HostileGraveyardEntity implements GeoEntity, Angerable {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    protected static final byte ANIMATION_IDLE = 0;
    protected static final byte ANIMATION_WALK = 1;
    protected static final byte ANIMATION_DEATH = 3;
    protected static final byte ANIMATION_ATTACK = 5;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(NightmareEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> ATTACK_ANIM_TIMER = DataTracker.registerData(NightmareEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public final int ATTACK_ANIMATION_DURATION = 20;


    private static final TrackedData<Boolean> ANGRY;
    private static final TrackedData<Boolean> PROVOKED;
    private static final UniformIntProvider ANGER_TIME;
    private UUID target;
    private int angerTime;
    private int ageWhenTargetSet;

    public NightmareEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "nightmare");
        this.stepHeight = 1.0F;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGRY, false);
        this.dataTracker.startTracking(PROVOKED, false);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
    }

    public int getAnimationState() {
        return this.dataTracker.get(ANIMATION);
    }

    public void setState(byte time) {
        this.dataTracker.set(ANIMATION, time);
    }


    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChasePlayerGoal(this));
        this.goalSelector.add(2, new NightmareMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D, 0.0F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TeleportTowardsPlayerGoal(this, this::shouldAngerAt));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(4, new UniversalAngerGoal(this, false));
    }

    protected void mobTick() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimation(ANIMATION_ATTACK);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.world.isDay() && this.age >= this.ageWhenTargetSet + 600) {
            float f = this.getBrightnessAtEyes();
            if (f > 0.5F && this.world.isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((LivingEntity)null);
                this.teleportRandomly();
            }
        }

        super.mobTick();
    }


    public void tickMovement() {
        this.jumping = false;

        if (!this.world.isClient) {
            if (this.getTarget() != null) {
                if (this.getTarget().squaredDistanceTo(this) >= 1024.0D) {
                    stopAnger();
                }
            }
            this.tickAngerLogic((ServerWorld)this.world, true);
        }

        super.tickMovement();
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.world, nbt);
    }


    public static DefaultAttributeContainer.Builder createNightmareAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 28 && !this.world.isClient()) {
            this.world.sendEntityStatus(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source instanceof ProjectileDamageSource) {
            return false;
        } else {
            return super.damage(source, amount);
        }
    }


    public byte getAnimation() {
        return dataTracker.get(ANIMATION);
    }

    public void setAnimation(byte animation) {
        dataTracker.set(ANIMATION, animation);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.dataTracker.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.dataTracker.set(ATTACK_ANIM_TIMER, time);
    }


    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 2, event -> {
            if (isDead()) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_ATTACK && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01)) {
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
            if (getAttackAnimTimer() <= 0 && !(this.isDead() || this.getHealth() < 0.01)) {
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
        this.playSound(SoundEvents.ENTITY_ENDERMAN_AMBIENT, 1.0F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_ENDERMAN_HURT, 1.0F, -10.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F, -10.0F);
    }

    boolean isPlayerStaring(PlayerEntity player) {
        ItemStack itemStack = (ItemStack)player.getInventory().armor.get(3);
        if (itemStack.isOf(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        } else {
            Vec3d vec3d = player.getRotationVec(1.0F).normalize();
            Vec3d vec3d2 = new Vec3d(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
            double d = vec3d2.length();
            vec3d2 = vec3d2.normalize();
            double e = vec3d.dotProduct(vec3d2);

            if (e > 1.0D - 0.025D / d) {
                return player.canSee(this);
            } else {
                return false;
            }
        }
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.55F;
    }

    boolean teleportTo(Entity entity) {
        Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getBodyY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double e = this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D - vec3d.x * 16.0D;
        double f = this.getY() + (double)(this.random.nextInt(4) - 2) - vec3d.y * 16.0D;
        double g = this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D - vec3d.z * 16.0D;
        return this.teleportTo(e, f, g);
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > this.world.getBottomY() && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.world.getBlockState(mutable);
        boolean bl = blockState.getMaterial().blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            boolean bl3 = this.teleport(x, y, z, false);
            if (bl3 && !this.isSilent()) {
                //this.world.playSound((PlayerEntity)null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, -10.0F);
            }

            return bl3;
        } else {
            return false;
        }
    }

    protected boolean teleportRandomly() {
        if (!this.world.isClient() && this.isAlive()) {
            double d = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double e = this.getY() + (double)(this.random.nextInt(64) - 32);
            double f = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            return this.teleportTo(d, e, f);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasAngerTime() {
        return this.dataTracker.get(ANGRY);
    }

    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    public void setProvoked() {
        this.dataTracker.set(PROVOKED, true);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.target;
    }


    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.target = uuid;
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME.get(this.random));
    }

    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, false);
            this.setAngerTime(0);
            this.dataTracker.set(PROVOKED, false);
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
        }

    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayerEntity player) {
            if (player.hasStatusEffect(StatusEffects.BLINDNESS)) {
                TGAdvancements.KILL_WHILE_BLINDED.trigger(player);
            }
        }

        super.onKilledBy(adversary);
    }

    static {
        ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PROVOKED = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME = TimeHelper.betweenSeconds(20, 39);
    }


    static class ChasePlayerGoal extends Goal {
        private final NightmareEntity nightmare;
        @Nullable
        private LivingEntity target;

        public ChasePlayerGoal(NightmareEntity nightmare) {
            this.nightmare = nightmare;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            this.target = this.nightmare.getTarget();
            if (!(this.target instanceof PlayerEntity)) {
                return false;
            } else {
                double d = this.target.squaredDistanceTo(this.nightmare);
                return d > 256.0D ? false : this.nightmare.isPlayerStaring((PlayerEntity)this.target);
            }
        }

        public void start() {
            this.nightmare.getNavigation().stop();
        }

        public void tick() {
            this.nightmare.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    static class TeleportTowardsPlayerGoal extends ActiveTargetGoal<PlayerEntity> {
        private final NightmareEntity nightmare;
        @Nullable
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate = TargetPredicate.createAttackable().ignoreVisibility();

        public TeleportTowardsPlayerGoal(NightmareEntity nightmare, @Nullable Predicate<LivingEntity> targetPredicate) {
            super(nightmare, PlayerEntity.class, 10, false, false, targetPredicate);
            this.nightmare = nightmare;
            this.staringPlayerPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate((playerEntity) -> {
                return nightmare.isPlayerStaring((PlayerEntity)playerEntity);
            });
        }

        public boolean canStart() {
            this.targetPlayer = this.nightmare.world.getClosestPlayer(this.staringPlayerPredicate, this.nightmare);
            return this.targetPlayer != null;
        }

        public void start() {
            this.lookAtPlayerWarmup = this.getTickCount(5);
            this.ticksSinceUnseenTeleport = 0;
            this.nightmare.setProvoked();
        }

        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        public boolean shouldContinue() {
            if (this.targetPlayer != null) {
                if (!this.nightmare.isPlayerStaring(this.targetPlayer)) {
                    return false;
                } else {
                    this.nightmare.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.targetEntity != null && this.validTargetPredicate.test(this.nightmare, this.targetEntity) ? true : super.shouldContinue();
            }
        }

        public void tick() {
            if (this.nightmare.getTarget() == null) {
                super.setTargetEntity((LivingEntity)null);
            }

            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.targetEntity != null && !this.nightmare.hasVehicle()) {
                    // custom teleport injection
                    if (this.nightmare.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                        if (targetEntity.squaredDistanceTo(this.nightmare) <= 384.0D && targetEntity != null) {
                            if (targetEntity.squaredDistanceTo(this.nightmare) > 24.0D) {
                                targetEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100));
                                this.nightmare.teleportTo(targetEntity);
                            }
                        }
                        this.ticksSinceUnseenTeleport = 0;
                    // custom stop
                    } else if (this.targetEntity.squaredDistanceTo(this.nightmare) > 256.0D && this.ticksSinceUnseenTeleport++ >= this.getTickCount(80) && this.nightmare.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }

                super.tick();
            }

        }
    }


}
