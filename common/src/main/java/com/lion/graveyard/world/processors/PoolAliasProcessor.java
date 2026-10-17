package com.lion.graveyard.world.processors;

import com.lion.graveyard.init.TGOverrunVillage;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// This processor changes no block. The pool element reads it when it lists its jigsaw blocks.
public class PoolAliasProcessor extends StructureProcessor {

    private static final String POOL_KEY = "pool";

    public static final Codec<PoolAliasProcessor> CODEC = Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC)
            .fieldOf("aliases").xmap(PoolAliasProcessor::new, processor -> processor.aliases).codec();

    private final Map<Identifier, Identifier> aliases;

    public PoolAliasProcessor(Map<Identifier, Identifier> aliases) {
        this.aliases = aliases;
    }

    public List<StructureBlockInfo> apply(List<StructureBlockInfo> jigsaws) {
        List<StructureBlockInfo> result = new ArrayList<>(jigsaws.size());
        for (StructureBlockInfo jigsaw : jigsaws) {
            Identifier alias = jigsaw.nbt() == null ? null : this.aliases.get(Identifier.tryParse(jigsaw.nbt().getString(POOL_KEY)));
            if (alias == null) {
                result.add(jigsaw);
                continue;
            }
            NbtCompound nbt = jigsaw.nbt().copy();
            nbt.putString(POOL_KEY, alias.toString());
            result.add(new StructureBlockInfo(jigsaw.pos(), jigsaw.state(), nbt));
        }
        return result;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGOverrunVillage.POOL_ALIASES;
    }
}
