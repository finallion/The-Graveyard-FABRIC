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
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AcolyteEntity::canSpawn)
            .build();

    public static final EntityType<SkeletonCreeper> SKELETON_CREEPER = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(SkeletonCreeper::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.7F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SkeletonCreeper::canSpawn)
            .build();

    public static final EntityType<BaseGhoulEntity> GHOUL = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(BaseGhoulEntity::new)
            .dimensions(EntityDimensions.changing(0.8F, 2.1F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimatedGraveyardEntity::canSpawn)
            .build();

    public static final EntityType<RevenantEntity> REVENANT = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(RevenantEntity::new)
            .dimensions(EntityDimensions.changing(0.6F, 1.9F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimatedGraveyardEntity::canSpawn)
            .build();

    public static final EntityType<NightmareEntity> NIGHTMARE = FabricEntityTypeBuilder.createMob()
            .spawnGroup(SpawnGroup.MONSTER)
            .entityFactory(NightmareEntity::new)
            .dimensions(EntityDimensions.changing(0.6F, 2.6F))
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, NightmareEntity::canSpawn)
            .build();


    private static void register(String name, EntityType<?> type) {
        entities.add(type);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, name), type);

    }


    public static void registerEntities() {
        register("skeleton_creeper", SKELETON_CREEPER);
        register("acolyte", ACOLYTE);
        register("ghoul", GHOUL);
        register("reaper", REAPER);
        register("revenant", REVENANT);
        register("nightmare", NIGHTMARE);
    }



}
