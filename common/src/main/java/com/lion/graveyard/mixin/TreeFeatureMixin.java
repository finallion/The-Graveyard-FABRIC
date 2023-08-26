package main.java.com.lion.graveyard.mixin;

import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
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

    @Inject(method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Lnet/minecraft/world/gen/foliage/FoliagePlacer$BlockPlacer;Lnet/minecraft/world/gen/feature/TreeFeatureConfig;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void noTreesInStructuresGenerate(StructureWorldAccess world, Random random, BlockPos pos, BiConsumer<BlockPos, BlockState> rootPlacerReplacer, BiConsumer<BlockPos, BlockState> trunkPlacerReplacer, FoliagePlacer.BlockPlacer blockPlacer, TreeFeatureConfig config, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(pos.down());
        if (state.isOf(TGBlocks.TG_DIRT) || state.isOf(TGBlocks.TG_COARSE_DIRT) || state.isOf(TGBlocks.TG_GRASS_BLOCK) || state.isOf(TGBlocks.TG_MOSS_BLOCK) || state.isOf(TGBlocks.TG_ROOTED_DIRT) || state.isOf(TGBlocks.TG_PODZOL)) {
            cir.setReturnValue(false);
        }
    }

}

