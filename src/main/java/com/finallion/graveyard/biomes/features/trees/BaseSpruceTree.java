package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.trees.config.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class BaseSpruceTree extends Feature<TGTreeFeatureConfig> {


    public BaseSpruceTree(Codec<TGTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<TGTreeFeatureConfig> context) {
        return false;
    }


    public void setLeaves(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, BlockState state) {
        if (canReplace(context.getWorld(), pos)) {
            context.getWorld().setBlockState(pos, state, 2);
        }
    }

    public void setLeavesRandomized(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, BlockState state, int chance) {
        if (canReplace(context.getWorld(), pos) && context.getRandom().nextInt(chance) == 0) {
            context.getWorld().setBlockState(pos, state, 2);
        }
    }

    public void setBranchRandomized(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, BlockState state, int chance) {
        if (canReplace(context.getWorld(), pos) && context.getRandom().nextInt(chance) == 0) {
            context.getWorld().setBlockState(pos, state, 2);
        }
    }

    public static boolean canReplace(StructureWorldAccess world, BlockPos pos) {
        return isAirOrLeaves(world, pos) || isReplaceablePlant(world, pos) || isWater(world, pos);
    }

    private static boolean isWater(StructureWorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, (state) -> {
            return state.isOf(Blocks.WATER);
        });
    }

    public static boolean isAirOrLeaves(StructureWorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, (state) -> {
            return state.isAir() || state.isIn(BlockTags.LEAVES);
        });
    }

    private static boolean isReplaceablePlant(StructureWorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, (state) -> {
            Material material = state.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }


    public void generateBranchesOne(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, int chance) {

        setBranchRandomized(context, pos.add(1, 0, 0), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.X), chance);
        setBranchRandomized(context, pos.add(0, 0, 1), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.Z), chance);
        setBranchRandomized(context, pos.add(-1, 0, 0), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.X), chance);
        setBranchRandomized(context, pos.add(0, 0, -1), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.Z), chance);

    }

    public void generateBranchesTwo(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, int chance) {
        setBranchRandomized(context, pos.add(1, 0, 0), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.X), chance);
        setBranchRandomized(context, pos.add(0, 0, 1), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.Z), chance);
        setBranchRandomized(context, pos.add(-1, 0, 0), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.X), chance);
        setBranchRandomized(context, pos.add(0, 0, -1), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.Z), chance);
        setBranchRandomized(context, pos.add(2, 0, 0), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.X), chance);
        setBranchRandomized(context, pos.add(0, 0, 2), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.Z), chance);
        setBranchRandomized(context, pos.add(-2, 0, 0), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.X), chance);
        setBranchRandomized(context, pos.add(0, 0, -2), context.getConfig().woodState.with(Properties.AXIS, Direction.Axis.Z), chance);
    }



    public void randomSpreadOne(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare, int chance) {
        setLeavesRandomized(context, pos.add(1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -1), context.getConfig().leafState, chance);

    }

    public void randomSpreadTwo(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare, int chance) {
        setLeavesRandomized(context, pos.add(1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -1), context.getConfig().leafState, chance);

        if (!beSquare) {
            setLeavesRandomized(context, pos.add(2, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, 2), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(-2, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, -2), context.getConfig().leafState, chance);
        }
    }

    public void randomSpreadThree(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare, int chance) {
        setLeavesRandomized(context, pos.add(1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -2), context.getConfig().leafState, chance);

        if (!beSquare) {
            setLeavesRandomized(context, pos.add(3, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, 3), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(-3, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, -3), context.getConfig().leafState, chance);
        }
    }

    public void randomSpreadFour(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare, int chance) {
        setLeavesRandomized(context, pos.add(1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -1), context.getConfig().leafState, chance);

        setLeavesRandomized(context, pos.add(2, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, -2), context.getConfig().leafState, chance);

        setLeavesRandomized(context, pos.add(3, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(3, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(3, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -3), context.getConfig().leafState, chance);

        if (!beSquare) {
            setLeavesRandomized(context, pos.add(4, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, 4), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(-4, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, -4), context.getConfig().leafState, chance);
        }
    }

    public void randomSpreadFive(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare, int chance) {
        setLeavesRandomized(context, pos.add(1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -1), context.getConfig().leafState, chance);

        setLeavesRandomized(context, pos.add(2, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, -2), context.getConfig().leafState, chance);

        setLeavesRandomized(context, pos.add(3, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(3, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(3, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(3, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, 2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(2, 0, -3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(3, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, 3), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-3, 0, -2), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-2, 0, -3), context.getConfig().leafState, chance);

        setLeavesRandomized(context, pos.add(4, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, 4), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-4, 0, 0), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(0, 0, -4), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(4, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, 4), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-4, 0, 1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(1, 0, -4), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(4, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, 4), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-4, 0, -1), context.getConfig().leafState, chance);
        setLeavesRandomized(context, pos.add(-1, 0, -4), context.getConfig().leafState, chance);

        if (!beSquare) {
            setLeavesRandomized(context, pos.add(5, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, 5), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(-5, 0, 0), context.getConfig().leafState, chance);
            setLeavesRandomized(context, pos.add(0, 0, -5), context.getConfig().leafState, chance);
        }
    }

    public void generateOneStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                #
              # o #
                #
         */
        setLeaves(context, pos.add(1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -1), context.getConfig().leafState);

    }


    public void generateTwoStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                #
              # # #
            # # o # #
              # # #
                #
         */

        setLeaves(context, pos.add(1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -1), context.getConfig().leafState);

        if (!beSquare) {
            setLeaves(context, pos.add(2, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, 2), context.getConfig().leafState);
            setLeaves(context, pos.add(-2, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, -2), context.getConfig().leafState);
        }
    }



    public void generateThreeStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                #
              # # #
            # # # # #
          # # # o # # #
            # # # # #
              # # #
                #
         */

        setLeaves(context, pos.add(1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -2), context.getConfig().leafState);

        if (!beSquare) {
            setLeaves(context, pos.add(3, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, 3), context.getConfig().leafState);
            setLeaves(context, pos.add(-3, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, -3), context.getConfig().leafState);
        }

    }


    public void generateFourStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                  #
                # # #
              # # # # #
            # # # # # # #
          # # # # o # # # #
            # # # # # # #
              # # # # #
                # # #
                  #
         */

        setLeaves(context, pos.add(1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -1), context.getConfig().leafState);

        setLeaves(context, pos.add(2, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, -2), context.getConfig().leafState);

        setLeaves(context, pos.add(3, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -3), context.getConfig().leafState);
        setLeaves(context, pos.add(3, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -3), context.getConfig().leafState);
        setLeaves(context, pos.add(3, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -3), context.getConfig().leafState);

        if (!beSquare) {
            setLeaves(context, pos.add(4, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, 4), context.getConfig().leafState);
            setLeaves(context, pos.add(-4, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, -4), context.getConfig().leafState);
        }
    }

    public void generateFiveStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                    #
                  # # #
                # # # # #
              # # # # # # #
            # # # # # # # # #
          # # # # # o # # # # #
            # # # # # # # # #
              # # # # # # #
                # # # # #
                  # # #
                    #
         */
        setLeaves(context, pos.add(1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -1), context.getConfig().leafState);

        setLeaves(context, pos.add(2, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, -2), context.getConfig().leafState);

        setLeaves(context, pos.add(3, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -3), context.getConfig().leafState);
        setLeaves(context, pos.add(3, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -3), context.getConfig().leafState);
        setLeaves(context, pos.add(3, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -3), context.getConfig().leafState);
        setLeaves(context, pos.add(3, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, 2), context.getConfig().leafState);
        setLeaves(context, pos.add(2, 0, -3), context.getConfig().leafState);
        setLeaves(context, pos.add(3, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, 3), context.getConfig().leafState);
        setLeaves(context, pos.add(-3, 0, -2), context.getConfig().leafState);
        setLeaves(context, pos.add(-2, 0, -3), context.getConfig().leafState);

        setLeaves(context, pos.add(4, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, 4), context.getConfig().leafState);
        setLeaves(context, pos.add(-4, 0, 0), context.getConfig().leafState);
        setLeaves(context, pos.add(0, 0, -4), context.getConfig().leafState);
        setLeaves(context, pos.add(4, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, 4), context.getConfig().leafState);
        setLeaves(context, pos.add(-4, 0, 1), context.getConfig().leafState);
        setLeaves(context, pos.add(1, 0, -4), context.getConfig().leafState);
        setLeaves(context, pos.add(4, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, 4), context.getConfig().leafState);
        setLeaves(context, pos.add(-4, 0, -1), context.getConfig().leafState);
        setLeaves(context, pos.add(-1, 0, -4), context.getConfig().leafState);

        if (!beSquare) {
            setLeaves(context, pos.add(5, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, 5), context.getConfig().leafState);
            setLeaves(context, pos.add(-5, 0, 0), context.getConfig().leafState);
            setLeaves(context, pos.add(0, 0, -5), context.getConfig().leafState);
        }

    }


}
