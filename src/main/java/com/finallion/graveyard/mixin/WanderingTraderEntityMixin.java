package com.finallion.graveyard.mixin;

import com.finallion.graveyard.entities.HordeGraveyardEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin extends MerchantEntity {

    public WanderingTraderEntityMixin(EntityType<? extends WanderingTraderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At(value = "TAIL"))
    private void initCustomGoal(CallbackInfo ci) {
        this.goalSelector.add(1, new FleeEntityGoal<>(this, HordeGraveyardEntity.class, 10.0F, 0.5D, 0.5D));
        //this.goalSelector.add(1, new FleeEntityGoal<>(this, AcolyteEntity.class, 10.0F, 0.5D, 0.5D));
    }
}
