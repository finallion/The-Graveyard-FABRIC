package com.lion.graveyard.entities;

import com.lion.graveyard.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


public class FallingCorpse extends HostileEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation FALLING_ANIMATION = RawAnimation.begin().then("falling", Animation.LoopType.LOOP);
    private final RawAnimation LANDING_ANIMATION = RawAnimation.begin().then("landing", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation DESPAWN_ANIMATION = RawAnimation.begin().then("despawn", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final TrackedData<Boolean> IS_FALLING;
    private static final TrackedData<Boolean> HAS_COLLIDED;
    private static final TrackedData<Boolean> SINKING;
    private final float DAMAGE = 10.0F;
    private static final int LANDING_TICKS = 52;
    private static final int SINK_START_TICKS = 26;
    /// Ticks from ground contact to the slam keyframe of the landing animation.
    private static final int IMPACT_DELAY = 7;
    private static final int LANDING_TRANSITION_TICKS = 2;
    private int landingCounter = LANDING_TICKS;
    private int levitationCounter = 15;
    private float rotation;
    private int impactTicks = -1;
    private boolean impacted;

    public FallingCorpse(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 0;
        setRotation(getRandom().nextInt(361));
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    // mobTick discards the mob, because a kill shows the red overlay.
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (!impacted && impactTicks < 0) {
            impactTicks = IMPACT_DELAY;
        }
        return false;
    }

    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
            return true;
        }
        return false;
    }

    @Override
    public void onLanding() {
        setIsFalling(false);
        super.onLanding();
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_FALLING, true);
        this.dataTracker.startTracking(HAS_COLLIDED, false);
        this.dataTracker.startTracking(SINKING, false);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(AnimationTable.<FallingCorpse>create()
                .transitionTicks(() -> !isFalling() && !isSinking() ? LANDING_TRANSITION_TICKS : AnimationTable.TRANSITION_TICKS)
                .when(this::isFalling, FALLING_ANIMATION)
                .when(this::isSinking, DESPAWN_ANIMATION)
                .otherwise(LANDING_ANIMATION)
                .build(this, "controller"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    public static DefaultAttributeContainer.Builder createFallingCorpseAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 5.0D);
    }

    @Override
    public void tickMovement() {
        if (this.getBlockStateAtPos().isAir()) {
            for (int i = 0; i < 10; i++) {
                BlockPos pos = this.getBlockPos().add(0, -i, 0);
                BlockState state = this.getEntityWorld().getBlockState(pos);
                if (!state.isAir() && state.isSolidBlock(getEntityWorld(), pos)) {
                    MathUtil.createParticleDisk(this.getWorld(), pos.getX() + random.nextDouble(), pos.getY() + 1.3D, pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D,1, DustParticleEffect.DEFAULT, this.getRandom());
                    break;
                }
            }
        }

        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        if (levitationCounter > 0) {
            levitationCounter--;
            this.setVelocity(this.getVelocity().add(0.0D, 0.04D, 0.0D));
        } else {
            this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
        }

        if (impactTicks > 0 && --impactTicks == 0) {
            impact();
        }

        if (!isFalling() && landingCounter > 0) {
            landingCounter--;
        }

        if (landingCounter <= SINK_START_TICKS) {
            setSinking(true);
        }

        if (landingCounter <= 0) {
            this.discard();
        }

        if (!getEntityWorld().getBlockState(this.getBlockPos().down()).isAir()) {
            setIsFalling(false);
        }

        if (this.getBlockStateAtPos().isAir()) {
            setIsFalling(true);
        }

        super.mobTick();
    }

    private void impact() {
        impactTicks = -1;
        impacted = true;
        getEntityWorld().playSound(null, getBlockPos(), SoundEvents.ENTITY_HOSTILE_BIG_FALL, SoundCategory.HOSTILE, 2.0F, 0.8F);
        getEntityWorld().playSound(null, getBlockPos(), SoundEvents.BLOCK_BONE_BLOCK_BREAK, SoundCategory.HOSTILE, 1.2F, 0.6F);
        BlockState ground = getEntityWorld().getBlockState(getBlockPos().down());
        if (getEntityWorld() instanceof ServerWorld world && !ground.isAir()) {
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, ground), getX(), getY() + 0.1D, getZ(), 30, 0.6D, 0.05D, 0.6D, 0.15D);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!hasCollided() && isFalling()) {
            player.damage(this.getDamageSources().fall(), DAMAGE);
            setHasCollided(true);
        }
        super.onPlayerCollision(player);
    }

    public boolean isFalling() {
        return this.dataTracker.get(IS_FALLING);
    }

    public void setIsFalling(boolean isFalling) {
        this.dataTracker.set(IS_FALLING, isFalling);
    }

    public boolean isSinking() {
        return this.dataTracker.get(SINKING);
    }

    public void setSinking(boolean sinking) {
        this.dataTracker.set(SINKING, sinking);
    }

    public boolean hasCollided() {
        return this.dataTracker.get(HAS_COLLIDED);
    }

    public void setHasCollided(boolean hasCollided) {
        this.dataTracker.set(HAS_COLLIDED, hasCollided);
    }

    static {
        IS_FALLING = DataTracker.registerData(FallingCorpse.class, TrackedDataHandlerRegistry.BOOLEAN);
        HAS_COLLIDED = DataTracker.registerData(FallingCorpse.class, TrackedDataHandlerRegistry.BOOLEAN);
        SINKING = DataTracker.registerData(FallingCorpse.class, TrackedDataHandlerRegistry.BOOLEAN);
    }


}




