package com.lion.graveyard.entities;

import com.lion.graveyard.util.LegacyItemStacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

/// Vanilla data fixers skip Graveyard entities.
/// Reads the old NBT shapes of items, leads, attributes and effects.
public final class LegacyEntityData {
    private static final List<EquipmentSlot> HAND_SLOTS = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    );

    private static final Map<UUID, String> MODIFIER_IDS_BY_UUID = Map.ofEntries(
            modifier("736565d2-e1a7-403d-a3f8-1aeb3e302542", "creative_mode_block_range"),
            modifier("98491ef6-97b1-4584-ae82-71a8cc85cf73", "creative_mode_entity_range"),
            modifier("91AEAA56-376B-4498-935B-2F7F68070635", "effect.speed"),
            modifier("7107DE5E-7CE8-4030-940E-514C1F160890", "effect.slowness"),
            modifier("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", "effect.haste"),
            modifier("55FCED67-E92A-486E-9800-B47F202C4386", "effect.mining_fatigue"),
            modifier("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", "effect.strength"),
            modifier("C0105BF3-AEF8-46B0-9EBC-92943757CCBE", "effect.jump_boost"),
            modifier("22653B89-116E-49DC-9B6B-9971489B5BE5", "effect.weakness"),
            modifier("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", "effect.health_boost"),
            modifier("EAE29CF0-701E-4ED6-883A-96F798F3DAB5", "effect.absorption"),
            modifier("03C3C89D-7037-4B42-869F-B146BCB64D2E", "effect.luck"),
            modifier("CC5AF142-2BD2-4215-B636-2605AED11727", "effect.unluck"),
            modifier("6555be74-63b3-41f1-a245-77833b3c2562", "evil"),
            modifier("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce", "powder_snow"),
            modifier("662A6B8D-DA3E-4C1C-8813-96EA6097278D", "sprinting"),
            modifier("020E0DFB-87AE-4653-9556-831010E291A0", "attacking"),
            modifier("766bfa64-11f3-11ea-8d71-362b9e155667", "baby"),
            modifier("7E0292F2-9434-48D5-A29F-9583AF7DF27F", "covered"),
            modifier("9e362924-01de-4ddd-a2b2-d0f7a405a174", "suffocating"),
            modifier("5CD17E52-A79A-43D3-A529-90FDE04B181E", "drinking"),
            modifier("B9766B59-9566-4402-BC1F-2EE2A276D836", "baby"),
            modifier("49455A49-7EC5-45BA-B886-3B90B23A1718", "attacking"),
            modifier("845DB27C-C624-495F-8C9F-6020A9A58B6B", "armor.boots"),
            modifier("D8499B04-0E66-4726-AB29-64469D734E0D", "armor.leggings"),
            modifier("9F3D476D-C118-4544-8365-64846904B48E", "armor.chestplate"),
            modifier("2AD3F246-FEE1-4E67-B886-69FD380BB150", "armor.helmet"),
            modifier("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2", "armor.body"),
            modifier("b572ecd2-ac0c-4071-abde-9594af072a37", "enchantment.fire_protection"),
            modifier("40a9968f-5c66-4e2f-b7f4-2ec2f4b3e450", "enchantment.blast_protection"),
            modifier("07a65791-f64d-4e79-86c7-f83932f007ec", "enchantment.respiration"),
            modifier("60b1b7db-fffd-4ad0-817c-d6c6a93d8a45", "enchantment.aqua_affinity"),
            modifier("11dc269a-4476-46c0-aff3-9e17d7eb6801", "enchantment.depth_strider"),
            modifier("87f46a96-686f-4796-b035-22e16ee9e038", "enchantment.soul_speed"),
            modifier("b9716dbd-50df-4080-850e-70347d24e687", "enchantment.soul_speed"),
            modifier("92437d00-c3a7-4f2e-8f6c-1f21585d5dd0", "enchantment.swift_sneak"),
            modifier("5d3d087b-debe-4037-b53e-d84f3ff51f17", "enchantment.sweeping_edge"),
            modifier("3ceb37c0-db62-46b5-bd02-785457b01d96", "enchantment.efficiency"),
            modifier("CB3F55D3-645C-4F38-A497-9C13A33DB5CF", "base_attack_damage"),
            modifier("FA233E1C-4180-4865-B01B-BCCE9785ACA3", "base_attack_speed")
    );
    private static final Map<String, String> MODIFIER_IDS_BY_NAME = Map.of(
            "Random spawn bonus", "minecraft:random_spawn_bonus",
            "Random zombie-spawn bonus", "minecraft:zombie_random_spawn_bonus",
            "Leader zombie bonus", "minecraft:leader_zombie_bonus",
            "Zombie reinforcement callee charge", "minecraft:reinforcement_callee_charge",
            "Zombie reinforcement caller charge", "minecraft:reinforcement_caller_charge"
    );
    private static final List<String> OPERATIONS = List.of("add_value", "add_multiplied_base", "add_multiplied_total");
    private static final List<String> EFFECT_IDS = List.of(
            "speed",
            "slowness",
            "haste",
            "mining_fatigue",
            "strength",
            "instant_health",
            "instant_damage",
            "jump_boost",
            "nausea",
            "regeneration",
            "resistance",
            "fire_resistance",
            "water_breathing",
            "invisibility",
            "blindness",
            "night_vision",
            "hunger",
            "weakness",
            "poison",
            "wither",
            "health_boost",
            "absorption",
            "saturation",
            "glowing",
            "levitation",
            "luck",
            "unluck",
            "slow_falling",
            "conduit_power",
            "dolphins_grace",
            "bad_omen",
            "hero_of_the_village",
            "darkness"
    );
    private static final Map<String, String> EFFECT_RENAMES = Map.of(
            "Amplifier", "amplifier",
            "Duration", "duration",
            "Ambient", "ambient",
            "ShowParticles", "show_particles",
            "ShowIcon", "show_icon"
    );

    private LegacyEntityData() {}

    public static void read(Mob mob, CompoundTag nbt) {
        readEquipment(mob, nbt, "HandItems", HAND_SLOTS);
        readEquipment(mob, nbt, "ArmorItems", ARMOR_SLOTS);
        readLeash(mob, nbt);
        boolean attributesApplied = readAttributes(mob, nbt);
        readEffects(mob, nbt);

        if (attributesApplied && nbt.contains("Health", Tag.TAG_ANY_NUMERIC)) {
            mob.setHealth(nbt.getFloat("Health"));
        }
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

    private static Map.Entry<UUID, String> modifier(String uuid, String id) {
        return Map.entry(UUID.fromString(uuid), "minecraft:" + id);
    }

    private static boolean readAttributes(Mob mob, CompoundTag nbt) {
        if (mob.level().isClientSide || nbt.contains("attributes") || !nbt.contains("Attributes", Tag.TAG_LIST)) {
            return false;
        }

        mob.getAttributes().load(convertAttributes(nbt.getList("Attributes", Tag.TAG_COMPOUND)));
        return true;
    }

    public static ListTag convertAttributes(ListTag legacy) {
        ListTag converted = new ListTag();

        for (int i = 0; i < legacy.size(); i++) {
            CompoundTag attribute = legacy.getCompound(i);
            CompoundTag result = new CompoundTag();
            result.putString("id", attributeId(attribute.getString("Name")));

            if (attribute.contains("Base")) {
                result.putDouble("base", attribute.getDouble("Base"));
            }

            if (attribute.contains("Modifiers", Tag.TAG_LIST)) {
                result.put("modifiers", convertModifiers(attribute.getList("Modifiers", Tag.TAG_COMPOUND)));
            }

            converted.add(result);
        }

        return converted;
    }

    private static String attributeId(String name) {
        return "minecraft:horse.jump_strength".equals(ensureNamespaced(name)) ? "minecraft:generic.jump_strength" : name;
    }

    private static String ensureNamespaced(String id) {
        return id.indexOf(':') < 0 ? "minecraft:" + id : id;
    }

    private static ListTag convertModifiers(ListTag legacy) {
        Map<String, CompoundTag> modifiersById = new LinkedHashMap<>();

        for (int i = 0; i < legacy.size(); i++) {
            CompoundTag modifier = legacy.getCompound(i);
            UUID uuid = readUuid(modifier.getIntArray("UUID"));
            String idByUuid = uuid == null ? null : MODIFIER_IDS_BY_UUID.get(uuid);
            String idByName = MODIFIER_IDS_BY_NAME.get(modifier.getString("Name"));
            CompoundTag converted = new CompoundTag();
            converted.putDouble("amount", modifier.getDouble("Amount"));
            converted.putString("operation", operation(modifier.getInt("Operation")));

            if (idByUuid != null) {
                converted.putString("id", idByUuid);
                modifiersById.put(idByUuid, converted);
            } else if (idByName != null) {
                CompoundTag existing = modifiersById.get(idByName);

                if (existing == null) {
                    converted.putString("id", idByName);
                    modifiersById.put(idByName, converted);
                } else {
                    existing.putDouble("amount", existing.getDouble("amount") + converted.getDouble("amount"));
                }
            } else {
                String id = "minecraft:" + (uuid == null ? "unknown" : uuid.toString().toLowerCase(Locale.ROOT));
                converted.putString("id", id);
                modifiersById.put(id, converted);
            }
        }

        ListTag result = new ListTag();
        result.addAll(modifiersById.values());
        return result;
    }

    private static String operation(int legacy) {
        return legacy >= 0 && legacy < OPERATIONS.size() ? OPERATIONS.get(legacy) : "invalid";
    }

    @Nullable
    private static UUID readUuid(int[] parts) {
        if (parts.length != 4) {
            return null;
        }

        long most = ((long) parts[0] << 32) | (parts[1] & 0xFFFFFFFFL);
        long least = ((long) parts[2] << 32) | (parts[3] & 0xFFFFFFFFL);
        return new UUID(most, least);
    }

    private static void readEffects(Mob mob, CompoundTag nbt) {
        if (nbt.contains("active_effects") || !nbt.contains("ActiveEffects", Tag.TAG_LIST)) {
            return;
        }

        ListTag legacy = nbt.getList("ActiveEffects", Tag.TAG_COMPOUND);

        for (int i = 0; i < legacy.size(); i++) {
            CompoundTag converted = convertEffect(legacy.getCompound(i));
            MobEffectInstance effect = converted == null ? null : MobEffectInstance.load(converted);

            if (effect != null) {
                mob.getActiveEffectsMap().put(effect.getEffect(), effect);
            }
        }
    }

    @Nullable
    public static CompoundTag convertEffect(CompoundTag legacy) {
        if (!legacy.contains("Id", Tag.TAG_ANY_NUMERIC)) {
            return null;
        }

        int index = legacy.getInt("Id") - 1;

        if (index < 0 || index >= EFFECT_IDS.size()) {
            return null;
        }

        CompoundTag converted = new CompoundTag();
        converted.putString("id", "minecraft:" + EFFECT_IDS.get(index));

        EFFECT_RENAMES.forEach((legacyKey, key) -> {
            Tag value = legacy.get(legacyKey);

            if (value != null) {
                converted.put(key, value.copy());
            }
        });

        if (legacy.contains("HiddenEffect", Tag.TAG_COMPOUND)) {
            CompoundTag hidden = convertEffect(legacy.getCompound("HiddenEffect"));

            if (hidden != null) {
                converted.put("hidden_effect", hidden);
            }
        }

        return converted;
    }
}
