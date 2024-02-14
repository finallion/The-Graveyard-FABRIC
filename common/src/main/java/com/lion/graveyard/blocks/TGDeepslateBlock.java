package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class TGDeepslateBlock extends PillarBlock {

    private final Supplier<Block> clickedBlock;

    public TGDeepslateBlock(Supplier<Block> clickedBlock, BlockBehaviour.Properties settings) {
        super(settings);
        this.clickedBlock = clickedBlock;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(clickedBlock.get());
    }
}
