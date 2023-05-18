package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blocks.BrazierBlock;
import com.finallion.graveyard.init.TGAdvancements;
import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
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
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.UUID;

public class WraithEntity extends HostileGraveyardEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.2D, EntityAttributeModifier.Operation.ADDITION);
    private final AnimationBuilder DEATH_ANIMATION = new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private final AnimationBuilder IDLE_ANIMATION = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private final AnimationBuilder MOVE_ANIMATION = new AnimationBuilder().addAnimation("moving", ILoopType.EDefaultLoopTypes.LOOP);
    private final AnimationBuilder ATTACK_ANIMATION = new AnimationBuilder().addAnimation("attacking", ILoopType.EDefaultLoopTypes.LOOP);
    protected final byte ANIMATION_SPAWN = 0;
    protected final byte ANIMATION_IDLE = 1;
    protected final byte ANIMATION_DEATH = 2;
    protected final byte ANIMATION_ATTACK = 3;
    protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(WraithEntity.class, TrackedDataHandlerRegistry.BYTE);
    private boolean spawned = false;
    private int spawnTimer;

    @Nullable
    private BlockPos homePosition;
    private int timeSinceExtinguish;


    public WraithEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "wraith");
        //this.moveControl = new WraithMoveControl(this);
        this.moveControl = new FlightMoveControl(this, 0, true);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0F);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0F);
    }


    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        this.checkBlockCollision();
    }

    public void slowMovement(BlockState state, Vec3d multiplier) {
        if (!state.isOf(Blocks.COBWEB)) {
            super.slowMovement(state, multiplier);
        }

    }


    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new WraithMeleeGoal(this, 1.0D, false));
        this.goalSelector.add(3, new FlyGoal(this, 1.0D));
        //this.goalSelector.add(3, new WraithWanderAroundGoal());
        //this.goalSelector.add(4, new ChargeTargetGoal());
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(7, new ExtinguishGoal());
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
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

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source instanceof ProjectileDamageSource) {
            Entity entity = source.getSource();
            if (entity instanceof ArrowEntity) {
               return false;
            }
            return super.damage(source, amount);
        } else {
            return super.damage(source, amount);
        }
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        spawnTimer = 20;
        setAnimation(ANIMATION_SPAWN);
    }

    public void tick() {
        super.tick();
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

        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED);
        if (!isAttacking()) {
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
        }



        if (!this.isAlive()) {
            addDeathParticles();
        }
        super.tickMovement();
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        world.playSound(null, this.getBlockPos(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.HOSTILE,2.0F, -5.0F);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 42 && !this.world.isClient()) {
            this.world.sendEntityStatus(this, (byte) 60);
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

    @Debug
    public boolean hasHomePosition() {
        return this.homePosition != null;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource == DamageSource.CACTUS ||
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
            if (isAttacking()) {
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



    public static DefaultAttributeContainer.Builder createWraithAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 27.5D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.5D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.35);
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

    boolean isWithinDistance(BlockPos pos, int distance) {
        if (pos == null) {
            return false;
        }
        return pos.isWithinDistance(this.getBlockPos(), (double) distance);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.WRAITH_AMBIENT, 1.5F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.WRAITH_HURT, 1.0F, -10.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        playDeathSound();
    }

    private void playDeathSound() {
        for (int i = 0; i < 10; i++) {
            this.playSound(TGSounds.WRAITH_AMBIENT, 2.5F, -10.0F);
        }
    }


    @Override
    public boolean collidesWith(Entity other) {
        if (other instanceof WraithEntity) {
            return false;
        }
        return super.collidesWith(other);
    }

    @Override
    protected void playBlockFallSound() {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    class WraithMeleeGoal extends MeleeAttackGoal {
        WraithMeleeGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
        }

        public boolean canStart() {
            return super.canStart();
        }

        public boolean shouldContinue() {
            return super.shouldContinue();
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
                for (int i = 1; i <= 2; ++i) {
                    BlockPos blockPos = WraithEntity.this.getBlockPos().down(i);
                    BlockState blockState = WraithEntity.this.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    boolean bl = false;
                    boolean torchAndLantern = false;
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
                            WraithEntity.this.world.playSound((PlayerEntity) null, blockPos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            WraithEntity.this.world.setBlockState(blockPos, (BlockState) blockState.with(Properties.LIT, false));
                            WraithEntity.this.addExtinguishCounter();
                            triggerAdvancement(WraithEntity.this, bl);
                            return;
                        }
                    }
                    if (random.nextInt(10) == 0) {
                        if (blockState.isOf(Blocks.TORCH)) {
                            WraithEntity.this.world.setBlockState(blockPos, Blocks.SOUL_TORCH.getDefaultState());
                            torchAndLantern = true;
                        } else if (blockState.isOf(Blocks.LANTERN)) {
                            WraithEntity.this.world.setBlockState(blockPos, Blocks.SOUL_LANTERN.getDefaultState()
                                    .with(Properties.HANGING, blockState.get(Properties.HANGING))
                                    .with(Properties.WATERLOGGED, blockState.get(Properties.WATERLOGGED)));
                            torchAndLantern = true;
                        } else if (blockState.isOf(Blocks.WALL_TORCH)) {
                            WraithEntity.this.world.setBlockState(blockPos, Blocks.SOUL_WALL_TORCH.getDefaultState()
                                    .with(HorizontalFacingBlock.FACING, blockState.get(HorizontalFacingBlock.FACING)));
                            torchAndLantern = true;
                        }

                        if (torchAndLantern) {
                            WraithEntity.this.world.playSound((PlayerEntity) null, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            WraithEntity.this.addExtinguishCounter();
                            return;
                        }
                    }
                }

            }
        }
    }

    private static void triggerAdvancement(WraithEntity wraith, boolean bool) {
        if (bool) {
            PlayerEntity player = wraith.world.getClosestPlayer(wraith, 10.0D);
            if (player instanceof ServerPlayerEntity) {
                TGAdvancements.DIM_LIGHT.trigger((ServerPlayerEntity) player);
            }
        }
    }


    class WraithWanderAroundGoal extends Goal {
        private static final int MAX_DISTANCE = 15;

        WraithWanderAroundGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            return WraithEntity.this.navigation.isIdle() && WraithEntity.this.random.nextInt(10) == 0;
        }


        public boolean shouldContinue() {
            return WraithEntity.this.navigation.isFollowingPath();
        }


        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                WraithEntity.this.navigation.startMovingAlong(WraithEntity.this.navigation.findPathTo(new BlockPos(vec3d), 1), 1.0D);
            }

        }

        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec3d2;
            if (!WraithEntity.this.isWithinDistance(WraithEntity.this.homePosition, MAX_DISTANCE) && WraithEntity.this.hasHomePosition()) {
                Vec3d vec3dx = Vec3d.ofCenter(WraithEntity.this.homePosition);
                vec3d2 = vec3dx.subtract(WraithEntity.this.getPos()).normalize();
            } else {
                vec3d2 = WraithEntity.this.getRotationVec(0.0F);
            }


            Vec3d vec3d3 = AboveGroundTargeting.find(WraithEntity.this, 8, 2, vec3d2.x, vec3d2.z, 1.5707964F, 2, 1);
            return vec3d3 != null ? vec3d3 : NoPenaltySolidTargeting.find(WraithEntity.this, 8, 2, -2, vec3d2.x, vec3d2.z, 1.5707963705062866D);
        }
    }

}
