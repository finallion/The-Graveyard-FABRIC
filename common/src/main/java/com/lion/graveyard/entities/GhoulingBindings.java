package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/// Which Ghouling belongs to which staff, and where it was last seen.
///
/// A Ghouling that sits in an unloaded chunk cannot be asked anything, so the last known position
/// lives here instead; the staff uses it to fetch the Ghouling back. Hangs off the overworld so the
/// record survives dimension changes and restarts alike.
public final class GhoulingBindings extends SavedData {

    private static final String KEY = "graveyard_ghoulings";

    /// Older versions kept the bond as `ghoulingUUID:ownerUUID` lines in this file at the save root.
    private static final String LEGACY_FILE = "graveyardGhoulingUUIDmapping.txt";

    private final Map<UUID, Binding> bindings = new HashMap<>();

    public static GhoulingBindings get(ServerLevel world) {
        MinecraftServer server = world.getServer();
        return server.overworld().getDataStorage()
                .computeIfAbsent(GhoulingBindings::fromNbt, () -> importLegacy(server), KEY);
    }

    /// `pos` is unknown for bonds imported from the legacy file until the Ghouling ticks once.
    public record Binding(UUID owner, ResourceKey<Level> dimension, @Nullable BlockPos pos, boolean sitting) {
    }

    @Nullable
    public Binding get(UUID ghouling) {
        return this.bindings.get(ghouling);
    }

    public void update(GhoulingEntity ghouling) {
        UUID owner = ghouling.getOwnerUuid();

        if (owner == null) {
            return;
        }

        this.bindings.put(ghouling.getUUID(), new Binding(owner, ghouling.level().dimension(),
                ghouling.blockPosition(), ghouling.isSitting()));
        this.setDirty();
    }

    public void remove(UUID ghouling) {
        if (this.bindings.remove(ghouling) != null) {
            this.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        this.bindings.forEach((ghouling, binding) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("ghouling", ghouling);
            entry.putUUID("owner", binding.owner());
            entry.putString("dimension", binding.dimension().location().toString());

            if (binding.pos() != null) {
                entry.putInt("x", binding.pos().getX());
                entry.putInt("y", binding.pos().getY());
                entry.putInt("z", binding.pos().getZ());
            }

            entry.putBoolean("sitting", binding.sitting());
            list.add(entry);
        });
        nbt.put("ghoulings", list);
        return nbt;
    }

    private static GhoulingBindings fromNbt(CompoundTag nbt) {
        GhoulingBindings state = new GhoulingBindings();
        ListTag list = nbt.getList("ghoulings", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);

            if (!entry.hasUUID("ghouling") || !entry.hasUUID("owner")) {
                continue;
            }

            ResourceLocation dimension = ResourceLocation.tryParse(entry.getString("dimension"));

            if (dimension == null) {
                continue;
            }

            BlockPos pos = entry.contains("x") ? new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z")) : null;
            state.bindings.put(entry.getUUID("ghouling"), new Binding(entry.getUUID("owner"),
                    ResourceKey.create(Registries.DIMENSION, dimension), pos, entry.getBoolean("sitting")));
        }
        return state;
    }

    /// Runs once per world, the first time no state exists yet. The file itself is left in place.
    private static GhoulingBindings importLegacy(MinecraftServer server) {
        GhoulingBindings state = new GhoulingBindings();
        Path file = server.getWorldPath(LevelResource.ROOT).resolve(LEGACY_FILE);

        if (!Files.isRegularFile(file)) {
            return state;
        }

        try {
            for (String line : Files.readAllLines(file)) {
                String[] pair = line.split(":", 2);

                if (pair.length == 2) {
                    state.bindings.put(UUID.fromString(pair[0].trim()),
                            new Binding(UUID.fromString(pair[1].trim()), Level.OVERWORLD, null, false));
                }
            }
            state.setDirty();
        } catch (IOException | IllegalArgumentException e) {
            Graveyard.LOGGER.warn("Could not import {}: {}", LEGACY_FILE, e.toString());
        }
        return state;
    }
}
