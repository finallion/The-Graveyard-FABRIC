package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.entities.*;
import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.lion.graveyard.mixin.SpawnRestrictionAccessor;
import com.lion.graveyard.platform.BiomeModifications;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.SharedConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Heightmap;;

import java.util.function.Supplier;

public class TGEntities {

    private TGEntities() {
    }

    public static final Supplier<EntityType<ReaperEntity>> REAPER = RegistryHelper.registerEntityType("reaper", () -> EntityType.Builder.create(ReaperEntity::new, SpawnGroup.MONSTER).setDimensions(0.5F, 1.4F).build(Graveyard.createStringID("reaper")));
    public static final Supplier<EntityType<AcolyteEntity>> ACOLYTE = RegistryHelper.registerEntityType("acolyte", () -> EntityType.Builder.create(AcolyteEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("acolyte")));
    public static final Supplier<EntityType<CorruptedVindicator>> CORRUPTED_VINDICATOR = RegistryHelper.registerEntityType("corrupted_vindicator", () -> EntityType.Builder.create(CorruptedVindicator::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_vindicator")));
    public static final Supplier<EntityType<CorruptedPillager>> CORRUPTED_PILLAGER = RegistryHelper.registerEntityType("corrupted_pillager", () -> EntityType.Builder.create(CorruptedPillager::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_pillager")));
    public static final Supplier<EntityType<SkeletonCreeper>> SKELETON_CREEPER = RegistryHelper.registerEntityType("skeleton_creeper", () -> EntityType.Builder.create(SkeletonCreeper::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.7F).build(Graveyard.createStringID("skeleton_creeper")));
    public static final Supplier<EntityType<FallingCorpse>> FALLING_CORPSE = RegistryHelper.registerEntityType("falling_corpse", () -> EntityType.Builder.create(FallingCorpse::new, SpawnGroup.MISC).setDimensions(0.4F, 0.5F).build(Graveyard.createStringID("falling_corpse")));
    public static final Supplier<EntityType<GhoulEntity>> GHOUL = RegistryHelper.registerEntityType("ghoul", () -> EntityType.Builder.create(GhoulEntity::new, SpawnGroup.MONSTER).setDimensions(0.8F, 2.1F).build(Graveyard.createStringID("ghoul")));
    public static final Supplier<EntityType<RevenantEntity>> REVENANT = RegistryHelper.registerEntityType("revenant", () -> EntityType.Builder.create(RevenantEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("revenant")));
    public static final Supplier<EntityType<WraithEntity>> WRAITH = RegistryHelper.registerEntityType("wraith", () -> EntityType.Builder.create(WraithEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.8F).build(Graveyard.createStringID("wraith")));
    public static final Supplier<EntityType<NightmareEntity>> NIGHTMARE = RegistryHelper.registerEntityType("nightmare", () -> EntityType.Builder.create(NightmareEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 2.6F).build(Graveyard.createStringID("nightmare")));
    public static final Supplier<EntityType<GhoulingEntity>> GHOULING = RegistryHelper.registerEntityType("ghouling", () -> EntityType.Builder.create(GhoulingEntity::new, SpawnGroup.CREATURE).setDimensions(0.7F, 2.0F).build(Graveyard.createStringID("ghouling")));
    public static final Supplier<EntityType<LichEntity>> LICH = RegistryHelper.registerEntityType("lich", () -> EntityType.Builder.create(LichEntity::new, SpawnGroup.MONSTER).setDimensions(0.9F, 4.0F).build(Graveyard.createStringID("lich")));
    public static final Supplier<EntityType<SkullEntity>> SKULL = RegistryHelper.registerEntityType("skull", () -> EntityType.Builder.<SkullEntity>create(SkullEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build(Graveyard.createStringID("skull")));
    public static final Supplier<EntityType<NamelessHangedEntity>> NAMELESS_HANGED = RegistryHelper.registerEntityType("nameless_hanged", () -> EntityType.Builder.create(NamelessHangedEntity::new, SpawnGroup.MONSTER).setDimensions(0.8F, 2.5F).build(Graveyard.createStringID("nameless_hanged")));


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
                SpawnGroup.MONSTER, TGEntities.GHOUL.get(),
                config.mobConfigEntries.get("ghoul").weight,
                config.mobConfigEntries.get("ghoul").minGroup,
                config.mobConfigEntries.get("ghoul").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.REVENANT_SPAWNS,
                SpawnGroup.MONSTER, TGEntities.REVENANT.get(),
                config.mobConfigEntries.get("revenant").weight,
                config.mobConfigEntries.get("revenant").minGroup,
                config.mobConfigEntries.get("revenant").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.REAPER_SPAWNS,
                SpawnGroup.MONSTER, TGEntities.REAPER.get(),
                config.mobConfigEntries.get("reaper").weight,
                config.mobConfigEntries.get("reaper").minGroup,
                config.mobConfigEntries.get("reaper").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.NIGHTMARE_SPAWNS,
                SpawnGroup.MONSTER, TGEntities.NIGHTMARE.get(),
                config.mobConfigEntries.get("nightmare").weight,
                config.mobConfigEntries.get("nightmare").minGroup,
                config.mobConfigEntries.get("nightmare").maxGroup);

        BiomeModifications.addMobSpawn(TGTags.SKELETON_CREEPER_SPAWNS,
                SpawnGroup.MONSTER, TGEntities.SKELETON_CREEPER.get(),
                config.mobConfigEntries.get("skeleton_creeper").weight,
                config.mobConfigEntries.get("skeleton_creeper").minGroup,
                config.mobConfigEntries.get("skeleton_creeper").maxGroup);
    }

    private static void addSpawnRestrictions() {
        SpawnRestrictionAccessor.callRegister(REAPER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestrictionAccessor.callRegister(ACOLYTE.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
        SpawnRestrictionAccessor.callRegister(CORRUPTED_VINDICATOR.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
        SpawnRestrictionAccessor.callRegister(CORRUPTED_PILLAGER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
        SpawnRestrictionAccessor.callRegister(SKELETON_CREEPER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestrictionAccessor.callRegister(FALLING_CORPSE.get(), SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
        SpawnRestrictionAccessor.callRegister(GHOUL.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestrictionAccessor.callRegister(REVENANT.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestrictionAccessor.callRegister(WRAITH.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestrictionAccessor.callRegister(NIGHTMARE.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestrictionAccessor.callRegister(LICH.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
    }

    private static void createMobAttributes() {
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
