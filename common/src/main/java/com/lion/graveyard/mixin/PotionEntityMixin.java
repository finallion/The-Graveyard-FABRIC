package com.lion.graveyard.mixin;

import com.lion.graveyard.blocks.SmolderingWood;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin {

    @Inject(method = "extinguishFire", at = @At("HEAD"))
    private void graveyard$extinguishSmoldering(BlockPos pos, CallbackInfo info) {
        ProjectileEntity self = (ProjectileEntity) (Entity) (Object) this;
        SmolderingWood.extinguish(self.getWorld(), pos, self.getOwner());
    }
}
