package com.lion.graveyard.util;

import com.lion.graveyard.Graveyard;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/// Vanilla data fixers skip mod block entities and entities.
/// Without a data version, the stack shape gives the version.
public final class LegacyItemStacks {
    public static final int LEGACY_DATA_VERSION = 3465;
    public static final int PORT_1_21_1_DATA_VERSION = 3955;
    public static final int UNKNOWN_VERSION = -1;

    public static final String DATA_VERSION_KEY = "GraveyardDataVersion";
    private static final String EMPTY_ID = "minecraft:air";

    private LegacyItemStacks() {}

    public static int currentDataVersion() {
        return SharedConstants.getCurrentVersion().dataVersion().version();
    }

    public static void writeDataVersion(ValueOutput output) {
        output.putInt(DATA_VERSION_KEY, currentDataVersion());
    }

    public static int readDataVersion(ValueInput input) {
        return input.getIntOr(DATA_VERSION_KEY, UNKNOWN_VERSION);
    }

    public static ItemStack parse(HolderLookup.Provider registries, CompoundTag stackTag, int declaredVersion) {
        if (stackTag.isEmpty() || !stackTag.contains("id") || EMPTY_ID.equals(stackTag.getStringOr("id", ""))) {
            return ItemStack.EMPTY;
        }

        int from = sourceVersion(stackTag, declaredVersion);
        boolean legacy = stackTag.contains("Count") && !stackTag.contains("count");

        if (legacy && stackTag.getByteOr("Count", (byte) 0) <= 0) {
            return ItemStack.EMPTY;
        }

        CompoundTag current = from < currentDataVersion() ? upgrade(stackTag, from) : stackTag;
        return ItemStack.OPTIONAL_CODEC
                .parse(registries.createSerializationContext(NbtOps.INSTANCE), current)
                .resultOrPartial(message -> Graveyard.LOGGER.warn("Cannot read a stored item stack: {}", message))
                .orElse(ItemStack.EMPTY);
    }

    public static ItemStack read(ValueInput input, String key) {
        return input.read(key, CompoundTag.CODEC)
                .map(tag -> parse(input.lookup(), tag, readDataVersion(input)))
                .orElse(ItemStack.EMPTY);
    }

    public static void write(ValueOutput output, String key, ItemStack stack) {
        output.store(key, ItemStack.OPTIONAL_CODEC, stack);
        writeDataVersion(output);
    }

    public static void loadSlots(ValueInput input, NonNullList<ItemStack> items) {
        int declared = readDataVersion(input);

        for (CompoundTag slotTag : input.listOrEmpty(ContainerHelper.TAG_ITEMS, CompoundTag.CODEC)) {
            int slot = slotTag.getByteOr("Slot", (byte) 0) & 255;

            if (slot < items.size()) {
                items.set(slot, parse(input.lookup(), slotTag, declared));
            }
        }
    }

    public static void saveSlots(ValueOutput output, NonNullList<ItemStack> items) {
        ContainerHelper.saveAllItems(output, items);
        writeDataVersion(output);
    }

    private static int sourceVersion(CompoundTag stackTag, int declaredVersion) {
        if (declaredVersion != UNKNOWN_VERSION) {
            return declaredVersion;
        }

        if (stackTag.contains("Count") && !stackTag.contains("count")) {
            return LEGACY_DATA_VERSION;
        }

        if (stackTag.contains("count") && stackTag.contains("components")) {
            return PORT_1_21_1_DATA_VERSION;
        }

        return currentDataVersion();
    }

    private static CompoundTag upgrade(CompoundTag stackTag, int from) {
        try {
            Dynamic<Tag> fixed = DataFixers.getDataFixer().update(
                    References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, stackTag.copy()),
                    from,
                    currentDataVersion()
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
