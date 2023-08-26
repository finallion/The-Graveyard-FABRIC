package com.lion.graveyard.blocks;


import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public abstract class AbstractCoffinBlock<E extends BlockEntity> extends BlockWithEntity {
    protected final Supplier<BlockEntityType<? extends E>> entityTypeRetriever;

    protected AbstractCoffinBlock(Settings settings, Supplier<BlockEntityType<? extends E>> entityTypeSupplier) {
        super(settings);
        this.entityTypeRetriever = entityTypeSupplier;
    }

    public abstract DoubleBlockProperties.PropertySource<? extends SarcophagusBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked);
}
