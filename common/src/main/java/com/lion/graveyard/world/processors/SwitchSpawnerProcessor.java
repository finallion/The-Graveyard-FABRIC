package com.lion.graveyard.world.processors;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGProcessors;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class SwitchSpawnerProcessor extends StructureProcessor {
    public static final SwitchSpawnerProcessor INSTANCE = new SwitchSpawnerProcessor();
    public static final MapCodec<SwitchSpawnerProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader world,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo structureBlockInfo,
            StructureTemplate.StructureBlockInfo structureBlockInfo2,
            StructurePlaceSettings data
    ) {
        CompoundTag nbt = structureBlockInfo2.nbt();

        if (nbt == null || !(structureBlockInfo2.state().getBlock() instanceof SpawnerBlock) || !Graveyard.getConfig().flag("disableWitherSkeletonSpawner", false)) {
            return structureBlockInfo2;
        }

        CompoundTag patched = nbt.copy();
        boolean changed = swapEntityId(patched.getCompoundOrEmpty("SpawnData").getCompoundOrEmpty("entity"));
        ListTag potentials = patched.getListOrEmpty("SpawnPotentials");

        for (int i = 0; i < potentials.size(); i++) {
            changed |= swapEntityId(potentials.getCompoundOrEmpty(i).getCompoundOrEmpty("data").getCompoundOrEmpty("entity"));
        }

        return changed ? new StructureTemplate.StructureBlockInfo(structureBlockInfo2.pos(), structureBlockInfo2.state(), patched) : structureBlockInfo2;
    }

    private static boolean swapEntityId(CompoundTag entity) {
        if ("minecraft:wither_skeleton".equals(entity.getStringOr("id", ""))) {
            entity.putString("id", "minecraft:skeleton");
            return true;
        }

        return false;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGProcessors.SWITCH_SPAWNER;
    }
}
