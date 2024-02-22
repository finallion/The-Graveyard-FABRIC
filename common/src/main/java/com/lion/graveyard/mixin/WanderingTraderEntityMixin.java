package com.lion.graveyard.mixin;

import com.lion.graveyard.entities.HostileGraveyardEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillager {

    public WanderingTraderEntityMixin(EntityType<? extends WanderingTrader> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At(value = "TAIL"))
    private void initCustomGoal(CallbackInfo ci) {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, HostileGraveyardEntity.class, 10.0F, 0.5D, 0.5D));
    }
}
