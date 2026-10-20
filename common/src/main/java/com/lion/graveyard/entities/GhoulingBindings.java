package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.util.LegacySavedData;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/// Which Ghouling belongs to which staff, and where it was last seen.
///
/// An unloaded Ghouling cannot be queried, so its last position is stored here.
public final class GhoulingBindings extends SavedData {

    private static final String LEGACY_DATA_FILE = "graveyard_ghoulings.dat";

    private static final String LEGACY_FILE = "graveyardGhoulingUUIDmapping.txt";

    private static final Codec<GhoulingBindings> CODEC = CompoundTag.CODEC.xmap(GhoulingBindings::fromNbt, GhoulingBindings::toNbt);

    private static final SavedDataType<GhoulingBindings> TYPE = new SavedDataType<>(
            Graveyard.id("ghoulings"),
            GhoulingBindings::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private final Map<UUID, Binding> bindings = new HashMap<>();

    public static GhoulingBindings get(ServerLevel world) {
        SavedDataStorage storage = world.getServer().getDataStorage();
        GhoulingBindings bindings = storage.get(TYPE);

        if (bindings == null) {
            bindings = importLegacy(world.getServer());
            storage.set(TYPE, bindings);
        }

        return bindings;
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

        this.bindings.put(
                ghouling.getUUID(),
                new Binding(owner, ghouling.level().dimension(), ghouling.blockPosition(), ghouling.isSitting())
        );
        this.setDirty();
    }

    public void remove(UUID ghouling) {
        if (this.bindings.remove(ghouling) != null) {
            this.setDirty();
        }
    }

    private CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        this.bindings.forEach((ghouling, binding) -> {
            CompoundTag entry = new CompoundTag();
            entry.store("ghouling", UUIDUtil.CODEC, ghouling);
            entry.store("owner", UUIDUtil.CODEC, binding.owner());
            entry.putString("dimension", binding.dimension().identifier().toString());

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
        ListTag list = nbt.getListOrEmpty("ghoulings");
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompoundOrEmpty(i);
            UUID ghouling = entry.read("ghouling", UUIDUtil.CODEC).orElse(null);
            UUID owner = entry.read("owner", UUIDUtil.CODEC).orElse(null);

            if (ghouling == null || owner == null) {
                continue;
            }

            Identifier dimension = Identifier.tryParse(entry.getStringOr("dimension", ""));

            if (dimension == null) {
                continue;
            }

            BlockPos pos = entry.contains("x")
                    ? new BlockPos(entry.getIntOr("x", 0), entry.getIntOr("y", 0), entry.getIntOr("z", 0))
                    : null;
            state.bindings.put(
                    ghouling,
                    new Binding(
                            owner,
                            ResourceKey.create(Registries.DIMENSION, dimension),
                            pos,
                            entry.getBooleanOr("sitting", false)
                    )
            );
        }
        return state;
    }

    /// Runs once per world, the first time no state exists yet. The file itself is left in place.
    private static GhoulingBindings importLegacy(MinecraftServer server) {
        CompoundTag legacyData = LegacySavedData.read(server.getWorldPath(LevelResource.DATA), LEGACY_DATA_FILE);

        if (legacyData != null) {
            GhoulingBindings imported = fromNbt(legacyData);
            imported.setDirty();
            return imported;
        }

        GhoulingBindings state = new GhoulingBindings();
        Path file = server.getWorldPath(LevelResource.ROOT).resolve(LEGACY_FILE);

        if (!Files.isRegularFile(file)) {
            return state;
        }

        try {
            for (String line : Files.readAllLines(file)) {
                String[] pair = line.split(":", 2);

                if (pair.length == 2) {
                    state.bindings.put(
                            UUID.fromString(pair[0].trim()),
                            new Binding(UUID.fromString(pair[1].trim()), Level.OVERWORLD, null, false)
                    );
                }
            }
            state.setDirty();
        } catch (IOException | IllegalArgumentException e) {
            Graveyard.LOGGER.warn("Could not import {}: {}", LEGACY_FILE, e.toString());
        }
        return state;
    }
}
