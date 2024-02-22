package com.lion.graveyard.entities;

import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class ReaperEntity extends HostileGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    protected final byte ANIMATION_IDLE = 0;
    protected final byte ANIMATION_WALK = 1;
    protected final byte ANIMATION_SPAWN = 2;
    protected final byte ANIMATION_DEATH = 3;
    protected final byte ANIMATION_ATTACK = 4;
    protected static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(ReaperEntity.class, EntityDataSerializers.BYTE);

    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(ReaperEntity.class, EntityDataSerializers.BYTE);;
    private static final int CHARGING_FLAG = 1;
    Mob owner;
    @Nullable
    private BlockPos bounds;

    public ReaperEntity(EntityType<? extends ReaperEntity> entityType, Level world) {
        super(entityType, world, "reaper");
        this.moveControl = new ReaperMoveControl(this);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VEX_FLAGS, (byte)0);
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("BoundX")) {
            this.bounds = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
        }
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.bounds != null) {
            nbt.putInt("BoundX", this.bounds.getX());
            nbt.putInt("BoundY", this.bounds.getY());
            nbt.putInt("BoundZ", this.bounds.getZ());
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new ChargeTargetGoal());
        this.goalSelector.addGoal(8, new LookAtTargetGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new TrackOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public void move(MoverType movementType, Vec3 movement) {
        super.move(movementType, movement);
        this.checkInsideBlocks();
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }


    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        setAnimation(ANIMATION_SPAWN);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }


    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }


    private boolean areFlagsSet(int mask) {
        int i = (Byte)this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = (Byte)this.entityData.get(VEX_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    public static AttributeSupplier.Builder createReaperAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }


    public byte getAnimation() {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(byte animation) {
        entityData.set(ANIMATION, animation);
    }


    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 2, event -> {
            AnimationController controller = event.getController();
            float limbSwingAmount = event.getLimbSwingAmount();
            boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);
            boolean isDying = this.isDeadOrDying();
            boolean isAggressive = this.isAggressive();

            if (isDying) {
                controller.setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (isAggressive || isCharging()) {
                controller.setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            byte currentAnimation = getAnimation();
            switch (currentAnimation) {
                case ANIMATION_ATTACK -> controller.setAnimation(ATTACK_ANIMATION);
                default -> controller.setAnimation(isMoving ? WALK_ANIMATION : IDLE_ANIMATION);
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
        this.playSound(TGSounds.REAPER_AMBIENT.get(), 1.0F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.REAPER_HURT.get(), 1.0F, -10.0F);
    }

    protected SoundEvent getDeathSound() {
        return TGSounds.REAPER_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return -10.0F;
    }

    private class ReaperMoveControl extends MoveControl {
        public ReaperMoveControl(ReaperEntity owner) {
            super(owner);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vec3d = new Vec3(this.wantedX - ReaperEntity.this.getX(), this.wantedY - ReaperEntity.this.getY(), this.wantedZ - ReaperEntity.this.getZ());
                double d = vec3d.length();
                if (d < ReaperEntity.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    ReaperEntity.this.setDeltaMovement(ReaperEntity.this.getDeltaMovement().scale(0.5D));
                } else {
                    ReaperEntity.this.setDeltaMovement(ReaperEntity.this.getDeltaMovement().add(vec3d.scale(this.speedModifier * 0.05D / d)));
                    if (ReaperEntity.this.getTarget() == null) {
                        Vec3 vec3d2 = ReaperEntity.this.getDeltaMovement();
                        ReaperEntity.this.setYRot(-((float)Mth.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
                        ReaperEntity.this.yBodyRot = ReaperEntity.this.getYRot();
                    } else {
                        double e = ReaperEntity.this.getTarget().getX() - ReaperEntity.this.getX();
                        double f = ReaperEntity.this.getTarget().getZ() - ReaperEntity.this.getZ();
                        ReaperEntity.this.setYRot(-((float)Mth.atan2(e, f)) * 57.295776F);
                        ReaperEntity.this.yBodyRot = ReaperEntity.this.getYRot();
                    }
                }

            }
        }
    }

    class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (ReaperEntity.this.getTarget() != null && !ReaperEntity.this.getMoveControl().hasWanted() && ReaperEntity.this.random.nextInt(7) == 0) {
                return ReaperEntity.this.distanceToSqr(ReaperEntity.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return ReaperEntity.this.getMoveControl().hasWanted() && ReaperEntity.this.isCharging() && ReaperEntity.this.getTarget() != null && ReaperEntity.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingEntity = ReaperEntity.this.getTarget();
            Vec3 vec3d = livingEntity.getEyePosition();
            ReaperEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            ReaperEntity.this.setCharging(true);
            ReaperEntity.this.playSound(TGSounds.REAPER_CHARGE.get(), 1.0F, -10.0F);
        }

        public void stop() {
            ReaperEntity.this.setCharging(false);
        }

        public void tick() {
            LivingEntity livingEntity = ReaperEntity.this.getTarget();
            if (ReaperEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                ReaperEntity.this.doHurtTarget(livingEntity);
                ReaperEntity.this.setCharging(false);
            } else {
                double d = ReaperEntity.this.distanceToSqr(livingEntity);
                if (d < 9.0D) {
                    Vec3 vec3d = livingEntity.getEyePosition();
                    ReaperEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }

    private class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !ReaperEntity.this.getMoveControl().hasWanted() && ReaperEntity.this.random.nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockPos = ReaperEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = ReaperEntity.this.blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.offset(ReaperEntity.this.random.nextInt(15) - 7, ReaperEntity.this.random.nextInt(11) - 5, ReaperEntity.this.random.nextInt(15) - 7);
                if (ReaperEntity.this.level().isEmptyBlock(blockPos2)) {
                    ReaperEntity.this.moveControl.setWantedPosition((double)blockPos2.getX() + 0.5D, (double)blockPos2.getY() + 0.5D, (double)blockPos2.getZ() + 0.5D, 0.25D);
                    if (ReaperEntity.this.getTarget() == null) {
                        ReaperEntity.this.getLookControl().setLookAt((double)blockPos2.getX() + 0.5D, (double)blockPos2.getY() + 0.5D, (double)blockPos2.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class TrackOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions TRACK_OWNER_PREDICATE = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public TrackOwnerTargetGoal(PathfinderMob mob) {
            super(mob, false);
        }

        public boolean canUse() {
            return ReaperEntity.this.owner != null && ReaperEntity.this.owner.getTarget() != null && this.canAttack(ReaperEntity.this.owner.getTarget(), this.TRACK_OWNER_PREDICATE);
        }

        public void start() {
            ReaperEntity.this.setTarget(ReaperEntity.this.owner.getTarget());
            super.start();
        }
    }


}
