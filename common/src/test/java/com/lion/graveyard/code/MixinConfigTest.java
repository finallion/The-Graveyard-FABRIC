package com.lion.graveyard.code;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixinConfigTest {
    private static final Path COMMON_CONFIG = ModProject.RESOURCES.resolve("graveyard-common.mixins.json");
    private static final Path FABRIC_CONFIG = ModProject.FABRIC_MAIN.resolve("resources/graveyard-fabric.mixins.json");
    private static final Path NEOFORGE_CONFIG = ModProject.NEOFORGE_MAIN.resolve("resources/graveyard-neoforge.mixins.json");

    static Set<String> listed(String json) {
        JsonObject config = Json.parseObject(json);
        Set<String> names = new TreeSet<>();
        for (String section : List.of("mixins", "client", "server")) {
            if (config.has(section)) {
                for (JsonElement name : config.getAsJsonArray(section)) {
                    names.add(name.getAsString());
                }
            }
        }
        return names;
    }

    static String packageOf(String json) {
        return Json.parseObject(json).get("package").getAsString();
    }

    static List<String> missingClasses(String json, Path sourceRoot) {
        List<String> problems = new ArrayList<>();
        for (String name : listed(json)) {
            if (!Files.isRegularFile(sourceRoot.resolve(name.replace('.', '/') + ".java"))) {
                problems.add("listed but no source file: " + name);
            }
        }
        return problems;
    }

    static List<String> unlistedClasses(String json, Path mixinDir) {
        Set<String> listed = listed(json);
        List<String> problems = new ArrayList<>();
        for (Path file : ModProject.files(mixinDir, ".java")) {
            String name = ModProject.idOf(mixinDir, file, ".java").replace('/', '.');

            if (!listed.contains(name)) {
                problems.add("on disk but not listed: " + name);
            }
        }
        return problems;
    }

    @Test
    void commonConfigUsesTheModMixinPackage() {
        assertEquals("com.lion.graveyard.mixin", packageOf(ModProject.read(COMMON_CONFIG)));
    }

    @TestFactory
    Stream<DynamicTest> everyListedCommonMixinHasASourceFile() {
        Path mixinDir = ModProject.JAVA.resolve("mixin");
        return listed(ModProject.read(COMMON_CONFIG)).stream()
                .map(name -> DynamicTest.dynamicTest(
                        name,
                        () ->
                                assertTrue(Files.isRegularFile(mixinDir.resolve(name.replace('.', '/') + ".java")), "No source file for mixin " + name)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everyMixinSourceFileIsListed() {
        Path mixinDir = ModProject.JAVA.resolve("mixin");
        Set<String> listed = listed(ModProject.read(COMMON_CONFIG));
        return ModProject.files(mixinDir, ".java").stream()
                .map(file -> {
                    String name = ModProject.idOf(mixinDir, file, ".java").replace('/', '.');
                    return DynamicTest.dynamicTest(name, () -> assertTrue(listed.contains(name), "Mixin class not in graveyard-common.mixins.json: " + name));
                });
    }

    @TestFactory
    Stream<DynamicTest> everyMixinSourceFileCarriesTheMixinAnnotation() {
        Path mixinDir = ModProject.JAVA.resolve("mixin");
        return ModProject.files(mixinDir, ".java").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(mixinDir, file, ".java"),
                        () ->
                                assertTrue(Sources.code(file).contains("@Mixin("), "No @Mixin annotation")
                ));
    }

    @TestFactory
    Stream<DynamicTest> loaderMixinConfigsOnlyListExistingClasses() {
        return Stream.of(FABRIC_CONFIG, NEOFORGE_CONFIG)
                .map(config -> DynamicTest.dynamicTest(config.getFileName().toString(), () -> {
                    String json = ModProject.read(config);
                    Path root = (config == FABRIC_CONFIG ? ModProject.FABRIC_MAIN : ModProject.NEOFORGE_MAIN)
                            .resolve("java/" + packageOf(json).replace('.', '/'));
                    List<String> problems = missingClasses(json, root);
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @Test
    void checkFindsListedMixinWithoutFile(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve("RealMixin.java"), "@Mixin(Object.class) class RealMixin {}");
        String json = "{\"package\": \"p\", \"mixins\": [\"RealMixin\", \"GhostMixin\"], \"client\": [\"client.GhostClient\"]}";
        assertEquals(2, missingClasses(json, temp).size());
    }

    @Test
    void checkFindsMixinFileThatIsNotListed(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve("RealMixin.java"), "x");
        Files.writeString(temp.resolve("Forgotten.java"), "x");
        assertEquals(List.of("on disk but not listed: Forgotten"), unlistedClasses("{\"package\": \"p\", \"mixins\": [\"RealMixin\"]}", temp));
    }
}
