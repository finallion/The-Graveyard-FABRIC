package com.lion.graveyard.compat;

import com.lion.graveyard.config.ConfigSchema;
import com.lion.graveyard.config.LegacyKeys;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigCompatibilityTest {
    private static final Path CONFIG_DIR = ModProject.JAVA.resolve("config");
    private static final Pattern PUBLIC_FIELD = Pattern.compile("public\\s+(?:final\\s+)?[\\w<>, ]+\\s+(\\w+)\\s*(?:=|;)");
    private static final Pattern MANAGED_MOBS = Pattern.compile("MANAGED_MOBS\\s*=\\s*List\\.of\\(([^)]*)\\)");
    private static final Set<String> REMOVED_FIELDS = Set.of("field:GraveyardConfig:mobSpawnEnabled", "field:GraveyardConfig:particleConfigEntries");

    private static List<String> legacyPaths() {
        return new ArrayList<>(ModProject.snapshotLines("config-legacy-paths.txt"));
    }

    @Test
    void legacyListHas255Keys() {
        assertEquals(255, legacyPaths().size());
    }

    @TestFactory
    Stream<DynamicTest> everyLegacyKeyIsMigrated() {
        Set<String> targets = new HashSet<>(ConfigSchema.INSTANCE.leafPaths());
        Set<String> mapped = new HashSet<>();

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            mapped.add(entry.oldPath());
            assertTrue(entry.rule() == LegacyKeys.Rule.SKIP || targets.contains(entry.newPath()), "Target missing in the schema: " + entry.newPath());
        }

        return legacyPaths().stream()
                .map(path -> DynamicTest.dynamicTest(path, () -> assertTrue(mapped.contains(path), "Old key without a target: " + path)));
    }

    @Test
    void mappingHasNoExtraOldKeys() {
        Set<String> listed = new HashSet<>(legacyPaths());
        List<String> extra = new ArrayList<>();

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            if (!listed.contains(entry.oldPath())) {
                extra.add(entry.oldPath());
            }
        }

        assertTrue(extra.isEmpty(), "Mapped keys that no 3.0 file holds: " + extra);
        assertEquals(255, LegacyKeys.entries().size());
    }

    @Test
    void everyLeafIsReachedFromAnOldKey() {
        Set<String> reached = new HashSet<>();

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            if (entry.rule() != LegacyKeys.Rule.SKIP) {
                reached.add(entry.newPath());
            }
        }

        assertEquals(new TreeSet<>(ConfigSchema.INSTANCE.leafPaths()), new TreeSet<>(reached));
    }

    @TestFactory
    Stream<DynamicTest> frozenDefaultKeysStillMapToAnOldKey() {
        List<String> legacy = legacyPaths();
        return ModProject.snapshotLines("config-keys.txt").stream()
                .filter(line -> line.startsWith("default:"))
                .map(line -> DynamicTest.dynamicTest(line, () -> {
                    String[] parts = line.split(":");
                    String path = parts[1] + "." + parts[2];
                    boolean found = legacy.stream().anyMatch(old -> old.equals(path) || old.startsWith(path + "."));
                    assertTrue(found, "No old key behind " + line);
                }));
    }

    @TestFactory
    Stream<DynamicTest> schemaPathsNeverVanish() {
        Set<String> current = new HashSet<>(ConfigSchema.INSTANCE.leafPaths());
        return ModProject.snapshotLines("config-schema.txt").stream()
                .map(line -> DynamicTest.dynamicTest(line, () -> assertTrue(current.contains(line), "Config key vanished: " + line)));
    }

    @Test
    void schemaHas240Keys() {
        assertEquals(240, ConfigSchema.INSTANCE.leafPaths().size());
        assertEquals(240, ModProject.snapshotLines("config-schema.txt").size());
    }

    @TestFactory
    Stream<DynamicTest> entryFieldsStayAvailable() {
        Set<String> current = new TreeSet<>();

        for (Path file : ModProject.files(CONFIG_DIR, ".java")) {
            String name = file.getFileName().toString().replace(".java", "");

            if (name.equals("GraveyardConfig") || file.getParent().getFileName().toString().equals("entries")) {
                Matcher field = PUBLIC_FIELD.matcher(Sources.code(file));

                while (field.find()) {
                    current.add("field:" + name + ":" + field.group(1));
                }
            }
        }

        return ModProject.snapshotLines("config-keys.txt").stream()
                .filter(line -> line.startsWith("field:"))
                .filter(line -> !REMOVED_FIELDS.contains(line))
                .map(line -> DynamicTest.dynamicTest(line, () -> assertTrue(current.contains(line), "Config field vanished: " + line)));
    }

    @Test
    void mobIdsMatchTheSpawnGate() {
        String source = ModProject.read(ModProject.JAVA.resolve("entities/MobSpawnGate.java"));
        Matcher matcher = MANAGED_MOBS.matcher(source);
        assertTrue(matcher.find());
        List<String> gate = new ArrayList<>();

        for (String part : matcher.group(1).split(",")) {
            gate.add(part.trim().replace("\"", ""));
        }

        assertEquals(new TreeSet<>(gate), new TreeSet<>(ConfigSchema.ALL_MOB_IDS));
        assertEquals(15, ConfigSchema.ALL_MOB_IDS.size());
    }

    @Test
    void commentsAreOneLineEnglish() {
        for (ConfigSchema.Leaf leaf : ConfigSchema.INSTANCE.leaves()) {
            String comment = leaf.comment();
            assertFalse(comment.isBlank(), "Empty comment at " + leaf.path());
            assertTrue(comment.length() <= 130, "Comment too long at " + leaf.path());
            assertFalse(comment.contains("\n") || comment.contains("—"), "Bad comment at " + leaf.path());
        }

    }

    @Test
    void noOmegaConfigLeft() {
        for (String name : List.of("OmegaConfig.java", "Config.java", "annotations/Description.java")) {
            assertFalse(Files.exists(CONFIG_DIR.resolve(name)), name + " must not exist");
        }

        List<Path> roots = List.of(ModProject.JAVA, ModProject.FABRIC_JAVA, ModProject.NEOFORGE_JAVA);

        for (Path root : roots) {
            for (Path file : ModProject.files(root, ".java")) {
                String code = Sources.code(file);
                assertFalse(code.contains("OmegaConfig"), ModProject.display(file));
                assertFalse(code.contains("config.Config;"), ModProject.display(file));
                assertFalse(code.contains("annotations.Description"), ModProject.display(file));
            }
        }
    }

    @Test
    void facadeKeepsCallSiteApi() {
        String code = Sources.code(CONFIG_DIR.resolve("GraveyardConfig.java"));
        List<String> members = List.of(
                "structureConfigEntries",
                "mobConfigEntries",
                "hordeConfigEntries",
                "corruptedChampionConfigEntries",
                "booleanEntries",
                "numberEntries",
                "boolean enabled(Identifier",
                "double structureChance(",
                "boolean flag(",
                "boolean structuresOnly(",
                "boolean mobMayAppear(",
                "double number(",
                "MobConfigEntry mobEntry(",
                "boolean fogSpawn(",
                "StructureConfigEntry getStructure(",
                "ParticleConfigEntry getParticle(",
                "HordeConfigEntry getHorde("
        );

        for (String member : members) {
            assertTrue(code.contains(member), "Facade lost " + member);
        }
    }

    @Test
    void deleteOldConfigsKeepsCurrentFiles() {
        String code = ModProject.read(ModProject.FABRIC_JAVA.resolve("platform/fabric/ServerEvents.java"));
        assertFalse(code.contains("3.0-config"));
        assertFalse(code.contains("graveyard.json5"));
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
