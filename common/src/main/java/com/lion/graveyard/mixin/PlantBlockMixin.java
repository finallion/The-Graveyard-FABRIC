package com.lion.graveyard.mixin;


import com.lion.graveyard.init.TGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (BushBlock.class)
public class PlantBlockMixin {

    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    void canPlaceAt(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        Block block = world.getBlockState(pos.below()).getBlock();
        if (block.defaultBlockState().is(TGTags.NATURAL_HELPER_BLOCKS)) {
            info.setReturnValue(true);
        }
    }
}
