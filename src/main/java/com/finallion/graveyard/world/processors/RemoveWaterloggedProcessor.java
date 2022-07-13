package com.finallion.graveyard.world.processors;

import com.finallion.graveyard.TheGraveyard;

import com.finallion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.Waterloggable;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;


import javax.annotation.Nullable;

public class RemoveWaterloggedProcessor extends StructureProcessor {
    public static final RemoveWaterloggedProcessor INSTANCE = new RemoveWaterloggedProcessor();
    public static final Codec<RemoveWaterloggedProcessor> CODEC = Codec.unit(() -> INSTANCE);

    // removes waterlogged blockstate
    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlacementData data) {
        ChunkPos currentChunkPos = new ChunkPos(structureBlockInfo2.pos);
        if (structureBlockInfo2.state.getBlock() instanceof Waterloggable) {
            Chunk currentChunk = world.getChunk(currentChunkPos.x, currentChunkPos.z);
            if (world.getFluidState(structureBlockInfo2.pos).isIn(FluidTags.WATER)) {
                currentChunk.setBlockState(structureBlockInfo2.pos, structureBlockInfo2.state, false);
            }

        }

        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.REMOVE_WATERLOGGED;
    }
}
