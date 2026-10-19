package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.ChaseTarget;
import com.lion.graveyard.blocks.BrazierBlock;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class WraithEntity extends HostileGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final Identifier ATTACKING_SPEED_BOOST_ID = Graveyard.id("attacking_speed_boost_id");
    private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, 0.2D, AttributeModifier.Operation.ADD_VALUE);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().thenPlayAndHold("death");
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().thenPlayAndHold("spawn");
    private final RawAnimation MOVE_ANIMATION = RawAnimation.begin().then("moving", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attacking", Animation.LoopType.LOOP);
    protected final byte ANIMATION_SPAWN = 0;
    protected final byte ANIMATION_IDLE = 1;
    protected final byte ANIMATION_DEATH = 2;
    protected final byte ANIMATION_ATTACK = 3;
    protected static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(WraithEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(WraithEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHASING = SynchedEntityData.defineId(WraithEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int SPAWN_TICKS = 20;
    private static final int CHASE_END_TICKS = 20;
    private static final int DEATH_TICKS = 46;
    private int ticksWithoutTarget;

    @Nullable
    private BlockPos homePosition;
    private int timeSinceExtinguish;

    public WraithEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world, "wraith");
        this.moveControl = new FlyingMoveControl(this, 0, true);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);
    }

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        super.move(movementType, movement);
        this.applyEffectsFromBlocks();
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, multiplier);
        }

    }

    @Override
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

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation birdNavigation = new FlyingPathNavigation(this, world) {
            @Override
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }

            @Override
            public void tick() {
                super.tick();
            }
        };
        birdNavigation.setCanOpenDoors(false);
        birdNavigation.setCanFloat(false);
        birdNavigation.getNodeEvaluator().setCanPassDoors(true);
        return birdNavigation;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (this.isInvulnerableTo(serverLevel, source)) {
            return false;
        } else if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            Entity entity = source.getDirectEntity();

            if (entity instanceof Arrow) {
               return false;
            }

            return super.hurtServer(serverLevel, source, amount);
        } else {
            return super.hurtServer(serverLevel, source, amount);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION, ANIMATION_IDLE);
        builder.define(SPAWN_TIMER, 0);
        builder.define(CHASING, false);
    }

    public void startSpawnAnimation() {
        this.entityData.set(SPAWN_TIMER, SPAWN_TICKS);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide()) {
            int spawnTicks = this.entityData.get(SPAWN_TIMER);

            if (spawnTicks > 0) {
                this.entityData.set(SPAWN_TIMER, spawnTicks - 1);
            }
        }

        super.tick();
        this.setNoGravity(true);

        if (this.homePosition == null) {
            homePosition = WraithEntity.this.blockPosition();
        }

        if (this.random.nextFloat() < 0.025F) {
            addParticles();

        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        if (this.homePosition != null) {
            output.putInt("BoundX", this.homePosition.getX());
            output.putInt("BoundY", this.homePosition.getY());
            output.putInt("BoundZ", this.homePosition.getZ());
        }

        output.putInt("timeSinceExtinguish", this.timeSinceExtinguish);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        if (input.getInt("BoundX").isPresent()) {
            this.homePosition = new BlockPos(input.getIntOr("BoundX", 0), input.getIntOr("BoundY", 0), input.getIntOr("BoundZ", 0));
        }

        this.timeSinceExtinguish = input.getIntOr("timeSinceExtinguish", 0);
    }

    @Override
    public void aiStep() {
        if (level().isClientSide() && isSpawning()) {
            addParticles();
        }

        if (!level().isClientSide()) {
            trackChase();
            AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.FLYING_SPEED);

            if (this.getTarget() == null) {
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
            } else if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST_ID)) {
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
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData entityData
    ) {
        world.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE,2.0F, -5.0F);
        startSpawnAnimation();
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_TICKS && !this.level().isClientSide()) {
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

    @VisibleForDebug
    public boolean hasHomePosition() {
        return this.homePosition != null;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.BYPASSES_ARMOR) ||
                damageSource.is(DamageTypeTags.BYPASSES_EFFECTS) ||
                damageSource.is(DamageTypeTags.BYPASSES_SHIELD)) {
            return true;
        }

        return super.isInvulnerableTo(serverLevel, damageSource);
    }

    public static AttributeSupplier.Builder createWraithAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 27.5D)
                .add(Attributes.ATTACK_DAMAGE, 6.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.35);
    }

    /// The vanilla melee goal stops at each path end.
    private void trackChase() {
        if (ChaseTarget.isLive(this)) {
            this.ticksWithoutTarget = 0;
            this.entityData.set(CHASING, true);
        } else if (this.entityData.get(CHASING) && ++this.ticksWithoutTarget > CHASE_END_TICKS) {
            this.entityData.set(CHASING, false);
        }
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    public byte getAnimation() {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(byte animation) {
        entityData.set(ANIMATION, animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(AnimationTable.<WraithEntity>create()
                .transitionTicks(() -> isSpawning() ? 0 : AnimationTable.TRANSITION_TICKS)
                .when(this::isDeadOrDying, DEATH_ANIMATION)
                .when(this::isSpawning, SPAWN_ANIMATION)
                .when(() -> this.entityData.get(CHASING), ATTACK_ANIMATION)
                .when(AnimationTable::isMoving, MOVE_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .startAt(SPAWN_ANIMATION, wraith -> wraith.isSpawning() && AnimationPreview.forcedAnimation(wraith) == null ? Math.max(0, SPAWN_TICKS - wraith.entityData.get(SPAWN_TIMER)) : 0)
                .build(this, "controller", isSpawning() ? 0 : AnimationTable.TRANSITION_TICKS));
    }

    private boolean isSpawning() {
        return this.entityData.get(SPAWN_TIMER) > 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    boolean isWithinDistance(BlockPos pos, int distance) {
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
    public void die(DamageSource source) {
        super.die(source);
        playDeathSound();
    }

    private void playDeathSound() {
        for (int i = 0; i < 10; i++) {
            this.playSound(TGSounds.WRAITH_AMBIENT.get(), 2.5F, -10.0F);
        }
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

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(
            double heightDifference,
            boolean onGround,
            BlockState landedState,
            BlockPos landedPosition
    ) {
    }

    class WraithMeleeGoal extends MeleeAttackGoal {
        WraithMeleeGoal(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
        }

        @Override
        public boolean canUse() {
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }
    }

    private class ExtinguishGoal extends Goal {

        ExtinguishGoal() {
            super();
        }

        @Override
        public boolean canUse() {
            if (!(WraithEntity.this.level() instanceof ServerLevel serverLevel)
                    || !serverLevel.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.MOB_GRIEFING)
                    || !com.lion.graveyard.Graveyard.getConfig().flag("wraithGriefing", true)) {
                return false;
            }

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

        @Override
        public void tick() {
            if (WraithEntity.this.random.nextInt(this.adjustedTickDelay(10)) == 0) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPos blockPos = WraithEntity.this.blockPosition().below(i);
                    BlockState blockState = WraithEntity.this.level().getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    boolean litCandle = false;
                    boolean torchAndLantern = false;

                    if (blockState.is(BlockTags.CANDLES)) {
                        if (block instanceof CandleBlock) {
                            if (blockState.getValue(BlockStateProperties.LIT)) {
                                litCandle = true;
                            }
                        } else if (block instanceof BrazierBlock) {
                            if (blockState.getValue(BlockStateProperties.LIT)) {
                                litCandle = true;
                            }
                        }

                        if (litCandle) {
                            WraithEntity.this.level().playSound((Player) null, blockPos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                            WraithEntity.this.level().setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.LIT, false));
                            WraithEntity.this.addExtinguishCounter();
                            triggerAdvancement(WraithEntity.this, litCandle);
                            return;
                        }
                    }

                    if (random.nextInt(10) == 0) {
                        if (blockState.is(Blocks.TORCH)) {
                            WraithEntity.this.level().setBlockAndUpdate(blockPos, Blocks.SOUL_TORCH.defaultBlockState());
                            torchAndLantern = true;
                        } else if (blockState.is(Blocks.LANTERN)) {
                            WraithEntity.this.level().setBlockAndUpdate(
                                    blockPos,
                                    Blocks.SOUL_LANTERN.defaultBlockState()
                                            .setValue(BlockStateProperties.HANGING, blockState.getValue(BlockStateProperties.HANGING))
                                            .setValue(BlockStateProperties.WATERLOGGED, blockState.getValue(BlockStateProperties.WATERLOGGED))
                            );
                            torchAndLantern = true;
                        } else if (blockState.is(Blocks.WALL_TORCH)) {
                            WraithEntity.this.level().setBlockAndUpdate(
                                    blockPos,
                                    Blocks.SOUL_WALL_TORCH.defaultBlockState()
                                            .setValue(HorizontalDirectionalBlock.FACING, blockState.getValue(HorizontalDirectionalBlock.FACING))
                            );
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
                TGAdvancements.DIM_LIGHT.trigger((ServerPlayer) player);
            }
        }
    }

    class WraithWanderAroundGoal extends Goal {
        private static final int MAX_DISTANCE = 15;

        WraithWanderAroundGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return WraithEntity.this.navigation.isDone() && WraithEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return WraithEntity.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3d = this.getRandomLocation();

            if (vec3d != null) {
                WraithEntity.this.navigation.moveTo(WraithEntity.this.navigation.createPath(BlockPos.containing(vec3d), 1), 1.0D);
            }

        }

        @Nullable
        private Vec3 getRandomLocation() {
            Vec3 vec3d2;

            if (!WraithEntity.this.isWithinDistance(WraithEntity.this.homePosition, MAX_DISTANCE) && WraithEntity.this.hasHomePosition()) {
                Vec3 vec3dx = Vec3.atCenterOf(WraithEntity.this.homePosition);
                vec3d2 = vec3dx.subtract(WraithEntity.this.position()).normalize();
            } else {
                vec3d2 = WraithEntity.this.getViewVector(0.0F);
            }

            Vec3 vec3d3 = HoverRandomPos.getPos(WraithEntity.this, 8, 2, vec3d2.x, vec3d2.z, 1.5707964F, 2, 1);
            return vec3d3 != null ? vec3d3 : AirAndWaterRandomPos.getPos(WraithEntity.this, 8, 2, -2, vec3d2.x, vec3d2.z, 1.5707963705062866D);
        }
    }

}
