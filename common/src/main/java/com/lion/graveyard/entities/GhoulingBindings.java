package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
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
public final class GhoulingBindings extends PersistentState {

    private static final String KEY = "graveyard_ghoulings";

    /// Older versions kept the bond as `ghoulingUUID:ownerUUID` lines in this file at the save root.
    private static final String LEGACY_FILE = "graveyardGhoulingUUIDmapping.txt";

    private final Map<UUID, Binding> bindings = new HashMap<>();

    public static GhoulingBindings get(ServerWorld world) {
        MinecraftServer server = world.getServer();
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(GhoulingBindings::fromNbt, () -> importLegacy(server), KEY);
    }

    /// `pos` is unknown for bonds imported from the legacy file until the Ghouling ticks once.
    public record Binding(UUID owner, RegistryKey<World> dimension, @Nullable BlockPos pos, boolean sitting) {
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

        this.bindings.put(ghouling.getUuid(), new Binding(owner, ghouling.getWorld().getRegistryKey(),
                ghouling.getBlockPos(), ghouling.isSitting()));
        this.markDirty();
    }

    public void remove(UUID ghouling) {
        if (this.bindings.remove(ghouling) != null) {
            this.markDirty();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        this.bindings.forEach((ghouling, binding) -> {
            NbtCompound entry = new NbtCompound();
            entry.putUuid("ghouling", ghouling);
            entry.putUuid("owner", binding.owner());
            entry.putString("dimension", binding.dimension().getValue().toString());

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

    private static GhoulingBindings fromNbt(NbtCompound nbt) {
        GhoulingBindings state = new GhoulingBindings();
        NbtList list = nbt.getList("ghoulings", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);

            if (!entry.containsUuid("ghouling") || !entry.containsUuid("owner")) {
                continue;
            }

            Identifier dimension = Identifier.tryParse(entry.getString("dimension"));

            if (dimension == null) {
                continue;
            }

            BlockPos pos = entry.contains("x") ? new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z")) : null;
            state.bindings.put(entry.getUuid("ghouling"), new Binding(entry.getUuid("owner"),
                    RegistryKey.of(RegistryKeys.WORLD, dimension), pos, entry.getBoolean("sitting")));
        }
        return state;
    }

    /// Runs once per world, the first time no state exists yet. The file itself is left in place.
    private static GhoulingBindings importLegacy(MinecraftServer server) {
        GhoulingBindings state = new GhoulingBindings();
        Path file = server.getSavePath(WorldSavePath.ROOT).resolve(LEGACY_FILE);

        if (!Files.isRegularFile(file)) {
            return state;
        }

        try {
            for (String line : Files.readAllLines(file)) {
                String[] pair = line.split(":", 2);

                if (pair.length == 2) {
                    state.bindings.put(UUID.fromString(pair[0].trim()),
                            new Binding(UUID.fromString(pair[1].trim()), World.OVERWORLD, null, false));
                }
            }
            state.markDirty();
        } catch (IOException | IllegalArgumentException e) {
            Graveyard.LOGGER.warn("Could not import {}: {}", LEGACY_FILE, e.toString());
        }
        return state;
    }
}
