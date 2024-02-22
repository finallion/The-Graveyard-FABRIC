package com.lion.graveyard.entities;

import com.lion.graveyard.init.TGBlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;


import java.util.Collection;
import java.util.Random;

public class SkeletonCreeper extends Creeper {
    private Player closestPlayer;
    private final double explosionRadius = 3.5D;

    public SkeletonCreeper(EntityType<? extends Creeper> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createSkeletonCreeperAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.29D);
    }

    public boolean canStart() {
        this.closestPlayer = this.level().getNearestPlayer(this, 8.0D);
        return this.closestPlayer != null;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected void dropCustomDeathLoot(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        Entity entity = source.getEntity();
        if (entity instanceof Creeper) {
            Creeper creeperEntity = (Creeper)entity;
            // changed from onHeadsDropped so every skeleton creeper will drop its skeleton instead of only one per charged creeper
            if (creeperEntity.isIgnited() && random.nextBoolean()) {
                this.spawnAtLocation(TGBlocks.CREEPER_SKELETON.get().asItem());
            }
        } else {
            super.dropCustomDeathLoot(source, lootingMultiplier, allowDrops);
        }
    }


    public void explode() {
        if (!this.level().isClientSide()) {
            Level.ExplosionInteraction destructionType = Level.ExplosionInteraction.NONE;
            float f = this.isPowered() ? 2.0F : 1.0F;

            if (canStart()) {
                this.playSound(SoundEvents.END_PORTAL_SPAWN, 1.0F, 10.0F);
                this.closestPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 125));
            }

            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, destructionType);
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaEffectCloudEntity.setRadius(2.5F);
            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
            areaEffectCloudEntity.setWaitTime(10);
            areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
            areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

            for (MobEffectInstance statusEffectInstance : collection) {
                areaEffectCloudEntity.addEffect(new MobEffectInstance(statusEffectInstance));
            }

            this.level().addFreshEntity(areaEffectCloudEntity);
        }
    }


    @Override
    public void aiStep() {
        if (random.nextInt(7) == 0) {
            this.level().addParticle(ParticleTypes.ASH, this.getRandomX(2.0D), this.getRandomY() + new Random().nextInt(1), this.getRandomZ(2.0), 2.0D, 7.0D, 2.0D);

        }
        super.aiStep();
    }

}
