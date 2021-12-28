package com.finallion.graveyard.world.features.trees;


import com.finallion.graveyard.world.features.trees.config.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class FallenSpruceTree extends BaseSpruceTree {

    public FallenSpruceTree(Codec<TGTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<TGTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        BlockState wood = context.getConfig().woodState;

        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        Direction direction = Direction.from(axis, random.nextBoolean() ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);


        int length = random.nextInt(9) + 2;

        if (!world.getBlockState(blockPos).isAir()) return false;

        for (int i = 0; i < length; i++) {
            if (world.getBlockState(blockPos.offset(direction, i).down()).isSolidBlock(world, blockPos.offset(direction, i).down()) && canBeReplaced(world.getBlockState(blockPos.offset(direction, i)))) {
                world.setBlockState(blockPos.offset(direction, i), wood.with(Properties.AXIS, axis), 2);
                if (random.nextBoolean() && world.getBlockState(blockPos.offset(direction, i).up()).isAir()) {
                    world.setBlockState(blockPos.offset(direction, i).up(), Blocks.MOSS_CARPET.getDefaultState(), 2);
                }
            } else {
                break;
            }
        }



        return false;

    }

    private boolean canBeReplaced(BlockState state) {
        if (state.getBlock() instanceof PlantBlock || state.isAir()) {
            return true;
        }
        return false;
    }
}
