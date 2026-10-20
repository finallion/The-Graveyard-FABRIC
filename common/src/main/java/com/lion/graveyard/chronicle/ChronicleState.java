package com.lion.graveyard.chronicle;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.util.LegacySavedData;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ChronicleState extends SavedData {

    private static final String LEGACY_FILE = "graveyard_chronicle.dat";

    private static final int MAX_PAGES_PER_PLAYER = 512;

    private static final Codec<ChronicleState> CODEC = CompoundTag.CODEC.xmap(ChronicleState::fromNbt, ChronicleState::toNbt);

    private static final SavedDataType<ChronicleState> TYPE = new SavedDataType<>(Graveyard.id("chronicle"), ChronicleState::new, CODEC, DataFixTypes.LEVEL);

    private final Map<UUID, Set<String>> found = new HashMap<>();

    public static ChronicleState get(MinecraftServer server) {
        SavedDataStorage storage = server.getDataStorage();
        ChronicleState state = storage.get(TYPE);

        if (state == null) {
            state = importLegacy(server);
            storage.set(TYPE, state);
        }

        return state;
    }

    private static ChronicleState importLegacy(MinecraftServer server) {
        CompoundTag legacy = LegacySavedData.read(server.getWorldPath(LevelResource.DATA), LEGACY_FILE);

        if (legacy == null) {
            return new ChronicleState();
        }

        ChronicleState state = fromNbt(legacy);
        state.setDirty();
        return state;
    }

    public Set<String> found(UUID player) {
        return Collections.unmodifiableSet(this.found.getOrDefault(player, Collections.emptySet()));
    }

    public boolean add(UUID player, String page) {
        if (!Chronicle.isValidId(page)) {
            return false;
        }

        Set<String> existing = this.found.get(player);

        if (existing != null && existing.size() >= MAX_PAGES_PER_PLAYER && !existing.contains(page)) {
            return false;
        }

        boolean added = this.found.computeIfAbsent(player, id -> new LinkedHashSet<>()).add(page);

        if (added) {
            this.setDirty();
        }

        return added;
    }

    public boolean remove(UUID player, String page) {
        Set<String> pages = this.found.get(player);
        boolean removed = pages != null && pages.remove(page);

        if (removed) {
            this.setDirty();
        }

        return removed;
    }

    public void clear(UUID player) {
        if (this.found.remove(player) != null) {
            this.setDirty();
        }
    }

    private CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        CompoundTag players = new CompoundTag();
        this.found.forEach((id, pages) -> {
            ListTag list = new ListTag();
            pages.forEach(page -> list.add(StringTag.valueOf(page)));
            players.put(id.toString(), list);
        });
        nbt.put("players", players);
        return nbt;
    }

    private static ChronicleState fromNbt(CompoundTag nbt) {
        ChronicleState state = new ChronicleState();
        CompoundTag players = nbt.getCompoundOrEmpty("players");
        for (String key : players.keySet()) {
            UUID player;
            try {
                player = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                Graveyard.LOGGER.warn("Skipping the chronicle entry with the invalid player id {}", key, e);
                continue;
            }
            Set<String> pages = new LinkedHashSet<>();
            for (Tag element : players.getListOrEmpty(key)) {
                String page = element.asString().orElse(null);

                if (element instanceof StringTag && Chronicle.isValidId(page)) {
                    pages.add(page);
                }
            }
            state.found.put(player, pages);
        }
        return state;
    }
}
