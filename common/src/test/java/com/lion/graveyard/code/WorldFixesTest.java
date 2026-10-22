package com.lion.graveyard.code;

import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldFixesTest {
    private static String code(Path root, String relative) {
        return Sources.code(root.resolve(relative));
    }

    private static String body(String relative, String method) {
        return JavaText.joinedBodies(JavaText.methods(code(ModProject.JAVA, relative)), method);
    }

    @Test
    void namelessHangedKeepsOffersWhileTrading() {
        String tick = body("entities/NamelessHangedEntity.java", "tick");

        assertTrue(tick.contains("isBrightOutside() && this.offers != null && !this.isTrading()"));
    }

    @Test
    void hordeSpawnerGatesOnDifficultyAndGameRule() {
        for (Path root : new Path[] {ModProject.FABRIC_JAVA, ModProject.NEOFORGE_JAVA}) {
            String code = code(root, "platform/" + (root == ModProject.FABRIC_JAVA ? "fabric" : "neoforge") + "/HordeSpawner.java");

            assertTrue(code.contains("monstersAllowed("));
            assertFalse(code.contains(", true, true)"));
        }
    }

    @Test
    void sarcophagusWraithObeysTheSpawnGate() {
        String spawn = body("blocks/SarcophagusBlock.java", "spawnGhost");

        assertTrue(spawn.contains("MobSpawnGate.blocks("));
        assertTrue(spawn.contains("if (allowed)"));
    }

    @Test
    void lichReleasesItsAltarOnRemoval() {
        String lich = code(ModProject.JAVA, "entities/LichEntity.java");

        assertTrue(lich.contains("public void remove(RemovalReason"));
        assertTrue(body("entities/LichEntity.java", "remove").contains("releaseAltar()"));
        assertTrue(lich.contains("shouldDestroy()"));
        assertTrue(code(ModProject.JAVA, "blocks/AltarBlock.java").contains("hasLivingLich"));
    }

    @Test
    void lichWithoutSavedHomeBelongsToEveryAltar() {
        String lich = code(ModProject.JAVA, "entities/LichEntity.java");

        assertTrue(lich.contains("private boolean homeKnown;"));
        assertTrue(body("entities/LichEntity.java", "onSummoned").contains("this.homeKnown = true;"));
        assertTrue(body("entities/LichEntity.java", "readAdditionalSaveData").contains("this.homeKnown = true;"));
        assertTrue(body("entities/LichEntity.java", "addAdditionalSaveData").contains("homePos != null && this.homeKnown"));
        assertFalse(lich.contains("homeKnown = false;"));
        assertTrue(body("blocks/AltarBlock.java", "hasLivingLich").contains("!lich.isHomeKnown()"));
    }

    @Test
    void hordeSpawnerRespectsTheServerMonsterSetting() {
        String allowed = body("entities/horde/GraveyardHordeSpawner.java", "monstersAllowed");

        assertTrue(allowed.contains("isSpawningMonsters()"));
    }

    @Test
    void tormentedVillagerPlacesNoLight() {
        String code = code(ModProject.JAVA, "entities/TormentedVillagerEntity.java");

        assertFalse(code.contains("carryLight"));
        assertFalse(code.contains("Blocks.LIGHT.defaultBlockState()"));
        assertTrue(code.contains("\"LightPos\""));
        assertTrue(code.contains("isLoaded("));
    }

    @Test
    void switchSpawnerPatchesTheReturnedInfo() {
        String code = code(ModProject.JAVA, "world/processors/SwitchSpawnerProcessor.java");

        assertFalse(code.contains("getBlockEntity"));
        assertTrue(code.contains("nbt.copy()"));
        assertTrue(code.contains("SpawnPotentials"));
    }

    @Test
    void namelessHangedTradeEntryFailureIsContained() {
        String parse = body("trades/NamelessHangedTrades.java", "parse");

        assertTrue(parse.contains("TradeOfferManager.deserializeTrade("));
        assertTrue(parse.contains("catch (RuntimeException"));
        assertTrue(parse.contains("return null;"));
    }
}
