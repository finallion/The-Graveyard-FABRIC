package com.lion.graveyard.code;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityChunkUpgradeTest {
    private static final String MIXIN = Sources.code(ModProject.JAVA.resolve("mixin/EntityChunkUpgradeMixin.java"));
    private static final String HELPER = Sources.code(ModProject.JAVA.resolve("util/EntityChunkUpgrade.java"));
    private static final String MOONRISE = Sources.code(ModProject.JAVA.resolve("mixin/MoonriseEntityChunkMixin.java"));

    @Test
    void mixinIsRegisteredInTheCommonConfig() {
        assertTrue(ModProject.read(ModProject.RESOURCES.resolve("graveyard-common.mixins.json")).contains("\"EntityChunkUpgradeMixin\""));
    }

    @Test
    void mixinTargetsTheEntityChunkUpgrade() {
        assertTrue(MIXIN.contains("@Mixin(SimpleRegionStorage.class)"));
        assertTrue(MIXIN.contains("upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;"));
        assertTrue(MIXIN.contains("DataFixTypes.ENTITY_CHUNK"));
        assertTrue(HELPER.contains("SharedConstants.WORLD_VERSION"));
    }

    @Test
    void mixinAppendsTheModEntitiesUnchanged() {
        assertTrue(HELPER.contains("startsWith(\"graveyard:\")"));
        assertTrue(HELPER.contains("upgraded.addAll(modEntities)"));
        assertTrue(MIXIN.contains("EntityChunkUpgrade.upgradeWithoutGraveyardEntities"));
    }

    @Test
    void moonriseMixinIsOptionalAndRegistered() {
        String moonriseConfig = ModProject.read(ModProject.RESOURCES.resolve("graveyard-moonrise.mixins.json"));
        assertTrue(moonriseConfig.contains("\"MoonriseEntityChunkMixin\""));
        assertTrue(moonriseConfig.contains("\"required\": false"));
        assertFalse(ModProject.read(ModProject.RESOURCES.resolve("graveyard-common.mixins.json")).contains("Moonrise"));
        assertTrue(MOONRISE.contains("@Pseudo"));
        assertTrue(MOONRISE.contains("ca.spottedleaf.moonrise.patches.chunk_system.ChunkSystemConverters"));
        assertTrue(MOONRISE.contains("method = \"convertEntityChunkCompoundTag\""));
        assertTrue(MOONRISE.contains("require = 0"));
        assertTrue(MOONRISE.contains("EntityChunkUpgrade.upgradeWithoutGraveyardEntities"));
    }
}
