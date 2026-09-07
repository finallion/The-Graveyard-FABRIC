package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.FlyOntoPerchGoal;
import com.lion.graveyard.entities.ai.goals.StartleGoal;
import com.lion.graveyard.entities.animation.AnimationTable;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/// Raven and magpie: one rig, one brain, two textures and sizes. Ambient only — they perch on
/// gravestones and fences and take off when someone comes close, which is all a graveyard needs
/// from a bird.
public class CorvidEntity extends PathAwareEntity implements GeoEntity {

    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation PERCH = RawAnimation.begin().thenLoop("perch");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CorvidEntity(EntityType<? extends CorvidEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 10, false);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    public static DefaultAttributeContainer.Builder createCorvidAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    public boolean isMagpie() {
        return this.getType() == TGEntities.MAGPIE.get();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new StartleGoal(this, 4.0D));
        this.goalSelector.add(2, new FlyOntoPerchGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world);
        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(false);
        navigation.setCanEnterOpenDoors(true);
        return navigation;
    }

    public boolean isFlying() {
        return !this.isOnGround();
    }

    public boolean isPerched() {
        return this.isOnGround() && this.getWorld().getBlockState(this.getBlockPos().down()).isIn(TGTags.CORVID_PERCHES);
    }

    /// Birds spawn in daylight on the surface; the graveyard structures add them on top of this.
    public static boolean canSpawn(EntityType<? extends CorvidEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        BlockState below = world.getBlockState(pos.down());
        boolean lit = world.getLightLevel(LightType.SKY, pos) > 8;
        return lit && (below.isIn(TGTags.CORVID_PERCHES) || below.isOpaque()) && canMobSpawn(type, world, reason, pos, random);
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return true;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 200;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public int getLimitPerChunk() {
        return 4;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationTable.<CorvidEntity>create()
                .when(this::isFlying, FLY)
                .otherwise(PERCH)
                .build(this, "controller"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
