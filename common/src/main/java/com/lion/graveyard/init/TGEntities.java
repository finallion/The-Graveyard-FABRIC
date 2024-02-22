package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.entities.*;
import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.lion.graveyard.platform.BiomeModifications;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

public class TGEntities {

    private TGEntities() {
    }

    public static final Supplier<EntityType<ReaperEntity>> REAPER = RegistryHelper.registerEntityType("reaper", () -> EntityType.Builder.of(ReaperEntity::new, MobCategory.MONSTER).sized(0.5F, 1.4F).build(Graveyard.createStringID("reaper")));
    public static final Supplier<EntityType<AcolyteEntity>> ACOLYTE = RegistryHelper.registerEntityType("acolyte", () -> EntityType.Builder.of(AcolyteEntity::new, MobCategory.MONSTER).sized(0.6F, 1.9F).build(Graveyard.createStringID("acolyte")));
    public static final Supplier<EntityType<CorruptedVindicator>> CORRUPTED_VINDICATOR = RegistryHelper.registerEntityType("corrupted_vindicator", () -> EntityType.Builder.of(CorruptedVindicator::new, MobCategory.MONSTER).sized(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_vindicator")));
    public static final Supplier<EntityType<CorruptedPillager>> CORRUPTED_PILLAGER = RegistryHelper.registerEntityType("corrupted_pillager", () -> EntityType.Builder.of(CorruptedPillager::new, MobCategory.MONSTER).sized(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_pillager")));
    public static final Supplier<EntityType<SkeletonCreeper>> SKELETON_CREEPER = RegistryHelper.registerEntityType("skeleton_creeper", () -> EntityType.Builder.of(SkeletonCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).build(Graveyard.createStringID("skeleton_creeper")));
    public static final Supplier<EntityType<FallingCorpse>> FALLING_CORPSE = RegistryHelper.registerEntityType("falling_corpse", () -> EntityType.Builder.of(FallingCorpse::new, MobCategory.MISC).sized(0.4F, 0.5F).build(Graveyard.createStringID("falling_corpse")));
    public static final Supplier<EntityType<GhoulEntity>> GHOUL = RegistryHelper.registerEntityType("ghoul", () -> EntityType.Builder.of(GhoulEntity::new, MobCategory.MONSTER).sized(0.8F, 2.1F).build(Graveyard.createStringID("ghoul")));
    public static final Supplier<EntityType<RevenantEntity>> REVENANT = RegistryHelper.registerEntityType("revenant", () -> EntityType.Builder.of(RevenantEntity::new, MobCategory.MONSTER).sized(0.6F, 1.9F).build(Graveyard.createStringID("revenant")));
    public static final Supplier<EntityType<WraithEntity>> WRAITH = RegistryHelper.registerEntityType("wraith", () -> EntityType.Builder.of(WraithEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).build(Graveyard.createStringID("wraith")));
    public static final Supplier<EntityType<NightmareEntity>> NIGHTMARE = RegistryHelper.registerEntityType("nightmare", () -> EntityType.Builder.of(NightmareEntity::new, MobCategory.MONSTER).sized(0.6F, 2.6F).build(Graveyard.createStringID("nightmare")));
    public static final Supplier<EntityType<GhoulingEntity>> GHOULING = RegistryHelper.registerEntityType("ghouling", () -> EntityType.Builder.of(GhoulingEntity::new, MobCategory.CREATURE).sized(0.7F, 2.0F).build(Graveyard.createStringID("ghouling")));
    public static final Supplier<EntityType<LichEntity>> LICH = RegistryHelper.registerEntityType("lich", () -> EntityType.Builder.of(LichEntity::new, MobCategory.MONSTER).sized(0.9F, 4.0F).build(Graveyard.createStringID("lich")));
    public static final Supplier<EntityType<SkullEntity>> SKULL = RegistryHelper.registerEntityType("skull", () -> EntityType.Builder.<SkullEntity>of(SkullEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(Graveyard.createStringID("skull")));
    public static final Supplier<EntityType<NamelessHangedEntity>> NAMELESS_HANGED = RegistryHelper.registerEntityType("nameless_hanged", () -> EntityType.Builder.of(NamelessHangedEntity::new, MobCategory.MONSTER).sized(0.8F, 2.5F).build(Graveyard.createStringID("nameless_hanged")));


    public static void init() {
        createMobAttributes();
    }

    public static void postInit() {
        addSpawnRestrictions();
        addSpawns();
    }

    private static void addSpawns() {
        GraveyardConfig config = Graveyard.getConfig();

        BiomeModifications.addMobSpawn(TGTags.GHOUL_SPAWNS,
                MobCategory.MONSTER, TGEntities.GHOUL.get(),
                config.mobConfigEntries.get("ghoul").weight,
                config.mobConfigEntries.get("ghoul").minGroup,
                config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.REVENANT_SPAWNS,
                MobCategory.MONSTER, TGEntities.REVENANT.get(),
                config.mobConfigEntries.get("revenant").weight,
                config.mobConfigEntries.get("revenant").minGroup,
                config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.REAPER_SPAWNS,
                MobCategory.MONSTER, TGEntities.REAPER.get(),
                config.mobConfigEntries.get("reaper").weight,
                config.mobConfigEntries.get("reaper").minGroup,
                config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.NIGHTMARE_SPAWNS,
                MobCategory.MONSTER, TGEntities.NIGHTMARE.get(),
                config.mobConfigEntries.get("nightmare").weight,
                config.mobConfigEntries.get("nightmare").minGroup,
                config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.SKELETON_CREEPER_SPAWNS,
                MobCategory.MONSTER, TGEntities.SKELETON_CREEPER.get(),
                config.mobConfigEntries.get("skeleton_creeper").weight,
                config.mobConfigEntries.get("skeleton_creeper").minGroup,
                config.mobConfigEntries.get("skeleton_creeper").maxGroup);
    }

    public static void addSpawnRestrictions() {
        SpawnPlacements.register(REAPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(ACOLYTE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(CORRUPTED_VINDICATOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(CORRUPTED_PILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(SKELETON_CREEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(FALLING_CORPSE.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(GHOUL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(REVENANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(NIGHTMARE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(LICH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
    }

    public static void createMobAttributes() {
        RegistryHelper.registerEntityAttribute(TGEntities.REAPER, ReaperEntity::createReaperAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.ACOLYTE, CorruptedIllager::createCorruptedIllagerAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.CORRUPTED_VINDICATOR, CorruptedIllager::createCorruptedIllagerAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.CORRUPTED_PILLAGER, CorruptedPillager::createCorruptedPillagerAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.SKELETON_CREEPER, SkeletonCreeper::createSkeletonCreeperAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.FALLING_CORPSE, FallingCorpse::createFallingCorpseAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.GHOUL, GhoulEntity::createGhoulAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.REVENANT, RevenantEntity::createRevenantAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.WRAITH, WraithEntity::createWraithAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.NIGHTMARE, NightmareEntity::createNightmareAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.GHOULING, GhoulingEntity::createGhoulingAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.NAMELESS_HANGED, NamelessHangedEntity::createNamelessHangedAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.LICH, LichEntity::createLichAttributes);
    }
}
