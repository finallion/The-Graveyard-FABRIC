package com.lion.graveyard.code;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyEntityDataTest {
    private static final String SOURCE = Sources.code(ModProject.JAVA.resolve("entities/LegacyEntityData.java"));

    @Test
    void attributesAndEffectsAreReadOnlyWhenTheCurrentKeyIsMissing() {
        assertTrue(SOURCE.contains("nbt.contains(\"attributes\")"));
        assertTrue(SOURCE.contains("nbt.contains(\"active_effects\")"));
    }

    @Test
    void attributeKeysAreMappedLikeTheVanillaFixer() {
        assertTrue(SOURCE.contains("\"Name\""));
        assertTrue(SOURCE.contains("result.putString(\"id\""));
        assertTrue(SOURCE.contains("result.putDouble(\"base\""));
        assertTrue(SOURCE.contains("result.put(\"modifiers\""));
        assertTrue(SOURCE.contains("\"add_multiplied_base\""));
        assertTrue(SOURCE.contains("\"add_multiplied_total\""));
    }

    @Test
    void modifierUuidsBecomeResourceLocationIds() {
        assertTrue(SOURCE.contains("\"91AEAA56-376B-4498-935B-2F7F68070635\", \"effect.speed\""));
        assertTrue(SOURCE.contains("\"Random spawn bonus\", \"minecraft:random_spawn_bonus\""));
        assertTrue(SOURCE.contains("modifier.getIntArray(\"UUID\")") || SOURCE.contains("modifier.getIntArray(\"UUID\").orElse"));
    }

    @Test
    void effectKeysAndNumericIdsAreMappedLikeTheVanillaFixer() {
        assertTrue(SOURCE.contains("\"speed\","));
        assertTrue(SOURCE.contains("\"hero_of_the_village\","));
        assertTrue(SOURCE.contains("\"ShowParticles\", \"show_particles\""));
        assertTrue(SOURCE.contains("\"hidden_effect\""));
    }

    @Test
    void legacyKeysAreNeverWritten() {
        assertFalse(SOURCE.contains("putString(\"Name\""));
        assertFalse(SOURCE.contains("put(\"Attributes\""));
        assertFalse(SOURCE.contains("put(\"ActiveEffects\""));
    }

    @Test
    void healthIsReadAgainAfterTheAttributesAreApplied() {
        assertTrue(SOURCE.contains("mob.setHealth(nbt.getFloat(\"Health\"))"));
    }
}
