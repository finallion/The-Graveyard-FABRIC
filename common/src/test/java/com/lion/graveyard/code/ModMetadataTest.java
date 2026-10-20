package com.lion.graveyard.code;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModMetadataTest {
    private static final Path FABRIC_MOD_JSON = ModProject.FABRIC_MAIN.resolve("resources/fabric.mod.json");
    private static final Path MODS_TOML = ModProject.NEOFORGE_MAIN.resolve("resources/META-INF/neoforge.mods.toml");
    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{(\\w+)}");
    private static final Pattern ACCESS_LINE = Pattern.compile(
            "(accessible|extendable|mutable)\\s+(class\\s+[\\w/$]+|method\\s+[\\w/$]+\\s+[\\w<>$]+\\s+\\S+|field\\s+[\\w/$]+\\s+[\\w$]+\\s+\\S+)");

    static Properties gradleProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(ModProject.read(ModProject.ROOT.resolve("gradle.properties"))));
        return properties;
    }

    static List<String> accessWidenerProblems(String text) {
        List<String> problems = new ArrayList<>();
        String[] lines = text.split("\\R");

        if (lines.length == 0 || !lines[0].matches("accessWidener\\s+v[12]\\s+official")) {
            problems.add("header must be 'accessWidener v2 official'");
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (!ACCESS_LINE.matcher(line).matches()) {
                problems.add("line " + (i + 1) + " is not a valid access rule: " + line);
            }
        }
        return problems;
    }

    static List<String> unexpandedPlaceholders(String text, List<String> expanded) {
        List<String> problems = new ArrayList<>();
        Matcher m = PLACEHOLDER.matcher(text);
        while (m.find()) {
            if (!expanded.contains(m.group(1))) {
                problems.add("placeholder ${" + m.group(1) + "} is never expanded");
            }
        }
        return problems;
    }

    @Test
    void accessWidenerIsValid() {
        List<String> problems = accessWidenerProblems(ModProject.read(ModProject.RESOURCES.resolve("graveyard.accesswidener")));
        assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
    }

    @Test
    void architecturyCommonJsonPointsAtTheAccessWidener() {
        JsonObject json = Json.parseObject(ModProject.read(ModProject.RESOURCES.resolve("architectury.common.json")));
        String file = json.get("accessWidener").getAsString();
        assertTrue(Files.isRegularFile(ModProject.RESOURCES.resolve(file)), "Missing " + file);
    }

    @Test
    void fabricModJsonVersionPlaceholderIsExpandedByTheBuild() {
        String build = ModProject.read(ModProject.ROOT.resolve("fabric/build.gradle"));
        List<String> problems = unexpandedPlaceholders(ModProject.read(FABRIC_MOD_JSON), build.contains("expand \"version\"") ? List.of("version") : List.of());
        assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
    }

    @Test
    void modsTomlVersionPlaceholderIsExpandedByTheBuild() {
        String build = ModProject.read(ModProject.ROOT.resolve("neoforge/build.gradle"));
        List<String> problems = unexpandedPlaceholders(ModProject.read(MODS_TOML), build.contains("expand \"version\"") ? List.of("version") : List.of());
        assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
    }

    @Test
    void bothLoadersDeclareTheModId() {
        assertEquals(ModProject.MOD_ID, Json.parseObject(ModProject.read(FABRIC_MOD_JSON)).get("id").getAsString());
        assertTrue(ModProject.read(MODS_TOML).contains("modId = \"" + ModProject.MOD_ID + "\""));
    }

    @Test
    void bothLoadersTargetTheBuildMinecraftVersion() throws IOException {
        String minecraft = gradleProperties().getProperty("minecraft_version");
        JsonObject depends = Json.parseObject(ModProject.read(FABRIC_MOD_JSON)).getAsJsonObject("depends");
        assertEquals(minecraft, depends.get("minecraft").getAsString());
        assertTrue(ModProject.read(MODS_TOML).contains("versionRange = \"[" + minecraft + ","), "mods.toml range does not start at " + minecraft);
    }

    @Test
    void bothLoadersRequireTheBuildGeckoLibMajorMinor() throws IOException {
        Properties properties = gradleProperties();
        String[] version = properties.getProperty("geckolib_version_fabric").split("\\.");
        String majorMinor = version[0] + "." + version[1];
        assertTrue(Json.parseObject(ModProject.read(FABRIC_MOD_JSON)).getAsJsonObject("depends").get("geckolib").getAsString().contains(majorMinor));
        assertTrue(ModProject.read(MODS_TOML).contains("versionRange=\"[" + properties.getProperty("geckolib_version_fabric") + ","));
        assertEquals(properties.getProperty("geckolib_version_fabric"), properties.getProperty("geckolib_version_neoforge"));
    }

    @TestFactory
    Stream<DynamicTest> fabricEntrypointsAndMixinConfigsExist() {
        JsonObject json = Json.parseObject(ModProject.read(FABRIC_MOD_JSON));
        List<DynamicTest> tests = new ArrayList<>();
        for (var group : json.getAsJsonObject("entrypoints").entrySet()) {
            for (JsonElement entry : group.getValue().getAsJsonArray()) {
                String className = entry.getAsString();
                tests.add(DynamicTest.dynamicTest(
                        "entrypoint " + className,
                        () ->
                                assertTrue(Files.isRegularFile(ModProject.FABRIC_MAIN.resolve("java/" + className.replace('.', '/') + ".java")))
                ));
            }
        }
        for (JsonElement mixin : json.getAsJsonArray("mixins")) {
            String name = mixin.getAsString();
            tests.add(DynamicTest.dynamicTest(
                    "mixin config " + name,
                    () ->
                            assertTrue(Files.isRegularFile(ModProject.FABRIC_MAIN.resolve("resources/" + name))
                                    || Files.isRegularFile(ModProject.RESOURCES.resolve(name)))
            ));
        }
        tests.add(DynamicTest.dynamicTest(
                "icon",
                () ->
                        assertTrue(Files.isRegularFile(ModProject.FABRIC_MAIN.resolve("resources/" + json.get("icon").getAsString())))
        ));
        return tests.stream();
    }

    @Test
    void neoforgeEntrypointClassExists() {
        assertTrue(Files.isRegularFile(ModProject.NEOFORGE_JAVA.resolve("neoforge/GraveyardNeoforge.java")));
    }

    @Test
    void accessWidenerCheckRejectsBadHeaderAndBadRule() {
        assertEquals(2, accessWidenerProblems("accessWidener named\naccessible methode a/B c ()V").size());
        assertTrue(accessWidenerProblems("accessWidener v2 official\n\naccessible method a/B <init> (Z)V").isEmpty());
        assertTrue(accessWidenerProblems("accessWidener v2 official\naccessible class a/B$C\nmutable field a/B f I").isEmpty());
    }

    @Test
    void placeholderCheckFindsUnexpandedKey() {
        assertEquals(1, unexpandedPlaceholders("{\"v\": \"${version}\", \"x\": \"${other}\"}", List.of("version")).size());
    }
}
