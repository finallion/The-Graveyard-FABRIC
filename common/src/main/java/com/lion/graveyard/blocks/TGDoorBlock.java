package com.lion.graveyard.blocks;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TGDoorBlock extends DoorBlock {

    // access to private door constructor
    public TGDoorBlock(Properties settings) {
        super(settings, BlockSetType.IRON);
    }
}
