package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.control.CorvidMoveControl;
import com.lion.graveyard.entities.ai.goals.FlyOntoPerchGoal;
import com.lion.graveyard.entities.ai.goals.StartleGoal;
import com.lion.graveyard.entities.animation.AnimationTable;
import com.lion.graveyard.blocks.GravestoneBlock;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.init.TGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CorvidEntity extends PathfinderMob implements GeoEntity {

    private static final double WALK_MIN_SPEED_SQ = 2.5E-3D;
    private static final double GROUND_TURN_MIN_SPEED_SQ = 1.0E-4D;
    private static final int WALK_HOLD_TICKS = 3;
    protected static final RawAnimation TAKEOFF = RawAnimation.begin().thenPlay("animation.crow.takeoff");
    protected static final RawAnimation GLIDE = RawAnimation.begin().thenLoop("animation.crow.glide");
    protected static final RawAnimation FLY = RawAnimation.begin().thenLoop("animation.crow.fly");
    protected static final RawAnimation LAND = RawAnimation.begin().thenPlayAndHold("animation.crow.land");
    protected static final RawAnimation HOP = RawAnimation.begin().thenLoop("animation.magpie.hop");
    protected static final RawAnimation WATCH = RawAnimation.begin().thenLoop("animation.crow.watch");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.crow.idle");

    private static final EntityDataAccessor<Byte> FLIGHT_STATE;

    private static final int GRAVESTONE_CHECK_INTERVAL = 20;
    private static final double GRAVESTONE_WITNESS_RANGE = 4.0D;
    private static final int GLIDE_MIN = 6;
    private static final int GLIDE_MAX = 70;
    private static final int FLAP_BURST = 40;
    private static final int HOME_RADIUS = 24;
    private static final int DEATH_ANIMATION_DURATION = 32 + AnimationTable.TRANSITION_TICKS;
    private static final int DEAD_FALL_LIMIT = 200;
    private static final int IMPACT_REST_TICKS = 20;
    private static final int FLAP_SOUND_INTERVAL = 36;
    private static final int TAKEOFF_SOUND_COOLDOWN = 100;
    private static final float FLAP_SOUND_VOLUME = 0.08F;
    private static final float TAKEOFF_SOUND_VOLUME = 0.25F;
    private static final float LAND_SOUND_VOLUME = 0.2F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private BlockPos home;
    private int liftDelay;
    private int takeoffCooldown;
    private int flightTicks;
    private int restTicks;
    private float lookWeight = 1.0F;
    private float previousLookWeight = 1.0F;
    private int lookWeightAge = -1;
    private int walkTicks;
    private boolean flying;
    private boolean nearGround;
    private int descendTicks;
    private int landingTicks;
    private int landingCooldown;
    private int flapSoundTicks;
    private int takeoffSoundCooldown;
    private boolean diedInAir;
    private int impactTicks;
    private boolean watching;
    private int glideTicks;
    private int stuckTicks;
    private int peckTicks;
    private boolean webbed;

    public CorvidEntity(EntityType<? extends CorvidEntity> type, Level world) {
        super(type, world);
        this.moveControl = new CorvidMoveControl(this);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createCorvidAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                // FOLLOW_RANGE also limits the A* search. The default is too small to path over a wall.
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public enum Species {
        RAVEN("raven", "raven"),
        MAGPIE("magpie", "crow"),
        CROW("crow", "crow");

        private final ResourceLocation texture;
        private final ResourceLocation model;
        private final RawAnimation walkAnimation;
        private final RawAnimation peckAnimation;
        private final RawAnimation deathAnimation;

        // The raven model is larger. Its peck and death clips move the parts farther.
        Species(String name, String bodyClips) {
            this.texture = Graveyard.id("textures/entity/" + name + ".png");
            this.model = Graveyard.id("geo/" + name + ".geo.json");
            this.walkAnimation = RawAnimation.begin().thenLoop("animation." + name + ".walk");
            this.peckAnimation = RawAnimation.begin().thenPlay("animation." + bodyClips + ".peck");
            this.deathAnimation = RawAnimation.begin().thenPlayAndHold("animation." + bodyClips + ".death");
        }

        public ResourceLocation getTexture() {
            return this.texture;
        }

        public RawAnimation getWalkAnimation() {
            return this.walkAnimation;
        }

        public RawAnimation getPeckAnimation() {
            return this.peckAnimation;
        }

        public RawAnimation getDeathAnimation() {
            return this.deathAnimation;
        }

        public ResourceLocation getModel() {
            return this.model;
        }
    }

    public Species getSpecies() {
        return Species.RAVEN;
    }

    public enum FlightState {
        IDLE((byte) 0), WATCH((byte) 1), FLY((byte) 2), GLIDE((byte) 3), LAND((byte) 4), HOP((byte) 5), WALK((byte) 6);

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

    /// A move packet without rotation halts the client yaw lerp halfway. On the ground the body follows the client motion.
    @Override
    protected float tickHeadTurn(float bodyRotation, float headRotation) {
        FlightState state = this.getFlightState();

        if (this.level().isClientSide() && this.isAlive() && (state == FlightState.HOP || state == FlightState.WALK)) {
            double dx = this.getX() - this.xo;
            double dz = this.getZ() - this.zo;

            if (dx * dx + dz * dz > GROUND_TURN_MIN_SPEED_SQ) {
                this.yBodyRot = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
                return headRotation;
            }
        }

        return super.tickHeadTurn(bodyRotation, headRotation);
    }

    public FlightState getFlightState() {
        return FlightState.byId(this.entityData.get(FLIGHT_STATE));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLIGHT_STATE, FlightState.IDLE.id);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason,
            @Nullable SpawnGroupData entityData) {
        if (spawnReason != MobSpawnType.SPAWN_EGG && spawnReason != MobSpawnType.COMMAND && spawnReason != MobSpawnType.BUCKET) {
            this.home = this.blockPosition();
        }

        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        if (this.home != null) {
            nbt.putLong("HomePos", this.home.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("HomePos")) {
            this.home = BlockPos.of(nbt.getLong("HomePos"));
        }
    }

    @Nullable
    public BlockPos getHome() {
        return this.home;
    }

    public void setHome(BlockPos pos) {
        this.home = pos;
    }

    @Override
    public void restoreFrom(net.minecraft.world.entity.Entity original) {
        super.restoreFrom(original);

        if (original.level() != this.level()) {
            this.home = null;
        }
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
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StartleGoal(this, 4.0D));
        this.goalSelector.addGoal(2, new FlyOntoPerchGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, world);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(false);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    /// travel() uses this value in the air. GENERIC_FLYING_SPEED does not change acceleration.
    @Override
    protected float getFlyingSpeed() {
        return 0.03F;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        if (this.tickCount % GRAVESTONE_CHECK_INTERVAL == 0 && this.getSpecies() == Species.RAVEN && this.isPerched() && this.isOnGravestone()
                && this.level() instanceof ServerLevel world) {
            TGAdvancements.triggerNearby(world, this.position(), GRAVESTONE_WITNESS_RANGE, TGAdvancements.RAVEN_ON_GRAVESTONE);
        }

        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        boolean moving = dx * dx + dz * dz > 0.0004D;
        // A push from another bird is not flight. Steered movement is.
        boolean steering = !this.getNavigation().isDone() || this.isGliding();
        boolean airborne = !this.onGround() || (moving && steering);

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

        if (this.getDeltaMovement().y < -0.05D) {
            this.descendTicks++;
        } else {
            this.descendTicks = 0;
        }

        if (this.descendTicks >= 3 && this.nearGround && !this.onGround() && !this.isHopping() && this.landingCooldown == 0) {
            this.landingTicks = 24;
            this.landingCooldown = 40;
        }

        if (this.onGround() && this.landingTicks > 4) {
            this.landingTicks = 4;

            if (this.isAlive()) {
                this.playWingSound(TGSounds.CORVID_LAND.get(), LAND_SOUND_VOLUME);
            }
        }

        if (this.getDeltaMovement().y > 0.05D) {
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

        if (this.takeoffSoundCooldown > 0) {
            this.takeoffSoundCooldown--;
        }

        if (this.peckTicks > 0) {
            this.peckTicks--;
        }

        double vy = this.getDeltaMovement().y;
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

        boolean onGround = this.onGround();

        if (this.tickCount % 10 == 0) {
            Player player = this.level().getNearestPlayer(this, 8.0D);
            this.watching = onGround && player != null && !player.isSpectator();
        }

        if (this.home == null && this.tickCount % 20 == 0 && (this.isPerched() || (onGround && this.isTouching(TGTags.CROW_LANDMARKS)))) {
            this.home = this.blockPosition();
        }

        boolean walkedThisTick = this.canWalk() && onGround && !this.isHopping() && dx * dx + dz * dz > WALK_MIN_SPEED_SQ;

        if (walkedThisTick && !this.isWalking()) {
            float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
            this.setYRot(yaw);
            this.setYBodyRot(yaw);
            this.setYHeadRot(yaw);
        }

        if ((this.isWalking() && onGround) || walkedThisTick) {
            this.walkTicks = WALK_HOLD_TICKS;
        } else if (this.walkTicks > 0) {
            this.walkTicks--;
        }

        boolean walkingOnGround = this.walkTicks > 0 && !this.flying;
        boolean airborneNow = !this.onGround() || this.getDeltaMovement().y > 0.1D || (moving && steering);
        boolean flightHeld = airborneNow || this.restTicks < 3;
        FlightState state;

        if (this.isHopping() && (this.nearGround || onGround)) {
            state = FlightState.HOP;
        } else if (this.landingTicks > 0) {
            state = FlightState.LAND;
        } else if (walkingOnGround) {
            state = FlightState.WALK;
        } else if (this.flying && gliding && flightHeld) {
            state = FlightState.GLIDE;
        } else if (this.flying && flightHeld) {
            state = FlightState.FLY;
        } else if (this.watching) {
            state = FlightState.WATCH;
        } else {
            state = FlightState.IDLE;
        }

        if (state != this.getFlightState()) {
            this.entityData.set(FLIGHT_STATE, state.id);
        }

        if (state != FlightState.FLY || !this.isAlive()) {
            this.flapSoundTicks = 0;
        } else if (++this.flapSoundTicks >= FLAP_SOUND_INTERVAL) {
            this.flapSoundTicks = 0;
            this.playWingSound(TGSounds.CORVID_FLY.get(), FLAP_SOUND_VOLUME);
        }

        this.tryPeck(onGround, moving);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Server only. The flags below are never set on the client.
        if (this.level().isClientSide()) {
            return;
        }

        boolean navigating = !this.getNavigation().isDone();
        // Vanilla sets isOnGround only on a downward collision, so give a grounded bird its gravity back.
        if (this.onGround() && !this.isHopping()) {
            this.setNoGravity(false);
            Vec3 v = this.getDeltaMovement();

            if (v.y > -0.08D) {
                this.setDeltaMovement(v.x, -0.08D, v.z);
            }
        }

        // Delay the impulse until the crouch in the takeoff animation ends.
        boolean lifted = false;

        if (this.liftDelay > 0 && --this.liftDelay == 0) {
            this.lift();
            lifted = true;
        }

        // Safety net for goals that path on the ground without takeOff().
        if (this.onGround() && !this.isHopping() && navigating && this.deathTime == 0 && !this.isDeadOrDying()
                && !this.horizontalCollision && !this.isPreparingTakeoff() && !lifted) {
            this.takeOff();
        }

        if (!this.isNoGravity() && !this.onGround() && !this.isHopping() && !this.isDeadOrDying()) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x, Math.max(v.y, -0.3D), v.z);
        }

        if (this.isLanding() && !this.onGround() && !this.isHopping()) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x * 0.85D, v.y, v.z * 0.85D);
        }

        if (this.horizontalCollision && navigating && !this.isHopping() && !this.isPreparingTakeoff()
                && (this.flying || this.onGround())) {
            this.stuckTicks++;
        } else {
            this.stuckTicks = 0;
        }

        if (this.stuckTicks >= 20) {
            this.getNavigation().stop();
            this.stuckTicks = 0;
            float yaw = this.getYRot() + 180.0F;
            this.setYRot(yaw);
            this.setYBodyRot(yaw);
            this.setYHeadRot(yaw);
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x, 0.25D, v.z);
            this.hasImpulse = true;
        }

        // In a cobweb, Entity.move sets the velocity to zero, so apply the climb here.
        if (this.webbed && this.isAlive()) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x, 0.3D, v.z);
            this.hasImpulse = true;
            this.webbed = false;
        }

        if (this.isInWater() && !this.onGround() && this.deathTime == 0) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x, Math.max(v.y, 0.3D), v.z);
            this.hasImpulse = true;
        }
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
        if (state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, new Vec3(0.5D, 0.5D, 0.5D));
            this.webbed = !this.level().isClientSide();
            return;
        }

        super.makeStuckInBlock(state, multiplier);
    }

    private boolean computeNearGround() {
        Level world = this.level();
        for (int n = 1; n <= 3; n++) {
            BlockPos pos = this.blockPosition().below(n);

            if (!world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /// Server only. GeckoLib sends the animation trigger to the client.
    public void takeOff() {
        if (this.level().isClientSide()) {
            return;
        }

        if (!this.onGround() || this.isHopping() || this.deathTime > 0 || this.isDeadOrDying()) {
            return;
        }

        // isOnGround is still true in the jump tick.
        if (this.getDeltaMovement().y > 0.1D) {
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
        if (!this.onGround() || this.isHopping() || this.deathTime > 0 || this.isDeadOrDying()) {
            return;
        }

        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(v.x, 0.22D, v.z);
        this.hasImpulse = true;
        this.landingTicks = 0;

        if (this.takeoffSoundCooldown == 0) {
            this.takeoffSoundCooldown = TAKEOFF_SOUND_COOLDOWN;
            this.playWingSound(TGSounds.CORVID_TAKEOFF.get(), TAKEOFF_SOUND_VOLUME);
        }
    }

    private void playWingSound(SoundEvent sound, float volume) {
        this.playSound(sound, volume, 0.85F + this.random.nextFloat() * 0.15F);
    }

    private void tryPeck(boolean onGround, boolean moving) {
        FlightState flight = this.getFlightState();

        if (!onGround || moving || this.deathTime != 0 || this.isHopping() || !this.canPeck()
                || !this.getNavigation().isDone() || (flight != FlightState.IDLE && flight != FlightState.WATCH)) {
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

    private boolean isOnGravestone() {
        BlockPos pos = this.blockPosition();
        return this.state(pos).getBlock() instanceof GravestoneBlock || this.state(pos.below()).getBlock() instanceof GravestoneBlock;
    }

    public boolean isPerched() {
        return this.onGround() && this.isTouching(this.getPerchTag());
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

    public boolean isWalking() {
        return false;
    }

    protected boolean canWalk() {
        return false;
    }

    protected boolean canPeck() {
        return true;
    }

    private BlockState state(BlockPos pos) {
        return this.level().getBlockState(pos);
    }

    private boolean isTouching(TagKey<Block> tag) {
        BlockPos pos = this.blockPosition();
        return this.state(pos).is(tag)
                || this.state(pos.below()).is(tag);
    }

    private boolean isOnBlock(Block block) {
        BlockPos pos = this.blockPosition();
        return this.state(pos).is(block)
                || this.state(pos.below()).is(block);
    }

    public static boolean canSpawn(EntityType<? extends CorvidEntity> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        BlockState below = world.getBlockState(pos.below());
        boolean lit = world.getBrightness(LightLayer.SKY, pos) > 8;
        return lit && (below.is(TGTags.CORVID_PERCHES) || below.canOcclude()) && checkMobSpawnRules(type, world, reason, pos, random);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        return !MobSpawnGate.blocks(this.getType(), spawnReason) && !MobSpawnGate.inForbiddenBiome(world, this.blockPosition(), this.getType(), spawnReason);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
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
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 4;
    }

    public float stepLookWeight(boolean suppressed, float partialTick) {
        if (this.tickCount != this.lookWeightAge) {
            this.lookWeightAge = this.tickCount;
            this.previousLookWeight = this.lookWeight;
            float step = 1.0F / AnimationTable.TRANSITION_TICKS;
            this.lookWeight = Mth.clamp(this.lookWeight + (suppressed ? -step : step), 0.0F, 1.0F);
        }

        return Mth.lerp(partialTick, this.previousLookWeight, this.lookWeight);
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;

        if (this.level().isClientSide() || this.isRemoved()) {
            return;
        }

        if (!this.diedInAir) {
            if (this.deathTime >= DEATH_ANIMATION_DURATION) {
                this.removeDeadBody();
            }

            return;
        }

        boolean landed = this.onGround() || this.isInWater() || this.deathTime >= DEAD_FALL_LIMIT;

        if (landed && this.impactTicks == 0) {
            this.playSound(SoundEvents.GENERIC_SMALL_FALL, 0.6F, 1.2F);
        }

        if (landed && ++this.impactTicks >= IMPACT_REST_TICKS && this.deathTime >= DEATH_ANIMATION_DURATION) {
            this.removeDeadBody();
        }
    }

    private void removeDeadBody() {
        this.level().broadcastEntityEvent(this, (byte) 60);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.diedInAir = !this.onGround();
        // The move control does not tick after death, so restore gravity here.
        this.setNoGravity(false);
        // Stop the path, or the safety net lifts the dead bird.
        this.getNavigation().stop();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(AnimationTable.<CorvidEntity>create()
                .when(() -> this.deathTime > 0, this.getSpecies().getDeathAnimation())
                .when(() -> this.getFlightState() == FlightState.HOP, HOP)
                .when(() -> this.getFlightState() == FlightState.WALK, this.getSpecies().getWalkAnimation())
                .when(() -> this.getFlightState() == FlightState.LAND, LAND)
                .when(() -> this.getFlightState() == FlightState.GLIDE, GLIDE)
                .when(() -> this.getFlightState() == FlightState.FLY, FLY)
                .when(() -> this.getFlightState() == FlightState.WATCH, WATCH)
                .otherwise(IDLE)
                .build(this, "controller")
                .triggerableAnim("peck", this.getSpecies().getPeckAnimation())
                .triggerableAnim("takeoff", TAKEOFF));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    static {
        FLIGHT_STATE = SynchedEntityData.defineId(CorvidEntity.class, EntityDataSerializers.BYTE);
    }
}
