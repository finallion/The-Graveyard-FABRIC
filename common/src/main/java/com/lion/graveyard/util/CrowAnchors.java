package com.lion.graveyard.util;

import com.lion.graveyard.init.TGTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class CrowAnchors {

    private static final int RADIUS = 32;
    /// Grid step. It keeps a scan at 1089 lookups.
    private static final int STEP = 2;
    private static final int MIN_FIELD_HITS = 8;
    private static final int MIN_LANDMARK_HITS = 2;

    private static final int DEATH_MEMORY_TICKS = 2400;
    private static final int MAX_DEATH_SITES = 64;
    private static final int DEATH_SEARCH_RADIUS_SQ = 48 * 48;

    /// Server thread only. The map is not thread-safe.
    private static final Map<RegistryKey<World>, Deque<DeathSite>> DEATH_SITES = new HashMap<>();

    private CrowAnchors() {
    }

    public static void recordDeath(World world, BlockPos pos) {
        if (world.isClient()) {
            return;
        }
        Deque<DeathSite> sites = DEATH_SITES.computeIfAbsent(world.getRegistryKey(), key -> new ArrayDeque<>());
        sites.addLast(new DeathSite(pos.toImmutable(), world.getTime() + DEATH_MEMORY_TICKS));
        while (sites.size() > MAX_DEATH_SITES) {
            sites.removeFirst();
        }
    }

    @Nullable
    public static Anchor find(ServerWorld world, BlockPos origin) {
        BlockPos death = findDeathSite(world, origin);
        if (death != null) {
            return new Anchor(death, Kind.DEATH);
        }

        int fieldHits = 0;
        long fieldX = 0L;
        long fieldZ = 0L;
        int landmarkHits = 0;
        long landmarkX = 0L;
        long landmarkZ = 0L;

        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -RADIUS; dx <= RADIUS; dx += STEP) {
            for (int dz = -RADIUS; dz <= RADIUS; dz += STEP) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                // Skip unloaded chunks. A heightmap lookup loads the chunk.
                if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                    continue;
                }
                int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                BlockState ground = world.getBlockState(pos.set(x, topY - 1, z));
                if (ground.isOf(Blocks.FARMLAND) || ground.isIn(BlockTags.CROPS)) {
                    fieldHits++;
                    fieldX += x;
                    fieldZ += z;
                } else if (ground.isIn(TGTags.CROW_LANDMARKS)) {
                    landmarkHits++;
                    landmarkX += x;
                    landmarkZ += z;
                } else {
                    // The heightmap ignores blocks without collision, such as skeletons.
                    BlockState surface = world.getBlockState(pos.set(x, topY, z));
                    if (surface.isIn(BlockTags.CROPS)) {
                        fieldHits++;
                        fieldX += x;
                        fieldZ += z;
                    } else if (surface.isIn(TGTags.CORVID_BONES)) {
                        landmarkHits++;
                        landmarkX += x;
                        landmarkZ += z;
                    }
                }
            }
        }

        if (fieldHits >= MIN_FIELD_HITS) {
            return new Anchor(surface(world, (int) (fieldX / fieldHits), (int) (fieldZ / fieldHits)), Kind.FIELD);
        }
        if (landmarkHits >= MIN_LANDMARK_HITS) {
            return new Anchor(surface(world, (int) (landmarkX / landmarkHits), (int) (landmarkZ / landmarkHits)), Kind.LANDMARK);
        }
        return null;
    }

    @Nullable
    private static BlockPos findDeathSite(ServerWorld world, BlockPos origin) {
        Deque<DeathSite> sites = DEATH_SITES.get(world.getRegistryKey());
        if (sites == null) {
            return null;
        }

        long now = world.getTime();
        BlockPos closest = null;
        double closestDistance = Double.MAX_VALUE;

        Iterator<DeathSite> it = sites.iterator();
        while (it.hasNext()) {
            DeathSite site = it.next();
            // `/time set` can move the time backwards.
            if (now >= site.expiresAt() || site.expiresAt() - now > DEATH_MEMORY_TICKS) {
                it.remove();
                continue;
            }
            double distance = site.pos().getSquaredDistance(origin);
            if (distance < DEATH_SEARCH_RADIUS_SQ && distance < closestDistance) {
                closest = site.pos();
                closestDistance = distance;
            }
        }
        return closest;
    }

    private static BlockPos surface(ServerWorld world, int x, int z) {
        return new BlockPos(x, world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z), z);
    }

    private record DeathSite(BlockPos pos, long expiresAt) {
    }

    public record Anchor(BlockPos pos, Kind kind) {
    }

    public enum Kind {
        DEATH, FIELD, LANDMARK
    }
}
