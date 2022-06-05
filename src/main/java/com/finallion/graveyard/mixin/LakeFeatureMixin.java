package com.finallion.graveyard.mixin;

import com.finallion.graveyard.init.TGConfiguredStructureFeatures;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LakeFeature.class)
public class LakeFeatureMixin {

    @Inject(at = @At("HEAD"), method = "generate", cancellable = true)
    private void generateNoLakes(FeatureContext<LakeFeature.Config> context, CallbackInfoReturnable<Boolean> info) {
        ChunkSectionPos chunkPos = ChunkSectionPos.from(context.getOrigin());
        StructureHolder structureHolder = context.getWorld().getChunk(context.getOrigin());

        StructureAccessor structureAccessor = ((ChunkRegionAccessor)context.getWorld()).getStructureAccessor();

        for (Structure structure : TGConfiguredStructureFeatures.structures) {
            StructureStart structureStart = structureAccessor.getStructureStart(chunkPos, structure, structureHolder);

            if (structureStart != null && structureStart.hasChildren()) {
                info.setReturnValue(false);
            }
        }
    }
}

