package com.lion.graveyard.mixin;

import com.lion.graveyard.util.EntityChunkUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Pseudo mixin. The target class exists only when Moonrise is loaded.
@Pseudo
@Mixin(targets = "ca.spottedleaf.moonrise.patches.chunk_system.ChunkSystemConverters", remap = false)
public abstract class MoonriseEntityChunkMixin {
    @Shadow(remap = false)
    public static CompoundTag convertEntityChunkCompoundTag(CompoundTag tag, ServerLevel level) {
        throw new AssertionError();
    }

    @Inject(method = "convertEntityChunkCompoundTag", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void graveyard$upgradeAroundModEntities(
            CompoundTag tag,
            ServerLevel level,
            CallbackInfoReturnable<CompoundTag> cir
    ) {
        CompoundTag result = EntityChunkUpgrade.upgradeWithoutGraveyardEntities(
                tag,
                -1,
                copy -> convertEntityChunkCompoundTag(copy, level)
        );

        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
