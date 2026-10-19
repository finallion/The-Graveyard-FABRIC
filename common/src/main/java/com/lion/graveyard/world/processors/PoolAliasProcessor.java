package com.lion.graveyard.world.processors;

import com.lion.graveyard.init.TGOverrunVillage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/// This processor changes no block. The pool element reads it when it lists its jigsaw blocks.
public class PoolAliasProcessor extends StructureProcessor {

    private static final String POOL_KEY = "pool";

    public static final MapCodec<PoolAliasProcessor> CODEC = Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC)
            .fieldOf("aliases").xmap(PoolAliasProcessor::new, processor -> processor.aliases);

    private final Map<Identifier, Identifier> aliases;

    public PoolAliasProcessor(Map<Identifier, Identifier> aliases) {
        this.aliases = aliases;
    }

    public List<StructureBlockInfo> apply(List<StructureBlockInfo> jigsaws) {
        List<StructureBlockInfo> result = new ArrayList<>(jigsaws.size());
        for (StructureBlockInfo jigsaw : jigsaws) {
            Identifier alias = jigsaw.nbt() == null ? null : this.aliases.get(Identifier.tryParse(jigsaw.nbt().getStringOr(POOL_KEY, "")));

            if (alias == null) {
                result.add(jigsaw);
                continue;
            }

            CompoundTag nbt = jigsaw.nbt().copy();
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
