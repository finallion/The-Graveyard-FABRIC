package com.lion.graveyard.util;

import java.util.function.UnaryOperator;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;

public final class EntityChunkUpgrade {
    private EntityChunkUpgrade() {}

    /// Returns null if the vanilla fixer must run.
    public static CompoundTag upgradeWithoutGraveyardEntities(
            CompoundTag tag,
            int defaultVersion,
            UnaryOperator<CompoundTag> vanillaUpgrade
    ) {
        if (NbtUtils.getDataVersion(tag, defaultVersion) >= SharedConstants.WORLD_VERSION) {
            return null;
        }

        if (!hasModEntity(tag.getListOrEmpty("Entities"))) {
            return null;
        }

        CompoundTag copy = tag.copy();
        ListTag entities = copy.getListOrEmpty("Entities");
        ListTag modEntities = new ListTag();

        for (int i = entities.size() - 1; i >= 0; i--) {
            if (isModEntity(entities.getCompoundOrEmpty(i))) {
                modEntities.add(0, entities.remove(i));
            }
        }

        CompoundTag result = vanillaUpgrade.apply(copy);
        ListTag upgraded = result.getListOrEmpty("Entities");
        upgraded.addAll(modEntities);
        result.put("Entities", upgraded);

        return result;
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
