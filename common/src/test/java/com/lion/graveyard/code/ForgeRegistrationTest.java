package com.lion.graveyard.code;

import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForgeRegistrationTest {
    private static String forge(String relative) {
        return Sources.code(ModProject.FORGE_JAVA.resolve(relative));
    }

    private static String common(String relative) {
        return Sources.code(ModProject.JAVA.resolve(relative));
    }

    private static int countOf(String text, String needle) {
        int count = 0;

        for (int at = text.indexOf(needle); at >= 0; at = text.indexOf(needle, at + 1)) {
            count++;
        }

        return count;
    }

    @Test
    void forgeRegistersProcessorsThroughTheDeferredRegister() {
        String main = forge("forge/GraveyardForge.java");
        String helper = JavaText.joinedBodies(JavaText.methods(forge("platform/forge/RegistryHelperImpl.java")), "registerStructureProcessor");

        assertFalse(main.contains("Registry.register("));
        assertTrue(helper.contains("STRUCTURE_PROCESSORS.register"));
        assertTrue(main.contains("STRUCTURE_PROCESSORS.register(bus)"));
    }

    @Test
    void fabricRegistersCriteriaImmediately() {
        String body = JavaText.joinedBodies(JavaText.methods(Sources.code(ModProject.FABRIC_JAVA.resolve("platform/fabric/RegistryHelperImpl.java"))), "registerCriterion");

        assertTrue(body.contains("CriteriaTriggers.register("));
    }

    @Test
    void criteriaRegisterThroughThePlatformHelper() {
        String code = common("init/TGAdvancements.java");

        assertFalse(code.contains("CriteriaTriggers.register"));
        assertEquals(20, countOf(code, "trigger(Graveyard.id(" + (char) 34));
        assertTrue(forge("forge/GraveyardForge.java").contains("CRITERIA.forEach(CriteriaTriggers::register)"));
    }

    @Test
    void forgeSpawnGateRunsLast() {
        String main = forge("forge/GraveyardForge.java");

        assertTrue(main.contains("MobSpawnEvent.PositionCheck"));
        assertTrue(main.contains("MobSpawnEvent.SpawnPlacementCheck"));
        assertTrue(main.contains("EventPriority.LOWEST, GraveyardForge::gatePositionCheck"));
        assertTrue(main.contains("EventPriority.LOWEST, GraveyardForge::gateSpawnPlacement"));
        assertTrue(main.contains("MobSpawnGate.blocks("));
        assertTrue(main.contains("MobSpawnGate.inForbiddenBiome("));
        assertFalse(main.contains("Result.ALLOW"));
    }

    @Test
    void scarecrowScanSkipsEmptySections() {
        String code = common("util/Scarecrows.java");

        assertTrue(code.contains("maybeHas("));
        assertTrue(code.contains("getChunkNow("));
        assertFalse(code.contains("betweenClosed("));
        assertTrue(code.contains("RADIUS = 8;"));
    }
}
