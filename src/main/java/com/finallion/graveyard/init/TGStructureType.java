package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.structures.TGJigsawStructure;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class TGStructureType<S extends Structure> {
    public static void init() {}

    public static final StructureType<TGJigsawStructure> TG_JIGSAW = register("graveyard:tg_jigsaw", TGJigsawStructure.CODEC);

    private static <S extends Structure> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(Registries.STRUCTURE_TYPE, id, () -> {
            return codec;
        });
    }
}
