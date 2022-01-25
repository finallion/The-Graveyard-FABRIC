package com.finallion.graveyard.world.features.surfaceFeatures.coralFeatures;

import com.finallion.graveyard.world.features.surfaceFeatures.DeadCoralFeature;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.Random;

public class DeadCoralMushroomFeature extends DeadCoralFeature {
    public DeadCoralMushroomFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    protected boolean generateCoral(WorldAccess world, Random random, BlockPos pos, BlockState state) {
        int i = random.nextInt(3) + 3;
        int j = random.nextInt(3) + 3;
        int k = random.nextInt(3) + 3;
        int l = random.nextInt(3) + 1;
        BlockPos.Mutable mutable = pos.mutableCopy();

        for(int m = 0; m <= j; ++m) {
            for(int n = 0; n <= i; ++n) {
                for(int o = 0; o <= k; ++o) {
                    mutable.set(m + pos.getX(), n + pos.getY(), o + pos.getZ());
                    mutable.move(Direction.DOWN, l);
                    if ((m != 0 && m != j || n != 0 && n != i) && (o != 0 && o != k || n != 0 && n != i) && (m != 0 && m != j || o != 0 && o != k) && (m == 0 || m == j || n == 0 || n == i || o == 0 || o == k) && !(random.nextFloat() < 0.1F) && !this.generateCoralPiece(world, random, mutable, state)) {
                    }
                }
            }
        }

        return true;
    }
}
