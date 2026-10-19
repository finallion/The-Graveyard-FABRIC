package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

/// Decides if a graveyard mob may appear on its own.
/// Eggs, commands, buckets and player summons never pass through this check.
public final class MobSpawnGate {
    public static final List<String> MANAGED_MOBS = List.of(
            "ghoul",
            "revenant",
            "nightmare",
            "skeleton_creeper",
            "acolyte",
            "reaper",
            "corrupted_vindicator",
            "corrupted_pillager",
            "corrupted_evoker",
            "wraith",
            "raven",
            "magpie",
            "crow",
            "tormented_villager",
            "nameless_hanged"
    );

    private static final Set<EntitySpawnReason> AUTOMATIC = EnumSet.of(
            EntitySpawnReason.NATURAL,
            EntitySpawnReason.CHUNK_GENERATION,
            EntitySpawnReason.STRUCTURE,
            EntitySpawnReason.SPAWNER,
            EntitySpawnReason.PATROL,
            EntitySpawnReason.EVENT,
            EntitySpawnReason.REINFORCEMENT
    );

    private static final TagKey<Biome> NO_MOB_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("no_mob_spawns"));

    private MobSpawnGate() {}

    public static boolean inForbiddenBiome(
            LevelAccessor world,
            BlockPos pos,
            EntityType<?> type,
            EntitySpawnReason reason
    ) {
        return (reason == EntitySpawnReason.NATURAL || reason == EntitySpawnReason.CHUNK_GENERATION)
                && Graveyard.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace())
                && world.getBiome(pos).is(NO_MOB_SPAWNS);
    }

    public static boolean blocks(EntityType<?> type, EntitySpawnReason reason) {
        return AUTOMATIC.contains(reason) && isSwitchedOff(type);
    }

    public static boolean isSwitchedOff(EntityType<?> type) {
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);

        if (!Graveyard.MOD_ID.equals(id.getNamespace()) || !MANAGED_MOBS.contains(id.getPath())) {
            return false;
        }

        GraveyardConfig config = Graveyard.getConfig();
        return config.structuresOnly() || !config.mobMayAppear(id.getPath());
    }
}
