package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blocks.BrazierBlock;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;

public class WraithEntity extends HostileGraveyardEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    private final AnimationBuilder MOVE_ANIMATION = new AnimationBuilder().addAnimation("moving", true);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attacking", true);
    protected final byte ANIMATION_SPAWN = 0;
    protected final byte ANIMATION_IDLE = 1;
    protected final byte ANIMATION_DEATH = 2;
    protected final byte ANIMATION_ATTACK = 3;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(WraithEntity.class, TrackedDataHandlerRegistry.BYTE);
    private boolean spawned = false;
    private int spawnTimer;

    private static final UniformIntProvider WANDER_RANGE_HORIZONTAL = UniformIntProvider.create(-7, 7);
    private static final UniformIntProvider WANDER_RANGE_VERTICAL = UniformIntProvider.create(-5, 5);
    protected static final TrackedData<Byte> VEX_FLAGS = DataTracker.registerData(WraithEntity.class, TrackedDataHandlerRegistry.BYTE);
    private BlockPos homePosition;
    private int timeSinceExtinguish;


    public WraithEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "wraith");
        this.moveControl = new WraithMoveControl(this);

    }

    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        this.checkBlockCollision();
    }


    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        //this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(7, new ExtinguishGoal());
        this.goalSelector.add(8, new FlyAroundGoal());
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VEX_FLAGS, (byte)0);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        spawnTimer = 20;
        setAnimation(ANIMATION_SPAWN);
    }

    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
        if (getHomePosition() == null) {
            homePosition = WraithEntity.this.getBlockPos();
        }

        if (this.random.nextFloat() < 0.025F) {
            addParticles();

        }
    }

    private BlockPos getHomePosition() {
        return homePosition;
    }
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.homePosition != null) {
            nbt.putInt("BoundX", this.homePosition.getX());
            nbt.putInt("BoundY", this.homePosition.getY());
            nbt.putInt("BoundZ", this.homePosition.getZ());
        }

        nbt.putInt("timeSinceExtinguish", this.timeSinceExtinguish);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BoundX")) {
            this.homePosition = new BlockPos(
                    nbt.getInt("BoundX"),
                    nbt.getInt("BoundY"),
                    nbt.getInt("BoundZ"));
        }
        this.timeSinceExtinguish = nbt.getInt("timeSinceExtinguish");
    }

    @Override
    public void tickMovement() {
        spawnTimer--;
        if (world.isClient() && spawnTimer >= 0 && spawned) {
            addParticles();
        }


        if (!this.isAlive()) {
            addDeathParticles();
        }
        super.tickMovement();
    }


    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 43 && !this.world.isClient()) {
            this.world.sendEntityStatus(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }


    private void addParticles() {
        world.addParticle(ParticleTypes.SOUL, this.getX() + (random.nextDouble() - random.nextDouble()), this.getY(), this.getZ() + (random.nextDouble() - random.nextDouble()), 0, 0.05F, 0);
    }

    private void addDeathParticles() {
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.SMOKE, this.getX() + (random.nextDouble() - random.nextDouble()), this.getY() + 1.0D, this.getZ() + (random.nextDouble() - random.nextDouble()), 0, 0.05F, 0);

        }
    }

    int getTimeSinceExtinguish() {
        return this.timeSinceExtinguish;
    }


    void addExtinguishCounter() {
        ++this.timeSinceExtinguish;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource == DamageSource.LAVA ||
                damageSource == DamageSource.IN_WALL ||
                damageSource == DamageSource.CACTUS ||
                damageSource == DamageSource.DROWN ||
                damageSource == DamageSource.SWEET_BERRY_BUSH ||
                damageSource == DamageSource.HOT_FLOOR ||
                damageSource == DamageSource.FLY_INTO_WALL ||
                damageSource == DamageSource.FALLING_BLOCK ||
                damageSource == DamageSource.FALL ||
                damageSource == DamageSource.ANVIL)
            return true;

        return super.isInvulnerableTo(damageSource);
    }


    @SuppressWarnings("rawtypes")
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        float limbSwingAmount = event.getLimbSwingAmount();
        boolean isMoving = !(limbSwingAmount > -0.05F && limbSwingAmount < 0.05F);

        if (this.isDead()) {
            controller.setAnimation(DEATH_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (spawnTimer < 0) {
            if (isCharging() || isAttacking()) {
                controller.setAnimation(ATTACK_ANIMATION);
            } else if (isMoving) {
                controller.setAnimation(MOVE_ANIMATION);
            } else {
                controller.setAnimation(IDLE_ANIMATION);
            }
            return PlayState.CONTINUE;

        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if (getAnimation() == 0) {
            event.getController().setAnimation(SPAWN_ANIMATION);
            spawned = true;

            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                    .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.2).build());
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
        data.addAnimationController(new AnimationController(this, "controller2", 0, this::predicate2));
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public boolean isCharging() {
        return this.areFlagsSet(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
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

    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 1.5F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.BLOCK_SOUL_SAND_HIT, 1.0F, -10.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        for (int i = 0; i < 10; i++) {
            this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 2.0F, -10.0F);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    private class WraithMoveControl extends MoveControl {
        public WraithMoveControl(WraithEntity owner) {
            super(owner);
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - WraithEntity.this.getX(), this.targetY - WraithEntity.this.getY(), this.targetZ - WraithEntity.this.getZ());
                double d = vec3d.length();
                if (d < WraithEntity.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    WraithEntity.this.setVelocity(WraithEntity.this.getVelocity().multiply(0.5D));
                } else {
                    WraithEntity.this.setVelocity(WraithEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05D / d)));
                    if (WraithEntity.this.getTarget() == null) {
                        Vec3d vec3d2x = WraithEntity.this.getVelocity();
                        WraithEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2x.x, vec3d2x.z)) * 57.295776F);
                        WraithEntity.this.bodyYaw = WraithEntity.this.getYaw();
                    } else {
                        double vec3d2 = WraithEntity.this.getTarget().getX() - WraithEntity.this.getX();
                        double e = WraithEntity.this.getTarget().getZ() - WraithEntity.this.getZ();
                        WraithEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2, e)) * 57.295776F);
                        WraithEntity.this.bodyYaw = WraithEntity.this.getYaw();
                    }
                }

            }
        }
    }


    class FlyAroundGoal extends Goal {
        private FlyAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !WraithEntity.this.getMoveControl().isMoving() && WraithEntity.this.random.nextInt(2) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            if (getHomePosition() == null) homePosition = getBlockPos();

            for (int i = 0; i < 3; ++i) {
                BlockPos randomTarget = homePosition.add(WANDER_RANGE_HORIZONTAL.get(random), WANDER_RANGE_VERTICAL.get(random), WANDER_RANGE_HORIZONTAL.get(random));

                if (WraithEntity.this.world.isAir(randomTarget)) {
                    WraithEntity.this.moveControl.moveTo((double) randomTarget.getX() + 0.5D, (double) randomTarget.getY() + 0.5D, (double) randomTarget.getZ() + 0.5D, 0.25D);
                    if(WraithEntity.this.getTarget() == null) {
                        WraithEntity.this.getLookControl().lookAt((double) randomTarget.getX() + 0.5D, (double) randomTarget.getY() + 0.5D, (double) randomTarget.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }

    private class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            if (WraithEntity.this.getTarget() != null && !WraithEntity.this.getMoveControl().isMoving() && WraithEntity.this.random.nextInt(toGoalTicks(3)) == 0) {
                return WraithEntity.this.squaredDistanceTo(WraithEntity.this.getTarget()) > 2.0D;
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            return WraithEntity.this.getMoveControl().isMoving() && WraithEntity.this.isCharging() && WraithEntity.this.getTarget() != null && WraithEntity.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingEntity = WraithEntity.this.getTarget();
            if (livingEntity != null) {
                BlockPos pos = livingEntity.getBlockPos();
                WraithEntity.this.moveControl.moveTo(pos.getX(), pos.getY() + 0.5D, pos.getZ(), 0.6D);
            }

            WraithEntity.this.setCharging(true);
        }

        public void stop() {
            WraithEntity.this.setCharging(false);
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = WraithEntity.this.getTarget();
            if (livingEntity != null) {
                if (WraithEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                    WraithEntity.this.tryAttack(livingEntity);
                    WraithEntity.this.setCharging(false);
                } else {
                    double d = WraithEntity.this.squaredDistanceTo(livingEntity);
                    if (d < 6.0D) {
                        Vec3d vec3d = livingEntity.getEyePos();
                        WraithEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 0.6D);
                    }
                }

            }
        }
    }

    private class ExtinguishGoal extends Goal {

        ExtinguishGoal() {
            super();
        }

        public boolean canStart() {
            if (WraithEntity.this.getTimeSinceExtinguish() >= 10) {
                return false;
            } else if (WraithEntity.this.random.nextFloat() < 0.3F) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart();
        }

        public void tick() {
            if (WraithEntity.this.random.nextInt(this.getTickCount(10)) == 0) {
                for(int i = 1; i <= 2; ++i) {
                    BlockPos blockPos = WraithEntity.this.getBlockPos().down(i);
                    BlockState blockState = WraithEntity.this.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    boolean bl = false;
                    if (blockState.isIn(BlockTags.CANDLES)) {
                        if (block instanceof CandleBlock) {
                            if (blockState.get(Properties.LIT)) {
                                bl = true;
                            }
                        } else if (block instanceof BrazierBlock) {
                            if (blockState.get(Properties.LIT)) {
                                bl = true;
                            }
                        }
                        if (bl) {
                            //WraithEntity.this.world.syncWorldEvent(1502, blockPos, 0);
                            WraithEntity.this.world.playSound((PlayerEntity)null, blockPos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            WraithEntity.this.world.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, false));
                            WraithEntity.this.addExtinguishCounter();
                        }
                    }
                }

            }
        }
    }
}
