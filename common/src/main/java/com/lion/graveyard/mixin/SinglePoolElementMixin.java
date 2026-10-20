package com.lion.graveyard.mixin;

import com.lion.graveyard.world.processors.PoolAliasProcessor;
import com.lion.graveyard.world.processors.RemoveWaterloggedCryptProcessor;
import com.lion.graveyard.world.processors.RemoveWaterloggedProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.JigsawBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {

    @Shadow
    @Final
    protected Holder<StructureProcessorList> processors;

    @Inject(method = "getShuffledJigsawBlocks", at = @At("RETURN"), cancellable = true)
    private void graveyard$applyPoolAliases(
            StructureTemplateManager structureTemplateManager,
            BlockPos pos,
            Rotation rotation,
            RandomSource random,
            CallbackInfoReturnable<List<JigsawBlockInfo>> callback
    ) {
        if (!this.processors.isBound()) {
            return;
        }

        for (StructureProcessor processor : this.processors.value().list()) {
            if (processor instanceof PoolAliasProcessor poolAliases) {
                List<StructureBlockInfo> infos = callback.getReturnValue()
                        .stream()
                        .map(JigsawBlockInfo::info)
                        .toList();
                List<JigsawBlockInfo> aliased = new ArrayList<>();

                for (StructureBlockInfo info : poolAliases.apply(infos)) {
                    aliased.add(JigsawBlockInfo.of(info));
                }

                callback.setReturnValue(aliased);

                return;
            }
        }
    }

    @Inject(method = "getSettings", at = @At("RETURN"))
    private void graveyard$keepWorldWaterOut(
            Rotation rotation,
            BoundingBox box,
            LiquidSettings liquidSettings,
            boolean keepJigsaws,
            CallbackInfoReturnable<StructurePlaceSettings> callback
    ) {
        if (!this.processors.isBound()) {
            return;
        }

        for (StructureProcessor processor : this.processors.value().list()) {
            if (processor instanceof RemoveWaterloggedProcessor || processor instanceof RemoveWaterloggedCryptProcessor) {
                callback.getReturnValue().setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
                return;
            }
        }
    }
}
