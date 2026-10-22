package com.lion.graveyard.code;

import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobFixesTest {
    private static String code(String relative) {
        return Sources.code(ModProject.JAVA.resolve(relative));
    }

    private static String body(String relative, String method) {
        return JavaText.joinedBodies(JavaText.methods(code(relative)), method);
    }

    @Test
    void lichPhaseOneKeepsItsMeleeGoalRunning() {
        String goal = code("entities/ai/goals/LichMeleeGoal.java");
        String hold = body("entities/ai/goals/LichMeleeGoal.java", "isPhaseOneHold");

        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "canContinueToUse").contains("isPhaseOneHold(target)"));
        assertTrue(hold.contains("getPhase() == 1"));
        assertTrue(hold.contains("NO_CREATIVE_OR_SPECTATOR"));
        assertFalse(hold.contains("getSquaredMaxAttackDistance"));
        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "tick").contains("!isStraightBelowOrAbove(target)"));
        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "tick").contains("setYRot(this.mob.yHeadRot)"));
        assertTrue(goal.contains("this.mob.getNavigation().stop();"));
    }

    @Test
    void meleeGoalRechecksWhenTheTargetChangesHeight() {
        String reset = body("entities/ai/goals/AbstractGraveyardMeleeGoal.java", "isNewOrFarMoved");

        assertTrue(reset.contains("checkedTargetY"));
        assertTrue(reset.contains("dy * dy"));
    }

    @Test
    void meleeGoalSkipsTheThrottleWithinReach() {
        String canUse = body("entities/ai/goals/AbstractGraveyardMeleeGoal.java", "canUse");

        assertTrue(canUse.contains("this.isNewOrFarMoved(target) || this.isInReach(target)"));
    }

    @Test
    void meleeGoalThrottleDoesNotRecurse() {
        String canUse = body("entities/ai/goals/AbstractGraveyardMeleeGoal.java", "canUse");

        assertTrue(canUse.contains("isNewOrFarMoved"));
        assertFalse(canUse.contains("canUse()"));
        assertFalse(canUse.contains("lastUpdateTime -="));
    }

    @Test
    void staffCallKeepsTheBondAndSearchesAllLevels() {
        String call = body("item/BoneStaffItem.java", "call");

        assertFalse(call.contains("bindings.remove"));
        assertFalse(call.contains(".remove(ghoulingId)"));
        assertTrue(call.contains("entity.graveyard.ghouling.not_found"));
        assertTrue(call.indexOf("findLoaded(") >= 0);
        assertTrue(call.indexOf("findLoaded(") < call.indexOf("getLevel(binding.dimension())"));
    }

    @Test
    void staffTellsWhenTheFetchGivesUp() {
        String code = code("item/BoneStaffItem.java");
        String tick = JavaText.joinedBodies(JavaText.methods(code), "inventoryTick");

        assertTrue(tick.contains("entity.graveyard.ghouling.gave_up"));
        assertTrue(tick.contains("misses + 1 == MAX_FETCH_MISSES"));
        assertTrue(code.contains("SIT_CLICK_TICKS = 8;"));
        assertTrue(code.contains("STORE_TICKS = 30;"));
    }

    @Test
    void namelessHangedExposesItsTradesToTheVtpButtons() {
        String code = code("entities/NamelessHangedEntity.java");

        assertTrue(code.contains("implements GeoEntity, TradeControl"));
        assertTrue(body("entities/NamelessHangedEntity.java", "rerollAll").contains("updateTrades("));
        assertTrue(body("entities/NamelessHangedEntity.java", "catalogPool").contains("TRADES_REGISTRY"));
        assertTrue(body("entities/NamelessHangedEntity.java", "catalogMaxLevel").contains("return 1;"));
        assertTrue(body("entities/NamelessHangedEntity.java", "catalogPicks").contains("maxOffers()"));
        assertFalse(code.contains("supportsLevels"));
    }

    @Test
    void lichMeleeSoundStaysOutOfTheVanillaAttackSoundHook() {
        assertFalse(code("entities/LichEntity.java").contains("void playAttackSound("));
        assertTrue(body("entities/ai/goals/LichMeleeGoal.java", "attack").contains("playMeleeSwingSound()"));
    }

    @Test
    void hordeTriesSeveralSpotsForTheCaptain() {
        String file = "entities/horde/GraveyardHordeSpawner.java";
        String tick = body(file, "tick");

        assertTrue(code(file).contains("CAPTAIN_SPOTS = 10;"));
        assertTrue(tick.contains("spot < CAPTAIN_SPOTS"));
        assertTrue(tick.contains("spawnHordeEntity(world, mutable, random, true, illagerSpawn)"));
        assertTrue(body(file, "spawnHordeEntity").contains("canSpawnInDarkness"));
    }

    @Test
    void sarcophagusRenderBoxCoversBothHalves() {
        String box = body("blockentities/renders/SarcophagusBlockEntityRenderer.java", "getRenderBoundingBox");

        assertTrue(code("blockentities/renders/SarcophagusBlockEntityRenderer.java").contains("AABB getRenderBoundingBox(BlockEntity blockEntity)"));
        assertTrue(box.contains(".inflate(1.0D)"));
        assertTrue(box.contains(".expandTowards(0.0D, 1.0D, 0.0D)"));
    }

    @Test
    void minionStopsSittingPoseWhenItStopsStaying() {
        assertTrue(body("entities/GraveyardMinionEntity.java", "setSitting").contains("setInSittingPose(false)"));
    }

    @Test
    void serverComputesTheWalkAnimationForRunningMobs() {
        assertTrue(body("entities/GhoulEntity.java", "aiStep").contains("calculateEntityAnimation(false)"));
        assertTrue(body("entities/AnimatedCorruptedIllager.java", "aiStep").contains("calculateEntityAnimation(false)"));
    }

    @Test
    void wraithLetsKillAndVoidDamageThrough() {
        String body = body("entities/WraithEntity.java", "isInvulnerableTo");

        assertTrue(body.indexOf("BYPASSES_INVULNERABILITY") >= 0);
        assertTrue(body.indexOf("BYPASSES_INVULNERABILITY") < body.indexOf("BYPASSES_ARMOR"));
    }

    @Test
    void ghoulDamageLandsOnTheVisibleHitFrame() {
        assertTrue(code("entities/ai/goals/GhoulMeleeAttackGoal.java").contains("DAMAGE_START_IN_ANIM = 7;"));
    }
}
