package com.lion.graveyard.world.processors;

import com.lion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/// SinglePoolElementMixin disables fluid placement for pieces that use this processor.
public class RemoveWaterloggedCryptProcessor extends StructureProcessor {
    public static final Codec<RemoveWaterloggedCryptProcessor> CODEC = Codec.unit(RemoveWaterloggedCryptProcessor::new);

    public RemoveWaterloggedCryptProcessor() { }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader world, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, StructurePlaceSettings settings) {
        return infoIn2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.REMOVE_WATERLOGGED_CRYPT;
    }
}
