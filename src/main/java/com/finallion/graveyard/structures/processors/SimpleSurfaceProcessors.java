package com.finallion.graveyard.structures.processors;

import com.finallion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class SimpleSurfaceProcessors extends StructureProcessor {
    public static final SimpleSurfaceProcessors INSTANCE = new SimpleSurfaceProcessors();
    public static final Codec<SimpleSurfaceProcessors> CODEC = Codec.unit(() -> INSTANCE);

    // allows to swap out blocks on generation (before tree and ore generation sadly)
    // this is just a test case
    @Override
    public Structure.StructureBlockInfo process(WorldView worldReader, BlockPos jigsawPiecePos, BlockPos jigsawPieceBottomCenterPos, Structure.StructureBlockInfo blockInfoLocal, Structure.StructureBlockInfo blockInfoGlobal, StructurePlacementData structurePlacementData) {
        if (blockInfoGlobal.state.isOf(Blocks.NETHERRACK)) {
            blockInfoGlobal = new Structure.StructureBlockInfo(blockInfoGlobal.pos, Blocks.AIR.getDefaultState(), blockInfoGlobal.tag);
        }
        return blockInfoGlobal;
    }



    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.SIMPLE_SURFACE_PROCESSOR;
    }
}
