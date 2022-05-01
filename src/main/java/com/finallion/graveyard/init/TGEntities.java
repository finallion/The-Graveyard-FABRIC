package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class TGEntities {
    public static List<EntityType<?>> entities = new ArrayList<>();

    //public static final EntityType<AcolyteEntity> ACOLYTE = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AcolyteEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.9F)).build();
    public static final EntityType<ReaperEntity> REAPER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ReaperEntity::new).dimensions(EntityDimensions.changing(0.5F, 1.4F)).build();

    public static final EntityType<AcolyteEntity> ACOLYTE = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(AcolyteEntity::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.9F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInLight)
            .build();

    public static final EntityType<CorruptedVindicator> CORRUPTED_VINDICATOR = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(CorruptedVindicator::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.9F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInLight)
            .build();

    public static final EntityType<CorruptedPillager> CORRUPTED_PILLAGER = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(CorruptedPillager::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.9F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInLight)
            .build();

    public static final EntityType<SkeletonCreeper> SKELETON_CREEPER = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(SkeletonCreeper::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.7F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInDarkness)
            .build();

    public static final EntityType<GhoulEntity> GHOUL = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(GhoulEntity::new)
            .dimensions(EntityDimensions.changing(0.8F, 2.1F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInDarkness)
            .build();

    public static final EntityType<RevenantEntity> REVENANT = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(RevenantEntity::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.9F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInDarkness)
            .build();

    public static final EntityType<WraithEntity> WRAITH = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(WraithEntity::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.8F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInDarkness)
            .build();

    public static final EntityType<NightmareEntity> NIGHTMARE = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(NightmareEntity::new)
            .dimensions(EntityDimensions.changing(0.6F, 2.6F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileGraveyardEntity::canSpawnInDarkness)
            .build();


    private static void register(String name, EntityType<?> type) {
        entities.add(type);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, name), type);

    }


    public static void registerEntities() {
        register("skeleton_creeper", SKELETON_CREEPER);
        register("acolyte", ACOLYTE);
        register("corrupted_vindicator", CORRUPTED_VINDICATOR);
        register("corrupted_pillager", CORRUPTED_PILLAGER);
        register("ghoul", GHOUL);
        register("reaper", REAPER);
        register("revenant", REVENANT);
        register("nightmare", NIGHTMARE);
        register("wraith", WRAITH);
    }



}
