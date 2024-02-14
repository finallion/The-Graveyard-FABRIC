package com.lion.graveyard.entities.ai.goals;

import com.google.common.collect.AbstractIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.Player;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/*
public class BreakBlockGoal extends Goal {
    private final GhoulingEntity minion;
    private BlockPos targetPos;
    private Block targetBlock;
    private float breakProgress;
    private int misses = 0;


    public BreakBlockGoal(GhoulingEntity minion, Block block, BlockPos pos) {
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
        this.minion = minion;
        this.targetBlock = block;
        this.targetPos = pos;
    }

    public boolean canStart() {
        if (minion.canCollect() && minion.getTarget() == null && targetBlock != null && targetPos != null) {
            int counter = 0;
            for (BlockPos position : BlockPos.iterateOutwards(targetPos,  5, 3, 5)) {
                BlockState blockState = minion.getLevel().getBlockState(position);
                if (blockState.isOf(targetBlock)) {
                    counter++;
                    if (minion.getNavigation().getCurrentPath() != null && minion.getNavigation().getCurrentPath().reachesTarget()) {
                        System.out.println("Dont Reach!");
                        continue;
                    }
                    if (minion.getNavigation().startMovingTo(position.getX(), position.getY(), position.getZ(), 1.0F)) {
                        return true;
                    }
                }
            }
            if (counter == 0) {
                stop();
            }
        }
        stop();
        return false;
    }

    public void tick() {
        BlockState blockState = minion.world.getBlockState(targetPos);
        if (blockState.getBlock() != targetBlock) {
            this.canStart();
            return;
        }

        if (minion.squaredDistanceTo(targetPos.getX(), targetPos.getY() - 1, targetPos.getZ()) <= 8.0D) {
            breakProgress += 0.02; // BreakDoorGoal from Zombie
            minion.world.setBlockBreakingInfo(minion.getId(), targetPos, (int) (breakProgress / minion.world.getBlockState(targetPos).getHardness(minion.world, targetPos) * 10.0F));
            if (breakProgress >= minion.world.getBlockState(targetPos).getHardness(minion.world, targetPos)) {
                minion.world.setBlockBreakingInfo(minion.getId(), targetPos, -1);
                minion.world.breakBlock(targetPos, true, minion);
                breakProgress = 0;
            }
        } else {
            if (!minion.getNavigation().isFollowingPath()) { // floating blocks helper
                System.out.println("Stopping");
                stop();
            } else {
                minion.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 1.0F);
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return targetPos != null && minion.getTarget() == null && minion.canCollect();
    }

    @Override
    public void stop() {
        System.out.println("Stopping");
        targetPos = null;
        targetBlock = null;
        minion.getNavigation().stop();
        minion.setCanCollect(false);
        super.stop();
    }
}

 */
