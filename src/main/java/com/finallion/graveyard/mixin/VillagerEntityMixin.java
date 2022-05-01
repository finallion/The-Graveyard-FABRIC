package com.finallion.graveyard.mixin;

import com.finallion.graveyard.entities.HordeGraveyardEntity;
import com.finallion.graveyard.entities.HostileGraveyardEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends PassiveEntity {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;Lnet/minecraft/village/VillagerType;)V", at = @At(value = "TAIL"))
    private void inject(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type, CallbackInfo ci) {
        this.goalSelector.add(1, new FleeEntityGoal<>(this, HostileGraveyardEntity.class, 10.0F, 0.7D, 1.0D));
    }
}
