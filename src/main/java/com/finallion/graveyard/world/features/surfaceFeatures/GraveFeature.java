package com.finallion.graveyard.world.features.surfaceFeatures;

import com.finallion.graveyard.init.TGBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.Random;

public class GraveFeature extends Feature<DefaultFeatureConfig> {

    private static WeightedBlockStateProvider graveStones = new WeightedBlockStateProvider(DataPool.<BlockState>builder()
            .add(TGBlocks.GRAVESTONE.getDefaultState(), 25)
            .add(TGBlocks.COBBLESTONE_GRAVESTONE.getDefaultState(), 25)
            .add(TGBlocks.DEEPSLATE_GRAVESTONE.getDefaultState(), 25)
            .add(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.getDefaultState(), 25));

    private static final Direction[] randomDirection = new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};


    public GraveFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();

        if (FeatureHelper.canBePlaced(world.getBlockState(blockPos.down())) && world.getBlockState(blockPos).isAir() && random.nextInt(10) == 0) {
            world.setBlockState(blockPos, graveStones.getBlockState(random, blockPos).with(HorizontalFacingBlock.FACING, randomDirection[random.nextInt(4)]), 2);
        }


        return true;

    }

}
