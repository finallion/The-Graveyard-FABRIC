package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.*;
import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.lion.graveyard.platform.BiomeModifications;
import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.config.entries.MobConfigEntry;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;

public class TGEntities {

    private TGEntities() {}

    public static final Supplier<EntityType<ReaperEntity>> REAPER = RegistryHelper.registerEntityType("reaper", EntityType.Builder.of(ReaperEntity::new, MobCategory.MONSTER).sized(0.5F, 1.4F));
    public static final Supplier<EntityType<AcolyteEntity>> ACOLYTE = RegistryHelper.registerEntityType("acolyte", EntityType.Builder.of(AcolyteEntity::new, MobCategory.MONSTER).sized(0.6F, 1.9F).ridingOffset(-0.7F));
    public static final Supplier<EntityType<CorruptedVindicator>> CORRUPTED_VINDICATOR = RegistryHelper.registerEntityType("corrupted_vindicator", EntityType.Builder.of(CorruptedVindicator::new, MobCategory.MONSTER).sized(0.6F, 1.9F).ridingOffset(-0.7F));
    public static final Supplier<EntityType<CorruptedPillager>> CORRUPTED_PILLAGER = RegistryHelper.registerEntityType("corrupted_pillager", EntityType.Builder.of(CorruptedPillager::new, MobCategory.MONSTER).sized(0.6F, 1.9F).ridingOffset(-0.7F));
    public static final Supplier<EntityType<CorruptedEvoker>> CORRUPTED_EVOKER = RegistryHelper.registerEntityType("corrupted_evoker", EntityType.Builder.of(CorruptedEvoker::new, MobCategory.MONSTER).sized(0.6F, 1.9F).ridingOffset(-0.7F));
    public static final Supplier<EntityType<SkeletonCreeper>> SKELETON_CREEPER = RegistryHelper.registerEntityType("skeleton_creeper", EntityType.Builder.of(SkeletonCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F));
    public static final Supplier<EntityType<FallingCorpse>> FALLING_CORPSE = RegistryHelper.registerEntityType("falling_corpse", EntityType.Builder.of(FallingCorpse::new, MobCategory.MISC).sized(0.4F, 0.5F));
    public static final Supplier<EntityType<GhoulEntity>> GHOUL = RegistryHelper.registerEntityType("ghoul", EntityType.Builder.of(GhoulEntity::new, MobCategory.MONSTER).sized(0.8F, 2.1F).ridingOffset(-0.7F));
    public static final Supplier<EntityType<TormentedVillagerEntity>> TORMENTED_VILLAGER = RegistryHelper.registerEntityType("tormented_villager", EntityType.Builder.of(TormentedVillagerEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F));
    public static final Supplier<EntityType<RevenantEntity>> REVENANT = RegistryHelper.registerEntityType("revenant", EntityType.Builder.of(RevenantEntity::new, MobCategory.MONSTER).sized(0.6F, 1.9F).ridingOffset(-0.7F));
    public static final Supplier<EntityType<WraithEntity>> WRAITH = RegistryHelper.registerEntityType("wraith", EntityType.Builder.of(WraithEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F));
    public static final Supplier<EntityType<NightmareEntity>> NIGHTMARE = RegistryHelper.registerEntityType("nightmare", EntityType.Builder.of(NightmareEntity::new, MobCategory.MONSTER).sized(0.6F, 2.6F).eyeHeight(2.55F));
    public static final Supplier<EntityType<GhoulingEntity>> GHOULING = RegistryHelper.registerEntityType("ghouling", EntityType.Builder.of(GhoulingEntity::new, MobCategory.CREATURE).sized(0.7F, 2.0F));
    public static final Supplier<EntityType<LichEntity>> LICH = RegistryHelper.registerEntityType("lich", EntityType.Builder.of(LichEntity::new, MobCategory.MONSTER).sized(0.9F, 4.0F).eyeHeight(4.0F));
    public static final Supplier<EntityType<SkullEntity>> SKULL = RegistryHelper.registerEntityType(
            "skull",
            EntityType.Builder.<SkullEntity>of(SkullEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
    );
    public static final Supplier<EntityType<NamelessHangedEntity>> NAMELESS_HANGED = RegistryHelper.registerEntityType("nameless_hanged", EntityType.Builder.of(NamelessHangedEntity::new, MobCategory.MONSTER).sized(0.8F, 2.5F).eyeHeight(2.0F));
    public static final Supplier<EntityType<CorvidEntity>> RAVEN = RegistryHelper.registerEntityType("raven", EntityType.Builder.of(CorvidEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.6F));
    public static final Supplier<EntityType<CorvidEntity>> MAGPIE = RegistryHelper.registerEntityType(
            "magpie",
            EntityType.Builder.<CorvidEntity>of(MagpieEntity::new, MobCategory.AMBIENT).sized(0.4F, 0.5F)
    );
    public static final Supplier<EntityType<CorvidEntity>> CROW = RegistryHelper.registerEntityType(
            "crow",
            EntityType.Builder.<CorvidEntity>of(CrowEntity::new, MobCategory.AMBIENT).sized(0.45F, 0.55F)
    );


    public static void init() {
        createMobAttributes();
        addSpawnRestrictions();
    }

    public static void postInit() {
        addSpawns();
    }

    public record NaturalSpawn(
            String mob,
            TagKey<Biome> tag,
            MobCategory group,
            Supplier<? extends EntityType<?>> type
    ) {
        public int weight() {
            return Graveyard.getConfig().mobEntry(mob).weight;
        }

        public MobSpawnSettings.SpawnerData entry() {
            MobConfigEntry entry = Graveyard.getConfig().mobEntry(mob);
            return entry.weight <= 0 ? null : new MobSpawnSettings.SpawnerData(type.get(), entry.minGroup, entry.maxGroup);
        }
    }

    public static List<NaturalSpawn> naturalSpawns() {
        return List.of(
                new NaturalSpawn("ghoul", TGTags.GHOUL_SPAWNS, MobCategory.MONSTER, GHOUL),
                new NaturalSpawn("revenant", TGTags.REVENANT_SPAWNS, MobCategory.MONSTER, REVENANT),
                new NaturalSpawn("reaper", TGTags.REAPER_SPAWNS, MobCategory.MONSTER, REAPER),
                new NaturalSpawn("nightmare", TGTags.NIGHTMARE_SPAWNS, MobCategory.MONSTER, NIGHTMARE),
                new NaturalSpawn("skeleton_creeper", TGTags.SKELETON_CREEPER_SPAWNS, MobCategory.MONSTER, SKELETON_CREEPER),
                new NaturalSpawn("raven", TGTags.RAVEN_SPAWNS, MobCategory.AMBIENT, RAVEN),
                new NaturalSpawn("magpie", TGTags.MAGPIE_SPAWNS, MobCategory.AMBIENT, MAGPIE),
                new NaturalSpawn("crow", TGTags.CROW_SPAWNS, MobCategory.AMBIENT, CROW),
                new NaturalSpawn("acolyte", biomeTag("acolyte_spawns"), MobCategory.MONSTER, ACOLYTE),
                new NaturalSpawn("corrupted_vindicator", biomeTag("corrupted_vindicator_spawns"), MobCategory.MONSTER, CORRUPTED_VINDICATOR),
                new NaturalSpawn("corrupted_pillager", biomeTag("corrupted_pillager_spawns"), MobCategory.MONSTER, CORRUPTED_PILLAGER),
                new NaturalSpawn("corrupted_evoker", biomeTag("corrupted_evoker_spawns"), MobCategory.MONSTER, CORRUPTED_EVOKER),
                new NaturalSpawn("wraith", biomeTag("wraith_spawns"), MobCategory.MONSTER, WRAITH),
                new NaturalSpawn("tormented_villager", biomeTag("tormented_villager_spawns"), MobCategory.MONSTER, TORMENTED_VILLAGER)
        );
    }

    private static TagKey<Biome> biomeTag(String name) {
        return TagKey.create(Registries.BIOME, Graveyard.id(name));
    }

    private static void addSpawns() {
        for (NaturalSpawn spawn : naturalSpawns()) {
            MobSpawnSettings.SpawnerData entry = spawn.entry();

            if (entry != null) {
                BiomeModifications.addMobSpawn(spawn.tag(), spawn.group(), entry.type(), spawn.weight(), entry.minCount(), entry.maxCount());
            }
        }
    }

    private static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gated(
            SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        return (type, world, reason, pos, random) -> !MobSpawnGate.blocks(type, reason)
                && !MobSpawnGate.inForbiddenBiome(world, pos, type, reason)
                && predicate.test(type, world, reason, pos, random);
    }

    private static void addSpawnRestrictions() {
        RegistryHelper.registerSpawnPlacement(REAPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(ACOLYTE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkAnyLightMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(CORRUPTED_VINDICATOR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkAnyLightMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(CORRUPTED_PILLAGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkAnyLightMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(CORRUPTED_EVOKER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkAnyLightMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(SKELETON_CREEPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(FALLING_CORPSE, SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkAnyLightMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(GHOUL, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(TORMENTED_VILLAGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(TormentedVillagerEntity::canSpawn));
        RegistryHelper.registerSpawnPlacement(REVENANT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(WRAITH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(NIGHTMARE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(LICH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gated(Monster::checkMonsterSpawnRules));
        RegistryHelper.registerSpawnPlacement(RAVEN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, CorvidEntity::canSpawn);
        RegistryHelper.registerSpawnPlacement(MAGPIE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, CorvidEntity::canSpawn);
        RegistryHelper.registerSpawnPlacement(CROW, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, CorvidEntity::canSpawn);
    }

    private static final List<Holder<Attribute>> SCALED_ATTRIBUTES = List.of(
            Attributes.MAX_HEALTH,
            Attributes.ATTACK_DAMAGE,
            Attributes.MOVEMENT_SPEED,
            Attributes.FLYING_SPEED
    );

    private static Supplier<AttributeSupplier.Builder> scaled(String mob, Supplier<AttributeSupplier.Builder> base) {
        return () -> {
            MobConfigEntry entry = Graveyard.getConfig().mobEntry(mob);
            AttributeSupplier defaults = base.get().build();
            AttributeSupplier.Builder result = base.get();
            for (Holder<Attribute> attribute : SCALED_ATTRIBUTES) {
                double factor = factor(entry, attribute);

                if (factor != 1.0D && defaults.hasAttribute(attribute)) {
                    result.add(attribute, defaults.getBaseValue(attribute) * factor);
                }
            }
            return result;
        };
    }

    private static double factor(MobConfigEntry entry, Holder<Attribute> attribute) {
        if (attribute == Attributes.MAX_HEALTH) {
            return entry.health();
        }

        if (attribute == Attributes.ATTACK_DAMAGE) {
            return entry.damage();
        }

        if (attribute == Attributes.MOVEMENT_SPEED || attribute == Attributes.FLYING_SPEED) {
            return entry.speed();
        }

        return 1.0D;
    }

    private static void createMobAttributes() {
        RegistryHelper.registerEntityAttribute(TGEntities.REAPER, scaled("reaper", ReaperEntity::createReaperAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.ACOLYTE, scaled("acolyte", CorruptedIllager::createCorruptedIllagerAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.CORRUPTED_VINDICATOR, scaled("corrupted_vindicator", CorruptedIllager::createCorruptedIllagerAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.CORRUPTED_EVOKER, scaled("corrupted_evoker", CorruptedIllager::createCorruptedIllagerAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.CORRUPTED_PILLAGER, scaled("corrupted_pillager", CorruptedPillager::createCorruptedPillagerAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.SKELETON_CREEPER, scaled("skeleton_creeper", SkeletonCreeper::createSkeletonCreeperAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.FALLING_CORPSE, FallingCorpse::createFallingCorpseAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.GHOUL, scaled("ghoul", GhoulEntity::createGhoulAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.TORMENTED_VILLAGER, scaled("tormented_villager", TormentedVillagerEntity::createTormentedVillagerAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.REVENANT, scaled("revenant", RevenantEntity::createRevenantAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.WRAITH, scaled("wraith", WraithEntity::createWraithAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.NIGHTMARE, scaled("nightmare", NightmareEntity::createNightmareAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.GHOULING, GhoulingEntity::createGhoulingAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.NAMELESS_HANGED, NamelessHangedEntity::createNamelessHangedAttributes);
        RegistryHelper.registerEntityAttribute(TGEntities.RAVEN, scaled("raven", CorvidEntity::createCorvidAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.MAGPIE, scaled("magpie", CorvidEntity::createCorvidAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.CROW, scaled("crow", CorvidEntity::createCorvidAttributes));
        RegistryHelper.registerEntityAttribute(TGEntities.LICH, LichEntity::createLichAttributes);
    }
}
