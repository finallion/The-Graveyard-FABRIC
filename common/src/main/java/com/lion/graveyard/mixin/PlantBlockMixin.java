package com.lion.graveyard.mixin;


import com.lion.graveyard.blocks.TGGrassBlock;
import com.lion.graveyard.blocks.TGMossBlock;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (PlantBlock.class)
public class PlantBlockMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        Block block = world.getBlockState(pos.down()).getBlock();
        if (block.getDefaultState().isIn(TGTags.NATURAL_HELPER_BLOCKS)) {
            info.setReturnValue(true);
        }
    }
}
