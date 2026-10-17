package com.lion.graveyard.entities.projectiles;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGEntities;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SkullEntity extends AbstractHurtingProjectile {

    public static Packet<ClientGamePacketListener> createPacket(Entity entity) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(BuiltInRegistries.ENTITY_TYPE.getId(entity.getType()));
        buf.writeUUID(entity.getUUID());
        buf.writeVarInt(entity.getId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(Mth.floor(entity.getXRot() * 256.0F / 360.0F));
        buf.writeByte(Mth.floor(entity.getYRot() * 256.0F / 360.0F));
        buf.writeFloat(entity.getXRot());
        buf.writeFloat(entity.getYRot());
        return new ClientboundAddEntityPacket(entity, buf.arrayOffset());
    }

    private static final EntityDataAccessor<Boolean> CHARGED;

    public SkullEntity(EntityType<? extends SkullEntity> entityType, Level world) {
        super(entityType, world);
    }

    public SkullEntity(Level world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(TGEntities.SKULL.get(), owner, directionX, directionY, directionZ, world);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return createPacket(this);
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
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return this.isCharged() && WitherBoss.canDestroy(blockState) ? Math.min(0.8F, max) : max;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!this.getCommandSenderWorld().isClientSide) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            boolean damaged;

            if (entity2 instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity2;
                damaged = entity.hurt(this.damageSources().indirectMagic(this, livingEntity), 10.0F);

                if (damaged) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingEntity, entity);
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

        if (!this.getCommandSenderWorld().isClientSide) {
            Explosion.BlockInteraction destructionType = Explosion.BlockInteraction.KEEP;
            var champion = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion");
            Level.ExplosionInteraction sourceType = champion != null && champion.skullsDestroyBlocks() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
            float radius = champion != null ? champion.skullExplosionRadius() : 2.0F;
            this.getCommandSenderWorld().explode(this, this.getX(), this.getY(), this.getZ(), radius, false, sourceType);
            this.discard();
        }

    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CHARGED, false);
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

