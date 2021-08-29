package com.finallion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.function.Supplier;

public class TGStoneBlock extends Block {

    private final Supplier<Block> clickedBlock;

    public TGStoneBlock(Supplier<Block> clickedBlock, Settings settings) {
        super(settings);
        this.clickedBlock = clickedBlock;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(clickedBlock.get());
    }


}
