package com.lion.graveyard.assets;

import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceJsonTest {

    static List<Path> allJsonFiles() {
        List<Path> files = new ArrayList<>();
        for (Path root : List.of(ModProject.RESOURCES, ModProject.FABRIC_MAIN.resolve("resources"), ModProject.NEOFORGE_MAIN.resolve("resources"))) {
            files.addAll(ModProject.files(root, ".json"));
            files.addAll(ModProject.files(root, ".mcmeta"));
        }
        return files;
    }

    @Test
    void jsonFilesWereFound() {
        assertTrue(allJsonFiles().size() > 500,
                "Expected the full resource tree, found " + allJsonFiles().size());
    }

    @TestFactory
    Stream<DynamicTest> everyJsonFileParsesStrictly() {
        return allJsonFiles().stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.display(file), () -> {
                    String text = ModProject.read(file);
                    assertFalse(text.startsWith("﻿"), "File starts with a byte order mark");
                    Json.parseStrict(text);
                }));
    }

    @TestFactory
    Stream<DynamicTest> noJsonFileRepeatsAnObjectKey() {
        return allJsonFiles().stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.display(file), () -> {
                    List<String> duplicates = Json.duplicateKeys(ModProject.read(file));
                    assertTrue(duplicates.isEmpty(), () -> String.join("\n", duplicates));
                }));
    }

    @Test
    void strictParserRejectsTrailingComma() {
        assertThrows(RuntimeException.class, () -> Json.parseStrict("{\"a\": 1,}"));
    }

    @Test
    void duplicateKeyCheckFindsRepeatedKey() {
        assertEquals(1, Json.duplicateKeys("{\"a\": 1, \"b\": {\"x\": 1, \"x\": 2}}").size());
    }
}
