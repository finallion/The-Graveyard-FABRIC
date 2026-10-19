package com.lion.graveyard.item;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public final class LegacyItemData {
    public static final String BLOOD = "Blood";
    public static final String PAGE = "page";
    public static final String GHOULING_UUID = "GhoulingUUID";
    public static final String OWNER_UUID = "OwnerUUID";
    public static final String BOND_ID = "BondId";
    public static final String FETCH_CALL = "FetchCall";
    public static final String FETCH_MISSES = "FetchMisses";
    public static final String STORED_GHOULING = "StoredGhouling";

    private static final CompoundTag NONE = new CompoundTag();

    private LegacyItemData() {}

    public static boolean hasData(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    /// Returns a copy of the custom data. Write changes with edit.
    public static CompoundTag view(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? NONE : data.copyTag();
    }

    public static void edit(ItemStack stack, Consumer<CompoundTag> change) {
        CompoundTag before = view(stack);
        CompoundTag after = before.copy();
        change.accept(after);

        if (after.equals(before)) {
            return;
        }

        if (after.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(after));
        }
    }

    @Nullable
    public static UUID uuid(ItemStack stack, String key) {
        CompoundTag data = view(stack);
        return data.read(key, UUIDUtil.CODEC).orElse(null);
    }

    @Nullable
    public static UUID ownerUuid(ItemStack stack) {
        return uuid(stack, OWNER_UUID);
    }

    @Nullable
    public static UUID ghoulingUuid(ItemStack stack) {
        return uuid(stack, GHOULING_UUID);
    }

    @Nullable
    public static UUID bondId(ItemStack stack) {
        return uuid(stack, BOND_ID);
    }

    public static int fetchMisses(ItemStack stack) {
        return view(stack).getIntOr(FETCH_MISSES, 0);
    }

    public static void putUuid(CompoundTag data, String key, UUID value) {
        data.store(key, UUIDUtil.CODEC, value);
    }

    public static boolean isStored(ItemStack stack) {
        return view(stack).contains(STORED_GHOULING);
    }

    public static void store(ItemStack stack, CompoundTag entity) {
        edit(stack, data -> data.put(STORED_GHOULING, entity));
    }

    public static void clearStored(ItemStack stack) {
        edit(stack, data -> data.remove(STORED_GHOULING));
    }

    public static void clearBond(ItemStack stack) {
        edit(stack, data -> {
            data.remove(OWNER_UUID);
            data.remove(GHOULING_UUID);
            data.remove(BOND_ID);
            data.remove(FETCH_CALL);
        });
    }

    public static void clearFetchCall(ItemStack stack) {
        edit(stack, data -> data.remove(FETCH_CALL));
    }

    public static void clearFetchMisses(ItemStack stack) {
        edit(stack, data -> data.remove(FETCH_MISSES));
    }
}
