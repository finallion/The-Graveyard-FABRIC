package com.lion.graveyard.pose;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.Graveyard;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/// Every pose the client knows, and the rule that says which one a given remains shows.
///
/// Poses are client resources, not world data: only the renderer needs the geometry, and keeping
/// them out of the block entity means nothing has to be synchronised and no world carries a copy of
/// a file it cannot read.
public final class SkeletalPoses {

    /// How often a bone marked optional is actually missing.
    private static final int MISSING_PART_PERCENT = 30;

    private static final Map<Identifier, SkeletalPose> POSES = new LinkedHashMap<>();

    private SkeletalPoses() {
    }

    public static void clear() {
        POSES.clear();
    }

    /// Replaces the whole set on every resource reload; appending would leave a pose behind after
    /// its file was deleted.
    public static void load(Map<Identifier, JsonElement> files) {
        clear();
        files.forEach((id, element) -> {
            if (element.isJsonObject()) {
                put(id, element.getAsJsonObject());
            } else {
                Graveyard.getLogger().error("Skipping skeletal pose {}: not an object.", id);
            }
        });

        Graveyard.getLogger().info("Loaded {} skeletal poses.", count());
    }

    public static void put(Identifier id, JsonObject json) {
        try {
            POSES.put(id, SkeletalPose.fromJson(id, json));
        } catch (RuntimeException e) {
            Graveyard.getLogger().error("Skipping skeletal pose {}: {}", id, e.getMessage());
        }
    }

    public static int count() {
        return POSES.size();
    }

    /// Picks by highest score rather than by index, so adding a pose later steals roughly one in N
    /// of the remains already standing instead of reshuffling every one of them.
    @Nullable
    public static SkeletalPose pick(long seed) {
        SkeletalPose best = null;
        long bestScore = Long.MIN_VALUE;

        for (Map.Entry<Identifier, SkeletalPose> entry : POSES.entrySet()) {
            long score = mix(seed ^ entry.getKey().hashCode());
            if (score > bestScore) {
                bestScore = score;
                best = entry.getValue();
            }
        }

        return best;
    }

    /// Whether the optional bone at this index is one of the missing ones. Decided from the seed, so
    /// the same remains loses the same bone every time it is drawn.
    public static boolean isMissing(long seed, int index) {
        return Math.floorMod(mix(seed + 0x9E3779B97F4A7C15L * (index + 1)), 100) < MISSING_PART_PERCENT;
    }

    /// splitmix64's finalizer: a cheap way to get well-spread bits out of neighbouring positions.
    private static long mix(long value) {
        long z = value + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
