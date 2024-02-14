package com.lion.graveyard.blocks;

import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class DarkIronBars extends IronBarsBlock {
    // extra class to access protected PaneBlock class
    public DarkIronBars(BlockBehaviour.Properties settings) {
        super(settings);
    }
}
