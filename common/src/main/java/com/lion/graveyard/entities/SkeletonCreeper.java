package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.init.TGBlocks;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.Collection;
import java.util.Random;

public class SkeletonCreeper extends Creeper {
    private Player closestPlayer;

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

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        LegacyEntityData.write(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        LegacyEntityData.read(this, input);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        Entity entity = source.getEntity();

        if (entity instanceof Creeper) {
            Creeper creeperEntity = (Creeper)entity;

            if (creeperEntity.isPowered() && random.nextBoolean()) {
                this.spawnAtLocation(level, TGBlocks.CREEPER_SKELETON.get().asItem());
            }
        } else {
            super.dropCustomDeathLoot(level, source, recentlyHit);
        }
    }

    public void explode() {
        if (!this.level().isClientSide()) {
            GraveyardConfig config = Graveyard.getConfig();
            Level.ExplosionInteraction destructionType = config.flag("skeletonCreeperDestroysBlocks", false) ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
            float f = this.isPowered() ? 2.0F : 1.0F;

            if (canStart()) {
                this.playSound(SoundEvents.END_PORTAL_SPAWN, 1.0F, 10.0F);
                this.closestPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 125));
            }

            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius(config) * f, destructionType);
            this.discard();
            this.spawnEffectsCloud();
        }
    }

    private void spawnEffectsCloud() {
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

    private static float explosionRadius(GraveyardConfig config) {
        double radius = config.number("skeletonCreeperExplosionRadius", 3.5D);
        return Double.isFinite(radius) ? (float) Math.max(0.0D, Math.min(16.0D, radius)) : 3.5F;
    }
}
