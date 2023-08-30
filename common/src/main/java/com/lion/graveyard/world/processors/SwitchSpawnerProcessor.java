package com.lion.graveyard.world.processors;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class SwitchSpawnerProcessor extends StructureProcessor {
    public static final SwitchSpawnerProcessor INSTANCE = new SwitchSpawnerProcessor();
    public static final Codec<SwitchSpawnerProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlacementData data) {
        if (structureBlockInfo2.state().getBlock() instanceof SpawnerBlock && Graveyard.getConfig().booleanEntries.get("disableWitherSkeletonSpawner")) {
            BlockPos worldPos = structureBlockInfo2.pos();
            BlockEntity blockEntity = world.getBlockEntity(worldPos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                NbtCompound nbt = structureBlockInfo2.nbt();
                if (nbt != null) {
                    NbtCompound nbtCompound = nbt.getCompound("SpawnData");
                    if (nbtCompound.toString().contains("wither_skeleton")) {
                        ((MobSpawnerBlockEntity) blockEntity).setEntityType(EntityType.SKELETON, data.getRandom(worldPos));
                    }
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
