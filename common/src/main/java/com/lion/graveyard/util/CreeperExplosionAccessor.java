package com.lion.graveyard.util;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CreeperExplosionAccessor {

    // helper class to access the private explode method of the creeper entity
    // needed if more creeper like-mobs getadded

    public static void explode(Creeper creeperEntity, CallbackInfo info) {
        if (creeperEntity instanceof main.java.com.lion.graveyard.entities.SkeletonCreeper) {
            if (!creeperEntity.getEntityWorld().isClient()) {
                ((main.java.com.lion.graveyard.entities.SkeletonCreeper) creeperEntity).explode();
                info.cancel();
            }

        }
    }
}
