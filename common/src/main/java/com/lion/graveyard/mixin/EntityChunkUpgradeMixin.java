package com.lion.graveyard.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
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

        if (NbtUtils.getDataVersion(tag, defaultVersion) >= SharedConstants.WORLD_VERSION) {
            return;
        }

        if (!hasModEntity(tag.getListOrEmpty("Entities"))) {
            return;
        }

        CompoundTag copy = tag.copy();
        ListTag entities = copy.getListOrEmpty("Entities");
        ListTag modEntities = new ListTag();

        for (int i = entities.size() - 1; i >= 0; i--) {
            if (isModEntity(entities.getCompoundOrEmpty(i))) {
                modEntities.add(0, entities.remove(i));
            }
        }

        CompoundTag result = ((SimpleRegionStorage) (Object) this).upgradeChunkTag(copy, defaultVersion);
        ListTag upgraded = result.getListOrEmpty("Entities");
        upgraded.addAll(modEntities);
        result.put("Entities", upgraded);
        cir.setReturnValue(result);
    }

    private static boolean hasModEntity(ListTag entities) {
        for (int i = 0; i < entities.size(); i++) {
            if (isModEntity(entities.getCompoundOrEmpty(i))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isModEntity(CompoundTag entity) {
        return entity.getStringOr("id", "").startsWith("graveyard:");
    }
}
