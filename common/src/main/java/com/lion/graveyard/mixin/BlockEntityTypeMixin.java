package com.lion.graveyard.mixin;

import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin {
    @Inject(method = "onlyOpCanSetNbt", at = @At("HEAD"), cancellable = true)
    private void graveyard$gravestoneIsOpOnly(CallbackInfoReturnable<Boolean> info) {
        if ((Object) this == TGBlockEntities.GRAVESTONE_BLOCK_ENTITY.get()) {
            info.setReturnValue(true);
        }
    }
}
