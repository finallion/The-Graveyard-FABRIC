package com.lion.graveyard.mixin;

import com.lion.graveyard.init.TGStructures;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LakeFeature.class)
public class LakeFeatureMixin {

    @Inject(at = @At("HEAD"), method = "generate", cancellable = true)
    private void generateNoLakes(FeatureContext<LakeFeature.Config> context, CallbackInfoReturnable<Boolean> info) {
        // A feature placed outside world generation, such as by /place, has no chunk region.
        if (!(context.getWorld() instanceof ChunkRegionAccessor chunkRegion)) {
            return;
        }
        ChunkSectionPos chunkPos = ChunkSectionPos.from(context.getOrigin());
        StructureHolder structureHolder = context.getWorld().getChunk(context.getOrigin());
        StructureAccessor structureAccessor = chunkRegion.getStructureAccessor();

        for (Structure structure : TGStructures.structures) {
            StructureStart structureStart = structureAccessor.getStructureStart(chunkPos, structure, structureHolder);

            if (structureStart != null && structureStart.hasChildren()) {
                info.setReturnValue(false);
                return;
            }
        }
    }
}
