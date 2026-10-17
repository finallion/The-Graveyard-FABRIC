package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.*;
import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.lion.graveyard.platform.BiomeModifications;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import com.lion.graveyard.config.entries.MobConfigEntry;
import net.minecraft.world.Heightmap;

import java.util.List;
import java.util.function.Supplier;

public class TGEntities {

    private TGEntities() {
    }

    public static final Supplier<EntityType<ReaperEntity>> REAPER = RegistryHelper.registerEntityType("reaper", () -> EntityType.Builder.create(ReaperEntity::new, SpawnGroup.MONSTER).setDimensions(0.5F, 1.4F).build(Graveyard.createStringID("reaper")));
    public static final Supplier<EntityType<AcolyteEntity>> ACOLYTE = RegistryHelper.registerEntityType("acolyte", () -> EntityType.Builder.create(AcolyteEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("acolyte")));
    public static final Supplier<EntityType<CorruptedVindicator>> CORRUPTED_VINDICATOR = RegistryHelper.registerEntityType("corrupted_vindicator", () -> EntityType.Builder.create(CorruptedVindicator::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_vindicator")));
    public static final Supplier<EntityType<CorruptedPillager>> CORRUPTED_PILLAGER = RegistryHelper.registerEntityType("corrupted_pillager", () -> EntityType.Builder.create(CorruptedPillager::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_pillager")));
    public static final Supplier<EntityType<CorruptedEvoker>> CORRUPTED_EVOKER = RegistryHelper.registerEntityType("corrupted_evoker", () -> EntityType.Builder.create(CorruptedEvoker::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("corrupted_evoker")));
    public static final Supplier<EntityType<SkeletonCreeper>> SKELETON_CREEPER = RegistryHelper.registerEntityType("skeleton_creeper", () -> EntityType.Builder.create(SkeletonCreeper::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.7F).build(Graveyard.createStringID("skeleton_creeper")));
    public static final Supplier<EntityType<FallingCorpse>> FALLING_CORPSE = RegistryHelper.registerEntityType("falling_corpse", () -> EntityType.Builder.create(FallingCorpse::new, SpawnGroup.MISC).setDimensions(0.4F, 0.5F).build(Graveyard.createStringID("falling_corpse")));
    public static final Supplier<EntityType<GhoulEntity>> GHOUL = RegistryHelper.registerEntityType("ghoul", () -> EntityType.Builder.create(GhoulEntity::new, SpawnGroup.MONSTER).setDimensions(0.8F, 2.1F).build(Graveyard.createStringID("ghoul")));
    public static final Supplier<EntityType<TormentedVillagerEntity>> TORMENTED_VILLAGER = RegistryHelper.registerEntityType("tormented_villager", () -> EntityType.Builder.create(TormentedVillagerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).build(Graveyard.createStringID("tormented_villager")));
    public static final Supplier<EntityType<RevenantEntity>> REVENANT = RegistryHelper.registerEntityType("revenant", () -> EntityType.Builder.create(RevenantEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.9F).build(Graveyard.createStringID("revenant")));
    public static final Supplier<EntityType<WraithEntity>> WRAITH = RegistryHelper.registerEntityType("wraith", () -> EntityType.Builder.create(WraithEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.8F).build(Graveyard.createStringID("wraith")));
    public static final Supplier<EntityType<NightmareEntity>> NIGHTMARE = RegistryHelper.registerEntityType("nightmare", () -> EntityType.Builder.create(NightmareEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 2.6F).build(Graveyard.createStringID("nightmare")));
    public static final Supplier<EntityType<GhoulingEntity>> GHOULING = RegistryHelper.registerEntityType("ghouling", () -> EntityType.Builder.create(GhoulingEntity::new, SpawnGroup.CREATURE).setDimensions(0.7F, 2.0F).build(Graveyard.createStringID("ghouling")));
    public static final Supplier<EntityType<LichEntity>> LICH = RegistryHelper.registerEntityType("lich", () -> EntityType.Builder.create(LichEntity::new, SpawnGroup.MONSTER).setDimensions(0.9F, 4.0F).build(Graveyard.createStringID("lich")));
    public static final Supplier<EntityType<SkullEntity>> SKULL = RegistryHelper.registerEntityType("skull", () -> EntityType.Builder.<SkullEntity>create(SkullEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build(Graveyard.createStringID("skull")));
    public static final Supplier<EntityType<NamelessHangedEntity>> NAMELESS_HANGED = RegistryHelper.registerEntityType("nameless_hanged", () -> EntityType.Builder.create(NamelessHangedEntity::new, SpawnGroup.MONSTER).setDimensions(0.8F, 2.5F).build(Graveyard.createStringID("nameless_hanged")));
    public static final Supplier<EntityType<CorvidEntity>> RAVEN = RegistryHelper.registerEntityType("raven", () -> EntityType.Builder.create(CorvidEntity::new, SpawnGroup.AMBIENT).setDimensions(0.5F, 0.6F).build(Graveyard.createStringID("raven")));
    public static final Supplier<EntityType<CorvidEntity>> MAGPIE = RegistryHelper.registerEntityType("magpie", () -> EntityType.Builder.<CorvidEntity>create(MagpieEntity::new, SpawnGroup.AMBIENT).setDimensions(0.4F, 0.5F).build(Graveyard.createStringID("magpie")));
    public static final Supplier<EntityType<CorvidEntity>> CROW = RegistryHelper.registerEntityType("crow", () -> EntityType.Builder.<CorvidEntity>create(CrowEntity::new, SpawnGroup.AMBIENT).setDimensions(0.45F, 0.55F).build(Graveyard.createStringID("crow")));


    public static void init() {
        createMobAttributes();
    }

    public static void postInit() {
        addSpawnRestrictions();
        addSpawns();
    }

    public record NaturalSpawn(String mob, TagKey<Biome> tag, SpawnGroup group, Supplier<? extends EntityType<?>> type) {
        public SpawnSettings.SpawnEntry entry() {
            MobConfigEntry entry = Graveyard.getConfig().mobEntry(mob);
            return entry.weight <= 0 ? null : new SpawnSettings.SpawnEntry(type.get(), entry.weight, entry.minGroup, entry.maxGroup);
        }
    }

    public static List<NaturalSpawn> naturalSpawns() {
        return List.of(
                new NaturalSpawn("ghoul", TGTags.GHOUL_SPAWNS, SpawnGroup.MONSTER, GHOUL),
                new NaturalSpawn("revenant", TGTags.REVENANT_SPAWNS, SpawnGroup.MONSTER, REVENANT),
                new NaturalSpawn("reaper", TGTags.REAPER_SPAWNS, SpawnGroup.MONSTER, REAPER),
                new NaturalSpawn("nightmare", TGTags.NIGHTMARE_SPAWNS, SpawnGroup.MONSTER, NIGHTMARE),
                new NaturalSpawn("skeleton_creeper", TGTags.SKELETON_CREEPER_SPAWNS, SpawnGroup.MONSTER, SKELETON_CREEPER),
                new NaturalSpawn("raven", TGTags.RAVEN_SPAWNS, SpawnGroup.AMBIENT, RAVEN),
                new NaturalSpawn("magpie", TGTags.MAGPIE_SPAWNS, SpawnGroup.AMBIENT, MAGPIE),
                new NaturalSpawn("crow", TGTags.CROW_SPAWNS, SpawnGroup.AMBIENT, CROW),
                new NaturalSpawn("acolyte", biomeTag("acolyte_spawns"), SpawnGroup.MONSTER, ACOLYTE),
                new NaturalSpawn("corrupted_vindicator", biomeTag("corrupted_vindicator_spawns"), SpawnGroup.MONSTER, CORRUPTED_VINDICATOR),
                new NaturalSpawn("corrupted_pillager", biomeTag("corrupted_pillager_spawns"), SpawnGroup.MONSTER, CORRUPTED_PILLAGER),
                new NaturalSpawn("corrupted_evoker", biomeTag("corrupted_evoker_spawns"), SpawnGroup.MONSTER, CORRUPTED_EVOKER),
                new NaturalSpawn("wraith", biomeTag("wraith_spawns"), SpawnGroup.MONSTER, WRAITH),
                new NaturalSpawn("tormented_villager", biomeTag("tormented_villager_spawns"), SpawnGroup.MONSTER, TORMENTED_VILLAGER));
    }

    private static TagKey<Biome> biomeTag(String name) {
        return TagKey.of(RegistryKeys.BIOME, Graveyard.id(name));
    }

    private static void addSpawns() {
        for (NaturalSpawn spawn : naturalSpawns()) {
            SpawnSettings.SpawnEntry entry = spawn.entry();

            if (entry != null) {
                BiomeModifications.addMobSpawn(spawn.tag(), spawn.group(), entry.type, entry.getWeight().getValue(), entry.minGroupSize, entry.maxGroupSize);
            }
        }
    }

    private static <T extends MobEntity> SpawnRestriction.SpawnPredicate<T> gated(SpawnRestriction.SpawnPredicate<T> predicate) {
        return (type, world, reason, pos, random) -> !MobSpawnGate.blocks(type, reason)
                && !MobSpawnGate.inForbiddenBiome(world, pos, type, reason)
                && predicate.test(type, world, reason, pos, random);
    }

    private static void addSpawnRestrictions() {
        SpawnRestriction.register(REAPER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(ACOLYTE.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnIgnoreLightLevel));
        SpawnRestriction.register(CORRUPTED_VINDICATOR.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnIgnoreLightLevel));
        SpawnRestriction.register(CORRUPTED_PILLAGER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnIgnoreLightLevel));
        SpawnRestriction.register(CORRUPTED_EVOKER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnIgnoreLightLevel));
        SpawnRestriction.register(SKELETON_CREEPER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(FALLING_CORPSE.get(), SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnIgnoreLightLevel));
        SpawnRestriction.register(GHOUL.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(TORMENTED_VILLAGER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(TormentedVillagerEntity::canSpawn));
        SpawnRestriction.register(REVENANT.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(WRAITH.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(NIGHTMARE.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(LICH.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, gated(HostileEntity::canSpawnInDark));
        SpawnRestriction.register(RAVEN.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, CorvidEntity::canSpawn);
        SpawnRestriction.register(MAGPIE.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, CorvidEntity::canSpawn);
        SpawnRestriction.register(CROW.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, CorvidEntity::canSpawn);
    }

    private static final List<EntityAttribute> SCALED_ATTRIBUTES = List.of(EntityAttributes.GENERIC_MAX_HEALTH, EntityAttributes.GENERIC_ATTACK_DAMAGE,
            EntityAttributes.GENERIC_MOVEMENT_SPEED, EntityAttributes.GENERIC_FLYING_SPEED);

    private static Supplier<DefaultAttributeContainer.Builder> scaled(String mob, Supplier<DefaultAttributeContainer.Builder> base) {
        return () -> {
            MobConfigEntry entry = Graveyard.getConfig().mobEntry(mob);
            DefaultAttributeContainer defaults = base.get().build();
            DefaultAttributeContainer.Builder result = base.get();
            for (EntityAttribute attribute : SCALED_ATTRIBUTES) {
                double factor = factor(entry, attribute);

                if (factor != 1.0D && defaults.has(attribute)) {
                    result.add(attribute, defaults.getBaseValue(attribute) * factor);
                }
            }
            return result;
        };
    }

    private static double factor(MobConfigEntry entry, EntityAttribute attribute) {
        if (attribute == EntityAttributes.GENERIC_MAX_HEALTH) {
            return entry.health();
        }

        if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
            return entry.damage();
        }

        if (attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED || attribute == EntityAttributes.GENERIC_FLYING_SPEED) {
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
