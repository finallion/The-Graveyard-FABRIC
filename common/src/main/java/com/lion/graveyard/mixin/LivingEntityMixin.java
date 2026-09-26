package com.lion.graveyard.mixin;

import com.lion.graveyard.util.CrowAnchors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void recordDeathSite(DamageSource damageSource, CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        CrowAnchors.recordDeath(entity.getWorld(), entity.getBlockPos());
    }
}
