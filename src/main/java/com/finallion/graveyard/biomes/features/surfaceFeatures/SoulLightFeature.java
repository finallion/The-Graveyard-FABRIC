package com.finallion.graveyard.biomes.features.surfaceFeatures;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class SoulLightFeature extends Feature<DefaultFeatureConfig> {


    public SoulLightFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);

        mutable.set(blockPos);
        mutable.move(random.nextInt(10) - random.nextInt(10), 63, random.nextInt(10) - random.nextInt(10));

        int randomHeight = random.nextInt(6) + 1;

        if (world.getBlockState(mutable).isOf(Blocks.LILY_PAD) && world.getBlockState(mutable.up()).isAir()) {
            System.out.println(mutable);
            world.setBlockState(mutable.move(0, randomHeight, 0), Blocks.BLACK_CANDLE.getDefaultState()
                    .with(Properties.CANDLES, random.nextInt(4) + 1)
                    .with(Properties.LIT, true), 2);

            return true;
        }




        return false;
    }
}
