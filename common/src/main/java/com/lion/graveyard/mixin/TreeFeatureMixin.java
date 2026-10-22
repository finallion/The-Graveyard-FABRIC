package com.lion.graveyard.mixin;

import com.lion.graveyard.init.TGTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin {
    @Inject(method = "doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer$FoliageSetter;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void noTreesInStructuresGenerate(
            WorldGenLevel world,
            RandomSource random,
            BlockPos pos,
            BiConsumer<BlockPos, BlockState> rootPlacerReplacer,
            BiConsumer<BlockPos, BlockState> trunkPlacerReplacer,
            FoliagePlacer.FoliageSetter blockPlacer,
            TreeConfiguration config,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockState state = world.getBlockState(pos.below());

        if (state.is(TGTags.NATURAL_HELPER_BLOCKS)) {
            cir.setReturnValue(false);
        }
    }

}

