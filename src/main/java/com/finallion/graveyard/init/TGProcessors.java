package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.structures.processors.SimpleSurfaceProcessors;
import com.google.common.collect.ImmutableList;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;

public class TGProcessors {

    public static StructureProcessorType<SimpleSurfaceProcessors> SIMPLE_SURFACE_PROCESSOR = () -> SimpleSurfaceProcessors.CODEC;


    // called in the template pool under processor
    public static final StructureProcessorList SIMPLE_SURFACE_LIST = registerStructureProcessorList("simple_surface_list", ImmutableList.of(
            new SimpleSurfaceProcessors()
    ));

    private static void registerProcessors() {
        Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(TheGraveyard.MOD_ID, "simple_surface_processor"), SIMPLE_SURFACE_PROCESSOR);

    }

    public static StructureProcessorList registerStructureProcessorList(String id, ImmutableList<StructureProcessor> processorList) {
        Identifier identifier = new Identifier(TheGraveyard.MOD_ID, id);
        StructureProcessorList structureProcessorList = new StructureProcessorList(processorList);
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST, identifier, structureProcessorList);
    }

    public static void init() {
        registerProcessors();
    }


}
