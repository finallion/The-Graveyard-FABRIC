package com.lion.graveyard.forge;

import com.lion.graveyard.init.TGEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record ConfigSpawnsBiomeModifier(String mob) implements BiomeModifier {
    public static final Codec<ConfigSpawnsBiomeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("mob").forGetter(ConfigSpawnsBiomeModifier::mob)
    ).apply(instance, ConfigSpawnsBiomeModifier::new));

    @Override
    public void modify(RegistryEntry<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !biome.isIn(BiomeTags.IS_OVERWORLD)) {
            return;
        }
        for (TGEntities.NaturalSpawn spawn : TGEntities.naturalSpawns()) {
            if (!spawn.mob().equals(mob) || !biome.isIn(spawn.tag())) {
                continue;
            }
            SpawnSettings.SpawnEntry entry = spawn.entry();
            if (entry != null) {
                builder.getMobSpawnSettings().spawn(spawn.group(), entry);
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
