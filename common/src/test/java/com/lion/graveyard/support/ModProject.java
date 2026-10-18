package com.lion.graveyard.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ModProject {
    public static final String MOD_ID = "graveyard";

    public static final Path ROOT = Path.of(System.getProperty("graveyard.rootDir", "."));
    public static final Path COMMON_MAIN = ROOT.resolve("common/src/main");
    public static final Path JAVA = COMMON_MAIN.resolve("java/com/lion/graveyard");
    public static final Path RESOURCES = COMMON_MAIN.resolve("resources");
    public static final Path ASSETS = RESOURCES.resolve("assets/" + MOD_ID);
    public static final Path DATA = RESOURCES.resolve("data/" + MOD_ID);

    public static final Path FABRIC_MAIN = ROOT.resolve("fabric/src/main");
    public static final Path NEOFORGE_MAIN = ROOT.resolve("neoforge/src/main");
    public static final Path FABRIC_JAVA = FABRIC_MAIN.resolve("java/com/lion/graveyard");
    public static final Path NEOFORGE_JAVA = NEOFORGE_MAIN.resolve("java/com/lion/graveyard");

    public static final String BLOCKSTATES = "blockstates";
    public static final String MODELS = "models";
    public static final String TEXTURES = "textures";
    public static final String SOUNDS = "sounds";
    public static final String LANG = "lang";
    public static final String GEO = "geckolib/models";
    public static final String ANIMATIONS = "geckolib/animations";
    public static final String PARTICLES = "particles";

    public static final String LOOT_TABLES = "loot_table";
    public static final String BLOCK_LOOT = LOOT_TABLES + "/blocks";
    public static final String RECIPES = "recipe";
    public static final String ADVANCEMENTS = "advancement";
    public static final String STRUCTURES = "structure";
    public static final String TAGS = "tags";
    public static final String TAGS_BLOCKS = TAGS + "/block";
    public static final String TAGS_ITEMS = TAGS + "/item";
    public static final String TAGS_BIOMES = TAGS + "/worldgen/biome";
    public static final String WORLDGEN = "worldgen";
    public static final String STRUCTURE_SETS = WORLDGEN + "/structure_set";
    public static final String STRUCTURE_FEATURES = WORLDGEN + "/structure";
    public static final String TEMPLATE_POOLS = WORLDGEN + "/template_pool";
    public static final String PROCESSOR_LISTS = WORLDGEN + "/processor_list";

    private ModProject() {}

    public static List<Path> files(Path dir, String suffix) {
        if (!Files.isDirectory(dir)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(suffix))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String read(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] readBytes(Path file) {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String idOf(Path base, Path file, String suffix) {
        String relative = base.relativize(file).toString().replace('\\', '/');
        return relative.endsWith(suffix) ? relative.substring(0, relative.length() - suffix.length()) : relative;
    }

    public static String display(Path file) {
        return ROOT.relativize(file).toString().replace('\\', '/');
    }

    public static boolean isModId(String namespacedId) {
        return namespacedId.startsWith(MOD_ID + ":");
    }

    public static String stripNamespace(String namespacedId) {
        int colon = namespacedId.indexOf(':');
        return colon < 0 ? namespacedId : namespacedId.substring(colon + 1);
    }

    public static String snapshot(String name) {
        try (var in = ModProject.class.getResourceAsStream("/" + name)) {
            if (in == null) {
                throw new IllegalStateException("Missing test resource " + name);
            }

            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static java.util.Set<String> snapshotLines(String name) {
        java.util.Set<String> lines = new java.util.TreeSet<>();
        for (String line : snapshot(name).split("\\R")) {
            String trimmed = line.trim();

            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                lines.add(trimmed);
            }
        }
        return lines;
    }
}
