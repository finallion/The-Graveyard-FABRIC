package com.lion.graveyard.compat.legacy;

import com.google.gson.JsonObject;
import com.lion.graveyard.config.ConfigReader;
import com.lion.graveyard.config.ConfigValues;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.config.LegacyKeys;
import com.lion.graveyard.config.LegacyMigration;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyOracleTest {
    private static final Map<String, String> ACCESSORS = Map.of(
            "chance", "spawnChance",
            "chancePerCheck", "chance",
            "minDistance", "nearDistance",
            "maxDistance", "farDistance",
            "nightOnly", "onlyAtNight",
            "healthMultiplier", "health",
            "damageMultiplier", "damage",
            "speedMultiplier", "speed"
    );

    private static Path file;

    @BeforeAll
    static void createFile() throws IOException {
        file = Files.createTempFile("legacy-oracle", ".json5");
    }

    @AfterAll
    static void deleteFile() throws IOException {
        Files.deleteIfExists(file);
    }

    private static HeadOmega.Outcome head(String text) {
        try {
            Files.writeString(file, text, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }

        return HeadOmega.read(file);
    }

    private static Object normalized(Object value) {
        return value instanceof Number number ? (Object) number.doubleValue() : value;
    }

    private static Object reflect(Object entry, String name) {
        try {
            Method method = entry.getClass().getMethod(ACCESSORS.getOrDefault(name, name));
            return method.invoke(entry);
        } catch (NoSuchMethodException exception) {
            try {
                Field field = entry.getClass().getField(name);
                return field.get(entry);
            } catch (ReflectiveOperationException inner) {
                throw new IllegalStateException(inner);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static Object entryOf(Object config, String root, String id) {
        try {
            Map<?, ?> map = (Map<?, ?>) config.getClass().getField(root).get(config);
            return map.get(id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static int minOffers(double stored) {
        return Math.max(1, (int) stored);
    }

    private static int maxOffers(double min, double max) {
        return Math.max(minOffers(min), (int) max);
    }

    private static Object numberKey(String key, Map<String, Double> numbers) {
        return switch (key) {
            case "namelessHangedMinOffers" -> minOffers(numbers.get(key));
            case "namelessHangedMaxOffers" -> maxOffers(numbers.get("namelessHangedMinOffers"), numbers.get(key));
            case "skeletonCreeperExplosionRadius" -> Double.isFinite(numbers.get(key)) ? (float) Math.max(0.0D, Math.min(16.0D, numbers.get(key))) : 3.5F;
            default -> Math.max(0.0D, Math.min(1.0D, numbers.get(key)));
        };
    }

    private static Object headValue(HeadConfig config, LegacyKeys.Entry entry) {
        String[] part = entry.oldPath().split("\\.");

        if (entry.rule() == LegacyKeys.Rule.MOB_SWITCH) {
            return config.mobMayAppear(part[1]);
        }

        return switch (part[0]) {
            case "booleanEntries" -> config.booleanEntries.get(part[1]);
            case "numberEntries" -> numberKey(part[1], config.numberEntries);
            default -> reflect(entryOf(config, part[0], part[1]), part[2]);
        };
    }

    private static Object gameValue(GraveyardConfig config, LegacyKeys.Entry entry) {
        String[] part = entry.oldPath().split("\\.");

        if (entry.rule() == LegacyKeys.Rule.MOB_SWITCH) {
            return config.mobMayAppear(part[1]);
        }

        return switch (part[0]) {
            case "booleanEntries" -> config.booleanEntries.get(part[1]);
            case "numberEntries" -> numberKey(part[1], config.numberEntries);
            case "particleConfigEntries" -> reflect(config.getParticle(Identifier.fromNamespaceAndPath("graveyard", part[1])), part[2]);
            default -> reflect(entryOf(config, part[0], part[1]), part[2]);
        };
    }

    private static List<String> differences(String text) {
        HeadOmega.Outcome expected = head(text);
        ConfigReader.Result actual = LegacyMigration.migrate(text);
        List<String> found = new ArrayList<>();

        if (expected.rejected() != actual.failed()) {
            found.add("rejected: oracle " + expected.rejected() + " (" + expected.rejection() + "), migration " + actual.failed() + " (" + actual.failure() + ")");
        }

        GraveyardConfig game = new GraveyardConfig(actual.values());

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            if (entry.rule() == LegacyKeys.Rule.SKIP) {
                continue;
            }

            Object wanted = normalized(headValue(expected.config(), entry));
            Object got = normalized(gameValue(game, entry));

            if (!java.util.Objects.equals(wanted, got)) {
                found.add(entry.oldPath() + ": oracle " + wanted + ", migration " + got);
            }
        }

        return found;
    }

    private static void check(String name, String text) {
        List<String> found = differences(text);
        assertTrue(found.isEmpty(), name + "\n" + String.join("\n", found.subList(0, Math.min(found.size(), 8))));
    }

    @TestFactory
    Stream<DynamicTest> defaultFileKeyByKey() {
        return keyByKey(LegacyCorpus.sample());
    }

    @TestFactory
    Stream<DynamicTest> changedFileKeyByKey() {
        return keyByKey(LegacyCorpus.changed());
    }

    private static Stream<DynamicTest> keyByKey(JsonObject root) {
        String text = root.toString();
        HeadOmega.Outcome expected = head(text);
        GraveyardConfig game = new GraveyardConfig(LegacyMigration.migrate(text).values());
        assertFalse(expected.rejected(), expected.rejection());

        return LegacyKeys.entries().stream()
                .filter(entry -> entry.rule() != LegacyKeys.Rule.SKIP)
                .map(entry -> DynamicTest.dynamicTest(
                        entry.oldPath(),
                        () -> assertEquals(normalized(headValue(expected.config(), entry)), normalized(gameValue(game, entry)), entry.oldPath())
                ));
    }

    @TestFactory
    Stream<DynamicTest> handEditedFiles() {
        return LegacyCorpus.handEdited().stream()
                .map(item -> DynamicTest.dynamicTest(item.name(), () -> check(item.name(), item.text())));
    }

    @TestFactory
    Stream<DynamicTest> generatedValueVariants() {
        return LegacyCorpus.generated().stream()
                .map(item -> DynamicTest.dynamicTest(item.name(), () -> check(item.name(), item.text())));
    }

    @Test
    void corpusIsLargeEnough() {
        assertTrue(LegacyCorpus.handEdited().size() >= 25);
        assertEquals(255, LegacyKeys.entries().size());
    }

    @Test
    void corpusHoldsRejectedAndAcceptedFiles() {
        int rejected = 0;
        List<LegacyCorpus.Case> cases = LegacyCorpus.handEdited();

        for (LegacyCorpus.Case item : cases) {
            if (head(item.text()).rejected()) {
                rejected++;
            }
        }

        assertTrue(rejected >= 10, "rejected: " + rejected);
        assertTrue(rejected < cases.size() - 10);
    }

    @Test
    void rejectedFileGivesDefaults() {
        ConfigReader.Result result = LegacyMigration.migrate("{ \"booleanEntries\": ");
        assertTrue(result.failed());
        assertEquals(ConfigValues.defaults(), result.values());
    }
}
