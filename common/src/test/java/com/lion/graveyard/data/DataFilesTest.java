package com.lion.graveyard.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.support.DataIndex;
import com.lion.graveyard.support.DataRefs;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.RegistryIds;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataFilesTest {
    private static final DataIndex INDEX = DataIndex.mod();

    private static final Set<String> BLOCKS_WITHOUT_LOOT_TABLE = blocksThatDropNothingByDesign();

    private static Set<String> blocksThatDropNothingByDesign() {
        Set<String> ids = new java.util.TreeSet<>();
        ids.add("lore_book");
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("registerBlock\\(\\s*\"([^\"]+)\"([^;]*);").matcher(
                com.lion.graveyard.support.Sources.code(ModProject.JAVA.resolve("init/TGBlocks.java")));
        while (m.find()) {
            if (m.group(2).contains("overrideLootTable")
                    || m.group(2).contains("noLootTable")) {
                ids.add(m.group(1));
            }
        }
        return ids;
    }

    static Stream<DynamicTest> referenceTests(String folder) {
        Path dir = ModProject.DATA.resolve(folder);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".json"), () -> {
                            List<String> problems = DataRefs.problems(Json.parseStrict(ModProject.read(file)), INDEX);
                            assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                        }));
    }

    static List<String> tagProblems(String json, java.util.function.Predicate<String> registered, Set<String> tags) {
        List<String> problems = new ArrayList<>();
        JsonObject tag = Json.parseObject(json);
        JsonArray values = tag.has("values") ? tag.getAsJsonArray("values") : new JsonArray();
        for (JsonElement value : values) {
            String raw = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();

            if (!raw.startsWith("#") && !ModProject.isModId(raw)) {
                continue;
            }

            if (raw.startsWith("#")) {
                if (ModProject.isModId(raw.substring(1)) && !tags.contains(ModProject.stripNamespace(raw.substring(1)))) {
                    problems.add("missing tag " + raw);
                }
            } else if (!registered.test(ModProject.stripNamespace(raw))) {
                problems.add("unregistered " + raw);
            }
        }
        return problems;
    }

    private static final Set<String> RENAMED_ENTITY_TARGETS = Set.of("killer", "killer_player", "direct_killer");

    static List<String> renamedEntityTargets(JsonElement element, String path) {
        List<String> found = new ArrayList<>();

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            if (RENAMED_ENTITY_TARGETS.contains(element.getAsString())) {
                found.add(path + ": " + element.getAsString());
            }
        } else if (element.isJsonArray()) {
            int index = 0;
            for (JsonElement child : element.getAsJsonArray()) {
                found.addAll(renamedEntityTargets(child, path + "[" + index++ + "]"));
            }
        } else if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                found.addAll(renamedEntityTargets(entry.getValue(), path + "." + entry.getKey()));
            }
        }

        return found;
    }

    @TestFactory
    Stream<DynamicTest> lootTablesUseNoRenamedEntityTargets() {
        Path dir = ModProject.DATA.resolve(ModProject.LOOT_TABLES);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    List<String> found = renamedEntityTargets(Json.parseStrict(ModProject.read(file)), "$");
                    assertTrue(found.isEmpty(), () -> "Use attacker, attacking_player or direct_attacker: " + found);
                }));
    }

    @Test
    void renamedTargetCheckFindsOldNames() {
        assertEquals(2, renamedEntityTargets(Json.parseStrict("{\"conditions\":[{\"entity\":\"killer_player\"},{\"entity\":\"attacker\"},{\"x\":[\"direct_killer\"]}]}"), "$").size());
    }

    @TestFactory
    Stream<DynamicTest> lootTablesReferenceExistingIds() {
        return referenceTests(ModProject.LOOT_TABLES);
    }

    @TestFactory
    Stream<DynamicTest> recipesReferenceExistingIds() {
        return referenceTests(ModProject.RECIPES);
    }

    @TestFactory
    Stream<DynamicTest> advancementsReferenceExistingIds() {
        return referenceTests(ModProject.ADVANCEMENTS);
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredBlockHasALootTable() {
        return RegistryIds.of(RegistryIds.BLOCK).stream()
                .map(id -> DynamicTest.dynamicTest(id, () -> {
                    if (BLOCKS_WITHOUT_LOOT_TABLE.contains(id)) {
                        return;
                    }

                    assertTrue(ModProject.DATA.resolve(ModProject.BLOCK_LOOT).resolve(id + ".json").toFile().isFile(), "No loot table for block " + id);
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyBlockLootTableBelongsToARegisteredBlock() {
        Path dir = ModProject.DATA.resolve(ModProject.BLOCK_LOOT);
        Set<String> blocks = RegistryIds.of(RegistryIds.BLOCK);
        return ModProject.files(dir, ".json").stream()
                .map(file -> {
                    String id = ModProject.idOf(dir, file, ".json");
                    return DynamicTest.dynamicTest(id, () -> assertTrue(blocks.contains(id), "Loot table without a registered block: " + id));
                });
    }

    @TestFactory
    Stream<DynamicTest> everyRecipeResultIsARegisteredItem() {
        Path dir = ModProject.DATA.resolve(ModProject.RECIPES);
        Set<String> items = RegistryIds.of(RegistryIds.ITEM);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    JsonObject recipe = Json.parseObject(ModProject.read(file));

                    if (!recipe.has("result")) {
                        return;
                    }

                    JsonElement result = recipe.get("result");
                    String id = result.isJsonObject()
                            ? (result.getAsJsonObject().has("id") ? result.getAsJsonObject().get("id").getAsString() : null)
                            : result.getAsString();

                    if (id != null && ModProject.isModId(id)) {
                        assertTrue(items.contains(ModProject.stripNamespace(id)), "Result is not a registered item: " + id);
                    }
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyRecipeIngredientUsesTheStringForm() {
        Path dir = ModProject.DATA.resolve(ModProject.RECIPES);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    JsonObject recipe = Json.parseObject(ModProject.read(file));
                    List<JsonElement> ingredients = new ArrayList<>();

                    if (recipe.has("key")) {
                        recipe.getAsJsonObject("key").entrySet().forEach(entry -> ingredients.add(entry.getValue()));
                    }

                    if (recipe.has("ingredients")) {
                        recipe.getAsJsonArray("ingredients").forEach(ingredients::add);
                    }

                    if (recipe.has("ingredient")) {
                        ingredients.add(recipe.get("ingredient"));
                    }

                    for (JsonElement ingredient : ingredients) {
                        if (ingredient.isJsonArray()) {
                            ingredient.getAsJsonArray().forEach(part -> assertTrue(part.isJsonPrimitive(), "Ingredient list holds a non-string entry"));
                        } else {
                            assertTrue(ingredient.isJsonPrimitive(), "Ingredient is not a string: " + ingredient);
                        }
                    }
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyAdvancementTitleAndDescriptionHasAnEnglishText() {
        Path dir = ModProject.DATA.resolve(ModProject.ADVANCEMENTS);
        Map<String, String> english = Json.stringMap(ModProject.read(ModProject.ASSETS.resolve("lang/en_us.json")));
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    List<String> missing = new ArrayList<>();
                    for (Json.KeyedString entry : Json.strings(Json.parseStrict(ModProject.read(file)))) {
                        if (entry.key().equals("translate") && !english.containsKey(entry.value())) {
                            missing.add(entry.value());
                        }
                    }
                    assertTrue(missing.isEmpty(), "Missing lang keys: " + missing);
                }));
    }

    @TestFactory
    Stream<DynamicTest> blockTagsContainOnlyRegisteredBlocksAndExistingTags() {
        Path dir = ModProject.DATA.resolve(ModProject.TAGS_BLOCKS);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    List<String> problems = tagProblems(ModProject.read(file), RegistryIds::isBlock, INDEX.blockTags);
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @TestFactory
    Stream<DynamicTest> itemTagsContainOnlyRegisteredItemsAndExistingTags() {
        Path dir = ModProject.DATA.resolve(ModProject.TAGS_ITEMS);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    List<String> problems = tagProblems(ModProject.read(file), RegistryIds::isItem, INDEX.itemTags);
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @TestFactory
    Stream<DynamicTest> biomeTagsReferenceExistingTags() {
        Path dir = ModProject.DATA.resolve(ModProject.TAGS_BIOMES);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    List<String> problems = tagProblems(ModProject.read(file), id -> true, INDEX.biomeTags);
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @Test
    void referenceCheckFindsMissingItemTagAndLootTable() {
        DataIndex index = new DataIndex(
                Set.of("b"),
                Set.of("i"),
                Set.of(),
                Set.of(),
                Set.of("chests/c"),
                Set.of(),
                Set.of(),
                Set.of("t"),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of()
        );
        String json = "{\"a\": {\"item\": \"graveyard:i\"}, \"b\": {\"item\": \"graveyard:ghost\"}, \"c\": {\"tag\": \"graveyard:nope\"},"
                + " \"d\": {\"type\": \"minecraft:loot_table\", \"name\": \"graveyard:chests/zzz\"}, \"e\": {\"item\": \"minecraft:stone\"}}";
        assertEquals(3, DataRefs.problems(Json.parseStrict(json), index).size());
    }

    @Test
    void tagCheckFindsUnregisteredValue() {
        assertEquals(
                List.of("unregistered graveyard:ghost"),
                tagProblems("{\"values\": [\"minecraft:stone\", \"graveyard:ghost\", \"graveyard:real\"]}", id -> id.equals("real"), Set.of())
        );
    }
}
