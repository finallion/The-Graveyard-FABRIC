package com.lion.graveyard.entities;

import com.lion.graveyard.util.LegacyItemStacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/// Vanilla data fixers skip Graveyard entities.
/// This class reads the pre 1.20.5 item stack and lead shapes.
public final class LegacyEntityData {
    private static final List<EquipmentSlot> HAND_SLOTS = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    );

    private LegacyEntityData() {}

    public static void read(Mob mob, CompoundTag nbt) {
        readEquipment(mob, nbt, "HandItems", HAND_SLOTS);
        readEquipment(mob, nbt, "ArmorItems", ARMOR_SLOTS);
        readLeash(mob, nbt);
    }

    private static void readEquipment(Mob mob, CompoundTag nbt, String key, List<EquipmentSlot> slots) {
        ListTag stacks = nbt.getList(key, Tag.TAG_COMPOUND);

        for (int i = 0; i < stacks.size() && i < slots.size(); i++) {
            CompoundTag stackTag = stacks.getCompound(i);

            if (!stackTag.contains("Count") || stackTag.contains("count")) {
                continue;
            }

            ItemStack stack = LegacyItemStacks.parse(mob.registryAccess(), stackTag);

            if (!stack.isEmpty()) {
                mob.setItemSlot(slots.get(i), stack);
            }
        }
    }

    private static void readLeash(Mob mob, CompoundTag nbt) {
        if (mob.getLeashData() != null || nbt.contains("leash") || !nbt.contains("Leash", Tag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag legacy = nbt.getCompound("Leash");
        CompoundTag converted = new CompoundTag();

        if (legacy.hasUUID("UUID")) {
            converted.put("leash", legacy.copy());
        } else if (legacy.contains("X") && legacy.contains("Y") && legacy.contains("Z")) {
            int[] pos = {
                    legacy.getInt("X"),
                    legacy.getInt("Y"),
                    legacy.getInt("Z")
            };
            converted.put("leash", new IntArrayTag(pos));
        } else {
            return;
        }

        mob.setLeashData(mob.readLeashData(converted));
    }
}
