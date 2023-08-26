package main.java.com.lion.graveyard.world.processors;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGProcessors;
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
        if (structureBlockInfo2.state().getBlock() instanceof SpawnerBlock && TheGraveyard.config.booleanEntries.get("disableWitherSkeletonSpawner")) {
            BlockPos worldPos = structureBlockInfo2.pos();
            BlockEntity blockEntity = world.getBlockEntity(worldPos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                NbtCompound nbt = structureBlockInfo2.nbt();
                if (nbt != null) {
                    NbtCompound nbtCompound = nbt.getCompound("SpawnData");
                    if (nbtCompound.toString().contains("wither_skeleton")) {
                        ((MobSpawnerBlockEntity) blockEntity).setEntityType(EntityType.SKELETON, data.getRandom(worldPos));
                        //((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.SKELETON, (World) world, data.getRandom(worldPos), worldPos);
                        //TheGraveyard.LOGGER.error("The Graveyard Config: Wither Skeleton Spawner switched to Skeleton Spawner at " + worldPos);
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
