package com.finallion.graveyard.mixin;


import com.finallion.graveyard.init.TGStructures;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;


@Mixin(LakeFeature.class)
public class LakeFeatureMixin {


    @Inject(at = @At("HEAD"), method = "generate", cancellable = true)
    private void generateNoLakes(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SingleStateFeatureConfig singleStateFeatureConfig, CallbackInfoReturnable<Boolean> info) {
        ChunkSectionPos chunkPos = ChunkSectionPos.from(blockPos);
        for (StructureFeature<?> structure : TGStructures.structures) {
            if (structureWorldAccess.getStructures(chunkPos, structure).findAny().isPresent()) {
                info.setReturnValue(false);
            }
        }
    }
}
