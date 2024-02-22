package com.lion.graveyard.world.processors;

import com.lion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RemoveWaterloggedCryptProcessor extends StructureProcessor {

    // Better method to remove waterlogged bocks on structure gen
    // credit: https://github.com/YUNG-GANG/YUNGs-API/blob/multiloader/1.18/Common/src/main/java/com/yungnickyoung/minecraft/yungsapi/world/processor/ISafeWorldModifier.java

    public static final Codec<RemoveWaterloggedCryptProcessor> CODEC = Codec.unit(RemoveWaterloggedCryptProcessor::new);

    public RemoveWaterloggedCryptProcessor() { }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader world, BlockPos pos, BlockPos pos2, StructureTemplate.StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, StructurePlaceSettings settings) {
        if (infoIn2.state().hasProperty(BlockStateProperties.WATERLOGGED) && !infoIn2.state().getValue(BlockStateProperties.WATERLOGGED)) {
            ChunkPos currentChunkPos = new ChunkPos(infoIn2.pos());
            ChunkAccess currentChunk = world.getChunk(currentChunkPos.x, currentChunkPos.z);
            int sectionYIndex = currentChunk.getSectionIndex(infoIn2.pos().getY());

            if (sectionYIndex < 0) {
                return infoIn2;
            }

            LevelChunkSection currChunkSection = currentChunk.getSection(sectionYIndex);

            if (getFluidState(world, infoIn2.pos()).is(FluidTags.WATER)) {
                setBlock(currChunkSection, infoIn2.pos(), infoIn2.state());
            }

            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
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

                if (getFluidState(currChunkSection, mutable).is(FluidTags.WATER)) {
                    Optional<BlockState> blockState = getBlockState(currChunkSection, mutable);
                    if (blockState.isPresent() && !(blockState.get().hasProperty(BlockStateProperties.WATERLOGGED) && blockState.get().getValue(BlockStateProperties.WATERLOGGED))) {
                        setBlock(currChunkSection, mutable, Blocks.DEEPSLATE.defaultBlockState());
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

    private FluidState getFluidState(LevelReader world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
        int sectionYIndex = world.getSectionIndex(pos.getY());
        LevelChunkSection chunkSection = chunk.getSection(sectionYIndex);
        return getFluidState(chunkSection, pos);
    }

    private FluidState getFluidState(LevelChunkSection chunkSection, BlockPos pos) {
        if (chunkSection == null) return Fluids.EMPTY.defaultFluidState();
        return chunkSection.getFluidState(
                SectionPos.sectionRelative(pos.getX()),
                SectionPos.sectionRelative(pos.getY()),
                SectionPos.sectionRelative(pos.getZ()));
    }

    private Optional<BlockState> setBlock(LevelChunkSection chunkSection, BlockPos pos, BlockState state) {
        if (chunkSection == null) return Optional.empty();
        return Optional.of(chunkSection.setBlockState(
                SectionPos.sectionRelative(pos.getX()),
                SectionPos.sectionRelative(pos.getY()),
                SectionPos.sectionRelative(pos.getZ()),
                state,
                false));
    }


    private Optional<BlockState> getBlockState(LevelChunkSection chunkSection, BlockPos pos) {
        if (chunkSection == null) return Optional.empty();
        return Optional.of(chunkSection.getBlockState(
                SectionPos.sectionRelative(pos.getX()),
                SectionPos.sectionRelative(pos.getY()),
                SectionPos.sectionRelative(pos.getZ())));
    }


}
