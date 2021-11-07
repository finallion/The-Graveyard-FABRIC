package com.finallion.graveyard.entities;

import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class SkeletonCreeper extends CreeperEntity {
    private AttributeContainer attributeContainer;
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    private PlayerEntity closestPlayer;
    private final double explosionRadius = 3.5D;

    public SkeletonCreeper(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null)
            attributeContainer = new AttributeContainer(CreeperEntity.createCreeperAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.29D).build());
        return attributeContainer;
    }

    public static boolean canSpawn(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return isSpawnDark(world, pos, random);
    }

    public boolean canStart() {
        this.closestPlayer = this.world.getClosestPlayer(SkeletonCreeper.CLOSE_PLAYER_PREDICATE, this);
        return this.closestPlayer != null;
    }

    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        Entity entity = source.getAttacker();
        if (entity instanceof CreeperEntity) {
            CreeperEntity creeperEntity = (CreeperEntity)entity;
            // changed from onHeadsDropped so every skeleton creeper will drop its skeleton instead of only one per charged creeper
            if (creeperEntity.shouldRenderOverlay() && random.nextBoolean()) {
                this.dropItem(TGBlocks.CREEPER_SKELETON.asItem());
            }
        } else {
            super.dropEquipment(source, lootingMultiplier, allowDrops);
        }

    }

    public void explode() {
        if (!this.world.isClient) {
            Explosion.DestructionType destructionType = Explosion.DestructionType.NONE;
            float f = this.shouldRenderOverlay() ? 2.0F : 1.0F;

            if (canStart()) {
                this.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 1.0F, 10.0F);
                this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 125));
            }

            this.dead = true;
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, destructionType);
            this.discard();
            this.spawnEffectsCloud();
        }

    }

    private void spawnEffectsCloud() {
        Collection<StatusEffectInstance> collection = this.getStatusEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
            areaEffectCloudEntity.setRadius(2.5F);
            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
            areaEffectCloudEntity.setWaitTime(10);
            areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
            Iterator var3 = collection.iterator();

            while(var3.hasNext()) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var3.next();
                areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
            }

            this.world.spawnEntity(areaEffectCloudEntity);
        }

    }





    @Override
    public void tickMovement() {
        if (random.nextInt(7) == 0) {
            this.world.addParticle(ParticleTypes.ASH, this.getParticleX(2.0D), this.getRandomBodyY() + new Random().nextInt(1), this.getParticleZ(2.0), 2.0D, 7.0D, 2.0D);

        }
        super.tickMovement();
    }

    static {
        CLOSE_PLAYER_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(8);
    }

}
