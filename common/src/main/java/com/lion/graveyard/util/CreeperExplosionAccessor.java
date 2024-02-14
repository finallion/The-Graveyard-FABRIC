package com.lion.graveyard.util;

import com.lion.graveyard.entities.SkeletonCreeper;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CreeperExplosionAccessor {

    // helper class to access the private explode method of the creeper entity
    public static void explode(Creeper creeperEntity, CallbackInfo info) {
        if (creeperEntity instanceof SkeletonCreeper skeletonCreeper) {
            if (!creeperEntity.getEntityWorld().isClient()) {
                skeletonCreeper.explode();
                info.cancel();
            }

        }
    }
}
