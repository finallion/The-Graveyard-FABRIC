package com.finallion.graveyard.structures.processors;

import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGProcessors;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SimpleSurfaceProcessors extends StructureProcessor {
    public static final SimpleSurfaceProcessors INSTANCE = new SimpleSurfaceProcessors();
    public static final Codec<SimpleSurfaceProcessors> CODEC = Codec.unit(() -> INSTANCE);

    // allows to swap out blocks on generation (before tree and ore generation sadly)
    // this is just a test case
    @Override
    public Structure.StructureBlockInfo process(WorldView worldReader, BlockPos jigsawPiecePos, BlockPos jigsawPieceBottomCenterPos, Structure.StructureBlockInfo blockInfoLocal, Structure.StructureBlockInfo blockInfoGlobal, StructurePlacementData structurePlacementData) {
        if (blockInfoGlobal.state.isOf(Blocks.NETHERRACK)) {
            blockInfoGlobal = new Structure.StructureBlockInfo(blockInfoGlobal.pos, Blocks.AIR.getDefaultState(), blockInfoGlobal.nbt);
        }
        return blockInfoGlobal;
    }



    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.SIMPLE_SURFACE_PROCESSOR;
    }
}
