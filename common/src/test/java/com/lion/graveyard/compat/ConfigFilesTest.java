package com.lion.graveyard.compat;

import com.lion.graveyard.config.ConfigFiles;
import com.lion.graveyard.config.ConfigLog;
import com.lion.graveyard.config.ConfigReader;
import com.lion.graveyard.config.ConfigSchema;
import com.lion.graveyard.config.ConfigValues;
import com.lion.graveyard.config.ConfigWriter;
import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigFilesTest {
    private static final String LEGACY = ConfigFiles.LEGACY_FILE_NAME;

    private static final class Recorder implements ConfigLog {
        final List<String> lines = new ArrayList<>();

        @Override
        public void info(String message) {
            lines.add("info " + message);
        }

        @Override
        public void warn(String message) {
            lines.add("warn " + message);
        }

        @Override
        public void error(String message) {
            lines.add("error " + message);
        }
    }

    private static String sampleText() {
        return ModProject.snapshot("config-legacy-sample.json5");
    }

    private static String defaultsText() {
        return ConfigWriter.write(ConfigValues.defaults());
    }

    @Test
    void writerIsDeterministic() {
        assertEquals(defaultsText(), ConfigWriter.write(ConfigValues.defaults()));
    }

    @Test
    void defaultsSurviveWriteAndRead() {
        ConfigReader.Result result = ConfigReader.read(defaultsText());
        assertFalse(result.failed());
        assertFalse(result.hasProblems(), result.reset() + " " + result.dropped());
        assertEquals(ConfigValues.defaults(), result.values());
    }

    @Test
    void writerListsEveryKeyInSchemaOrder() {
        List<String> written = new ArrayList<>();

        for (String line : defaultsText().split("\n")) {
            String trimmed = line.trim();

            if (!trimmed.startsWith("//") && trimmed.contains(": ") && !trimmed.endsWith("{")) {
                written.add(trimmed.substring(0, trimmed.indexOf(':')));
            }
        }

        List<String> expected = new ArrayList<>();

        for (ConfigSchema.Leaf leaf : ConfigSchema.INSTANCE.leaves()) {
            expected.add(leaf.name());
        }

        assertEquals(expected, written);
    }

    @Test
    void writerFormatsNumbersAndLists() {
        String text = defaultsText();
        assertTrue(text.contains("chance: 1.0"));
        assertTrue(text.contains("castingHealth: 400.0"));
        assertTrue(text.contains("summonItems: [\"item.minecraft.debug_stick\"]"));
        assertTrue(text.contains("chancePerCheck: 0.2"));
        assertTrue(text.contains("minY: -40"));
        assertFalse(text.contains("\r"));
    }

    @Test
    void repeatedKeyCommentsAppearOnce() {
        String text = defaultsText();
        assertEquals(1, count(text, "// Set false to stop this structure from generating."));
        assertEquals(1, count(text, "// Health factor."));
        assertEquals(1, count(text, "// Chance from 0 to 1 that a placed structure generates."));
    }

    @Test
    void lenientReaderAcceptsOldCommentStyles() {
        String text = "/* header\n over lines */\n{\n  // line\n  general: { structuresOnly: true, /* inline */ bossMusic: false },\n  'chronicle': { toasts: false }\n}\n";
        ConfigReader.Result result = ConfigReader.read(text);
        assertFalse(result.failed(), result.failure());
        assertTrue(result.values().bool("general.structuresOnly"));
        assertFalse(result.values().bool("general.bossMusic"));
        assertFalse(result.values().bool("chronicle.toasts"));
    }

    @Test
    void lineCommentAfterValueKeepsNextLine() {
        ConfigReader.Result result = ConfigReader.read("{\n general: {\n structuresOnly: true // note\n , bossMusic: false\n }\n}");
        assertFalse(result.failed(), result.failure());
        assertTrue(result.values().bool("general.structuresOnly"));
        assertFalse(result.values().bool("general.bossMusic"));
    }

    @Test
    void wrongTypesOnlyResetThatKey() {
        String text = "{ general: { structuresOnly: \"yes\", bossMusic: false }, hordes: { spawnAttempts: 2.5, chancePerCheck: NaN, nightOnly: false },"
                + " corruptedChampion: { summoning: { summonItems: [1, 2], bloodEntities: [\"entity.minecraft.cow\"] } } }";
        ConfigReader.Result result = ConfigReader.read(text);
        assertFalse(result.failed(), result.failure());
        assertFalse(result.values().bool("general.structuresOnly"));
        assertFalse(result.values().bool("general.bossMusic"));
        assertEquals(40, result.values().integer("hordes.spawnAttempts"));
        assertEquals(0.2D, result.values().real("hordes.chancePerCheck"));
        assertFalse(result.values().bool("hordes.nightOnly"));
        assertEquals(List.of("item.minecraft.debug_stick"), result.values().strings("corruptedChampion.summoning.summonItems"));
        assertEquals(List.of("entity.minecraft.cow"), result.values().strings("corruptedChampion.summoning.bloodEntities"));
        assertTrue(result.reset().contains("general.structuresOnly"));
        assertTrue(result.reset().contains("hordes.chancePerCheck"));
    }

    @Test
    void limitsClampOrReplace() {
        String text = "{ structures: { altar: { chance: 5 } }, hordes: { maxDistance: 3, minDistance: 10 },"
                + " mobs: { ghoul: { spawn: { weight: 4, minGroup: 0, maxGroup: 0 }, health: -2, damage: 0 } },"
                + " corruptedChampion: { stats: { castingHealth: 0, armor: -1 }, effects: { musicVolume: 9 } } }";
        ConfigReader.Result result = ConfigReader.read(text);
        ConfigValues values = result.values();
        assertEquals(1.0D, values.real("structures.altar.chance"));
        assertEquals(10, values.integer("hordes.maxDistance"));
        assertEquals(1, values.integer("mobs.ghoul.spawn.minGroup"));
        assertEquals(1, values.integer("mobs.ghoul.spawn.maxGroup"));
        assertEquals(1.0D, values.real("mobs.ghoul.health"));
        assertEquals(0.05D, values.real("mobs.ghoul.damage"));
        assertEquals(400.0D, values.real("corruptedChampion.stats.castingHealth"));
        assertEquals(14.0D, values.real("corruptedChampion.stats.armor"));
        assertEquals(4.0D, values.real("corruptedChampion.effects.musicVolume"));
    }

    @Test
    void unknownKeysAreReported() {
        ConfigReader.Result result = ConfigReader.read("{ general: { bossMusic: true, extra: 1 }, other: {}, mobs: { ghost: { enabled: true } } }");
        assertTrue(result.dropped().contains("general.extra"));
        assertTrue(result.dropped().contains("other"));
        assertTrue(result.dropped().contains("mobs.ghost"));
    }

    @Test
    void brokenTextFailsWithoutThrowing() {
        assertTrue(ConfigReader.read("{ general: ").failed());
        assertTrue(ConfigReader.read("").failed());
        assertTrue(ConfigReader.read("[1, 2]").failed());
        assertEquals(ConfigValues.defaults(), ConfigReader.read("{{{").values());
    }

    @Test
    void missingFolderGetsDefaultFile(@TempDir Path temp) throws IOException {
        Path directory = temp.resolve("graveyard");
        ConfigValues values = ConfigFiles.load(directory, new Recorder());
        assertEquals(ConfigValues.defaults(), values);
        assertEquals(defaultsText(), Files.readString(directory.resolve(ConfigFiles.FILE_NAME)));
    }

    @Test
    void migrationNeverTouchesOldFile(@TempDir Path temp) throws IOException {
        Path old = temp.resolve(LEGACY);
        Files.writeString(old, sampleText(), StandardCharsets.UTF_8);
        byte[] before = Files.readAllBytes(old);
        long modified = Files.getLastModifiedTime(old).toMillis();
        Recorder log = new Recorder();
        ConfigValues first = ConfigFiles.load(temp, log);
        assertTrue(Files.exists(temp.resolve(ConfigFiles.FILE_NAME)));
        assertArrayEquals(before, Files.readAllBytes(old));
        assertEquals(modified, Files.getLastModifiedTime(old).toMillis());
        assertTrue(log.lines.stream().anyMatch(line -> line.contains(LEGACY)));
        Files.delete(old);
        assertEquals(first, ConfigFiles.load(temp, new Recorder()));
    }

    @Test
    void newFileWinsOverOldFile(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve(LEGACY), sampleText().replace("\"structuresOnly\": false", "\"structuresOnly\": true"), StandardCharsets.UTF_8);
        Files.writeString(temp.resolve(ConfigFiles.FILE_NAME), defaultsText(), StandardCharsets.UTF_8);
        assertFalse(ConfigFiles.load(temp, new Recorder()).bool("general.structuresOnly"));
    }

    @Test
    void brokenNewFileStaysUntouched(@TempDir Path temp) throws IOException {
        Path file = temp.resolve(ConfigFiles.FILE_NAME);
        Files.writeString(file, "{ general: { bossMusic: ", StandardCharsets.UTF_8);
        byte[] before = Files.readAllBytes(file);
        Recorder log = new Recorder();
        assertEquals(ConfigValues.defaults(), ConfigFiles.load(temp, log));
        assertArrayEquals(before, Files.readAllBytes(file));
        assertFalse(Files.exists(temp.resolve(ConfigFiles.FILE_NAME + ".bak")));
        assertTrue(log.lines.stream().anyMatch(line -> line.startsWith("error ")));
    }

    @Test
    void brokenOldFileWritesNothing(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve(LEGACY), "{ \"booleanEntries\": ", StandardCharsets.UTF_8);
        assertEquals(ConfigValues.defaults(), ConfigFiles.load(temp, new Recorder()));
        assertFalse(Files.exists(temp.resolve(ConfigFiles.FILE_NAME)));
    }

    @Test
    void partialFileKeepsUserValues(@TempDir Path temp) throws IOException {
        Path file = temp.resolve(ConfigFiles.FILE_NAME);
        Files.writeString(file, "{ general: { structuresOnly: true } }", StandardCharsets.UTF_8);
        ConfigValues values = ConfigFiles.load(temp, new Recorder());
        assertTrue(values.bool("general.structuresOnly"));
        assertFalse(Files.exists(temp.resolve(ConfigFiles.FILE_NAME + ".bak")));
        ConfigValues again = ConfigFiles.load(temp, new Recorder());
        assertEquals(values, again);
        assertTrue(Files.readString(file).contains("structuresOnly: true"));
    }

    @Test
    void unknownKeysAreDroppedWithBackup(@TempDir Path temp) throws IOException {
        Path file = temp.resolve(ConfigFiles.FILE_NAME);
        String text = "{ general: { structuresOnly: true, gone: 1 } }";
        Files.writeString(file, text, StandardCharsets.UTF_8);
        ConfigFiles.load(temp, new Recorder());
        assertEquals(text, Files.readString(temp.resolve(ConfigFiles.FILE_NAME + ".bak")));
        assertFalse(Files.readString(file).contains("gone"));
    }

    @Test
    void reformatOnlyNeedsNoBackup(@TempDir Path temp) throws IOException {
        Path file = temp.resolve(ConfigFiles.FILE_NAME);
        Files.writeString(file, defaultsText().replace("\n", "\r\n"), StandardCharsets.UTF_8);
        byte[] before = Files.readAllBytes(file);
        ConfigFiles.load(temp, new Recorder());
        assertArrayEquals(before, Files.readAllBytes(file));
        Files.writeString(file, defaultsText().replace("  ", "      "), StandardCharsets.UTF_8);
        ConfigFiles.load(temp, new Recorder());
        assertEquals(defaultsText(), Files.readString(file));
        assertFalse(Files.exists(temp.resolve(ConfigFiles.FILE_NAME + ".bak")));
    }

    @Test
    void unwritableFolderKeepsValuesInMemory(@TempDir Path temp) throws IOException {
        Path blocker = temp.resolve("blocked");
        Files.writeString(blocker, "file", StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        assertEquals(ConfigValues.defaults(), ConfigFiles.load(blocker.resolve("graveyard"), log));
        assertTrue(log.lines.stream().anyMatch(line -> line.startsWith("error ")));
    }

    @Test
    void prisonYKeysHaveTheirOwnComment() {
        String text = defaultsText();
        assertEquals(1, count(text, "// Lowest Y level of the crypt."));
        assertEquals(1, count(text, "// Lowest Y level of the prison."));
        assertEquals(1, count(text, "// Highest Y level of the prison."));
    }

    @Test
    void multiplierCommentNamesTheNegativeRule() {
        assertTrue(defaultsText().contains("A negative value resets to 1.0."));
    }

    @Test
    void contentAfterTheFirstObjectIsReportedAndBackedUp(@TempDir Path temp) throws IOException {
        Path file = temp.resolve(ConfigFiles.FILE_NAME);
        String text = "{ general: { structuresOnly: true } } { more: 1 }";
        Files.writeString(file, text, StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        assertTrue(ConfigFiles.load(temp, log).bool("general.structuresOnly"));
        assertEquals(text, Files.readString(temp.resolve(ConfigFiles.FILE_NAME + ".bak")));
        assertEquals(1, countLines(log, "after the first object"));
    }

    @Test
    void leftoverTemporaryFileIsDeleted(@TempDir Path temp) throws IOException {
        Path leftover = temp.resolve(ConfigFiles.FILE_NAME + ".tmp");
        Files.writeString(leftover, "x", StandardCharsets.UTF_8);
        ConfigFiles.load(temp, new Recorder());
        assertFalse(Files.exists(leftover));
    }

    @Test
    void brokenNewFileNamesBothFilesWhileTheOldFileExists(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve(ConfigFiles.FILE_NAME), "{ general: ", StandardCharsets.UTF_8);
        Files.writeString(temp.resolve(LEGACY), sampleText(), StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        ConfigFiles.load(temp, log);
        assertTrue(log.lines.stream().anyMatch(line -> line.contains("Delete or rename both files")), log.lines.toString());
    }

    @Test
    void brokenNewFileOnlyAsksForDeletionWithoutTheOldFile(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve(ConfigFiles.FILE_NAME), "{ general: ", StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        ConfigFiles.load(temp, log);
        assertTrue(log.lines.stream().anyMatch(line -> line.contains("delete the file " + ConfigFiles.FILE_NAME)), log.lines.toString());
        assertTrue(log.lines.stream().noneMatch(line -> line.contains("rename both")));
    }

    @Test
    void migrationListsUnknownKeysInOneLine(@TempDir Path temp) throws IOException {
        String text = sampleText().replace("\"structuresOnly\": false,", "\"structuresOnly\": false, \"madeUp\": true, \"alsoMadeUp\": false,");
        Files.writeString(temp.resolve(LEGACY), text, StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        ConfigFiles.load(temp, log);
        assertEquals(1, countLines(log, "are unknown"), log.lines.toString());
        assertTrue(log.lines.stream().anyMatch(line -> line.contains("booleanEntries.madeUp") && line.contains("booleanEntries.alsoMadeUp")));
    }

    @Test
    void migrationOfTheDefaultOldFileLogsNoProblem(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve(LEGACY), sampleText(), StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        ConfigFiles.load(temp, log);
        assertTrue(log.lines.stream().noneMatch(line -> line.startsWith("warn ") || line.startsWith("error ")), log.lines.toString());
    }

    @Test
    void rejectedOldFileLogsOneLineWithTheReason(@TempDir Path temp) throws IOException {
        Files.writeString(temp.resolve(LEGACY), "{ \"booleanEntries\": { \"structuresOnly\": 1 } }", StandardCharsets.UTF_8);
        Recorder log = new Recorder();
        assertEquals(ConfigValues.defaults(), ConfigFiles.load(temp, log));
        assertEquals(1, log.lines.size(), log.lines.toString());
        assertTrue(log.lines.get(0).contains(LEGACY));
        assertFalse(Files.exists(temp.resolve(ConfigFiles.FILE_NAME)));
    }

    private static long countLines(Recorder log, String part) {
        long count = 0;

        for (String line : log.lines) {
            if (line.contains(part)) {
                count++;
            }
        }

        return count;
    }

    private static int count(String text, String part) {
        int count = 0;
        int at = text.indexOf(part);

        while (at >= 0) {
            count++;
            at = text.indexOf(part, at + 1);
        }

        return count;
    }
}
