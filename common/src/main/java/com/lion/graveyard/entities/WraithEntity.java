package com.lion.graveyard.entities;

import com.lion.graveyard.blocks.BrazierBlock;
import com.lion.graveyard.init.TGCriteria;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
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

public class WraithEntity extends HostileGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.2D, AttributeModifier.Operation.ADDITION);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("moving", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attacking", Animation.LoopType.LOOP);
    protected final byte ANIMATION_SPAWN = 0;
    protected final byte ANIMATION_IDLE = 1;
    protected final byte ANIMATION_DEATH = 2;
    protected final byte ANIMATION_ATTACK = 3;
    protected static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(WraithEntity.class, EntityDataSerializers.BYTE);
    private boolean spawned = false;
    private int spawnTimer;

    @Nullable
    private BlockPos homePosition;
    private int timeSinceExtinguish;


    public WraithEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, "wraith");
        //this.moveControl = new WraithMoveControl(this);
        this.moveControl = new FlyingMoveControl(this, 0, true);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
    }


    public void move(MoverType movementType, Vec3 movement) {
        super.move(movementType, movement);
        this.checkInsideBlocks();
    }

    public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, multiplier);
        }

    }


    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WraithMeleeGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new ExtinguishGoal());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world) {
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanFloat(false);
        birdNavigation.setCanPassDoors(true);
        return birdNavigation;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            Entity entity = source.getEntity();
            if (entity instanceof Arrow) {
               return false;
            }
            return super.hurt(source, amount);
        } else {
            return super.hurt(source, amount);
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION, ANIMATION_IDLE);
        spawnTimer = 20;
        setAnimation(ANIMATION_SPAWN);
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);

        if (getHomePosition() == null) {
            homePosition = WraithEntity.this.blockPosition();
        }

        if (this.random.nextFloat() < 0.025F) {
            addParticles();

        }
    }


    private BlockPos getHomePosition() {
        return homePosition;
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.homePosition != null) {
            nbt.putInt("BoundX", this.homePosition.getX());
            nbt.putInt("BoundY", this.homePosition.getY());
            nbt.putInt("BoundZ", this.homePosition.getZ());
        }

        nbt.putInt("timeSinceExtinguish", this.timeSinceExtinguish);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("BoundX")) {
            this.homePosition = new BlockPos(
                    nbt.getInt("BoundX"),
                    nbt.getInt("BoundY"),
                    nbt.getInt("BoundZ"));
        }
        this.timeSinceExtinguish = nbt.getInt("timeSinceExtinguish");
    }



    @Override
    public void aiStep() {
        spawnTimer--;
        if (level().isClientSide() && spawnTimer >= 0 && spawned) {
            addParticles();
        }

        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.FLYING_SPEED);
        if (!isAggressive()) {
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST.getId());
        } else {
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTransientModifier(ATTACKING_SPEED_BOOST);
            }
        }



        if (!this.isAlive()) {
            addDeathParticles();
        }
        super.aiStep();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        world.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE,2.0F, -5.0F);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        for (int i = 0; i < 5; i++) {
            this.playSound(TGSounds.WRAITH_AMBIENT.get(), 2.0F, -10.0F);
        }
        if (this.deathTime == 42 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }


    private void addParticles() {
        level().addParticle(ParticleTypes.SOUL, this.getX() + (random.nextDouble() - random.nextDouble()), this.getY(), this.getZ() + (random.nextDouble() - random.nextDouble()), 0, 0.05F, 0);
    }

    private void addDeathParticles() {
        for (int i = 0; i < 10; i++) {
            level().addParticle(ParticleTypes.SMOKE, this.getX() + (random.nextDouble() - random.nextDouble()), this.getY() + 1.0D, this.getZ() + (random.nextDouble() - random.nextDouble()), 0, 0.05F, 0);
        }
    }

    int getTimeSinceExtinguish() {
        return this.timeSinceExtinguish;
    }


    void addExtinguishCounter() {
        ++this.timeSinceExtinguish;
    }


    public boolean hasHomePosition() {
        return this.homePosition != null;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.BYPASSES_ARMOR) ||
                damageSource.is(DamageTypeTags.BYPASSES_EFFECTS) ||
                damageSource.is(DamageTypeTags.BYPASSES_SHIELD))
            return true;

        return super.isInvulnerableTo(damageSource);
    }


    public static AttributeSupplier.Builder createWraithAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 27.5D)
                .add(Attributes.ATTACK_DAMAGE, 6.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.35);
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

            if (this.isDeadOrDying()) {
                controller.setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (spawnTimer < 0) {
                if (isAggressive()) {
                    controller.setAnimation(ATTACK_ANIMATION);
                } else if (isMoving) {
                    controller.setAnimation(MOVE_ANIMATION);
                } else {
                    controller.setAnimation(IDLE_ANIMATION);
                }
                return PlayState.CONTINUE;

            }
            return PlayState.CONTINUE;
        }));
        data.add(new AnimationController(this, "controller2", 0, event -> {
            if (getAnimation() == 0) {
                event.getController().setAnimation(SPAWN_ANIMATION);
                spawned = true;

                return PlayState.CONTINUE;
            }
            return PlayState.CONTINUE;
        }));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    boolean closerThan(BlockPos pos, int distance) {
        if (pos == null) {
            return false;
        }
        return pos.closerThan(this.blockPosition(), (double) distance);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.WRAITH_AMBIENT.get(), 1.5F, -10.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.WRAITH_HURT.get(), 1.0F, -10.0F);
    }

    @Override
    public boolean canCollideWith(Entity other) {
        if (other instanceof WraithEntity) {
            return false;
        }
        return super.canCollideWith(other);
    }

    @Override
    protected void playBlockFallSound() {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    class WraithMeleeGoal extends MeleeAttackGoal {
        WraithMeleeGoal(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
        }

        public boolean canUse() {
            return super.canUse();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }
    }


    private class ExtinguishGoal extends Goal {

        ExtinguishGoal() {
            super();
        }

        public boolean canUse() {
            if (WraithEntity.this.getTimeSinceExtinguish() >= 10) {
                return false;
            } else if (WraithEntity.this.random.nextFloat() < 0.3F) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        public void tick() {
            if (WraithEntity.this.random.nextInt(this.adjustedTickDelay(10)) == 0) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPos blockPos = WraithEntity.this.getOnPos().below(i);
                    BlockState blockState = WraithEntity.this.level().getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    boolean bl = false;
                    boolean torchAndLantern = false;
                    if (blockState.is(BlockTags.CANDLES)) {
                        if (block instanceof CandleBlock) {
                            if (blockState.getValue(BlockStateProperties.LIT)) {
                                bl = true;
                            }
                        } else if (block instanceof BrazierBlock) {
                            if (blockState.getValue(BlockStateProperties.LIT)) {
                                bl = true;
                            }
                        }
                        if (bl) {
                            //WraithEntity.this.level.syncWorldEvent(1502, blockPos, 0);
                            WraithEntity.this.level().playSound((Player) null, blockPos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                            WraithEntity.this.level().setBlock(blockPos, (BlockState) blockState.setValue(BlockStateProperties.LIT, false), 3);
                            WraithEntity.this.addExtinguishCounter();
                            triggerAdvancement(WraithEntity.this, bl);
                            return;
                        }
                    }
                    if (random.nextInt(10) == 0) {
                        if (blockState.is(Blocks.TORCH)) {
                            WraithEntity.this.level().setBlock(blockPos, Blocks.SOUL_TORCH.defaultBlockState(), 3);
                            torchAndLantern = true;
                        } else if (blockState.is(Blocks.LANTERN)) {
                            WraithEntity.this.level().setBlock(blockPos, Blocks.SOUL_LANTERN.defaultBlockState()
                                    .setValue(BlockStateProperties.HANGING, blockState.getValue(BlockStateProperties.HANGING))
                                    .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED)), 3);
                            torchAndLantern = true;
                        } else if (blockState.is(Blocks.WALL_TORCH)) {
                            WraithEntity.this.level().setBlock(blockPos, Blocks.SOUL_WALL_TORCH.defaultBlockState()
                                    .setValue(HorizontalDirectionalBlock.FACING, blockState.getValue(HorizontalDirectionalBlock.FACING)), 3);
                            torchAndLantern = true;
                        }

                        if (torchAndLantern) {
                            WraithEntity.this.level().playSound((Player) null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
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
            Player player = wraith.level().getNearestPlayer(wraith, 10.0D);
            if (player instanceof ServerPlayer) {
                TGCriteria.DIM_LIGHT.get().trigger((ServerPlayer) player);
            }
        }
    }
}
