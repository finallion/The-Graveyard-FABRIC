package com.lion.graveyard.compat;

import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDataCompatibilityTest {
    private static final Path LEGACY_ITEM_DATA = ModProject.JAVA.resolve("item/LegacyItemData.java");

    @Test
    void snapshotIsNotEmpty() {
        assertFalse(ModProject.snapshotLines("item-data-keys.txt").isEmpty());
    }

    @TestFactory
    Stream<DynamicTest> everyItemDataKeyIsStillHandled() throws IOException {
        String code = Files.readString(LEGACY_ITEM_DATA);
        return ModProject.snapshotLines("item-data-keys.txt").stream()
                .map(line -> {
                    String key = line.substring(line.indexOf(':') + 1);
                    return DynamicTest.dynamicTest(
                            line,
                            () ->
                                    assertTrue(code.contains("\"" + key + "\""), "Item data key no longer handled: " + line)
                    );
                });
    }
}
