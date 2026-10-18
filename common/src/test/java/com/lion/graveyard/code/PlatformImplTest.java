package com.lion.graveyard.code;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformImplTest {
    private static final Path PLATFORM = ModProject.JAVA.resolve("platform");
    private static final Pattern EXPECTED = Pattern.compile(
            "@ExpectPlatform\\s+public\\s+static\\s+(?:<[^>]+>\\s+)?([\\w<>\\[\\], ?.]+?)\\s+(\\w+)\\s*\\(([^)]*)\\)");
    private static final Pattern METHOD = Pattern.compile(
            "public\\s+static\\s+(?:<[^>]+>\\s+)?([\\w<>\\[\\], ?.]+?)\\s+(\\w+)\\s*\\(([^)]*)\\)");

    private static final Set<String> LOADER_ONLY = Set.of("ServerEvents");

    record Signature(String returnType, String name, List<String> parameterTypes) {
    }

    static String simple(String type) {
        String t = type.replaceAll("\\s+", " ").trim().replaceAll("\\bfinal\\s+", "");
        return t.replaceAll("\\b[a-z][\\w]*\\.", "")
                .replaceAll("\\s*,\\s*", ",")
                .replaceAll("\\s*<\\s*", "<")
                .replaceAll("\\s*>", ">");
    }

    static List<String> parameterTypes(String parameters) {
        List<String> types = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (char c : (parameters + ",").toCharArray()) {
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
            }

            if (c == ',' && depth == 0) {
                String parameter = current.toString().replaceAll("@\\w+(\\([^)]*\\))?\\s*", "").trim();

                if (!parameter.isEmpty()) {
                    int split = parameter.lastIndexOf(' ');
                    types.add(simple(split < 0 ? parameter : parameter.substring(0, split)));
                }

                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        return types;
    }

    static List<Signature> expected(String code) {
        List<Signature> result = new ArrayList<>();
        Matcher m = EXPECTED.matcher(code);
        while (m.find()) {
            result.add(new Signature(simple(m.group(1)), m.group(2), parameterTypes(m.group(3))));
        }
        return result;
    }

    static List<Signature> implemented(String code) {
        List<Signature> result = new ArrayList<>();
        Matcher m = METHOD.matcher(code);
        while (m.find()) {
            result.add(new Signature(simple(m.group(1)), m.group(2), parameterTypes(m.group(3))));
        }
        return result;
    }

    static List<String> expectedFiles() {
        List<String> names = new ArrayList<>();
        for (Path file : ModProject.files(PLATFORM, ".java")) {
            if (Sources.code(file).contains("@ExpectPlatform")) {
                names.add(file.getFileName().toString().replace(".java", ""));
            }
        }
        return names;
    }

    @Test
    void platformClassesWereFound() {
        assertTrue(expectedFiles().size() >= 5, expectedFiles().toString());
    }

    @TestFactory
    Stream<DynamicTest> everyExpectedMethodIsImplementedOnBothLoaders() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String className : expectedFiles()) {
            for (Signature signature : expected(Sources.code(PLATFORM.resolve(className + ".java")))) {
                for (String loader : List.of("fabric", "neoforge")) {
                    Path impl = (loader.equals("fabric") ? ModProject.FABRIC_JAVA : ModProject.NEOFORGE_JAVA)
                            .resolve("platform/" + loader + "/" + className + "Impl.java");
                    tests.add(DynamicTest.dynamicTest(loader + ": " + className + "." + signature.name(), () -> {
                        assertTrue(Files.isRegularFile(impl), "Missing " + ModProject.display(impl));
                        assertTrue(
                                implemented(Sources.code(impl)).contains(signature),
                                "No matching signature " + signature + " in " + ModProject.display(impl)
                        );
                    }));
                }
            }
        }
        return tests.stream();
    }

    @TestFactory
    Stream<DynamicTest> loaderHelperClassesExistOnBothLoaders() {
        Set<String> fabric = classNames(ModProject.FABRIC_JAVA.resolve("platform/fabric"));
        Set<String> neoforge = classNames(ModProject.NEOFORGE_JAVA.resolve("platform/neoforge"));
        Set<String> all = new TreeSet<>(fabric);
        all.addAll(neoforge);
        return all.stream()
                .filter(n -> !LOADER_ONLY.contains(n))
                .map(name -> DynamicTest.dynamicTest(name, () -> {
                    assertTrue(fabric.contains(name), name + " is missing on Fabric");
                    assertTrue(neoforge.contains(name), name + " is missing on NeoForge");
                }));
    }

    private static Set<String> classNames(Path dir) {
        Set<String> names = new TreeSet<>();
        for (Path file : ModProject.files(dir, ".java")) {
            names.add(file.getFileName().toString().replace(".java", ""));
        }
        return names;
    }

    @Test
    void parserReadsExpectedMethodAndMatchesImplementationDespiteImportsAndParameterNames() {
        String common = "@ExpectPlatform\npublic static Supplier<Map<String, java.util.List<Block>>> make(final Block block, Item.Settings settings) { throw new AssertionError(); }";
        String good = "public static Supplier<Map<String,List<Block>>> make(Block b, Item.Settings s) { return null; }";
        String badArity = "public static Supplier<Map<String,List<Block>>> make(Block b) { return null; }";
        String badType = "public static Supplier<Map<String,List<Block>>> make(Block b, Item.Settings s, int extra) { return null; }";
        Signature wanted = expected(common).get(0);
        assertEquals("make", wanted.name());
        assertTrue(implemented(good).contains(wanted));
        assertTrue(!implemented(badArity).contains(wanted));
        assertTrue(!implemented(badType).contains(wanted));
    }
}
