package com.lion.graveyard.util;

import com.lion.graveyard.entities.SkeletonCreeper;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CreeperExplosionAccessor {

    public static void explode(Creeper creeperEntity, CallbackInfo info) {
        if (creeperEntity instanceof SkeletonCreeper) {
            if (!creeperEntity.level().isClientSide()) {
                ((SkeletonCreeper) creeperEntity).explode();
                info.cancel();
            }

        }
    }
}
