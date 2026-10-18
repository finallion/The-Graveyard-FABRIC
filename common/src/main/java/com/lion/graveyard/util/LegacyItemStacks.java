package com.lion.graveyard.util;

import com.lion.graveyard.Graveyard;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;

public final class LegacyItemStacks {
    public static final int LEGACY_DATA_VERSION = 3465;

    private static final String EMPTY_ID = "minecraft:air";

    private LegacyItemStacks() {}

    public static ItemStack parse(HolderLookup.Provider registries, CompoundTag stackTag) {
        if (stackTag.isEmpty() || !stackTag.contains("id") || EMPTY_ID.equals(stackTag.getString("id"))) {
            return ItemStack.EMPTY;
        }

        boolean legacy = stackTag.contains("Count") && !stackTag.contains("count");

        if (legacy && stackTag.getByte("Count") <= 0) {
            return ItemStack.EMPTY;
        }

        return ItemStack.parseOptional(registries, legacy ? upgrade(stackTag) : stackTag);
    }

    public static void loadSlots(ListTag slots, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        for (int i = 0; i < slots.size(); i++) {
            CompoundTag slotTag = slots.getCompound(i);
            int slot = slotTag.getByte("Slot") & 255;

            if (slot < items.size()) {
                items.set(slot, parse(registries, slotTag));
            }
        }
    }

    private static CompoundTag upgrade(CompoundTag stackTag) {
        try {
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(
                    References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, stackTag.copy()),
                    LEGACY_DATA_VERSION,
                    SharedConstants.getCurrentVersion().getDataVersion().getVersion()
            );

            if (fixed.getValue() instanceof CompoundTag result) {
                return result;
            }
        } catch (RuntimeException e) {
            Graveyard.LOGGER.warn("Cannot upgrade a stored item stack", e);
        }

        return stackTag;
    }
}
