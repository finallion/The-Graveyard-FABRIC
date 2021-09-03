package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.enitites.SkeletonCreeper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TGEntities {

    public static final EntityType<SkeletonCreeper> SKELETON_CREEPER = FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkeletonCreeper::new).dimensions(EntityDimensions.fixed(0.6F, 1.7F)).build();


    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "skeleton_creeper"), SKELETON_CREEPER);
    }



}
