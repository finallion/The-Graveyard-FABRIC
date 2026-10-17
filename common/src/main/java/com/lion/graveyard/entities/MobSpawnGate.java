package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

/// Decides if a graveyard mob may appear on its own.
/// Eggs, commands, buckets and player summons never pass through this check.
public final class MobSpawnGate {
    public static final List<String> MANAGED_MOBS = List.of(
            "ghoul", "revenant", "nightmare", "skeleton_creeper", "acolyte", "reaper",
            "corrupted_vindicator", "corrupted_pillager", "corrupted_evoker", "wraith", "raven", "magpie", "crow",
            "tormented_villager", "nameless_hanged");

    private static final Set<MobSpawnType> AUTOMATIC = EnumSet.of(
            MobSpawnType.NATURAL, MobSpawnType.CHUNK_GENERATION, MobSpawnType.STRUCTURE, MobSpawnType.SPAWNER,
            MobSpawnType.PATROL, MobSpawnType.EVENT, MobSpawnType.REINFORCEMENT);

    private static final TagKey<Biome> NO_MOB_SPAWNS = TagKey.create(Registries.BIOME, Graveyard.id("no_mob_spawns"));

    private MobSpawnGate() {
    }

    public static boolean inForbiddenBiome(LevelAccessor world, BlockPos pos, EntityType<?> type, MobSpawnType reason) {
        return (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION)
                && Graveyard.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace())
                && world.getBiome(pos).is(NO_MOB_SPAWNS);
    }

    public static boolean blocks(EntityType<?> type, MobSpawnType reason) {
        return AUTOMATIC.contains(reason) && isSwitchedOff(type);
    }

    public static boolean isSwitchedOff(EntityType<?> type) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);

        if (!Graveyard.MOD_ID.equals(id.getNamespace()) || !MANAGED_MOBS.contains(id.getPath())) {
            return false;
        }

        GraveyardConfig config = Graveyard.getConfig();
        return config.structuresOnly() || !config.mobMayAppear(id.getPath());
    }
}
