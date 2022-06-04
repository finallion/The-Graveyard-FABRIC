package com.finallion.graveyard.world.processors;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class SwitchSpawnerProcessor extends StructureProcessor {
    public static final SwitchSpawnerProcessor INSTANCE = new SwitchSpawnerProcessor();
    public static final Codec<SwitchSpawnerProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @Nullable
    @Override
    public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData data) {
        if (structureBlockInfo2.state.getBlock() instanceof SpawnerBlock && TheGraveyard.config.booleanEntries.get("disableWitherSkeletonSpawner")) {
            BlockPos worldPos = structureBlockInfo2.pos;
            BlockEntity blockEntity = world.getBlockEntity(worldPos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                NbtCompound nbtCompound = structureBlockInfo2.nbt.getCompound("SpawnData");
                if (nbtCompound.toString().contains("wither_skeleton")) {
                    ((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.SKELETON);
                    //TheGraveyard.LOGGER.error("The Graveyard Config: Wither Skeleton Spawner switched to Skeleton Spawner at " + worldPos);
                }
            }
        }
        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.SWITCH_SPAWNER;
    }
}
