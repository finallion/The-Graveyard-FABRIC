package com.finallion.graveyard.world.features.surfaceFeatures;

import com.finallion.graveyard.util.TGTags;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class DeadCoralPatchFeature extends Feature<DefaultFeatureConfig> {


    public DeadCoralPatchFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);


        for (int i = 61; i < 80; i++) {
            mutable.set(blockPos);
            mutable.move(random.nextInt(3) - random.nextInt(3), 0, random.nextInt(3) - random.nextInt(3));
            mutable.setY(i);
            if (world.getBlockState(mutable).isSolidBlock(world, mutable) && world.getBlockState(mutable.up()).isAir() && world.getBiomeKey(mutable).get().getValue().getPath().contains("ancient_dead_coral_reef")) {
                if (random.nextInt(7) == 0) {
                    BlockState coral = TGTags.DEAD_CORALS.getRandom(random).getDefaultState().with(Properties.WATERLOGGED, false);
                    world.setBlockState(mutable.up(), coral, 2);
                    break;
                }
            } else if (world.getBlockState(mutable).isSolidBlock(world, mutable) && world.getBlockState(mutable.up()).isOf(Blocks.WATER) && world.getBiomeKey(mutable).get().getValue().getPath().contains("ancient_dead_coral_reef")) {
                if (random.nextBoolean()) {
                    BlockState coral = BlockTags.CORALS.getRandom(random).getDefaultState().with(Properties.WATERLOGGED, true);
                    world.setBlockState(mutable.up(), coral, 2);
                    break;
                }
            }
        }

        return true;

    }

}

