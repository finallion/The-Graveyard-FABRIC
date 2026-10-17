package com.lion.graveyard.mixin;

import com.lion.graveyard.world.processors.PoolAliasProcessor;
import com.lion.graveyard.world.processors.RemoveWaterloggedCryptProcessor;
import com.lion.graveyard.world.processors.RemoveWaterloggedProcessor;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockBox;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {

    @Shadow
    @Final
    protected RegistryEntry<StructureProcessorList> processors;

    @Inject(method = "getStructureBlockInfos", at = @At("RETURN"), cancellable = true)
    private void graveyard$applyPoolAliases(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random, CallbackInfoReturnable<List<StructureBlockInfo>> callback) {
        if (!this.processors.hasKeyAndValue()) {
            return;
        }

        for (StructureProcessor processor : this.processors.value().getList()) {
            if (processor instanceof PoolAliasProcessor poolAliases) {
                callback.setReturnValue(poolAliases.apply(callback.getReturnValue()));
                return;
            }
        }
    }

    @Inject(method = "createPlacementData", at = @At("RETURN"))
    private void graveyard$keepWorldWaterOut(BlockRotation rotation, BlockBox box, boolean keepJigsaws, CallbackInfoReturnable<StructurePlacementData> callback) {
        if (!this.processors.hasKeyAndValue()) {
            return;
        }

        for (StructureProcessor processor : this.processors.value().getList()) {
            if (processor instanceof RemoveWaterloggedProcessor || processor instanceof RemoveWaterloggedCryptProcessor) {
                callback.getReturnValue().setPlaceFluids(false);
                return;
            }
        }
    }
}
