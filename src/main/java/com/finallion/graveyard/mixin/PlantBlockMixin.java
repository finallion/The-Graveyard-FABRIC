package com.finallion.graveyard.mixin;

import com.finallion.graveyard.blocks.TGGrassBlock;
import com.finallion.graveyard.blocks.TGMossBlock;
import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (PlantBlock.class)
public class PlantBlockMixin {
    // allows to plant plants on replaceable helper blocks

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        Block block = world.getBlockState(pos.down()).getBlock();
        if (block instanceof TGGrassBlock || block instanceof TGMossBlock || block.getDefaultState().isOf(TGBlocks.TG_ROOTED_DIRT) || block.getDefaultState().isOf(TGBlocks.TG_DIRT) || block.getDefaultState().isOf(TGBlocks.TG_COARSE_DIRT) || block.getDefaultState().isOf(TGBlocks.TG_PODZOL)) {
            info.setReturnValue(true);
        }
    }
}
