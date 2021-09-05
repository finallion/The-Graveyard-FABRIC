package com.finallion.graveyard.enitites;

import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class SkeletonCreeper extends CreeperEntity {
    private AttributeContainer attributeContainer;
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE;
    private PlayerEntity closestPlayer;

    public SkeletonCreeper(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null)
            attributeContainer = new AttributeContainer(CreeperEntity.createCreeperAttributes().build());
        return attributeContainer;
    }


    @Override
    protected void mobTick() {
        if (canStart() && this.closestPlayer.world.random.nextInt(20) == 0) {
            this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100));
        }
        super.mobTick();
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
            if (creeperEntity.shouldRenderOverlay()) {
                this.dropItem(TGBlocks.CREEPER_SKELETON.asItem());
            } else {
                super.dropEquipment(source, lootingMultiplier, allowDrops);
            }
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
