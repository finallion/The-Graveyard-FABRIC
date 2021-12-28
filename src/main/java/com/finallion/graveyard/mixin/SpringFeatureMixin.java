package com.finallion.graveyard.mixin;

import com.finallion.graveyard.init.TGStructures;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpringFeature.class)
public class SpringFeatureMixin {

    @Inject(method = "generate(Lnet/minecraft/world/gen/feature/util/FeatureContext;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void generateNoLavaLakes(FeatureContext<SpringFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        ChunkSectionPos chunkPos = ChunkSectionPos.from(blockPos);
        for (StructureFeature<?> structure : TGStructures.structures) {
            if (structureWorldAccess.getStructures(chunkPos, structure).stream().findAny().isPresent()) {
                cir.setReturnValue(false);
            }
        }
    }
}
