package main.java.com.lion.graveyard.world.processors;

import com.finallion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.Optional;

public class RemoveWaterloggedCryptProcessor extends StructureProcessor {

    // Better method to remove waterlogged bocks on structure gen
    // credit: https://github.com/YUNG-GANG/YUNGs-API/blob/multiloader/1.18/Common/src/main/java/com/yungnickyoung/minecraft/yungsapi/world/processor/ISafeWorldModifier.java

    public static final Codec<RemoveWaterloggedCryptProcessor> CODEC = Codec.unit(RemoveWaterloggedCryptProcessor::new);

    public RemoveWaterloggedCryptProcessor() { }

    @Override
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, StructurePlacementData settings) {
        if (infoIn2.state().contains(Properties.WATERLOGGED) && !infoIn2.state().get(Properties.WATERLOGGED)) {
            ChunkPos currentChunkPos = new ChunkPos(infoIn2.pos());
            Chunk currentChunk = world.getChunk(currentChunkPos.x, currentChunkPos.z);
            int sectionYIndex = currentChunk.getSectionIndex(infoIn2.pos().getY());

            if (sectionYIndex < 0) {
                return infoIn2;
            }

            ChunkSection currChunkSection = currentChunk.getSection(sectionYIndex);

            if (getFluidState(world, infoIn2.pos()).isIn(FluidTags.WATER)) {
                setBlockState(currChunkSection, infoIn2.pos(), infoIn2.state());
            }

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                mutable.set(infoIn2.pos()).move(direction);
                if (currentChunkPos.x != mutable.getX() >> 4 || currentChunkPos.z != mutable.getZ() >> 4) {
                    currentChunkPos = new ChunkPos(mutable);
                    currentChunk = world.getChunk(currentChunkPos.x, currentChunkPos.z);
                    sectionYIndex = currentChunk.getSectionIndex(mutable.getY());
                    if (sectionYIndex < 0) {
                        return infoIn2;
                    }
                    currChunkSection = currentChunk.getSection(sectionYIndex);
                }

                if (getFluidState(currChunkSection, mutable).isIn(FluidTags.WATER)) {
                    Optional<BlockState> blockState = getBlockState(currChunkSection, mutable);
                    if (blockState.isPresent() && !(blockState.get().contains(Properties.WATERLOGGED) && blockState.get().get(Properties.WATERLOGGED))) {
                        setBlockState(currChunkSection, mutable, Blocks.DEEPSLATE.getDefaultState());
                    }
                }
            }
        }

        return infoIn2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.REMOVE_WATERLOGGED_CRYPT;
    }

    private FluidState getFluidState(WorldView world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        int sectionYIndex = world.getSectionIndex(pos.getY());
        ChunkSection chunkSection = chunk.getSection(sectionYIndex);
        return getFluidState(chunkSection, pos);
    }

    private FluidState getFluidState(ChunkSection chunkSection, BlockPos pos) {
        if (chunkSection == null) return Fluids.EMPTY.getDefaultState();
        return chunkSection.getFluidState(
                ChunkSectionPos.getLocalCoord(pos.getX()),
                ChunkSectionPos.getLocalCoord(pos.getY()),
                ChunkSectionPos.getLocalCoord(pos.getZ()));
    }

    private Optional<BlockState> setBlockState(ChunkSection chunkSection, BlockPos pos, BlockState state) {
        if (chunkSection == null) return Optional.empty();
        return Optional.of(chunkSection.setBlockState(
                ChunkSectionPos.getLocalCoord(pos.getX()),
                ChunkSectionPos.getLocalCoord(pos.getY()),
                ChunkSectionPos.getLocalCoord(pos.getZ()),
                state,
                false));
    }


    private Optional<BlockState> getBlockState(ChunkSection chunkSection, BlockPos pos) {
        if (chunkSection == null) return Optional.empty();
        return Optional.of(chunkSection.getBlockState(
                ChunkSectionPos.getLocalCoord(pos.getX()),
                ChunkSectionPos.getLocalCoord(pos.getY()),
                ChunkSectionPos.getLocalCoord(pos.getZ())));
    }


}
