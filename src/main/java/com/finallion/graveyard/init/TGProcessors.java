package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.processors.*;
import com.google.common.collect.ImmutableList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;

public class TGProcessors {

    // Processor uses:
    // Haunted House
    // Small Crypt
    // Wither Skeleton Mill

    public static StructureProcessorType<RemoveWaterloggedProcessor> REMOVE_WATERLOGGED = () -> RemoveWaterloggedProcessor.CODEC;
    public static StructureProcessorType<RemoveWaterloggedCryptProcessor> REMOVE_WATERLOGGED_CRYPT = () -> RemoveWaterloggedCryptProcessor.CODEC;
    public static StructureProcessorType<SwitchSpawnerProcessor> SWITCH_SPAWNER = () -> SwitchSpawnerProcessor.CODEC;

    public static void registerProcessors() {
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(TheGraveyard.MOD_ID, "remove_waterlogged_processor"), REMOVE_WATERLOGGED);
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(TheGraveyard.MOD_ID, "switch_spawner_processor"), SWITCH_SPAWNER);
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(TheGraveyard.MOD_ID, "waterlogged_crypt_processor"), REMOVE_WATERLOGGED_CRYPT);
    }
}
