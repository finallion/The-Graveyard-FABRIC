package com.lion.graveyard.util;

import com.lion.graveyard.init.TGTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

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
    private static final Map<ResourceKey<Level>, Deque<DeathSite>> DEATH_SITES = new HashMap<>();

    private CrowAnchors() {}

    public static void recordDeath(Level world, BlockPos pos) {
        if (world.isClientSide()) {
            return;
        }

        Deque<DeathSite> sites = DEATH_SITES.computeIfAbsent(world.dimension(), key -> new ArrayDeque<>());
        sites.addLast(new DeathSite(pos.immutable(), world.getGameTime() + DEATH_MEMORY_TICKS));
        while (sites.size() > MAX_DEATH_SITES) {
            sites.removeFirst();
        }
    }

    @Nullable
    public static Anchor find(ServerLevel world, BlockPos origin) {
        BlockPos death = findDeathSite(world, origin);

        if (death != null) {
            return new Anchor(death, Kind.DEATH);
        }

        int fieldHits = 0;
        long nearestField = Long.MAX_VALUE;
        int fieldX = 0;
        int fieldZ = 0;
        int landmarkHits = 0;
        long nearestLandmark = Long.MAX_VALUE;
        int landmarkX = 0;
        int landmarkZ = 0;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -RADIUS; dx <= RADIUS; dx += STEP) {
            for (int dz = -RADIUS; dz <= RADIUS; dz += STEP) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                long distance = (long) dx * dx + (long) dz * dz;
                // Skip unloaded chunks. A heightmap lookup loads the chunk.
                if (!world.hasChunk(x >> 4, z >> 4)) {
                    continue;
                }

                int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                BlockState ground = world.getBlockState(pos.set(x, topY - 1, z));

                if (ground.is(Blocks.FARMLAND) || ground.is(BlockTags.CROPS)) {
                    fieldHits++;

                    if (distance < nearestField) {
                        nearestField = distance;
                        fieldX = x;
                        fieldZ = z;
                    }
                } else if (ground.is(TGTags.CROW_LANDMARKS)) {
                    landmarkHits++;

                    if (distance < nearestLandmark) {
                        nearestLandmark = distance;
                        landmarkX = x;
                        landmarkZ = z;
                    }
                } else {
                    // The heightmap ignores blocks without collision, such as skeletons.
                    BlockState surface = world.getBlockState(pos.set(x, topY, z));

                    if (surface.is(BlockTags.CROPS)) {
                        fieldHits++;

                        if (distance < nearestField) {
                            nearestField = distance;
                            fieldX = x;
                            fieldZ = z;
                        }
                    } else if (surface.is(TGTags.CORVID_BONES)) {
                        landmarkHits++;

                        if (distance < nearestLandmark) {
                            nearestLandmark = distance;
                            landmarkX = x;
                            landmarkZ = z;
                        }
                    }
                }
            }
        }

        if (fieldHits >= MIN_FIELD_HITS) {
            return new Anchor(surface(world, fieldX, fieldZ), Kind.FIELD);
        }

        if (landmarkHits >= MIN_LANDMARK_HITS) {
            return new Anchor(surface(world, landmarkX, landmarkZ), Kind.LANDMARK);
        }

        return null;
    }

    @Nullable
    private static BlockPos findDeathSite(ServerLevel world, BlockPos origin) {
        Deque<DeathSite> sites = DEATH_SITES.get(world.dimension());

        if (sites == null) {
            return null;
        }

        long now = world.getGameTime();
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

            double distance = site.pos().distSqr(origin);

            if (distance < DEATH_SEARCH_RADIUS_SQ && distance < closestDistance) {
                closest = site.pos();
                closestDistance = distance;
            }
        }
        return closest;
    }

    private static BlockPos surface(ServerLevel world, int x, int z) {
        return new BlockPos(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
    }

    private record DeathSite(BlockPos pos, long expiresAt) {
    }

    public record Anchor(BlockPos pos, Kind kind) {
    }

    public enum Kind {
        DEATH, FIELD, LANDMARK
    }
}
