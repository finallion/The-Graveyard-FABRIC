package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.world.processors.RemoveWaterloggedCryptProcessor;
import com.lion.graveyard.world.processors.RemoveWaterloggedProcessor;
import com.lion.graveyard.world.processors.SwitchSpawnerProcessor;
import net.minecraft.structure.processor.StructureProcessorType;


public class TGProcessors {

    public static StructureProcessorType<RemoveWaterloggedProcessor> REMOVE_WATERLOGGED = () -> RemoveWaterloggedProcessor.CODEC;
    public static StructureProcessorType<RemoveWaterloggedCryptProcessor> REMOVE_WATERLOGGED_CRYPT = () -> RemoveWaterloggedCryptProcessor.CODEC;
    public static StructureProcessorType<SwitchSpawnerProcessor> SWITCH_SPAWNER = () -> SwitchSpawnerProcessor.CODEC;

    static {
        RegistryHelper.registerStructureProcessor("remove_waterlogged_processor", REMOVE_WATERLOGGED);
        RegistryHelper.registerStructureProcessor("switch_spawner_processor", SWITCH_SPAWNER);
        RegistryHelper.registerStructureProcessor("waterlogged_crypt_processor", REMOVE_WATERLOGGED_CRYPT);
    }

    public static void init() {}
}
