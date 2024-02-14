package com.lion.graveyard.blocks;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TGTrapDoorBlock extends TrapDoorBlock {

    // access to private trap door constructor
    public TGTrapDoorBlock(BlockBehaviour.Properties settings) {
        super(BlockSetType.IRON, settings);
    }
}
