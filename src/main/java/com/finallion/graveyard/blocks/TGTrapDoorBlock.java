package com.finallion.graveyard.blocks;

import net.minecraft.block.TrapdoorBlock;
import net.minecraft.sound.SoundEvents;

public class TGTrapDoorBlock extends TrapdoorBlock {

    // access to private trap door constructor
    public TGTrapDoorBlock(Settings settings) {
        super(settings, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN);
    }
}
