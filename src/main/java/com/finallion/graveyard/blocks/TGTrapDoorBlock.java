package com.finallion.graveyard.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.sound.SoundEvents;

public class TGTrapDoorBlock extends TrapdoorBlock {

    // access to private trap door constructor
    public TGTrapDoorBlock(Settings settings) {
        super(settings, BlockSetType.IRON);
    }
}
