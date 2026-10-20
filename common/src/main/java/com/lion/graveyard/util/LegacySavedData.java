package com.lion.graveyard.util;

import com.lion.graveyard.Graveyard;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LegacySavedData {

    private static final Logger LOGGER = LoggerFactory.getLogger(Graveyard.MOD_ID);

    private LegacySavedData() {}

    @Nullable
    public static CompoundTag read(Path dataFolder, String fileName) {
        Path file = dataFolder.resolve(fileName);

        if (!Files.isRegularFile(file)) {
            return null;
        }

        try {
            return NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()).getCompoundOrEmpty("data");
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Could not import {}: {}", fileName, e.toString());
            return null;
        }
    }
}
