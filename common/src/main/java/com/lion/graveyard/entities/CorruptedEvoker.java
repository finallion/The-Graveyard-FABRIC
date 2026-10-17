package com.lion.graveyard.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CorruptedEvoker extends CorruptedVindicator {

    public CorruptedEvoker(EntityType<? extends CorruptedIllager> entityType, Level world) {
        super(entityType, world, "corrupted_evoker");
    }
}
