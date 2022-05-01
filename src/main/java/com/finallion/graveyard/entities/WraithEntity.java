package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.horde.GraveyardHordeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;

import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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

public class WraithEntity extends HostileEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    private static final UniformIntProvider WANDER_RANGE_HORIZONTAL = UniformIntProvider.create(-5, 5);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", false);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", true);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attacking", true);
    protected final byte ANIMATION_SPAWN = 0;
    protected final byte ANIMATION_IDLE = 1;
    protected final byte ANIMATION_DEATH = 2;
    protected final byte ANIMATION_ATTACK = 3;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(WraithEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Byte> VEX_FLAGS;
    private boolean spawned = false;
    private int spawnTimer;

    public WraithEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    protected void initGoals() {
        super.initGoals();
        //this.goalSelector.add(0, new FlyAroundGoal());
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(4, new ChargeTargetGoal());
        //this.goalSelector.add(5, new WraithWanderAroundGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(VEX_FLAGS, (byte)0);
        spawnTimer = 20;
        setAnimation(ANIMATION_SPAWN);
    }


    @Override
    protected boolean isAffectedByDaylight() {
        return super.isAffectedByDaylight();
    }

    protected boolean burnsInDaylight() {
        return true;
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.WITHER) {
            if (TheGraveyard.config.mobConfigEntries.get("wraith").canBeWithered) {
                return true;
            } else {
                return false;
            }
        }

        return super.canHaveStatusEffect(effect);
    }


    @Override
    public void tickMovement() {
        spawnTimer--;
        if (world.isClient() && spawnTimer >= 0 && spawned) {
            addParticles();
        }


        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight() && TheGraveyard.config.mobConfigEntries.get("wraith").canBurnInSunlight;
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
        } else {
            addParticles();
            this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 1.0F, -10.0F);
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

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    private void addParticles() {
        world.addParticle(ParticleTypes.SOUL, this.getX() + (random.nextDouble() - random.nextDouble()), this.getY(), this.getZ() + (random.nextDouble() - random.nextDouble()), 0, 0.05F, 0);
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
            if (isMoving) {
                controller.setAnimation(ATTACK_ANIMATION);
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
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3).build());
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


    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 1.0F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.BLOCK_SOUL_SAND_HIT, 1.0F, -10.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 1.0F, -10.0F);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

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


    static {
        VEX_FLAGS = DataTracker.registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
    }


    private class ChargeTargetGoal extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            if (WraithEntity.this.getTarget() != null && !WraithEntity.this.getMoveControl().isMoving() && WraithEntity.this.random.nextInt(toGoalTicks(7)) == 0) {
                return WraithEntity.this.squaredDistanceTo(WraithEntity.this.getTarget()) > 4.0D;
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
                Vec3d vec3d = livingEntity.getEyePos();
                WraithEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
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
                    if (d < 9.0D) {
                        Vec3d vec3d = livingEntity.getEyePos();
                        WraithEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                    }
                }

            }
        }
    }

    private class FlyAroundGoal extends Goal {

        public FlyAroundGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !WraithEntity.this.getMoveControl().isMoving();
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos thisPos = WraithEntity.this.getBlockPos();
            for(int i = 0; i < 3; ++i) {
                BlockPos randomTarget = thisPos.add(WANDER_RANGE_HORIZONTAL.get(random), 0, WANDER_RANGE_HORIZONTAL.get(random));

                if (WraithEntity.this.world.canSetBlock(randomTarget)) {
                    WraithEntity.this.moveControl.moveTo((double) randomTarget.getX() + 0.5D, (double) randomTarget.getY() + 0.5D, (double) randomTarget.getZ() + 0.5D, 0.25D);

                    if(WraithEntity.this.getTarget() == null) {
                        WraithEntity.this.getLookControl().lookAt((double) randomTarget.getX() + 0.5D, (double) randomTarget.getY() + 0.5D, (double) randomTarget.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }

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

    class WraithWanderAroundGoal extends Goal {
        private final WraithEntity wraith;

        public WraithWanderAroundGoal(WraithEntity wraith) {
            this.wraith = wraith;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return this.wraith.getNavigation().isIdle() && this.wraith.getRandom().nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return this.wraith.getNavigation().isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d randomLocation = this.getRandomLocation();

            if (randomLocation == null) {
                return;
            }

            Path path = this.wraith.getNavigation().findPathTo(randomLocation.getX(), randomLocation.getY(), randomLocation.getZ(), 0);
            this.wraith.getNavigation().startMovingAlong(path, this.wraith.getMovementSpeed());
        }

        private Vec3d getRandomLocation() {
            var isAboveSurfaceLevel = this.wraith.getBlockPos().getY() >= 63;
            Vec3d rotationVec = this.wraith.getRotationVec(0.0F);
            Vec3d positionVec = FuzzyTargeting.find(this.wraith, 8, 4);

            /*
            if (isAboveSurfaceLevel) {
                positionVec = AboveGroundTargeting.find(this.wraith, 8, 4, rotationVec.getX(), rotationVec.getZ(), 1.5707964F, 1, 1);

                if (positionVec == null) {
                    positionVec = NoPenaltySolidTargeting.find(this.wraith, 8, 4, -2, rotationVec.getX(), rotationVec.getZ(), 1.5707963705062866D);
                }
            } else {
                positionVec = FuzzyTargeting.find(this.wraith, 8, 4);
            }

             */

            return positionVec;
        }
    }
}
