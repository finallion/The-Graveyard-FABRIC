package com.lion.graveyard.pose;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/// One arrangement of the bone parts, read from a file. A pose holds no randomness of its own:
/// which pose a block gets is drawn, the pose itself is fixed.
///
/// Positions are in pixels with y running **up** from the bottom of the block and the origin in its
/// middle, because that is how a builder thinks about a block. The renderer turns that into the
/// y-down frame the model parts want.
public record SkeletalPose(Identifier id, List<Placement> placements) {

    /// One part, placed. `optional` marks a bone that may be missing on a given remains - the one
    /// piece of variation a pose carries, and it can only ever remove a part, never move one.
    public record Placement(String part, float x, float y, float z,
                            float pitch, float yaw, float roll, float scale, boolean optional) {
    }

    public static SkeletalPose fromJson(Identifier id, JsonObject json) {
        JsonElement parts = json.get("parts");
        if (parts == null || !parts.isJsonArray()) {
            throw new IllegalArgumentException("pose has no \"parts\" array");
        }

        List<Placement> placements = new ArrayList<>();
        for (JsonElement element : parts.getAsJsonArray()) {
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("\"parts\" holds something that is not an object");
            }

            placements.add(placement(element.getAsJsonObject()));
        }

        if (placements.isEmpty()) {
            throw new IllegalArgumentException("pose places no parts");
        }

        return new SkeletalPose(id, List.copyOf(placements));
    }

    private static Placement placement(JsonObject json) {
        JsonElement part = json.get("part");
        if (part == null || !part.isJsonPrimitive()) {
            throw new IllegalArgumentException("a part entry has no \"part\"");
        }

        float[] pivot = triple(json, "pivot");
        float[] rotation = triple(json, "rotation");
        float scale = json.has("scale") ? json.get("scale").getAsFloat() : 1.0F;
        boolean optional = json.has("optional") && json.get("optional").getAsBoolean();

        return new Placement(part.getAsString(), pivot[0], pivot[1], pivot[2],
                rotation[0], rotation[1], rotation[2], scale, optional);
    }

    /// Missing means zero, so a part that sits at the origin unrotated needs no fields at all.
    private static float[] triple(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null) {
            return new float[3];
        }

        if (!element.isJsonArray() || element.getAsJsonArray().size() != 3) {
            throw new IllegalArgumentException("\"" + key + "\" must be three numbers");
        }

        JsonArray array = element.getAsJsonArray();
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }
}
