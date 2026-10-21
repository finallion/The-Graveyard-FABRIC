package com.lion.graveyard.assets;

import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.RegistryIds;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LangTest {
    private static final Path LANG_DIR = ModProject.ASSETS.resolve(ModProject.LANG);
    private static final Pattern PLACEHOLDER = Pattern.compile("%(?:\\d+\\$)?[a-zA-Z%]");

    static Map<String, String> load(Path file) {
        return Json.stringMap(ModProject.read(file));
    }

    static Map<String, String> english() {
        return load(LANG_DIR.resolve("en_us.json"));
    }

    static List<Path> translations() {
        return ModProject.files(LANG_DIR, ".json").stream()
                .filter(p -> !p.getFileName().toString().equals("en_us.json"))
                .toList();
    }

    static List<String> placeholders(String text) {
        List<String> found = new ArrayList<>();
        Matcher m = PLACEHOLDER.matcher(text);
        while (m.find()) {
            found.add(m.group());
        }
        Collections.sort(found);
        return found;
    }

    static List<String> keyDifferences(Map<String, String> base, Map<String, String> other) {
        List<String> problems = new ArrayList<>();
        for (String key : base.keySet()) {
            if (!other.containsKey(key)) {
                problems.add("missing key " + key);
            }
        }
        for (String key : other.keySet()) {
            if (!base.containsKey(key)) {
                problems.add("extra key " + key);
            }
        }
        return problems;
    }

    static List<String> placeholderDifferences(Map<String, String> base, Map<String, String> other) {
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, String> entry : base.entrySet()) {
            String translated = other.get(entry.getKey());

            if (translated != null && !placeholders(entry.getValue()).equals(placeholders(translated))) {
                problems.add(entry.getKey() + ": " + placeholders(entry.getValue()) + " vs " + placeholders(translated));
            }
        }
        return problems;
    }

    @Test
    void fifteenLanguagesExist() {
        assertEquals(15, ModProject.files(LANG_DIR, ".json").size());
    }

    @TestFactory
    Stream<DynamicTest> everyTranslationHasExactlyTheEnglishKeys() {
        Map<String, String> english = english();
        return translations().stream()
                .map(file -> DynamicTest.dynamicTest(file.getFileName().toString(), () -> {
                    List<String> problems = keyDifferences(english, load(file));
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyTranslationKeepsTheEnglishPlaceholders() {
        Map<String, String> english = english();
        return translations().stream()
                .map(file -> DynamicTest.dynamicTest(file.getFileName().toString(), () -> {
                    List<String> problems = placeholderDifferences(english, load(file));
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @TestFactory
    Stream<DynamicTest> noLanguageFileHasEmptyValues() {
        return ModProject.files(LANG_DIR, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(file.getFileName().toString(), () -> {
                    List<String> empty = new ArrayList<>();
                    load(file).forEach((key, value) -> {
                        if (value.isBlank()) {
                            empty.add(key);
                        }
                    });
                    assertTrue(empty.isEmpty(), () -> "Empty values: " + empty);
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredBlockHasAnEnglishName() {
        Map<String, String> english = english();
        return RegistryIds.of(RegistryIds.BLOCK).stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () ->
                                assertTrue(english.containsKey("block." + ModProject.MOD_ID + "." + id), "No name for block " + id)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredItemHasAnEnglishName() {
        Map<String, String> english = english();
        return RegistryIds.of(RegistryIds.ITEM).stream()
                .map(id -> DynamicTest.dynamicTest(id, () -> {
                    boolean internalPart = id.matches("(?:\\w+_coffin|sarcophagus)_(?:lid|base)");
                    boolean named = internalPart
                            || english.containsKey("item." + ModProject.MOD_ID + "." + id)
                            || english.containsKey("block." + ModProject.MOD_ID + "." + id);
                    assertTrue(named, "No name for item " + id);
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredEntityHasAnEnglishName() {
        Map<String, String> english = english();
        return RegistryIds.of(RegistryIds.ENTITY).stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () ->
                                assertTrue(english.containsKey("entity." + ModProject.MOD_ID + "." + id), "No name for entity " + id)
                ));
    }

    @Test
    void keyCheckReportsMissingAndExtraKeys() {
        Map<String, String> base = Map.of("a", "1", "b", "2");
        Map<String, String> other = Map.of("a", "1", "c", "3");
        assertEquals(new TreeSet<>(List.of("missing key b", "extra key c")), new TreeSet<>(keyDifferences(base, other)));
    }

    @Test
    void placeholderCheckReportsChangedFormatSpecifiers() {
        assertFalse(placeholderDifferences(Map.of("k", "Hello %s"), Map.of("k", "Hallo")).isEmpty());
        assertFalse(placeholderDifferences(Map.of("k", "%1$s and %2$s"), Map.of("k", "%1$s")).isEmpty());
        assertTrue(placeholderDifferences(Map.of("k", "%1$s and %2$s"), Map.of("k", "%2$s und %1$s")).isEmpty());
    }
}
