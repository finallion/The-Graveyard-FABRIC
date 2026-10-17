package com.lion.graveyard.chronicle;

import com.lion.graveyard.Graveyard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// Saves the found chronicle pages of each player on the overworld.
public final class ChronicleState extends SavedData {

    private static final String KEY = "graveyard_chronicle";

    private static final int MAX_PAGES_PER_PLAYER = 512;

    private final Map<UUID, Set<String>> found = new HashMap<>();

    public static ChronicleState get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(ChronicleState::fromNbt, ChronicleState::new, KEY);
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

    @Override
    public CompoundTag save(CompoundTag nbt) {
        CompoundTag players = new CompoundTag();
        this.found.forEach((id, pages) -> {
            ListTag list = new ListTag();
            pages.forEach(page -> list.add(StringTag.valueOf(page)));
            players.put(id.toString(), list);
        });
        nbt.put("players", players);
        return nbt;
    }

    public static ChronicleState fromNbt(CompoundTag nbt) {
        ChronicleState state = new ChronicleState();
        CompoundTag players = nbt.getCompound("players");
        for (String key : players.getAllKeys()) {
            UUID player;
            try {
                player = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                Graveyard.LOGGER.warn("Skipping the chronicle entry with the invalid player id {}", key, e);
                continue;
            }
            Set<String> pages = new LinkedHashSet<>();
            for (Tag element : players.getList(key, Tag.TAG_STRING)) {
                if (Chronicle.isValidId(element.getAsString())) {
                    pages.add(element.getAsString());
                }
            }
            state.found.put(player, pages);
        }
        return state;
    }
}
