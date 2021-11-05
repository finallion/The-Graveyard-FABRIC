package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.DirectionProperty;
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
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);
        BlockState wood = context.getConfig().woodState;
        BlockState leaf = context.getConfig().leafState;

        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        Direction direction = Direction.from(axis, random.nextBoolean() ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);


        int length = random.nextInt(7) + 2;

        if (!world.getBlockState(blockPos).isAir()) return false;

        for (int i = 0; i < length; i++) {
            if (world.getBlockState(blockPos.offset(direction, i).down()).isOf(Blocks.MOSS_BLOCK) && world.getBlockState(blockPos.offset(direction, i)).isAir()) {
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
}
