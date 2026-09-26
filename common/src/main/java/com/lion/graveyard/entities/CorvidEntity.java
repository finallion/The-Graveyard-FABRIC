package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.control.CorvidMoveControl;
import com.lion.graveyard.entities.ai.goals.FlyOntoPerchGoal;
import com.lion.graveyard.entities.ai.goals.StartleGoal;
import com.lion.graveyard.entities.animation.AnimationTable;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.init.TGTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CorvidEntity extends PathAwareEntity implements GeoEntity {

    protected static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.crow.death");
    protected static final RawAnimation TAKEOFF = RawAnimation.begin().thenPlay("animation.crow.takeoff");
    protected static final RawAnimation GLIDE = RawAnimation.begin().thenLoop("animation.crow.glide");
    protected static final RawAnimation FLY = RawAnimation.begin().thenLoop("animation.crow.fly");
    protected static final RawAnimation LAND = RawAnimation.begin().thenPlayAndHold("animation.crow.land");
    protected static final RawAnimation HOP = RawAnimation.begin().thenLoop("animation.magpie.hop");
    protected static final RawAnimation WATCH = RawAnimation.begin().thenLoop("animation.crow.watch");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.crow.idle");
    protected static final RawAnimation PECK = RawAnimation.begin().thenPlay("animation.crow.peck");

    private static final TrackedData<Byte> FLIGHT_STATE;

    private static final int GLIDE_MIN = 6;
    private static final int GLIDE_MAX = 70;
    private static final int FLAP_BURST = 40;
    private static final int HOME_RADIUS = 24;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private BlockPos home;
    private int liftDelay;
    private int takeoffCooldown;
    private int flightTicks;
    private int restTicks;
    private boolean flying;
    private boolean nearGround;
    private int descendTicks;
    private int landingTicks;
    private int landingCooldown;
    private boolean watching;
    private int glideTicks;
    private int stuckTicks;
    private int peckTicks;
    private boolean webbed;

    public CorvidEntity(EntityType<? extends CorvidEntity> type, World world) {
        super(type, world);
        this.moveControl = new CorvidMoveControl(this);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    public static DefaultAttributeContainer.Builder createCorvidAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
                // FOLLOW_RANGE also limits the A* search. The default is too small to path over a wall.
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    public enum Species {
        RAVEN("raven"),
        MAGPIE("magpie"),
        CROW("crow");

        private final Identifier texture;
        private final Identifier model;

        Species(String name) {
            this.texture = new Identifier(Graveyard.MOD_ID, "textures/entity/" + name + ".png");
            this.model = new Identifier(Graveyard.MOD_ID, "geo/" + name + ".geo.json");
        }

        public Identifier getTexture() {
            return this.texture;
        }

        public Identifier getModel() {
            return this.model;
        }
    }

    public Species getSpecies() {
        return Species.RAVEN;
    }

    public enum FlightState {
        IDLE((byte) 0), WATCH((byte) 1), FLY((byte) 2), GLIDE((byte) 3), LAND((byte) 4), HOP((byte) 5);

        private final byte id;

        FlightState(byte id) {
            this.id = id;
        }

        public static FlightState byId(byte id) {
            for (FlightState state : values()) {
                if (state.id == id) {
                    return state;
                }
            }
            return IDLE;
        }
    }

    public FlightState getFlightState() {
        return FlightState.byId(this.dataTracker.get(FLIGHT_STATE));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FLIGHT_STATE, FlightState.IDLE.id);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
            @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason != SpawnReason.SPAWN_EGG && spawnReason != SpawnReason.COMMAND && spawnReason != SpawnReason.BUCKET) {
            this.home = this.getBlockPos();
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.home != null) {
            nbt.putLong("HomePos", this.home.asLong());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        // HomePos is optional. A missing key means no home.
        if (nbt.contains("HomePos")) {
            this.home = BlockPos.fromLong(nbt.getLong("HomePos"));
        }
    }

    @Nullable
    public BlockPos getHome() {
        return this.home;
    }

    public void setHome(BlockPos pos) {
        this.home = pos;
    }

    public boolean isFarFromHome() {
        if (this.home == null) {
            return false;
        }
        double dx = this.getX() - this.home.getX();
        double dz = this.getZ() - this.home.getZ();
        return dx * dx + dz * dz > (double) (HOME_RADIUS * HOME_RADIUS);
    }

    public boolean isPreparingTakeoff() {
        return this.liftDelay > 0;
    }

    public TagKey<Block> getPerchTag() {
        return TGTags.CORVID_PERCHES;
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

    /// travel() uses this value in the air. GENERIC_FLYING_SPEED does not change acceleration.
    @Override
    protected float getOffGroundSpeed() {
        return 0.03F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            return;
        }

        double dx = this.getX() - this.prevX;
        double dz = this.getZ() - this.prevZ;
        boolean moving = dx * dx + dz * dz > 0.0004D;
        // A push from another bird is not flight. Steered movement is.
        boolean steering = !this.getNavigation().isIdle() || this.isGliding();
        boolean airborne = !this.isOnGround() || (moving && steering);
        if (airborne) {
            this.flightTicks++;
            this.restTicks = 0;
        } else {
            this.restTicks++;
            this.flightTicks = 0;
        }
        // Hysteresis, because isOnGround can flicker for one tick.
        if (this.flightTicks >= 2) {
            this.flying = true;
        } else if (this.restTicks >= 3) {
            this.flying = false;
        }
        if (this.isHopping()) {
            this.flightTicks = 0;
            this.flying = false;
        }

        this.nearGround = this.computeNearGround();

        if (this.getVelocity().y < -0.05D) {
            this.descendTicks++;
        } else {
            this.descendTicks = 0;
        }
        if (this.descendTicks >= 3 && this.nearGround && !this.isOnGround() && !this.isHopping() && this.landingCooldown == 0) {
            this.landingTicks = 24;
            this.landingCooldown = 40;
        }
        if (this.isOnGround() && this.landingTicks > 4) {
            this.landingTicks = 4;
        }
        if (this.getVelocity().y > 0.05D) {
            this.landingTicks = 0;
        }
        if (this.landingTicks > 0) {
            this.landingTicks--;
        }
        if (this.landingCooldown > 0) {
            this.landingCooldown--;
        }
        if (this.takeoffCooldown > 0) {
            this.takeoffCooldown--;
        }
        if (this.peckTicks > 0) {
            this.peckTicks--;
        }

        double vy = this.getVelocity().y;
        boolean climbing = vy > 0.03D;
        if (!this.flying || climbing || this.isHopping()) {
            this.glideTicks = 0;
        } else if (this.glideTicks < 0 || this.isGliding() || vy < -0.10D) {
            // A negative count is the forced flap burst. Let it run out.
            this.glideTicks++;
        } else {
            this.glideTicks = Math.max(0, this.glideTicks - 2);
        }
        if (this.glideTicks > GLIDE_MAX) {
            this.glideTicks = -FLAP_BURST;
        }
        boolean gliding = this.glideTicks >= GLIDE_MIN;

        boolean onGround = this.isOnGround();
        if (this.age % 10 == 0) {
            PlayerEntity player = this.getWorld().getClosestPlayer(this, 8.0D);
            this.watching = onGround && player != null && !player.isSpectator();
        }
        if (this.age % 20 == 0 && (this.isPerched() || (onGround && this.isTouching(TGTags.CROW_LANDMARKS)))) {
            this.home = this.getBlockPos();
        }

        boolean airborneNow = !this.isOnGround() || this.getVelocity().y > 0.1D || (moving && steering);
        FlightState state;
        if (this.isHopping() && this.nearGround) {
            state = FlightState.HOP;
        } else if (this.landingTicks > 0) {
            state = FlightState.LAND;
        } else if (this.flying && gliding && airborneNow) {
            state = FlightState.GLIDE;
        } else if (this.flying && airborneNow) {
            state = FlightState.FLY;
        } else if (this.watching) {
            state = FlightState.WATCH;
        } else {
            state = FlightState.IDLE;
        }
        if (state != this.getFlightState()) {
            this.dataTracker.set(FLIGHT_STATE, state.id);
        }

        this.tryPeck(onGround, moving);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        // Server only. The flags below are never set on the client.
        if (this.getWorld().isClient()) {
            return;
        }
        boolean navigating = !this.getNavigation().isIdle();
        // Vanilla sets isOnGround only on a downward collision, so give a grounded bird its gravity back.
        if (this.isOnGround() && !this.isHopping()) {
            this.setNoGravity(false);
            Vec3d v = this.getVelocity();
            if (v.y > -0.08D) {
                this.setVelocity(v.x, -0.08D, v.z);
            }
        }
        // Delay the impulse until the crouch in the takeoff animation ends.
        boolean lifted = false;
        if (this.liftDelay > 0 && --this.liftDelay == 0) {
            this.lift();
            lifted = true;
        }
        // Safety net for goals that path on the ground without takeOff().
        if (this.isOnGround() && !this.isHopping() && navigating && this.deathTime == 0 && !this.isDead()
                && !this.horizontalCollision && !this.isPreparingTakeoff() && !lifted) {
            this.takeOff();
        }
        if (!this.hasNoGravity() && !this.isOnGround() && !this.isHopping()) {
            Vec3d v = this.getVelocity();
            this.setVelocity(v.x, Math.max(v.y, -0.3D), v.z);
        }
        if (this.isLanding() && !this.isOnGround() && !this.isHopping()) {
            Vec3d v = this.getVelocity();
            this.setVelocity(v.x * 0.85D, v.y, v.z * 0.85D);
        }
        if (this.horizontalCollision && navigating && !this.isHopping() && !this.isPreparingTakeoff()
                && (this.flying || this.isOnGround())) {
            this.stuckTicks++;
        } else {
            this.stuckTicks = 0;
        }
        if (this.stuckTicks >= 20) {
            this.getNavigation().stop();
            this.stuckTicks = 0;
            float yaw = this.getYaw() + 180.0F;
            this.setYaw(yaw);
            this.setBodyYaw(yaw);
            this.setHeadYaw(yaw);
            Vec3d v = this.getVelocity();
            this.setVelocity(v.x, 0.25D, v.z);
            this.velocityDirty = true;
        }
        // In a cobweb, Entity.move sets the velocity to zero, so apply the climb here.
        if (this.webbed) {
            Vec3d v = this.getVelocity();
            this.setVelocity(v.x, 0.3D, v.z);
            this.velocityDirty = true;
            this.webbed = false;
        }
        if (this.isTouchingWater() && !this.isOnGround() && this.deathTime == 0) {
            Vec3d v = this.getVelocity();
            this.setVelocity(v.x, Math.max(v.y, 0.3D), v.z);
            this.velocityDirty = true;
        }
    }

    @Override
    public void slowMovement(BlockState state, Vec3d multiplier) {
        if (state.isOf(Blocks.COBWEB)) {
            super.slowMovement(state, new Vec3d(0.5D, 0.5D, 0.5D));
            this.webbed = !this.getWorld().isClient();
            return;
        }
        super.slowMovement(state, multiplier);
    }

    private boolean computeNearGround() {
        World world = this.getWorld();
        for (int n = 1; n <= 3; n++) {
            BlockPos pos = this.getBlockPos().down(n);
            if (!world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /// Server only. GeckoLib sends the animation trigger to the client.
    public void takeOff() {
        if (this.getWorld().isClient()) {
            return;
        }
        if (!this.isOnGround() || this.isHopping() || this.deathTime > 0 || this.isDead()) {
            return;
        }
        // isOnGround is still true in the jump tick.
        if (this.getVelocity().y > 0.1D) {
            return;
        }
        if (this.liftDelay == 0) {
            this.liftDelay = 4;
        }
        if (this.takeoffCooldown == 0) {
            this.triggerAnim("controller", "takeoff");
            this.takeoffCooldown = 30;
        }
    }

    private void lift() {
        if (!this.isOnGround() || this.isHopping() || this.deathTime > 0 || this.isDead()) {
            return;
        }
        Vec3d v = this.getVelocity();
        this.setVelocity(v.x, 0.22D, v.z);
        this.velocityDirty = true;
        this.landingTicks = 0;
    }

    private void tryPeck(boolean onGround, boolean moving) {
        if (!onGround || moving || this.deathTime != 0 || this.isHopping() || !this.canPeck()
                || !this.getNavigation().isIdle()) {
            return;
        }
        int chance = this.isOnFeedingGround() ? 60 : 240;
        if (this.getRandom().nextInt(chance) == 0) {
            this.triggerAnim("controller", "peck");
            this.peckTicks = 20;
        }
    }

    public boolean isPecking() {
        return this.peckTicks > 0;
    }

    private boolean isOnFeedingGround() {
        return this.isOnBlock(Blocks.FARMLAND) || this.isTouching(BlockTags.CROPS)
                || (this.isTouching(TGTags.CROW_SETTLE) && !this.isTouching(TGTags.CORVID_PERCHES));
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean isPerched() {
        return this.isOnGround() && this.isTouching(this.getPerchTag());
    }

    public boolean isNearGround() {
        return this.nearGround;
    }

    public boolean isLanding() {
        return this.landingTicks > 0;
    }

    public boolean isWatching() {
        return this.watching;
    }

    public boolean isGliding() {
        return false;
    }

    public boolean isHopping() {
        return false;
    }

    protected boolean canPeck() {
        return true;
    }

    private BlockState state(BlockPos pos) {
        return this.getWorld().getBlockState(pos);
    }

    private boolean isTouching(TagKey<Block> tag) {
        BlockPos pos = this.getBlockPos();
        return this.state(pos).isIn(tag) || this.state(pos.down()).isIn(tag);
    }

    private boolean isOnBlock(Block block) {
        BlockPos pos = this.getBlockPos();
        return this.state(pos).isOf(block) || this.state(pos.down()).isOf(block);
    }

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
        return switch (this.getSpecies()) {
            case RAVEN -> TGSounds.RAVEN_AMBIENT.get();
            case MAGPIE -> TGSounds.MAGPIE_AMBIENT.get();
            case CROW -> TGSounds.CROW_AMBIENT.get();
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return TGSounds.CORVID_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TGSounds.CORVID_DEATH.get();
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
    protected void updatePostDeath() {
        this.deathTime++;
        if (this.deathTime == 20 && !this.getWorld().isClient() && !this.isRemoved()) {
            this.getWorld().sendEntityStatus(this, (byte) 60);
        }
        // Vanilla removes the body after 20 ticks. The death animation needs 40.
        if (this.deathTime >= 40 && !this.getWorld().isClient() && !this.isRemoved()) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        // The move control does not tick after death, so restore gravity here.
        this.setNoGravity(false);
        // Stop the path, or the safety net lifts the dead bird.
        this.getNavigation().stop();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationTable.<CorvidEntity>create()
                .when(() -> this.deathTime > 0, DEATH)
                .when(() -> this.getFlightState() == FlightState.HOP, HOP)
                .when(() -> this.getFlightState() == FlightState.LAND, LAND)
                .when(() -> this.getFlightState() == FlightState.GLIDE, GLIDE)
                .when(() -> this.getFlightState() == FlightState.FLY, FLY)
                .when(() -> this.getFlightState() == FlightState.WATCH, WATCH)
                .otherwise(IDLE)
                .build(this, "controller")
                .triggerableAnim("peck", PECK)
                .triggerableAnim("takeoff", TAKEOFF));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static {
        FLIGHT_STATE = DataTracker.registerData(CorvidEntity.class, TrackedDataHandlerRegistry.BYTE);
    }
}
