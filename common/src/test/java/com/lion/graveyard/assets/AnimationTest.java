package com.lion.graveyard.assets;

import com.google.gson.JsonObject;
import com.lion.graveyard.support.AssetLookup;
import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// GeckoLib 4 plays keyframes in file order.
class AnimationTest {
    private static final Path DIR = ModProject.ASSETS.resolve(ModProject.ANIMATIONS);
    private static final Pattern ANIMATION_NAME = Pattern.compile("\"(animation\\.[a-z0-9_]+(?:\\.[a-z0-9_]+)+)\"");
    private static final Pattern ASSET_PATH = Pattern.compile("(?:MOD_ID\\s*,\\s*|Graveyard\\.id\\(\\s*)\"(textures/[A-Za-z0-9_/.\\-]+\\.png)\"");
    private static final Pattern RESOURCE_ID = Pattern.compile("Graveyard\\.id\\(\\s*\"([a-z0-9_/]+)\"");
    private static final Pattern ANIMATION_CONSTANT = Pattern.compile("ANIMATION\\s*=\\s*Graveyard\\.id\\(\\s*\"([a-z0-9_/]+)\"");
    private static final Pattern SPECIES_NAME = Pattern.compile("\\b[A-Z]+\\(\"([a-z]+)\",\\s*\"[a-z]+\"\\)");

    static boolean isNumeric(String key) {
        try {
            Double.parseDouble(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static List<String> keyframeOrderProblems(String rawText) {
        List<String> problems = new ArrayList<>();
        Json.walkObjects(rawText, (path, keys) -> {
            if (keys.size() < 2 || !keys.stream().allMatch(AnimationTest::isNumeric)) {
                return;
            }

            double previous = Double.NEGATIVE_INFINITY;
            for (String key : keys) {
                double time = Double.parseDouble(key);

                if (time < previous) {
                    problems.add(path + ": key " + key + " comes after " + previous);
                    return;
                }

                previous = time;
            }
        });
        return problems;
    }

    static Set<String> animationNames(String text) {
        Set<String> names = new TreeSet<>();
        JsonObject animations = Json.parseObject(text).getAsJsonObject("animations");

        if (animations != null) {
            names.addAll(animations.keySet());
        }

        return names;
    }

    static Set<String> allAnimationNames() {
        Set<String> names = new TreeSet<>();
        for (Path file : ModProject.files(DIR, ".json")) {
            names.addAll(animationNames(ModProject.read(file)));
        }
        return names;
    }

    @TestFactory
    Stream<DynamicTest> everyAnimationFileHasAnimationsObject() {
        return ModProject.files(DIR, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(DIR, file, ".json"), () -> {
                    JsonObject root = Json.parseObject(ModProject.read(file));
                    assertTrue(root.has("format_version"), "format_version missing");
                    assertTrue(root.has("animations") && root.get("animations").isJsonObject(), "animations object missing");
                }));
    }

    @TestFactory
    Stream<DynamicTest> keyframesStandInAscendingFileOrder() {
        return ModProject.files(DIR, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(DIR, file, ".json"), () -> {
                    List<String> problems = keyframeOrderProblems(ModProject.read(file));
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyAnimationRequestedByCodeExists() {
        Set<String> known = allAnimationNames();
        Set<String> requested = new TreeSet<>();
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            requested.addAll(Sources.matches(ANIMATION_NAME, Sources.code(file), 1));
        }
        return requested.stream().map(name -> DynamicTest.dynamicTest(
                name,
                () ->
                        assertTrue(known.contains(name), "Animation requested by code but in no animation file: " + name)
        ));
    }

    // The brazier model has no animation file.
    private static final Set<String> INTENTIONALLY_MISSING = Set.of("geckolib/animations/brazier.animation.json");
    private static final int MIN_ASSET_PATHS = 70;


    private static void addResourceIds(Set<String> paths, String code, String method, String directory, String suffix) {
        String body = JavaText.joinedBodies(JavaText.methods(code), method);

        for (String id : Sources.matches(RESOURCE_ID, body, 1)) {
            paths.add(directory + id + suffix);
        }
    }

    @TestFactory
    Stream<DynamicTest> everyGeoTextureAndAnimationPathNamedInCodeExists() {
        Set<String> paths = new TreeSet<>();
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            String code = Sources.code(file);
            paths.addAll(Sources.matches(ASSET_PATH, code, 1));
            addResourceIds(paths, code, "getModelResource", "geckolib/models/", ".geo.json");
            addResourceIds(paths, code, "getAnimationResource", "geckolib/animations/", ".animation.json");

            for (String id : Sources.matches(ANIMATION_CONSTANT, code, 1)) {
                paths.add("geckolib/animations/" + id + ".animation.json");
            }

            if (file.getFileName().toString().equals("CorvidEntity.java")) {
                for (String name : Sources.matches(SPECIES_NAME, code, 1)) {
                    paths.add("geckolib/models/" + name + ".geo.json");
                }
            }
        }
        DynamicTest found = DynamicTest.dynamicTest(
                "paths found",
                () ->
                        assertTrue(paths.size() >= MIN_ASSET_PATHS, "Only " + paths.size() + " paths found")
        );
        return Stream.concat(
                Stream.of(found),
                paths.stream()
                        .filter(p -> !INTENTIONALLY_MISSING.contains(p))
                        .map(path -> DynamicTest.dynamicTest(
                                path,
                                () ->
                                        assertTrue(AssetLookup.MOD.exists(ModProject.ASSETS.resolve(path)), "Referenced in code but missing: " + path)
                        ))
        );
    }

    private static List<String> keyframeOrderProblems(String path, List<String> keys) {
        List<String> problems = new ArrayList<>();
        double previous = Double.NEGATIVE_INFINITY;
        for (String key : keys) {
            double time = Double.parseDouble(key);

            if (time < previous) {
                problems.add(path + ": key " + key + " comes after " + previous);
            }

            previous = time;
        }
        return problems;
    }

    static List<String> startingKeyframeProblems(String rawText) {
        List<String> problems = new ArrayList<>();
        Json.walkObjects(rawText, (path, keys) -> {
            if (keys.isEmpty() || !keys.stream().allMatch(AnimationTest::isNumeric)) {
                return;
            }

            if (keys.stream().anyMatch(key -> Double.parseDouble(key) < 0.0D)) {
                problems.add(path + ": negative keyframe time in " + keys);
                return;
            }

            if (!keyframeOrderProblems(path, keys).isEmpty()) {
                problems.add(path + ": keyframes are not ascending in " + keys);
                return;
            }

            List<String> zeros = keys.stream()
                    .filter(key -> Double.parseDouble(key) == 0.0D)
                    .toList();

            if (zeros.size() > 1) {
                problems.add(path + ": more than one starting keyframe " + zeros);
            } else if (zeros.size() == 1 && !keys.get(0).equals(zeros.get(0))) {
                problems.add(path + ": starting keyframe " + zeros.get(0) + " is not first");
            }
        });
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> startingKeyframeComesFirstInEveryChannel() {
        return ModProject.files(DIR, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(DIR, file, ".json"), () -> {
                    List<String> problems = startingKeyframeProblems(ModProject.read(file));
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @Test
    void startingKeyframeCheckCatchesALateOrDoubleZero() {
        assertEquals(1, startingKeyframeProblems("{\"r\": {\"0.5\": 1, \"0\": 2}}").size());
        assertEquals(1, startingKeyframeProblems("{\"r\": {\"0\": 1, \"0.0\": 2}}").size());
        assertEquals(1, startingKeyframeProblems("{\"r\": {\"-0.05\": 1, \"1.9\": 2}}").size());
        assertEquals(1, startingKeyframeProblems("{\"r\": {\"0.5\": 1, \"0.2\": 2}}").size());
        assertTrue(startingKeyframeProblems("{\"r\": {\"0\": 1, \"0.5\": 2}, \"s\": {\"0.5\": 1}, \"t\": {\"x\": 1, \"0\": 2}}").isEmpty());
    }

    @Test
    void orderCheckCatchesIntegerKeyPulledToTheFront() {
        String broken = "{\"rotation\": {\"1\": [0,0,0], \"0.5\": [1,1,1], \"2\": [2,2,2]}}";
        assertEquals(1, keyframeOrderProblems(broken).size());
    }

    @Test
    void orderCheckAcceptsAscendingKeysAndIgnoresNamedKeys() {
        assertTrue(keyframeOrderProblems("{\"a\": {\"0\": 1, \"0.5\": 2, \"1\": 3}, \"b\": {\"x\": 1, \"0\": 2}}").isEmpty());
    }
}
