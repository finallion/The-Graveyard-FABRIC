package com.finallion.graveyard.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;

public class ReaperEntity extends HostileEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder WALK_ANIMATION = new AnimationBuilder().addAnimation("walk", true);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attack", true);
    protected final byte ANIMATION_IDLE = 0;
    protected final byte ANIMATION_WALK = 1;
    protected final byte ANIMATION_SPAWN = 2;
    protected final byte ANIMATION_DEATH = 3;
    protected final byte ANIMATION_ATTACK = 4;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(ReaperEntity.class, TrackedDataHandlerRegistry.BYTE);

    protected static final TrackedData<Byte> VEX_FLAGS = DataTracker.registerData(ReaperEntity.class, TrackedDataHandlerRegistry.BYTE);;
    private static final int CHARGING_FLAG = 1;
    MobEntity owner;
    @Nullable
    private BlockPos bounds;

    public ReaperEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new ReaperMoveControl(this);
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VEX_FLAGS, (byte)0);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BoundX")) {
            this.bounds = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.bounds != null) {
            nbt.putInt("BoundX", this.bounds.getX());
            nbt.putInt("BoundY", this.bounds.getY());
            nbt.putInt("BoundZ", this.bounds.getZ());
        }
    }

    @Override
    protected boolean isAffectedByDaylight() {
        return super.isAffectedByDaylight();
    }

    protected boolean burnsInDaylight() {
        return true;
    }

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight();
            if (bl) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }
        super.tickMovement();
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(8, new LookAtTargetGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new TrackOwnerTargetGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, true));
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        this.checkBlockCollision();
    }

    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
    }


    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        setAnimation(ANIMATION_SPAWN);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @SuppressWarnings("rawtypes")
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);
        boolean isDying = this.isDead();
        boolean isAttacking = this.isAttacking();

        if (isDying) {
            controller.setAnimation(DEATH_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (isAttacking || isCharging()) {
            controller.setAnimation(ATTACK_ANIMATION);
            return PlayState.CONTINUE;
        }

        byte currentAnimation = getAnimation();
        switch (currentAnimation) {
            case ANIMATION_ATTACK -> controller.setAnimation(ATTACK_ANIMATION);
            default -> controller.setAnimation(isMoving ? WALK_ANIMATION : IDLE_ANIMATION);
        }

        return PlayState.CONTINUE;
    }

    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }


    private boolean areFlagsSet(int mask) {
        int i = (Byte)this.dataTracker.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = (Byte)this.dataTracker.get(VEX_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.dataTracker.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }


    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D).build());
        }
        return attributeContainer;
    }

    public byte getAnimation() {
        return dataTracker.get(ANIMATION);
    }

    public void setAnimation(byte animation) {
        dataTracker.set(ANIMATION, animation);
    }


    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 2, this::predicate));
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }


    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_VEX_AMBIENT, 1.0F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_VEX_HURT, 1.0F, -10.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.ENTITY_VEX_DEATH, 1.0F, -10.0F);
    }

    private class ReaperMoveControl extends MoveControl {
        public ReaperMoveControl(ReaperEntity owner) {
            super(owner);
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - ReaperEntity.this.getX(), this.targetY - ReaperEntity.this.getY(), this.targetZ - ReaperEntity.this.getZ());
                double d = vec3d.length();
                if (d < ReaperEntity.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    ReaperEntity.this.setVelocity(ReaperEntity.this.getVelocity().multiply(0.5D));
                } else {
                    ReaperEntity.this.setVelocity(ReaperEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05D / d)));
                    if (ReaperEntity.this.getTarget() == null) {
                        Vec3d vec3d2 = ReaperEntity.this.getVelocity();
                        ReaperEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
                        ReaperEntity.this.bodyYaw = ReaperEntity.this.getYaw();
                    } else {
                        double e = ReaperEntity.this.getTarget().getX() - ReaperEntity.this.getX();
                        double f = ReaperEntity.this.getTarget().getZ() - ReaperEntity.this.getZ();
                        ReaperEntity.this.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                        ReaperEntity.this.bodyYaw = ReaperEntity.this.getYaw();
                    }
                }

            }
        }
    }

    class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            if (ReaperEntity.this.getTarget() != null && !ReaperEntity.this.getMoveControl().isMoving() && ReaperEntity.this.random.nextInt(7) == 0) {
                return ReaperEntity.this.squaredDistanceTo(ReaperEntity.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            return ReaperEntity.this.getMoveControl().isMoving() && ReaperEntity.this.isCharging() && ReaperEntity.this.getTarget() != null && ReaperEntity.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingEntity = ReaperEntity.this.getTarget();
            Vec3d vec3d = livingEntity.getEyePos();
            ReaperEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            ReaperEntity.this.setCharging(true);
            ReaperEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, -10.0F);
        }

        public void stop() {
            ReaperEntity.this.setCharging(false);
        }

        public void tick() {
            LivingEntity livingEntity = ReaperEntity.this.getTarget();
            if (ReaperEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                ReaperEntity.this.tryAttack(livingEntity);
                ReaperEntity.this.setCharging(false);
            } else {
                double d = ReaperEntity.this.squaredDistanceTo(livingEntity);
                if (d < 9.0D) {
                    Vec3d vec3d = livingEntity.getEyePos();
                    ReaperEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }

    private class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            return !ReaperEntity.this.getMoveControl().isMoving() && ReaperEntity.this.random.nextInt(7) == 0;
        }

        public boolean shouldContinue() {
            return false;
        }

        public void tick() {
            BlockPos blockPos = ReaperEntity.this.getBounds();
            if (blockPos == null) {
                blockPos = ReaperEntity.this.getBlockPos();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(ReaperEntity.this.random.nextInt(15) - 7, ReaperEntity.this.random.nextInt(11) - 5, ReaperEntity.this.random.nextInt(15) - 7);
                if (ReaperEntity.this.world.isAir(blockPos2)) {
                    ReaperEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5D, (double)blockPos2.getY() + 0.5D, (double)blockPos2.getZ() + 0.5D, 0.25D);
                    if (ReaperEntity.this.getTarget() == null) {
                        ReaperEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5D, (double)blockPos2.getY() + 0.5D, (double)blockPos2.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class TrackOwnerTargetGoal extends TrackTargetGoal {
        private final TargetPredicate TRACK_OWNER_PREDICATE = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

        public TrackOwnerTargetGoal(PathAwareEntity mob) {
            super(mob, false);
        }

        public boolean canStart() {
            return ReaperEntity.this.owner != null && ReaperEntity.this.owner.getTarget() != null && this.canTrack(ReaperEntity.this.owner.getTarget(), this.TRACK_OWNER_PREDICATE);
        }

        public void start() {
            ReaperEntity.this.setTarget(ReaperEntity.this.owner.getTarget());
            super.start();
        }
    }


}
