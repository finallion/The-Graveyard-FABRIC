package com.lion.graveyard.assets;

import com.lion.graveyard.support.Floors;
import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Every translation key that the code names must have an English entry. LangTest then covers the other languages.
class TranslationKeyUsageTest {
    private static final Pattern LITERAL = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"");

    private static final Pattern MOD_KEY_LITERAL = Pattern.compile("\"((?:block|item|entity|chronicle|gui|container|subtitles|advancements)\\.graveyard\\.[^\"]*)\"");

    private static final Map<String, String> VANILLA_KEYS = Map.of("book.pageIndicator", "Page counter of the base game book screen.");

    /// Reason per entry. A key built at run time has to start with one of these prefixes, and the file needs at least one key under it.
    private static final Map<String, String> DYNAMIC_PREFIXES = Map.of(
            "chronicle.graveyard.page.", "The page number completes the key.",
            "chronicle.graveyard.act.", "The act number completes the key."
    );

    record Keys(List<String> exact, List<String> prefixes) {
    }

    static Keys keysIn(String code) {
        List<String> exact = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        for (String args : JavaText.callArguments(code, "Component.translatable(")) {
            String first = JavaText.splitTopLevel(args, ',').get(0).trim();

            if (JavaText.splitTopLevel(first, '?').size() > 1) {
                Matcher literals = LITERAL.matcher(first);
                while (literals.find()) {
                    exact.add(literals.group(1));
                }
                continue;
            }

            StringBuilder prefix = new StringBuilder();
            boolean complete = true;
            for (String part : JavaText.splitTopLevel(first, '+')) {
                String token = part.trim();
                Matcher literal = LITERAL.matcher(token);

                if (literal.matches()) {
                    prefix.append(literal.group(1));
                } else if (token.equals("Graveyard.MOD_ID") || token.equals("MOD_ID")) {
                    prefix.append(ModProject.MOD_ID);
                } else {
                    complete = false;
                    break;
                }
            }

            if (prefix.length() > 0) {
                (complete ? exact : prefixes).add(prefix.toString());
            }
        }
        return new Keys(exact, prefixes);
    }

    /// Every string literal that looks like a key of this mod, wherever the code uses it.
    static Keys modKeyLiterals(String code) {
        List<String> exact = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        Matcher literals = MOD_KEY_LITERAL.matcher(code);
        while (literals.find()) {
            String key = literals.group(1);
            (key.endsWith(".") ? prefixes : exact).add(key);
        }
        return new Keys(exact, prefixes);
    }

    static List<String> missingKeys(Keys keys, Map<String, String> english) {
        List<String> problems = new ArrayList<>();
        for (String key : keys.exact()) {
            if (!english.containsKey(key) && !VANILLA_KEYS.containsKey(key)) {
                problems.add("missing key " + key);
            }
        }
        for (String prefix : keys.prefixes()) {
            if (!DYNAMIC_PREFIXES.containsKey(prefix)) {
                problems.add("dynamic key prefix " + prefix + " is not on the allow list");
            } else if (english.keySet().stream()
                    .noneMatch(k -> k.startsWith(prefix))) {
                problems.add("no key starts with " + prefix);
            }
        }
        return problems;
    }

    private static List<Path> sourceFiles() {
        List<Path> files = new ArrayList<>(ModProject.files(ModProject.JAVA, ".java"));
        files.addAll(ModProject.files(ModProject.FABRIC_JAVA, ".java"));
        files.addAll(ModProject.files(ModProject.NEOFORGE_JAVA, ".java"));
        return files;
    }

    @Test
    void translatableCallsWereFound() {
        int keys = 0;
        for (Path file : sourceFiles()) {
            Keys found = keysIn(Sources.code(file));
            keys += found.exact().size()
                    + found.prefixes().size();
        }
        assertTrue(keys >= 20, "only " + keys + " keys found");
    }

    @TestFactory
    Stream<DynamicTest> everyNamedTranslationKeyExistsInEnglish() {
        Map<String, String> english = LangTest.english();
        return Floors.atLeast(
                12,
                "files with translatable calls",
                sourceFiles().stream()
                        .filter(file -> Sources.code(file).contains("Component.translatable("))
                        .map(file -> DynamicTest.dynamicTest(
                                ModProject.display(file),
                                () ->
                                        assertEquals(List.of(), missingKeys(keysIn(Sources.code(file)), english))
                        ))
        );
    }

    @Test
    void modKeyLiteralsWereFound() {
        int keys = 0;
        for (Path file : sourceFiles()) {
            Keys found = modKeyLiterals(Sources.code(file));
            keys += found.exact().size()
                    + found.prefixes().size();
        }
        assertTrue(keys >= 25,"only " + keys + " key literals found");
    }

    @TestFactory
    Stream<DynamicTest> everyKeyLiteralExistsInEnglish() {
        Map<String, String> english = LangTest.english();
        return Floors.atLeast(
                5,
                "files with key literals",
                sourceFiles().stream()
                        .filter(file -> !modKeyLiterals(Sources.code(file)).exact().isEmpty() || !modKeyLiterals(Sources.code(file)).prefixes().isEmpty())
                        .map(file -> DynamicTest.dynamicTest(
                                ModProject.display(file),
                                () ->
                                        assertEquals(List.of(), missingKeys(modKeyLiterals(Sources.code(file)), english))
                        ))
        );
    }

    @Test
    void literalCheckCatchesKeysHeldInVariables() {
        Map<String, String> english = Map.of("entity.graveyard.mob.name", "x", "chronicle.graveyard.page.1", "y");
        assertEquals(List.of(), missingKeys(modKeyLiterals("String key = \"entity.graveyard.mob.name\";"), english));
        assertEquals(1, missingKeys(modKeyLiterals("String key = \"block.graveyard.altar.gone\";"), english).size());
        assertEquals(1, missingKeys(modKeyLiterals("failure = \"gui.graveyard.screen.\" + part;"), english).size());
        assertEquals(List.of(), missingKeys(modKeyLiterals("key = \"chronicle.graveyard.page.\" + n;"), english));
        assertEquals(List.of(), missingKeys(modKeyLiterals("String id = \"graveyard:ossuary\"; String other = \"item.minecraft.stick\";"), english));
    }

    @Test
    void keyCheckCatchesMissingAndDynamicKeys() {
        Map<String, String> english = Map.of("a.b", "x", "chronicle.graveyard.page.1", "y");
        assertEquals(1, missingKeys(keysIn("Component.translatable(\"a.missing\")"), english).size());
        assertEquals(List.of(), missingKeys(keysIn("Component.translatable(\"a.b\", 1, 2)"), english));
        assertEquals(1, missingKeys(keysIn("Component.translatable(sit ? \"a.b\" : \"a.gone\")"), english).size());
        assertEquals(List.of(), missingKeys(keysIn("Component.translatable(\"chronicle.\" + Graveyard.MOD_ID + \".page.\" + page)"), english));
        assertEquals(1, missingKeys(keysIn("Component.translatable(\"nothing.\" + page)"), english).size());
        assertEquals(List.of(), missingKeys(keysIn("Component.translatable(failure)"), english));
    }
}
