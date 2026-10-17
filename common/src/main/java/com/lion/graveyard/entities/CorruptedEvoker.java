package com.lion.graveyard.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class CorruptedEvoker extends CorruptedVindicator {

    public CorruptedEvoker(EntityType<? extends CorruptedIllager> entityType, World world) {
        super(entityType, world, "corrupted_evoker");
    }
}
