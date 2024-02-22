package com.lion.graveyard.entities;

import com.lion.graveyard.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class FallingCorpse extends Monster implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation FALLING_ANIMATION = RawAnimation.begin().then("falling", Animation.LoopType.LOOP);
    private final RawAnimation LANDING_ANIMATION = RawAnimation.begin().then("landing", Animation.LoopType.PLAY_ONCE).then("despawn", Animation.LoopType.PLAY_ONCE);
    private static final EntityDataAccessor<Boolean> IS_FALLING;
    private static final EntityDataAccessor<Boolean> HAS_COLLIDED;
    private final float DAMAGE = 10.0F;
    private int landingCounter = 40;
    private int levitationCounter = 15;
    private float rotation;

    public FallingCorpse(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        setRotation(getRandom().nextInt(361));
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    // I don't want the red overlay on death, so bypass landing effects and kill the mob after some ticks (in customServerAiStep())
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        this.level().playSound(null, this.blockPosition(), SoundEvents.HOSTILE_BIG_FALL, SoundSource.HOSTILE, 2.0F, 1.0F);
        return false;
    }

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

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_FALLING, true);
        this.entityData.define(HAS_COLLIDED, false);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "controller", 0, event -> {
            if (isFalling()) {
                event.setAnimation(FALLING_ANIMATION);
                return PlayState.CONTINUE;
            } else {
                event.getController().setAnimation(LANDING_ANIMATION);
                return PlayState.CONTINUE;
            }
        }));
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
        if (this.getBlockStateOn().isAir()) {
            for (int i = 0; i < 10; i++) {
                BlockPos pos = this.blockPosition().offset(0, -i, 0);
                BlockState state = this.level().getBlockState(pos);
                if (!state.isAir() && state.isSolidRender(this.level(), pos)) {
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

        // kill after despawn animation has played to avoid red death overlay
        if (!isFalling() && landingCounter > 0) {
            landingCounter--;
        }

        if (landingCounter <= 0) {
            this.discard();
        }

        if (!level().getBlockState(this.blockPosition().below()).isAir()) {
            setIsFalling(false);
        }

        if (this.getBlockStateOn().isAir()) {
            setIsFalling(true);
        }

        super.customServerAiStep();
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

    public boolean hasCollided() {
        return this.entityData.get(HAS_COLLIDED);
    }

    public void setHasCollided(boolean hasCollided) {
        this.entityData.set(HAS_COLLIDED, hasCollided);
    }

    static {
        IS_FALLING = SynchedEntityData.defineId(FallingCorpse.class, EntityDataSerializers.BOOLEAN);
        HAS_COLLIDED = SynchedEntityData.defineId(FallingCorpse.class, EntityDataSerializers.BOOLEAN);
    }


}




