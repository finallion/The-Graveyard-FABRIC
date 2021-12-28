package com.finallion.graveyard.util;


import com.finallion.graveyard.entities.SkeletonCreeper;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CreeperExplosionAccessor {

    // helper class to access the private explode method of the creeper entity
    // needed if more creeper like mobs getStructure added

    public static void explode(CreeperEntity creeperEntity, CallbackInfo info) {
        if (creeperEntity instanceof SkeletonCreeper) {
            if (!creeperEntity.world.isClient()) {
                ((SkeletonCreeper) creeperEntity).explode();
                info.cancel();
            }

        }
    }
}
