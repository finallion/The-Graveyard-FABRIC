package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/// Decides if a graveyard mob may appear on its own.
/// Eggs, commands, buckets and player summons never pass through this check.
public final class MobSpawnGate {
    public static final List<String> MANAGED_MOBS = List.of(
            "ghoul", "revenant", "nightmare", "skeleton_creeper", "acolyte", "reaper",
            "corrupted_vindicator", "corrupted_pillager", "corrupted_evoker", "wraith", "raven", "magpie", "crow",
            "tormented_villager", "nameless_hanged");

    private static final Set<SpawnReason> AUTOMATIC = EnumSet.of(
            SpawnReason.NATURAL, SpawnReason.CHUNK_GENERATION, SpawnReason.STRUCTURE, SpawnReason.SPAWNER,
            SpawnReason.PATROL, SpawnReason.EVENT, SpawnReason.REINFORCEMENT);

    private static final TagKey<Biome> NO_MOB_SPAWNS = TagKey.of(RegistryKeys.BIOME, new Identifier(Graveyard.MOD_ID, "no_mob_spawns"));

    private MobSpawnGate() {
    }

    public static boolean inForbiddenBiome(WorldAccess world, BlockPos pos, EntityType<?> type, SpawnReason reason) {
        return (reason == SpawnReason.NATURAL || reason == SpawnReason.CHUNK_GENERATION)
                && Graveyard.MOD_ID.equals(Registries.ENTITY_TYPE.getId(type).getNamespace())
                && world.getBiome(pos).isIn(NO_MOB_SPAWNS);
    }

    public static boolean blocks(EntityType<?> type, SpawnReason reason) {
        return AUTOMATIC.contains(reason) && isSwitchedOff(type);
    }

    public static boolean isSwitchedOff(EntityType<?> type) {
        Identifier id = Registries.ENTITY_TYPE.getId(type);
        if (!Graveyard.MOD_ID.equals(id.getNamespace()) || !MANAGED_MOBS.contains(id.getPath())) {
            return false;
        }
        GraveyardConfig config = Graveyard.getConfig();
        return config.structuresOnly() || !config.mobMayAppear(id.getPath());
    }
}
