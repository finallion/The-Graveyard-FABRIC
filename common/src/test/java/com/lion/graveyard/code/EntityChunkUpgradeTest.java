package com.lion.graveyard.code;

import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityChunkUpgradeTest {
    private static final String MIXIN = Sources.code(ModProject.JAVA.resolve("mixin/EntityChunkUpgradeMixin.java"));

    @Test
    void mixinIsRegisteredInTheCommonConfig() {
        assertTrue(ModProject.read(ModProject.RESOURCES.resolve("graveyard-common.mixins.json")).contains("\"EntityChunkUpgradeMixin\""));
    }

    @Test
    void mixinTargetsTheEntityChunkUpgrade() {
        assertTrue(MIXIN.contains("@Mixin(SimpleRegionStorage.class)"));
        assertTrue(MIXIN.contains("upgradeChunkTag(Lnet/minecraft/nbt/CompoundTag;I)Lnet/minecraft/nbt/CompoundTag;"));
        assertTrue(MIXIN.contains("DataFixTypes.ENTITY_CHUNK"));
        assertTrue(MIXIN.contains("SharedConstants.WORLD_VERSION"));
    }

    @Test
    void mixinAppendsTheModEntitiesUnchanged() {
        assertTrue(MIXIN.contains("startsWith(\"graveyard:\")"));
        assertTrue(MIXIN.contains("upgraded.addAll(modEntities)"));
    }

    @Test
    void everyMobBaseClassReadsLegacyEntityData() {
        for (String file : new String[] {
                "HostileGraveyardEntity",
                "GraveyardMinionEntity",
                "CorvidEntity",
                "FallingCorpse",
                "LichEntity",
                "NamelessHangedEntity",
                "SkeletonCreeper",
                "TormentedVillagerEntity"
        }) {
            String source = Sources.code(ModProject.JAVA.resolve("entities/" + file + ".java"));

            assertTrue(source.contains("LegacyEntityData.read(this, nbt)"), file);
        }
    }
}
