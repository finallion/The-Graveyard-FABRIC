package com.lion.graveyard.neoforge;

import com.lion.graveyard.init.TGEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record ConfigSpawnsBiomeModifier(String mob) implements BiomeModifier {
    public static final MapCodec<ConfigSpawnsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("mob").forGetter(ConfigSpawnsBiomeModifier::mob)
    ).apply(instance, ConfigSpawnsBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !biome.is(BiomeTags.IS_OVERWORLD)) {
            return;
        }

        for (TGEntities.NaturalSpawn spawn : TGEntities.naturalSpawns()) {
            if (!spawn.mob().equals(mob) || !biome.is(spawn.tag())) {
                continue;
            }

            MobSpawnSettings.SpawnerData entry = spawn.entry();

            if (entry != null) {
                builder.getMobSpawnSettings().addSpawn(spawn.group(), spawn.weight(), entry);
            }
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
