package com.lion.graveyard.compat;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lion.graveyard.compat.legacy.LegacyCorpus;
import com.lion.graveyard.config.ConfigReader;
import com.lion.graveyard.config.ConfigSchema;
import com.lion.graveyard.config.ConfigValues;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.config.LegacyKeys;
import com.lion.graveyard.config.LegacyMigration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMigrationTest {
    @Test
    void sampleHoldsAll255Keys() {
        assertEquals(255, LegacyKeys.entries().size());

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            assertNotNull(LegacyCorpus.find(LegacyCorpus.sample(), entry.oldPath()), entry.oldPath());
        }
    }

    @Test
    void defaultOldFileEqualsDefaults() {
        ConfigReader.Result result = LegacyMigration.migrate(LegacyCorpus.sampleText());
        assertFalse(result.failed());
        assertFalse(result.hasProblems(), result.reset() + " " + result.dropped());
        assertEquals(ConfigValues.defaults(), result.values());
    }

    @Test
    void changedFileDiffersFromDefaults() {
        ConfigValues values = LegacyMigration.migrate(LegacyCorpus.changed().toString()).values();
        List<String> same = new ArrayList<>();

        for (ConfigSchema.Leaf leaf : ConfigSchema.INSTANCE.leaves()) {
            if (leaf.defaultValue().equals(values.get(leaf.path()))) {
                same.add(leaf.path());
            }
        }

        assertEquals(List.of("mobs.reaper.enabled"), same);
    }

    @Test
    void witherSkeletonSpawnerKeepsItsPolarity() {
        JsonObject old = LegacyCorpus.sample();
        LegacyCorpus.put(old, "booleanEntries.disableWitherSkeletonSpawner", new JsonPrimitive(true));
        ConfigValues values = LegacyMigration.migrate(old.toString()).values();
        assertTrue(values.bool("behaviour.replaceWitherSkeletonSpawner"));
        assertTrue(new GraveyardConfig(values).flag("disableWitherSkeletonSpawner", false));
    }

    @Test
    void tormentedVillagerLightIsSkippedWithoutALogLine() {
        JsonObject old = LegacyCorpus.sample();
        LegacyCorpus.put(old, "booleanEntries.tormentedVillagerLight", new JsonPrimitive(false));
        ConfigReader.Result result = LegacyMigration.migrate(old.toString());
        assertFalse(result.failed());
        assertFalse(result.hasProblems(), result.reset() + " " + result.dropped());
        assertEquals(ConfigValues.defaults(), result.values());
    }

    @Test
    void unknownOldKeysAreListed() {
        JsonObject old = LegacyCorpus.sample();
        LegacyCorpus.put(old, "somethingNew", new JsonPrimitive(1));
        LegacyCorpus.put(old, "mobConfigEntries.made_up.enabled", new JsonPrimitive(false));
        LegacyCorpus.put(old, "structureConfigEntries.altar.extraField", new JsonPrimitive(5));
        LegacyCorpus.put(old, "booleanEntries.madeUp", new JsonPrimitive(true));
        ConfigReader.Result result = LegacyMigration.migrate(old.toString());
        assertEquals(
                Set.of("somethingNew", "mobConfigEntries.made_up", "structureConfigEntries.altar.extraField", "booleanEntries.madeUp"),
                new HashSet<>(result.dropped())
        );
    }

    @Test
    void limitsReportTheOldPaths() {
        JsonObject old = LegacyCorpus.sample();
        LegacyCorpus.put(old, "mobConfigEntries.revenant.minGroup", new JsonPrimitive(0));
        LegacyCorpus.put(old, "hordeConfigEntries.horde_spawn.minDistance", new JsonPrimitive(60));
        LegacyCorpus.put(old, "numberEntries.namelessHangedMinOffers", new JsonPrimitive(12));
        ConfigReader.Result result = LegacyMigration.migrate(old.toString());
        assertTrue(result.reset().contains("mobConfigEntries.revenant.minGroup"), result.reset().toString());
        assertTrue(result.reset().contains("hordeConfigEntries.horde_spawn.maxDistance"), result.reset().toString());
        assertTrue(result.reset().contains("numberEntries.namelessHangedMaxOffers"), result.reset().toString());

        for (String path : result.reset()) {
            assertFalse(path.startsWith("mobs.") || path.startsWith("hordes."), path);
        }
    }

    @Test
    void brokenOldTextFails() {
        assertTrue(LegacyMigration.migrate("{ \"booleanEntries\": ").failed());
        assertTrue(LegacyMigration.migrate("[]").failed());
    }
}
