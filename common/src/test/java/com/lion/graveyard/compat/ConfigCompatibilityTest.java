package com.lion.graveyard.compat;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigCompatibilityTest {
    private static final Path CONFIG_DIR = ModProject.JAVA.resolve("config");
    private static final Pattern DEFAULT_KEY = Pattern.compile("(\\w+)\\.putIfAbsent\\(\\s*\"([^\"]+)\"");
    private static final Pattern PUBLIC_FIELD = Pattern.compile("public\\s+(?:final\\s+)?[\\w<>, ]+\\s+(\\w+)\\s*(?:=|;)");
    private static final Pattern RETURN_LITERAL = Pattern.compile("String\\s+(getName|getExtension|getDirectory)\\(\\)\\s*\\{\\s*return\\s+\"([^\"]+)\"");

    public static Set<String> currentLines() {
        Set<String> lines = new TreeSet<>();
        String config = Sources.code(CONFIG_DIR.resolve("GraveyardConfig.java"));
        Matcher defaults = DEFAULT_KEY.matcher(config);
        while (defaults.find()) {
            lines.add("default:" + defaults.group(1) + ":" + defaults.group(2));
        }
        Matcher literal = RETURN_LITERAL.matcher(config);
        while (literal.find()) {
            lines.add(literal.group(1) + ":" + literal.group(2));
        }
        for (Path file : ModProject.files(CONFIG_DIR, ".java")) {
            String name = file.getFileName().toString().replace(".java", "");
            if (name.equals("GraveyardConfig") || file.getParent().getFileName().toString().equals("entries")) {
                Matcher field = PUBLIC_FIELD.matcher(Sources.code(file));
                while (field.find()) {
                    lines.add("field:" + name + ":" + field.group(1));
                }
            }
        }
        return lines;
    }

    @Test
    void extractorFindsConfigContent() {
        assertTrue(currentLines().size() > 80, "Only found " + currentLines().size());
    }

    @TestFactory
    Stream<DynamicTest> everySnapshotConfigKeyIsStillPresent() {
        Set<String> current = currentLines();
        return ModProject.snapshotLines("config-keys.txt").stream().map(line ->
                DynamicTest.dynamicTest(line, () -> assertTrue(current.contains(line), "Config key or field vanished: " + line)));
    }

    @Test
    void fieldPatternReadsFinalAndPlainFields() {
        Matcher m = PUBLIC_FIELD.matcher("public final int weight;\npublic Map<String, Boolean> flags = new HashMap<>();");
        assertTrue(m.find());
        assertEquals("weight", m.group(1));
        assertTrue(m.find());
        assertEquals("flags", m.group(1));
    }
}
