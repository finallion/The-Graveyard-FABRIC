package com.finallion.graveyard.entities;

import com.finallion.graveyard.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class FallingCorpse extends HostileEntity implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final AnimationBuilder FALLING_ANIMATION = new AnimationBuilder().addAnimation("falling", ILoopType.EDefaultLoopTypes.LOOP);
    private final AnimationBuilder LANDING_ANIMATION = new AnimationBuilder().addAnimation("landing", ILoopType.EDefaultLoopTypes.PLAY_ONCE).addAnimation("despawn", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private static final TrackedData<Boolean> IS_FALLING;
    private static final TrackedData<Boolean> HAS_COLLIDED;
    private final float DAMAGE = 10.0F;
    private int landingCounter = 40;
    private int levitationCounter = 15;
    private float rotation;

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

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (isFalling()) {
            event.getController().setAnimation(FALLING_ANIMATION);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(LANDING_ANIMATION);
            return PlayState.CONTINUE;
        }

    }

    // I don't want the red overlay on death, so bypass landing effects and kill the mob after some ticks (in mobTick())
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_HOSTILE_BIG_FALL, SoundCategory.HOSTILE, 2.0F, 1.0F);
        return false;
    }

    public boolean damage(DamageSource source, float amount) {
        if (source == DamageSource.OUT_OF_WORLD) {
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
    }


    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }


    public static DefaultAttributeContainer.Builder createFallingCorpseAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 5.0D);
    }

    @Override
    public void tickMovement() {
        if (this.getBlockStateAtPos().isAir()) {
            for (int i = 0; i < 20; i++) {
                BlockPos pos = this.getBlockPos().add(0, -i, 0);
                BlockState state = this.world.getBlockState(pos);
                if (!state.isAir() && state.isSolidBlock(world, pos)) {
                    //this.world.addParticle(ParticleTypes.SCULK_CHARGE_POP, pos.getX() + random.nextDouble() + random.nextDouble() - random.nextDouble(), pos.getY() + 1.3D, pos.getZ() + random.nextDouble() + random.nextDouble() - random.nextDouble(), 0.0D, 0.0D, 0.0D);
                    MathUtil.createParticleDisk(this.getWorld(), pos.getX() + random.nextDouble(), pos.getY() + 1.3D, pos.getZ() + + random.nextDouble(), 0.0D, 0.0D, 0.0D,1, DustParticleEffect.DEFAULT, this.getRandom());
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

        // kill after despawn animation has played to avoid red death overlay
        if (!isFalling() && landingCounter > 0) {
            landingCounter--;
        }

        if (landingCounter <= 0) {
            this.discard();
        }

        if (!world.getBlockState(this.getBlockPos().down()).isAir()) {
            setIsFalling(false);
        }

        if (this.getBlockStateAtPos().isAir()) {
            setIsFalling(true);
        }

        super.mobTick();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!hasCollided() && isFalling()) {
            player.damage(DamageSource.GENERIC, DAMAGE);
            setHasCollided(true);
        }
        super.onPlayerCollision(player);
    }


    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public boolean isFalling() {
        return this.dataTracker.get(IS_FALLING);
    }

    public void setIsFalling(boolean isFalling) {
        this.dataTracker.set(IS_FALLING, isFalling);
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
    }

}




