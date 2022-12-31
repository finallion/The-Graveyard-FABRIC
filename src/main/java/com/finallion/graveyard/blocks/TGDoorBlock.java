package com.finallion.graveyard.blocks;

import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.sound.SoundEvents;

public class TGDoorBlock extends DoorBlock {

    // access to private door constructor
    public TGDoorBlock(Settings settings) {
        super(settings, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN);
    }
}
