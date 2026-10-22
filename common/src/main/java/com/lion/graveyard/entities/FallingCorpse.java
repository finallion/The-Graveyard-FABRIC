package com.lion.graveyard.entities;

import com.lion.graveyard.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FallingCorpse extends Monster implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation FALLING_ANIMATION = RawAnimation.begin().then("falling", Animation.LoopType.LOOP);
    private final RawAnimation LANDING_ANIMATION = RawAnimation.begin().then("landing", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation DESPAWN_ANIMATION = RawAnimation.begin().then("despawn", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final EntityDataAccessor<Boolean> IS_FALLING;
    private static final EntityDataAccessor<Boolean> HAS_COLLIDED;
    private static final EntityDataAccessor<Boolean> SINKING;
    private final float DAMAGE = 10.0F;
    private static final int LANDING_TICKS = 52;
    private static final int SINK_START_TICKS = 26;
    /// Ticks from ground contact to the slam keyframe of the landing animation.
    private static final int IMPACT_DELAY = 7;
    private static final int LANDING_TRANSITION_TICKS = 2;
    private int landingCounter = LANDING_TICKS;
    private int levitationCounter = 15;
    private int impactTicks = -1;
    private boolean impacted;

    public FallingCorpse(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        xpReward = 0;
    }

    public float getRotation() {
        return Math.floorMod(getUUID().hashCode(), 361);
    }

    // mobTick discards the mob, because a kill shows the red overlay.
    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (!impacted && impactTicks < 0) {
            impactTicks = IMPACT_DELAY;
        }

        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            return true;
        }

        return false;
    }

    @Override
    public void resetFallDistance() {
        setIsFalling(false);
        super.resetFallDistance();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_FALLING, true);
        builder.define(HAS_COLLIDED, false);
        builder.define(SINKING, false);
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


    public static AttributeSupplier.Builder createFallingCorpseAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 5.0D);
    }

    @Override
    public void aiStep() {
        if (this.getInBlockState().isAir()) {
            for (int i = 0; i < 10; i++) {
                BlockPos pos = this.blockPosition().offset(0, -i, 0);
                BlockState state = this.getCommandSenderWorld().getBlockState(pos);

                if (!state.isAir() && state.isRedstoneConductor(getCommandSenderWorld(), pos)) {
                    MathUtil.createParticleDisk(this.level(), pos.getX() + random.nextDouble(), pos.getY() + 1.3D, pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D,1, DustParticleOptions.REDSTONE, this.getRandom());
                    break;
                }
            }
        }

        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        if (levitationCounter > 0) {
            levitationCounter--;
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
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

        if (!getCommandSenderWorld().getBlockState(this.blockPosition().below()).isAir()) {
            setIsFalling(false);
        }

        if (this.getInBlockState().isAir()) {
            setIsFalling(true);
        }

        super.customServerAiStep();
    }

    private void impact() {
        impactTicks = -1;
        impacted = true;
        getCommandSenderWorld().playSound(null, blockPosition(), SoundEvents.HOSTILE_BIG_FALL, SoundSource.HOSTILE, 2.0F, 0.8F);
        BlockState ground = getCommandSenderWorld().getBlockState(blockPosition().below());

        if (getCommandSenderWorld() instanceof ServerLevel world && !ground.isAir()) {
            world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), getX(), getY() + 0.1D, getZ(), 30, 0.6D, 0.05D, 0.6D, 0.15D);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!hasCollided() && isFalling()) {
            player.hurt(this.damageSources().fall(), DAMAGE);
            setHasCollided(true);
        }

        super.playerTouch(player);
    }

    public boolean isFalling() {
        return this.entityData.get(IS_FALLING);
    }

    public void setIsFalling(boolean isFalling) {
        this.entityData.set(IS_FALLING, isFalling);
    }

    public boolean isSinking() {
        return this.entityData.get(SINKING);
    }

    public void setSinking(boolean sinking) {
        this.entityData.set(SINKING, sinking);
    }

    public boolean hasCollided() {
        return this.entityData.get(HAS_COLLIDED);
    }

    public void setHasCollided(boolean hasCollided) {
        this.entityData.set(HAS_COLLIDED, hasCollided);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        LegacyEntityData.read(this, nbt);
    }

    static {
        IS_FALLING = SynchedEntityData.defineId(FallingCorpse.class, EntityDataSerializers.BOOLEAN);
        HAS_COLLIDED = SynchedEntityData.defineId(FallingCorpse.class, EntityDataSerializers.BOOLEAN);
        SINKING = SynchedEntityData.defineId(FallingCorpse.class, EntityDataSerializers.BOOLEAN);
    }


}




