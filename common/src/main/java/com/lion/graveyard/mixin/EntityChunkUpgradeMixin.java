package com.lion.graveyard.mixin;

import com.lion.graveyard.util.EntityChunkUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Keeps Graveyard entities out of the entity chunk fix so vanilla entities in the chunk are upgraded.
@Mixin(SimpleRegionStorage.class)
public abstract class EntityChunkUpgradeMixin {
    @Shadow
    @Final
    private DataFixTypes dataFixType;

    @Inject(
            method = "upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void graveyard$upgradeAroundModEntities(
            CompoundTag tag,
            int defaultVersion,
            CallbackInfoReturnable<CompoundTag> cir
    ) {
        if (this.dataFixType != DataFixTypes.ENTITY_CHUNK) {
            return;
        }

        SimpleRegionStorage self = (SimpleRegionStorage) (Object) this;
        CompoundTag result = EntityChunkUpgrade.upgradeWithoutGraveyardEntities(
                tag,
                defaultVersion,
                copy -> self.upgradeChunkTag(copy, defaultVersion)
        );

        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
