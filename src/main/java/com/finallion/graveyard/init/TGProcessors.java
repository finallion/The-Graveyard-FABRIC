package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.processors.RemoveWaterloggedProcessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;

public class TGProcessors {

    // Processor uses:
    // Haunted House
    // Small Crypt

    public static StructureProcessorType<RemoveWaterloggedProcessor> REMOVE_WATERLOGGED = () -> RemoveWaterloggedProcessor.CODEC;

    //public static final StructureProcessorType<RemoveWaterloggedProcessor> REMOVE_WATERLOGGED = StructureProcessorType.register("remove_waterlogged_processor_type", RemoveWaterloggedProcessor.CODEC);
    public static final StructureProcessorList WATERLOGGED_LIST = TGProcessors.register("waterlogged_processor_list", ImmutableList.of(new RemoveWaterloggedProcessor()));

    public static StructureProcessorList register(String id, ImmutableList<StructureProcessor> processorList) {
        Identifier identifier = new Identifier(TheGraveyard.MOD_ID, id);
        StructureProcessorList structureProcessorList = new StructureProcessorList(processorList);
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, identifier, structureProcessorList);
    }

    public static void registerProcessors() {
        Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(TheGraveyard.MOD_ID, "remove_waterlogged_processor"), REMOVE_WATERLOGGED);

    }
}
