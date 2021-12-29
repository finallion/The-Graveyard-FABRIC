package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.AcolyteEntity;
import com.finallion.graveyard.entities.BaseGhoulEntity;
import com.finallion.graveyard.entities.ReaperEntity;
import com.finallion.graveyard.entities.SkeletonCreeper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

public class TGEntities {

    public static final EntityType<AcolyteEntity> ACOLYTE = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AcolyteEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.9F)).build();
    public static final EntityType<ReaperEntity> REAPER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ReaperEntity::new).dimensions(EntityDimensions.changing(0.5F, 1.4F)).build();


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
            .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BaseGhoulEntity::canSpawn)
            .build();



    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "skeleton_creeper"), SKELETON_CREEPER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "acolyte"), ACOLYTE);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "ghoul"), GHOUL);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "reaper"), REAPER);
    }



}
