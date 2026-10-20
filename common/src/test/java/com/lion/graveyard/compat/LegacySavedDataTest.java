package com.lion.graveyard.compat;

import com.lion.graveyard.util.LegacySavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacySavedDataTest {
    private static final String CHRONICLE_FILE = "graveyard_chronicle.dat";
    private static final String GHOULINGS_FILE = "graveyard_ghoulings.dat";

    @TempDir
    Path dataFolder;

    private void writeFlatFile(String name, CompoundTag data) throws IOException {
        CompoundTag root = new CompoundTag();
        root.put("data", data);
        root.putInt("DataVersion", 4325);
        NbtIo.writeCompressed(root, this.dataFolder.resolve(name));
    }

    @Test
    void readsTheDataTagOfTheOldChronicleFile() throws IOException {
        CompoundTag players = new CompoundTag();
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf("page_one"));
        players.put("00000000-0000-0000-0000-000000000001", pages);
        CompoundTag data = new CompoundTag();
        data.put("players", players);
        writeFlatFile(CHRONICLE_FILE, data);

        CompoundTag read = LegacySavedData.read(this.dataFolder, CHRONICLE_FILE);

        assertNotNull(read);
        assertEquals(1, read.getCompoundOrEmpty("players").getListOrEmpty("00000000-0000-0000-0000-000000000001").size());
    }

    @Test
    void readsTheDataTagOfTheOldGhoulingFile() throws IOException {
        CompoundTag data = new CompoundTag();
        data.put("ghoulings", new ListTag());
        writeFlatFile(GHOULINGS_FILE, data);

        CompoundTag read = LegacySavedData.read(this.dataFolder, GHOULINGS_FILE);

        assertNotNull(read);
        assertTrue(read.contains("ghoulings"));
    }

    @Test
    void leavesTheOldFileInPlace() throws IOException {
        writeFlatFile(CHRONICLE_FILE, new CompoundTag());

        LegacySavedData.read(this.dataFolder, CHRONICLE_FILE);

        assertTrue(Files.exists(this.dataFolder.resolve(CHRONICLE_FILE)));
    }

    @Test
    void returnsNullWithoutAFile() {
        assertNull(LegacySavedData.read(this.dataFolder, CHRONICLE_FILE));
    }

    @Test
    void returnsNullForACutShortFile() throws IOException {
        writeFlatFile(CHRONICLE_FILE, new CompoundTag());
        Path file = this.dataFolder.resolve(CHRONICLE_FILE);
        byte[] bytes = Files.readAllBytes(file);
        Files.write(file, Arrays.copyOf(bytes, bytes.length / 2));

        assertNull(LegacySavedData.read(this.dataFolder, CHRONICLE_FILE));
    }

    @Test
    void returnsNullForAFileThatIsNotNbt() throws IOException {
        Files.writeString(this.dataFolder.resolve(CHRONICLE_FILE), "not nbt");

        assertNull(LegacySavedData.read(this.dataFolder, CHRONICLE_FILE));
    }
}
