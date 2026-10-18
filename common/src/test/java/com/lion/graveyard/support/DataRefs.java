package com.lion.graveyard.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DataRefs {
    private DataRefs() {}

    public static List<String> problems(JsonElement root, DataIndex index) {
        List<String> problems = new ArrayList<>();
        visit(root, "", null, index, problems);
        return problems;
    }

    private static void visit(
            JsonElement element,
            String key,
            JsonObject owner,
            DataIndex index,
            List<String> problems
    ) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                visit(entry.getValue(), entry.getKey(), object, index, problems);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                visit(child, key, owner, index, problems);
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            check(key, element.getAsString(), owner, index, problems);
        }
    }

    private static void check(String key, String value, JsonObject owner, DataIndex index, List<String> problems) {
        String reference = value.startsWith("#") ? value.substring(1) : value;

        if (!ModProject.isModId(reference)) {
            return;
        }

        String id = ModProject.stripNamespace(reference);
        boolean ok;
        switch (key) {
            case "tag" -> ok = index.anyTag(id);
            case "loot_table", "LootTable" -> ok = index.lootTables.contains(id);
            case "parent" -> ok = index.advancements.contains(id);
            case "recipes" -> ok = index.recipes.contains(id);
            case "structure", "structures" -> ok = index.structureFeatures.contains(id);
            case "block" -> ok = index.blocks.contains(id);
            case "name", "value" -> {
                String type = owner != null && owner.has("type") ? owner.get("type").getAsString() : "";
                ok = type.equals("minecraft:loot_table") ? index.lootTables.contains(id) : index.items.contains(id) || index.blocks.contains(id);
            }
            default -> ok = value.startsWith("#") ? index.anyTag(id) : index.anyKnownId(id);
        }

        if (!ok) {
            problems.add(key + " -> " + value);
        }
    }
}
