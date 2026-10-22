package com.lion.graveyard.compat;

import com.lion.graveyard.support.Floors;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.NbtKeys;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NbtCompatibilityTest {
    private static final List<String> HOLDER_CLASSES = List.of(
            "GravestoneBlockEntity",
            "UrnBlockEntity",
            "SarcophagusBlockEntity",
            "LoreBookBlockEntity",
            "ChronicleState"
    );

    @Test
    void holderClassesStillDeclareAnNbtReader() {
        Set<String> readers = NbtKeys.currentReadLines();
        for (String holder : HOLDER_CLASSES) {
            assertTrue(readers.stream().anyMatch(line -> line.startsWith(holder + ":")), holder + " reads no NBT key any more");
        }
    }

    @TestFactory
    Stream<DynamicTest> everySnapshotKeyIsStillRead() {
        Set<String> current = NbtKeys.currentReadLines();
        return ModProject.snapshotLines("nbt-read-keys.txt").stream()
                .map(line ->
                        DynamicTest.dynamicTest(line, () -> assertTrue(current.contains(line), "NBT key no longer read: " + line)));
    }

    @TestFactory
    Stream<DynamicTest> everyWrittenKeyIsAlsoRead() {
        List<DynamicTest> tests = new ArrayList<>();
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            String code = Sources.code(file);

            if (!NbtKeys.declaresNbtReader(code)) {
                continue;
            }

            String name = file.getFileName().toString().replace(".java", "");
            tests.add(DynamicTest.dynamicTest(name, () -> {
                Set<String> unread = new java.util.TreeSet<>(NbtKeys.writeKeys(code));
                unread.removeAll(NbtKeys.readKeys(code));
                assertTrue(unread.isEmpty(), "Written but never read: " + unread);
            }));
        }
        return Floors.atLeast(15, "files that read NBT", tests.stream());
    }

    @Test
    void extractorResolvesLiteralsConstantsAndInventoryHelpers() {
        String code = "static final String PAGE_KEY = \"page\";\n"
                + "void r(CompoundTag nbt) { nbt.getString(PAGE_KEY); nbt.contains(\"front_text\"); ContainerHelper.loadAllItems(nbt, l); tryLoadLootTable(nbt); }\n"
                + "void w(CompoundTag nbt) { nbt.putString(PAGE_KEY, p); nbt.putBoolean(\"is_waxed\", true); }";
        assertEquals(Set.of("page", "front_text", "Items", "LootTable"), NbtKeys.readKeys(code));
        assertEquals(Set.of("page", "is_waxed"), NbtKeys.writeKeys(code));
    }

    @Test
    void removingAReadKeyIsDetected() {
        Set<String> before = NbtKeys.readKeys("void r(CompoundTag nbt) { nbt.getBoolean(\"is_waxed\"); nbt.getString(\"material\"); }");
        Set<String> after = NbtKeys.readKeys("void r(CompoundTag nbt) { nbt.getBoolean(\"is_waxed\"); }");
        assertFalse(after.containsAll(before));
    }
}
