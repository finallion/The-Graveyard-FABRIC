package com.lion.graveyard.compat;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.RegistryIds;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrySnapshotTest {

    static Set<String> missing(Set<String> expected, Set<String> actual) {
        Set<String> result = new TreeSet<>(expected);
        result.removeAll(actual);
        return result;
    }

    @Test
    void registrationsAreFound() {
        assertTrue(RegistryIds.of(RegistryIds.BLOCK).size() > 100);
        assertTrue(RegistryIds.of(RegistryIds.ITEM).size() > 150);
        assertTrue(RegistryIds.of(RegistryIds.ENTITY).size() > 10);
        assertTrue(RegistryIds.of(RegistryIds.BLOCK_ENTITY).size() >= 5);
        assertTrue(RegistryIds.of(RegistryIds.SOUND).size() > 50);
    }

    @TestFactory
    Stream<DynamicTest> everySnapshotIdIsStillRegistered() {
        Set<String> current = RegistryIds.current();
        return ModProject.snapshotLines("registry-ids.txt").stream()
                .map(entry ->
                        DynamicTest.dynamicTest(entry, () -> assertTrue(current.contains(entry), "Registered id vanished: " + entry)));
    }

    @TestFactory
    Stream<DynamicTest> everyLegacyIdIsStillRegistered() {
        Set<String> current = RegistryIds.current();
        return ModProject.snapshotLines("registry-legacy-ids.txt").stream()
                .map(entry ->
                        DynamicTest.dynamicTest(entry, () -> assertTrue(current.contains(entry), "Id from the multiloader rebuild vanished: " + entry)));
    }

    @Test
    void knownRemovedLegacyIdsStayListedUntilRegisteredAgain() {
        Set<String> stale = new TreeSet<>(ModProject.snapshotLines("registry-legacy-removed.txt"));
        stale.retainAll(RegistryIds.current());
        assertTrue(stale.isEmpty(), "Registered again, move into registry-legacy-ids.txt: " + stale);
    }

    @Test
    void snapshotCheckReportsRemovedId() {
        Set<String> expected = Set.of("block:a", "block:b");
        assertEquals(Set.of("block:b"), missing(expected, Set.of("block:a", "block:c")));
        assertFalse(missing(expected, Set.of("block:a")).isEmpty());
    }

    @Test
    void extractorReadsRegistrationCallsAndIgnoresComments() {
        String code = com.lion.graveyard.support.Sources.stripComments(
                "// RegistryHelper.registerBlock(\"old\", x)\nRegistryHelper.registerBlock(\"live\", () -> b);\nRegistryHelper.registerItem( \"spaced\", y);");
        assertEquals(Set.of("block:live", "item:spaced"), RegistryIds.extract(code, false));
    }
}
