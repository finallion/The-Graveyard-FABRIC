package com.lion.graveyard.mixin;

import com.lion.graveyard.util.CreeperExplosionAccessor;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

    @Inject(method="explode", at = @At("HEAD"), cancellable = true)
    private void explode(CallbackInfo info) {
        CreeperEntity creeperEntity = ((CreeperEntity) (Object) this);
        CreeperExplosionAccessor.explode(creeperEntity, info);

    }
}
