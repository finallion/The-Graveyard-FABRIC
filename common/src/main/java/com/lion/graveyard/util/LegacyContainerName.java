package com.lion.graveyard.util;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lion.graveyard.Graveyard;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Optional;

/// Vanilla data fixers skip mod block entities.
/// A custom name in the JSON string format needs the chest data fixer.
public final class LegacyContainerName {
    public static final String CUSTOM_NAME_KEY = "CustomName";

    private LegacyContainerName() {}

    public static Optional<Component> read(ValueInput input) {
        Optional<String> stored = input.getString(CUSTOM_NAME_KEY);

        if (stored.isEmpty() || !looksLikeJson(stored.get())) {
            return Optional.empty();
        }

        int storedVersion = LegacyItemStacks.readDataVersion(input);
        int fromVersion = storedVersion == LegacyItemStacks.UNKNOWN_VERSION
                ? LegacyItemStacks.PORT_1_21_1_DATA_VERSION
                : storedVersion;

        if (fromVersion >= LegacyItemStacks.currentDataVersion()) {
            return Optional.empty();
        }

        HolderLookup.Provider registries = input.lookup();
        Optional<Component> fixed = runFixer(registries, stored.get(), fromVersion);

        if (fixed.isPresent()) {
            return fixed;
        }

        return parseJson(registries, stored.get());
    }

    private static boolean looksLikeJson(String text) {
        return text.startsWith("{") || text.startsWith("[") || text.startsWith("\"");
    }

    private static Optional<Component> runFixer(HolderLookup.Provider registries, String json, int fromVersion) {
        try {
            Dynamic<Tag> empty = new Dynamic<>(NbtOps.INSTANCE, new CompoundTag());
            Dynamic<Tag> chest = empty
                    .set("id", empty.createString("minecraft:chest"))
                    .set(CUSTOM_NAME_KEY, empty.createString(json));
            Dynamic<Tag> fixed = DataFixers.getDataFixer()
                    .update(References.BLOCK_ENTITY, chest, fromVersion, LegacyItemStacks.currentDataVersion());

            if (fixed.getValue() instanceof CompoundTag fixedChest && fixedChest.get(CUSTOM_NAME_KEY) != null) {
                return ComponentSerialization.CODEC
                        .parse(registries.createSerializationContext(NbtOps.INSTANCE), fixedChest.get(CUSTOM_NAME_KEY))
                        .resultOrPartial(message -> Graveyard.LOGGER.warn("Cannot read a fixed container name: {}", message));
            }
        } catch (RuntimeException e) {
            Graveyard.LOGGER.warn("Cannot run the data fixer on a container name", e);
        }

        return Optional.empty();
    }

    private static Optional<Component> parseJson(HolderLookup.Provider registries, String json) {
        try {
            return ComponentSerialization.CODEC
                    .parse(registries.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(json))
                    .result();
        } catch (JsonParseException e) {
            return Optional.empty();
        }
    }
}
