package com.lion.graveyard.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class ConfigFiles {
    public static final String FILE_NAME = "graveyard.json5";
    public static final String LEGACY_FILE_NAME = "the-graveyard-3.0-config.json5";

    private static final int MAX_LISTED = 10;

    private ConfigFiles() {}

    public static ConfigValues load(Path directory, ConfigLog log) {
        try {
            Path current = directory.resolve(FILE_NAME);
            Path legacy = directory.resolve(LEGACY_FILE_NAME);
            deleteTemporary(directory, log);

            if (Files.exists(current)) {
                return loadCurrent(directory, current, Files.exists(legacy), log);
            }

            if (Files.exists(legacy)) {
                return migrate(directory, current, legacy, log);
            }

            ConfigValues values = ConfigValues.defaults();
            write(directory, current, values, log);
            return values;
        } catch (IOException | RuntimeException exception) {
            log.error("Could not load the config: " + exception);
            log.info("Using default values.");
            return ConfigValues.defaults();
        }
    }

    private static ConfigValues loadCurrent(Path directory, Path current, boolean legacyExists, ConfigLog log) throws IOException {
        String text = Files.readString(current, StandardCharsets.UTF_8);
        ConfigReader.Result result = ConfigReader.read(text);

        if (result.failed()) {
            reportUnreadable(result.failure(), legacyExists, log);
            return result.values();
        }

        String rewritten = ConfigWriter.write(result.values());

        if (rewritten.equals(normalizeLineEnds(text))) {
            return result.values();
        }

        if (result.hasProblems()) {
            backup(directory, text, log);
            reportProblems(FILE_NAME, result, false, log);
        }

        write(directory, current, result.values(), log);
        return result.values();
    }

    private static ConfigValues migrate(Path directory, Path current, Path legacy, ConfigLog log) throws IOException {
        String text = Files.readString(legacy, StandardCharsets.UTF_8);
        ConfigReader.Result result = LegacyMigration.migrate(text);

        if (result.failed()) {
            log.error("Could not read " + LEGACY_FILE_NAME + ": " + result.failure() + ". The file is left unchanged and not used. Using default values.");
            return result.values();
        }

        log.info("Reading " + LEGACY_FILE_NAME + " once to create " + FILE_NAME + ". The old file stays as it is.");
        reportProblems(LEGACY_FILE_NAME, result, true, log);
        write(directory, current, result.values(), log);
        return result.values();
    }

    private static void write(Path directory, Path target, ConfigValues values, ConfigLog log) {
        try {
            Files.createDirectories(directory);
            Path temporary = directory.resolve(FILE_NAME + ".tmp");
            Files.writeString(temporary, ConfigWriter.write(values), StandardCharsets.UTF_8);
            move(temporary, target);
        } catch (IOException exception) {
            deleteQuietly(directory.resolve(FILE_NAME + ".tmp"));
            log.error("Could not write " + FILE_NAME + ": " + exception);
            log.info("Using the values in memory.");
        }
    }

    private static void deleteTemporary(Path directory, ConfigLog log) {
        try {
            Files.deleteIfExists(directory.resolve(FILE_NAME + ".tmp"));
        } catch (IOException exception) {
            log.warn("Could not delete " + FILE_NAME + ".tmp: " + exception);
        }
    }

    private static void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            // Ignore a failed delete. The next write replaces the file.
        }
    }

    private static void backup(Path directory, String text, ConfigLog log) {
        try {
            Files.writeString(directory.resolve(FILE_NAME + ".bak"), text, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            log.warn("Could not write the backup of " + FILE_NAME + ": " + exception);
        }
    }

    private static void move(Path temporary, Path target) throws IOException {
        try {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void reportUnreadable(String cause, boolean legacyExists, ConfigLog log) {
        log.error("Could not read " + FILE_NAME + ": " + cause);
        log.warn("The file " + FILE_NAME + " is left unchanged and not used.");

        if (legacyExists) {
            log.info("Using default values. Delete " + FILE_NAME + " to read " + LEGACY_FILE_NAME + " again. Delete or rename both files to get the default values.");
        } else {
            log.info("Using default values. If this problem persists, delete the file " + FILE_NAME + " and try again.");
        }
    }

    private static void reportProblems(String fileName, ConfigReader.Result result, boolean legacy, ConfigLog log) {
        if (!result.reset().isEmpty()) {
            log.warn("These values in " + fileName + " were not valid and use a default or a limit: " + listed(result.reset()));
        }

        if (!result.dropped().isEmpty()) {
            String effect = legacy ? "are unknown and are not used: " : "are unknown and are removed: ";
            log.warn("These keys in " + fileName + " " + effect + listed(result.dropped()));
        }

        if (result.hasTrailingContent()) {
            log.warn("The file " + fileName + " holds content after the first object. The content is removed.");
        }
    }

    private static String listed(List<String> names) {
        if (names.size() <= MAX_LISTED) {
            return String.join(", ", names);
        }

        return String.join(", ", names.subList(0, MAX_LISTED)) + " and " + (names.size() - MAX_LISTED) + " more";
    }

    private static String normalizeLineEnds(String text) {
        return text.replace("\r\n", "\n");
    }
}
