package com.lion.graveyard.mixin;

import com.lion.graveyard.init.TGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;


@Mixin(TreeFeature.class)
public class TreeFeatureMixin {
    // helper blocks could be swapped to the vanilla ones during generation, but trees generate later than structures
    // therefore trees would spawn inside the structure
    // this mixin checks during tree generation if the game tries to place a tree on one of the helper blocks

    @Inject(method = "doPlace", at = @At(value = "HEAD"), cancellable = true)
    private void noTreesInStructuresGenerate(WorldGenLevel world, RandomSource random, BlockPos pos, BiConsumer<BlockPos, BlockState> rootPlacerReplacer, BiConsumer<BlockPos, BlockState> trunkPlacerReplacer, FoliagePlacer.FoliageSetter blockPlacer, TreeConfiguration config, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(pos.below());
        if (state.is(TGTags.NATURAL_HELPER_BLOCKS)) {
            cir.setReturnValue(false);
        }
    }

}

