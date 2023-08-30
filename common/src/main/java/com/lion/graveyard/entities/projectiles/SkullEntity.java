package com.lion.graveyard.entities.projectiles;

import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.init.TGEntities;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SkullEntity extends ExplosiveProjectileEntity {

    public static Packet<ClientPlayPacketListener> createPacket(Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Registries.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeUuid(entity.getUuid());
        buf.writeVarInt(entity.getId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(MathHelper.floor(entity.getPitch() * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(entity.getYaw() * 256.0F / 360.0F));
        buf.writeFloat(entity.getPitch());
        buf.writeFloat(entity.getYaw());
        return new EntitySpawnS2CPacket(entity, buf.arrayOffset());
    }

    private static final TrackedData<Boolean> CHARGED;

    public SkullEntity(EntityType<? extends SkullEntity> entityType, World world) {
        super(entityType, world);
    }

    public SkullEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(TGEntities.SKULL.get(), owner, directionX, directionY, directionZ, world);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return createPacket(this);
    }


    protected float getDrag() {
        return this.isCharged() ? 0.73F : super.getDrag();
    }

    @Override
    public float getBrightnessAtEyes() {
        return 0.0F;
    }

    public boolean isOnFire() {
        return false;
    }

    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return this.isCharged() && WitherEntity.canDestroy(blockState) ? Math.min(0.8F, max) : max;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getEntityWorld().isClient) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            boolean bl;
            if (entity2 instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity2;
                bl = entity.damage(this.getDamageSources().indirectMagic(this, livingEntity), 10.0F);
                if (bl) {
                    if (entity.isAlive()) {
                        this.applyDamageEffects(livingEntity, entity);
                    }
                }
            }


        }
    }

    public void tick() {
        super.tick();
        Vec3d vec3d = this.getVelocity();
        this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + vec3d.x * 0.4D, this.getY() + vec3d.y + 0.5D, this.getZ() + vec3d.z * 0.4D, 0.0D, 0.0D, 0.0D);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getEntityWorld().isClient) {
            Explosion.DestructionType destructionType = Explosion.DestructionType.KEEP;
            World.ExplosionSourceType sourceType = World.ExplosionSourceType.NONE;
            this.getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.0F, false, sourceType);
            this.discard();
        }

    }

    @Override
    public boolean canHit() {
        return false;
    }

    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(CHARGED, false);
    }

    public boolean isCharged() {
        return (Boolean)this.dataTracker.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.dataTracker.set(CHARGED, charged);
    }

    protected boolean isBurning() {
        return false;
    }

    static {
        CHARGED = DataTracker.registerData(SkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}

