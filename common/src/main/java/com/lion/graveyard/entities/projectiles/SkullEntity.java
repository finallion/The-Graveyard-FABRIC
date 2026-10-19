package com.lion.graveyard.entities.projectiles;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SkullEntity extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Boolean> CHARGED;
    private static final double INITIAL_SPEED = 0.001D;

    public SkullEntity(EntityType<? extends SkullEntity> entityType, Level world) {
        super(entityType, world);
    }

    public SkullEntity(Level world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(TGEntities.SKULL.get(), owner, new Vec3(directionX, directionY, directionZ), world);
        this.setDeltaMovement(new Vec3(directionX, directionY, directionZ).normalize().scale(INITIAL_SPEED));
    }

    @Override
    protected float getInertia() {
        return this.isCharged() ? 0.73F : super.getInertia();
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 0.0F;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public float getBlockExplosionResistance(
            Explosion explosion,
            BlockGetter world,
            BlockPos pos,
            BlockState blockState,
            FluidState fluidState,
            float max
    ) {
        return this.isCharged() && WitherBoss.canDestroy(blockState) ? Math.min(0.8F, max) : max;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            boolean damaged;

            if (entity2 instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity2;
                damaged = entity.hurtServer((ServerLevel) this.level(), this.damageSources().indirectMagic(this, livingEntity), 10.0F);

                if (damaged) {
                    if (entity.isAlive() && this.level() instanceof ServerLevel serverLevel) {
                        EnchantmentHelper.doPostAttackEffects(serverLevel, entity, this.damageSources().indirectMagic(this, livingEntity));
                    }
                }
            }


        }
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3d = this.getDeltaMovement();
        this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + vec3d.x * 0.4D, this.getY() + vec3d.y + 0.5D, this.getZ() + vec3d.z * 0.4D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide()) {
            Explosion.BlockInteraction destructionType = Explosion.BlockInteraction.KEEP;
            var champion = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion");
            Level.ExplosionInteraction sourceType = champion != null && champion.skullsDestroyBlocks() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
            float radius = champion != null ? champion.skullExplosionRadius() : 2.0F;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), radius, false, sourceType);
            this.discard();
        }

    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGED, false);
    }

    public boolean isCharged() {
        return (Boolean)this.entityData.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(CHARGED, charged);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    static {
        CHARGED = SynchedEntityData.defineId(SkullEntity.class, EntityDataSerializers.BOOLEAN);
    }
}

