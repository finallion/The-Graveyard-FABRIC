package com.finallion.graveyard.world.features.surfaceFeatures;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class FallenTreeFeature extends Feature<DefaultFeatureConfig> {


    public FallenTreeFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);
        BlockState wood = Blocks.STRIPPED_SPRUCE_LOG.getDefaultState();

        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        Direction direction = Direction.from(axis, random.nextBoolean() ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);

        if (!world.getBlockState(blockPos).isAir()) return false;

        int length = random.nextInt(9) + 2;

        for (int i = 93; i < world.getHeight(); i++) {
            for (int ii = 0; ii < length; ii++) {
                if (ii == 0) {
                    if (world.getBlockState(blockPos.offset(direction, ii + 1).down()).getBlock() != Blocks.SOUL_SAND || world.getBlockState(blockPos.offset(direction, ii + 2).down()).getBlock() != Blocks.SOUL_SAND) {
                        break;
                    }
                }

                if (world.getBlockState(blockPos.offset(direction, ii).down()).getBlock() == Blocks.SOUL_SAND && canBeReplaced(world.getBlockState(blockPos.offset(direction, ii)))) {
                    world.setBlockState(blockPos.offset(direction, ii), wood.with(Properties.AXIS, axis), 2);

                    // moss carpet on top of the trunk
                    if (random.nextBoolean() && world.getBlockState(blockPos.offset(direction, ii).up()).isAir()) {
                        world.setBlockState(blockPos.offset(direction, ii).up(), Blocks.MOSS_CARPET.getDefaultState(), 2);
                    }
                } else {
                    break;
                }
            }
        }

        return true;

    }

    private boolean canBeReplaced(BlockState state) {
        if (state.getBlock() instanceof PlantBlock || state.isAir()) {
            return true;
        }
        return false;
    }

}

