package com.lion.graveyard.entities;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lion.graveyard.util.LegacyItemStacks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/// Vanilla data fixers skip Graveyard entities.
public final class LegacyEntityData {
    private static final List<EquipmentSlot> HAND_SLOTS = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    );

    private LegacyEntityData() {}

    public static void write(ValueOutput output) {
        LegacyItemStacks.writeDataVersion(output);
    }

    public static void read(Mob mob, ValueInput input) {
        if (LegacyItemStacks.readDataVersion(input) != LegacyItemStacks.UNKNOWN_VERSION) {
            return;
        }

        readCustomName(mob, input);
        readEquipment(mob, input);
        readDropChances(mob, input, "HandDropChances", HAND_SLOTS);
        readDropChances(mob, input, "ArmorDropChances", ARMOR_SLOTS);
        mob.fallDistance = input.getFloatOr("FallDistance", 0.0F);
        mob.setLeftHanded(input.getBooleanOr("LeftHanded", false));
        readLeash(mob, input);
    }

    public static <T extends Mob & NeutralMob> void readLegacyAngerTarget(T mob, Level level, ValueInput input) {
        if (mob.getPersistentAngerTarget() != null) {
            return;
        }

        input.read("AngryAt", UUIDUtil.CODEC).ifPresent(id -> {
            EntityReference<LivingEntity> reference = EntityReference.of(id);

            mob.setPersistentAngerTarget(reference);

            if (level instanceof ServerLevel) {
                mob.setTarget(EntityReference.getLivingEntity(reference, level));
            }
        });
    }

    @Nullable
    public static Component storedCustomName(HolderLookup.Provider registries, CompoundTag entity) {
        Tag name = entity.get("CustomName");

        if (name == null) {
            return null;
        }

        if (name instanceof StringTag && !entity.contains(LegacyItemStacks.DATA_VERSION_KEY)) {
            return parseJson(registries, name.asString().orElse(""));
        }

        return ComponentSerialization.CODEC
                .parse(registries.createSerializationContext(NbtOps.INSTANCE), name)
                .result()
                .orElse(null);
    }

    @Nullable
    private static Component parseJson(HolderLookup.Provider registries, String json) {
        try {
            return ComponentSerialization.CODEC
                    .parse(registries.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json))
                    .result()
                    .orElse(null);
        } catch (JsonParseException e) {
            return null;
        }
    }

    private static void readLeash(Mob mob, ValueInput input) {
        if (mob.getLeashData() != null || input.read("leash", ExtraCodecs.NBT).isPresent()) {
            return;
        }

        Tag converted = input.read("Leash", ExtraCodecs.NBT).map(LegacyEntityData::convertLeash).orElse(null);

        if (converted == null) {
            return;
        }

        Leashable.LeashData.CODEC
                .parse(input.lookup().createSerializationContext(NbtOps.INSTANCE), converted)
                .result()
                .ifPresent(mob::setLeashData);
    }

    @Nullable
    private static Tag convertLeash(Tag leash) {
        if (!(leash instanceof CompoundTag compound)) {
            return null;
        }

        if (compound.contains("UUID")) {
            return compound;
        }

        if (compound.contains("X") && compound.contains("Y") && compound.contains("Z")) {
            return new IntArrayTag(new int[] {
                    compound.getIntOr("X", 0),
                    compound.getIntOr("Y", 0),
                    compound.getIntOr("Z", 0)
            });
        }

        return null;
    }

    private static void readCustomName(Mob mob, ValueInput input) {
        Tag stored = input.read("CustomName", ExtraCodecs.NBT).orElse(null);

        if (!(stored instanceof StringTag)) {
            return;
        }

        String text = stored.asString().orElse("");
        Component parsed = parseJson(input.lookup(), text);
        mob.setCustomName(parsed != null ? parsed : Component.literal(text));
    }

    private static void readEquipment(Mob mob, ValueInput input) {
        if (input.child("equipment").isPresent()) {
            return;
        }

        int declared = LegacyItemStacks.readDataVersion(input);
        applyStacks(mob, input, declared, "HandItems", HAND_SLOTS);
        applyStacks(mob, input, declared, "ArmorItems", ARMOR_SLOTS);
    }

    private static void applyStacks(Mob mob, ValueInput input, int declared, String key, List<EquipmentSlot> slots) {
        int index = 0;

        for (CompoundTag stackTag : input.listOrEmpty(key, CompoundTag.CODEC)) {
            if (index >= slots.size()) {
                return;
            }

            ItemStack stack = LegacyItemStacks.parse(input.lookup(), stackTag, declared);

            if (!stack.isEmpty()) {
                mob.setItemSlot(slots.get(index), stack);
            }

            index++;
        }
    }

    private static void readDropChances(Mob mob, ValueInput input, String key, List<EquipmentSlot> slots) {
        int index = 0;

        for (float chance : input.listOrEmpty(key, Codec.FLOAT)) {
            if (index >= slots.size()) {
                return;
            }

            mob.setDropChance(slots.get(index), chance);
            index++;
        }
    }
}
