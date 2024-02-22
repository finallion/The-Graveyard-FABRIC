package com.lion.graveyard.mixin;

import com.lion.graveyard.util.CreeperExplosionAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin {

    @Inject(method="explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void explode(CallbackInfo info) {
        Creeper creeperEntity = ((Creeper) (Object) this);
        CreeperExplosionAccessor.explode(creeperEntity, info);

    }
}
