package com.lion.graveyard.world.processors;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class SwitchSpawnerProcessor extends StructureProcessor {
    public static final SwitchSpawnerProcessor INSTANCE = new SwitchSpawnerProcessor();
    public static final Codec<SwitchSpawnerProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlaceSettings data) {
        if (structureBlockInfo2.state().getBlock() instanceof SpawnerBlock && Graveyard.getConfig().booleanEntries.get("disableWitherSkeletonSpawner")) {
            BlockPos worldPos = structureBlockInfo2.pos();
            BlockEntity blockEntity = world.getBlockEntity(worldPos);
            if (blockEntity instanceof SpawnerBlockEntity) {
                CompoundTag nbt = structureBlockInfo2.nbt();
                if (nbt != null) {
                    CompoundTag nbtCompound = nbt.getCompound("SpawnData");
                    if (nbtCompound.toString().contains("wither_skeleton")) {
                        ((SpawnerBlockEntity) blockEntity).setEntityId(EntityType.SKELETON, data.getRandom(worldPos));
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
