package com.lion.graveyard.chronicle;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/// Which chronicle pages each player has found. Lives on the overworld so it follows the player
/// across dimensions; a page is a string id so datapacks can add chapters without touching Java.
public final class ChronicleState extends PersistentState {

    private static final String KEY = "graveyard_chronicle";

    private final Map<UUID, Set<String>> found = new HashMap<>();

    public static ChronicleState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(ChronicleState::fromNbt, ChronicleState::new, KEY);
    }

    public Set<String> found(UUID player) {
        return Collections.unmodifiableSet(this.found.getOrDefault(player, Collections.emptySet()));
    }

    /// Returns true only the first time a page is found, so callers can toast on it.
    public boolean add(UUID player, String page) {
        boolean added = this.found.computeIfAbsent(player, id -> new LinkedHashSet<>()).add(page);
        if (added) this.markDirty();
        return added;
    }

    public boolean remove(UUID player, String page) {
        Set<String> pages = this.found.get(player);
        boolean removed = pages != null && pages.remove(page);
        if (removed) this.markDirty();
        return removed;
    }

    public void clear(UUID player) {
        if (this.found.remove(player) != null) this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound players = new NbtCompound();
        this.found.forEach((id, pages) -> {
            NbtList list = new NbtList();
            pages.forEach(page -> list.add(NbtString.of(page)));
            players.put(id.toString(), list);
        });
        nbt.put("players", players);
        return nbt;
    }

    public static ChronicleState fromNbt(NbtCompound nbt) {
        ChronicleState state = new ChronicleState();
        NbtCompound players = nbt.getCompound("players");
        for (String key : players.getKeys()) {
            Set<String> pages = new LinkedHashSet<>();
            for (NbtElement element : players.getList(key, NbtElement.STRING_TYPE)) {
                pages.add(element.asString());
            }
            state.found.put(UUID.fromString(key), pages);
        }
        return state;
    }
}
