package com.lion.graveyard.blocks;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TGTrapDoorBlock extends TrapDoorBlock {

    // access to private trap door constructor
    public TGTrapDoorBlock(Properties settings) {
        super(settings, BlockSetType.IRON);
    }
}
