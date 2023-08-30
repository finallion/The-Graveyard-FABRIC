package com.lion.graveyard.util;

import com.lion.graveyard.entities.SkeletonCreeper;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CreeperExplosionAccessor {

    // helper class to access the private explode method of the creeper entity
    public static void explode(CreeperEntity creeperEntity, CallbackInfo info) {
        if (creeperEntity instanceof SkeletonCreeper) {
            if (!creeperEntity.getEntityWorld().isClient()) {
                ((SkeletonCreeper) creeperEntity).explode();
                info.cancel();
            }

        }
    }
}
