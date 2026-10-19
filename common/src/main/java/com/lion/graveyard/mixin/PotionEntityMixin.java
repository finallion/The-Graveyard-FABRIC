package com.lion.graveyard.mixin;

import com.lion.graveyard.blocks.SmolderingWood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractThrownPotion.class)
public abstract class PotionEntityMixin {

    @Inject(method = "dowseFire", at = @At("HEAD"))
    private void graveyard$extinguishSmoldering(BlockPos pos, CallbackInfo info) {
        Projectile self = (Projectile) (Entity) (Object) this;
        SmolderingWood.extinguish(self.level(), pos, self.getOwner());
    }
}
