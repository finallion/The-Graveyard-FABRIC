package com.lion.graveyard.compat.legacy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.lion.graveyard.config.ConfigSchema;
import com.lion.graveyard.config.LegacyKeys;
import com.lion.graveyard.support.ModProject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class LegacyCorpus {
    public record Case(String name, String text) {
    }

    private LegacyCorpus() {}

    public static String sampleText() {
        return ModProject.snapshot("config-legacy-sample.json5");
    }

    public static JsonObject sample() {
        JsonReader reader = new JsonReader(new StringReader(sampleText()));
        reader.setLenient(true);
        return JsonParser.parseReader(reader).getAsJsonObject();
    }

    public static JsonElement find(JsonObject root, String path) {
        JsonElement current = root;

        for (String segment : path.split("\\.")) {
            if (!current.isJsonObject() || !current.getAsJsonObject().has(segment)) {
                return null;
            }

            current = current.getAsJsonObject().get(segment);
        }

        return current;
    }

    public static void put(JsonObject root, String path, JsonElement value) {
        String[] segments = path.split("\\.");
        JsonObject current = root;

        for (int i = 0; i < segments.length - 1; i++) {
            if (!current.has(segments[i]) || !current.get(segments[i]).isJsonObject()) {
                current.add(segments[i], new JsonObject());
            }

            current = current.getAsJsonObject(segments[i]);
        }

        current.add(segments[segments.length - 1], value);
    }

    public static void remove(JsonObject root, String path) {
        String parent = path.substring(0, path.lastIndexOf('.'));
        find(root, parent).getAsJsonObject().remove(path.substring(path.lastIndexOf('.') + 1));
    }

    public static JsonObject changed() {
        JsonObject copy = sample().deepCopy();

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            JsonElement value = find(copy, entry.oldPath());

            if (entry.rule() == LegacyKeys.Rule.SKIP) {
                put(copy, entry.oldPath(), new JsonPrimitive(!value.getAsBoolean()));
            } else {
                put(copy, entry.oldPath(), alter(value, ConfigSchema.INSTANCE.leaf(entry.newPath()), entry.rule()));
            }
        }

        return copy;
    }

    private static JsonElement alter(JsonElement value, ConfigSchema.Leaf leaf, LegacyKeys.Rule rule) {
        switch (leaf.kind()) {
            case BOOL:
                return new JsonPrimitive(!value.getAsBoolean());
            case INT:
                return new JsonPrimitive(value.getAsDouble() + (rule == LegacyKeys.Rule.FLOOR ? 1.5D : 1.0D));
            case STRING_LIST:
                JsonArray list = new JsonArray();
                list.add("item.minecraft.test_one");
                list.add("entity.minecraft.test_two");
                return list;
            default:
                double number = value.getAsDouble();
                return new JsonPrimitive(number <= 1.0D ? number * 0.5D : number + 1.0D);
        }
    }

    public static List<Case> handEdited() {
        List<Case> cases = new ArrayList<>();
        String lf = sampleText().replace("\r\n", "\n");

        syntax(cases, lf);
        values(cases);
        structure(cases);
        return cases;
    }

    public static List<Case> generated() {
        List<Case> cases = new ArrayList<>();

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            if (entry.rule() == LegacyKeys.Rule.SKIP || entry.rule() == LegacyKeys.Rule.MOB_SWITCH) {
                continue;
            }

            ConfigSchema.Leaf leaf = ConfigSchema.INSTANCE.leaf(entry.newPath());

            for (JsonElement value : variants(leaf, entry.rule())) {
                cases.add(edit("key " + entry.oldPath() + " = " + value, root -> put(root, entry.oldPath(), value)));
            }
        }

        for (String mob : ConfigSchema.ALL_MOB_IDS) {
            for (JsonElement value : variants(ConfigSchema.INSTANCE.leaf("general.structuresOnly"), LegacyKeys.Rule.COPY)) {
                cases.add(edit("spawn switch " + mob + " = " + value, root -> put(root, "mobSpawnEnabled." + mob, value)));

                if (ConfigSchema.isFullMob(mob)) {
                    cases.add(edit("own switch " + mob + " = " + value, root -> put(root, "mobConfigEntries." + mob + ".enabled", value)));
                }
            }
        }

        return cases;
    }

    private static List<JsonElement> variants(ConfigSchema.Leaf leaf, LegacyKeys.Rule rule) {
        List<JsonElement> list = new ArrayList<>();

        switch (leaf.kind()) {
            case BOOL -> {
                list.add(new JsonPrimitive("true"));
                list.add(new JsonPrimitive("false"));
                list.add(new JsonPrimitive("abc"));
                list.add(new JsonPrimitive(1));
                list.add(JsonNull.INSTANCE);
                list.add(new JsonPrimitive(true));
                list.add(new JsonPrimitive(false));
            }
            case INT -> {
                for (int number : new int[] {-5, 0, 1, 7, 65, 257, 300, 100000000, Integer.MAX_VALUE}) {
                    list.add(new JsonPrimitive(number));
                }

                list.add(new JsonPrimitive("25"));
                list.add(new JsonPrimitive(3000000000L));
                list.add(new JsonPrimitive(2.5D));
                list.add(new JsonPrimitive(7.0D));
                list.add(new JsonPrimitive("abc"));
                list.add(JsonNull.INSTANCE);

                if (rule == LegacyKeys.Rule.FLOOR) {
                    list.add(new JsonPrimitive(-2.5D));
                    list.add(new JsonPrimitive(0.5D));
                    list.add(new JsonPrimitive(8.9D));
                }
            }
            case DOUBLE, FLOAT, MULTIPLIER -> {
                for (double number : new double[] {-5.0D, -0.001D, 0.0D, 0.01D, 0.05D, 0.5D, 1.0D, 1.5D, 17.0D, 5000.0D, 1.0e9D, 1.0e50D}) {
                    list.add(new JsonPrimitive(number));
                }

                list.add(new JsonPrimitive("0.5"));
                list.add(new JsonPrimitive("NaN"));
                list.add(new JsonPrimitive("abc"));
                list.add(JsonNull.INSTANCE);
            }
            case STRING_LIST -> {
                list.add(new JsonArray());
                list.add(strings("a.b", "c.d"));
                list.add(numbers());
                list.add(JsonNull.INSTANCE);
                list.add(new JsonPrimitive("text"));
                list.add(objectInArray());
            }
        }

        return list;
    }

    private static JsonArray strings(String... items) {
        JsonArray array = new JsonArray();

        for (String item : items) {
            array.add(item);
        }

        return array;
    }

    private static JsonArray numbers() {
        JsonArray array = new JsonArray();
        array.add(1);
        array.add(2.5D);
        array.add(true);
        return array;
    }

    private static JsonArray objectInArray() {
        JsonArray array = new JsonArray();
        array.add(new JsonObject());
        return array;
    }

    private static Case edit(String name, Consumer<JsonObject> change) {
        JsonObject root = sample();
        change.accept(root);
        return new Case(name, root.toString());
    }

    private static void syntax(List<Case> cases, String lf) {
        cases.add(new Case("bom", "﻿" + lf));
        cases.add(new Case("crlf", lf.replace("\n", "\r\n")));
        cases.add(new Case("cr only", lf.replace("\n", "\r")));
        cases.add(new Case("trailing comma in object", lf.replace("\"wraithGriefing\": true\n", "\"wraithGriefing\": true,\n")));
        cases.add(new Case("trailing object", lf + "{}"));
        cases.add(new Case("trailing garbage", lf + "garbage"));
        cases.add(new Case("trailing block comment", lf + "/* tail */"));
        cases.add(new Case("trailing line comment", lf + "// tail\n"));
        cases.add(new Case("trailing blank lines", lf + "\n\n   \n"));
        cases.add(new Case("line comment after value", lf.replace("\"structuresOnly\": false,", "\"structuresOnly\": true, // note")));
        cases.add(new Case("line comment on own line", lf.replace("  \"booleanEntries\": {", "  // booleans\n  \"booleanEntries\": {")));
        cases.add(new Case("hash comment on own line", lf.replace("  \"booleanEntries\": {", "  # booleans\n  \"booleanEntries\": {")));
        cases.add(new Case("block comment after value", lf.replace("\"structuresOnly\": false,", "\"structuresOnly\": true, /* note */")));
        cases.add(new Case("single quoted key", lf.replace("\"structuresOnly\": false", "'structuresOnly': true")));
        cases.add(new Case("unquoted key", lf.replace("\"structuresOnly\": false", "structuresOnly: true")));
        cases.add(new Case("escaped key", lf.replace("\"structuresOnly\"", "\"structures\\u004fnly\"").replace("structures\\u004fnly\": false", "structures\\u004fnly\": true")));
        cases.add(new Case("upper case key is unknown", lf.replace("\"structuresOnly\": false", "\"STRUCTURESONLY\": true")));
        cases.add(new Case("empty text", ""));
        cases.add(new Case("blank text", "  \n "));
        cases.add(new Case("null literal", "null"));
        cases.add(new Case("array", "[]"));
        cases.add(new Case("empty object", "{}"));
        cases.add(new Case("truncated half", lf.substring(0, lf.length() / 2)));
        cases.add(new Case("extra closing brace", lf + "}"));
        cases.add(new Case("duplicate map key", lf.replace("\"numberEntries\": {", "\"numberEntries\": {\"corvidTheftChance\": 0.9, \"corvidTheftChance\": 0.8,")));
        cases.add(new Case("duplicate section", lf.substring(0, lf.lastIndexOf('}')).stripTrailing() + ",\n\"booleanEntries\": {\"structuresOnly\": true}\n}"));
        cases.add(new Case("duplicate entry field", lf.replace("\"structuresOnly\": false,", "\"structuresOnly\": false, \"structuresOnly\": true,")));
        cases.add(new Case("number without leading zero", lf.replace("\"corvidTheftChance\": 0.1", "\"corvidTheftChance\": .5")));
        cases.add(new Case("number with exponent", lf.replace("\"corvidTheftChance\": 0.1", "\"corvidTheftChance\": 5e-1")));
        cases.add(new Case("hex number", lf.replace("\"corvidTheftChance\": 0.1", "\"corvidTheftChance\": 0x10")));
        cases.add(new Case("unquoted NaN", lf.replace("\"corvidTheftChance\": 0.1", "\"corvidTheftChance\": NaN")));
        cases.add(new Case("unquoted Infinity", lf.replace("\"corvidTheftChance\": 0.1", "\"corvidTheftChance\": Infinity")));
        cases.add(new Case("unquoted minus Infinity", lf.replace("\"skeletonCreeperExplosionRadius\": 3.5", "\"skeletonCreeperExplosionRadius\": -Infinity")));
        cases.add(new Case("quoted NaN in numbers", lf.replace("\"corvidTheftChance\": 0.1", "\"corvidTheftChance\": \"NaN\"")));
        cases.add(new Case("tab indent", lf.replace("\n  ", "\n\t")));
    }

    private static void values(List<Case> cases) {
        cases.add(edit("quoted false", root -> put(root, "booleanEntries.crowsEatCrops", new JsonPrimitive("false"))));
        cases.add(edit("quoted true", root -> put(root, "booleanEntries.structuresOnly", new JsonPrimitive("true"))));
        cases.add(edit("quoted 25", root -> put(root, "hordeConfigEntries.horde_spawn.minDistance", new JsonPrimitive("25"))));
        cases.add(edit("quoted chance", root -> put(root, "structureConfigEntries.altar.chance", new JsonPrimitive("0.5"))));
        cases.add(edit("quoted weight", root -> put(root, "mobConfigEntries.ghoul.weight", new JsonPrimitive("3"))));
        cases.add(edit("bool as number", root -> put(root, "booleanEntries.structuresOnly", new JsonPrimitive(1))));
        cases.add(edit("number as bool", root -> put(root, "structureConfigEntries.altar.chance", new JsonPrimitive(true))));
        cases.add(edit("fraction in int", root -> put(root, "mobConfigEntries.ghoul.weight", new JsonPrimitive(2.5D))));
        cases.add(edit("int too large", root -> put(root, "mobConfigEntries.ghoul.weight", new JsonPrimitive(3000000000L))));
        cases.add(edit("whole double in int", root -> put(root, "mobConfigEntries.ghoul.weight", new JsonPrimitive(6.0D))));
        cases.add(edit("NaN chance in structure", root -> put(root, "structureConfigEntries.altar.chance", new JsonPrimitive(Double.NaN))));
        cases.add(edit("quoted NaN chance in structure", root -> put(root, "structureConfigEntries.altar.chance", new JsonPrimitive("NaN"))));
        cases.add(edit("NaN chance in horde", root -> put(root, "hordeConfigEntries.horde_spawn.chancePerCheck", new JsonPrimitive(Double.NaN))));
        cases.add(edit("Infinity chance in structure", root -> put(root, "structureConfigEntries.altar.chance", new JsonPrimitive(Double.POSITIVE_INFINITY))));
        cases.add(edit("NaN in number entries", root -> put(root, "numberEntries.corvidTheftChance", new JsonPrimitive(Double.NaN))));
        cases.add(edit("Infinity in unknown number entry", root -> put(root, "numberEntries.other", new JsonPrimitive(Double.POSITIVE_INFINITY))));
        cases.add(edit("NaN multiplier", root -> put(root, "mobConfigEntries.ghoul.healthMultiplier", new JsonPrimitive(Double.NaN))));
        cases.add(edit("NaN champion armor", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.armor", new JsonPrimitive(Double.NaN))));
        cases.add(edit("NaN champion float", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.healthInHuntingPhase", new JsonPrimitive(Double.NaN))));
        cases.add(edit("float overflow", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.healthInCastingPhase", new JsonPrimitive(1.0e50D))));
        cases.add(edit("negative multiplier", root -> put(root, "mobConfigEntries.ghoul.damageMultiplier", new JsonPrimitive(-3))));
        cases.add(edit("tiny multiplier", root -> put(root, "mobConfigEntries.ghoul.speedMultiplier", new JsonPrimitive(0.001D))));
        cases.add(edit("offers floored", root -> {
            put(root, "numberEntries.namelessHangedMinOffers", new JsonPrimitive(8.9D));
            put(root, "numberEntries.namelessHangedMaxOffers", new JsonPrimitive(3.0D));
        }));
        cases.add(edit("offers quoted", root -> put(root, "numberEntries.namelessHangedMinOffers", new JsonPrimitive("9"))));
        cases.add(edit("offers negative", root -> put(root, "numberEntries.namelessHangedMinOffers", new JsonPrimitive(-4))));
        cases.add(edit("group order", root -> {
            put(root, "mobConfigEntries.revenant.weight", new JsonPrimitive(4));
            put(root, "mobConfigEntries.revenant.minGroup", new JsonPrimitive(0));
            put(root, "mobConfigEntries.revenant.maxGroup", new JsonPrimitive(0));
        }));
        cases.add(edit("group with weight zero", root -> {
            put(root, "mobConfigEntries.revenant.weight", new JsonPrimitive(0));
            put(root, "mobConfigEntries.revenant.minGroup", new JsonPrimitive(0));
            put(root, "mobConfigEntries.revenant.maxGroup", new JsonPrimitive(-3));
        }));
        cases.add(edit("distance order", root -> {
            put(root, "hordeConfigEntries.horde_spawn.minDistance", new JsonPrimitive(60));
            put(root, "hordeConfigEntries.horde_spawn.maxDistance", new JsonPrimitive(10));
        }));
        cases.add(edit("distance below one", root -> put(root, "hordeConfigEntries.horde_spawn.minDistance", new JsonPrimitive(-9))));
        cases.add(edit("switch spawn false", root -> put(root, "mobSpawnEnabled.ghoul", new JsonPrimitive(false))));
        cases.add(edit("switch own false", root -> put(root, "mobConfigEntries.ghoul.enabled", new JsonPrimitive(false))));
        cases.add(edit("switch own false on wraith", root -> put(root, "mobConfigEntries.wraith.enabled", new JsonPrimitive(false))));
        cases.add(edit("switch own true on reaper", root -> put(root, "mobConfigEntries.reaper.enabled", new JsonPrimitive(true))));
        cases.add(edit("switch spawn quoted", root -> put(root, "mobSpawnEnabled.crow", new JsonPrimitive("false"))));
        cases.add(edit("switch spawn null", root -> put(root, "mobSpawnEnabled.crow", JsonNull.INSTANCE)));
        cases.add(edit("switch spawn missing", root -> remove(root, "mobSpawnEnabled.raven")));
        cases.add(edit("switch spawn unknown mob", root -> put(root, "mobSpawnEnabled.made_up", new JsonPrimitive(false))));
        cases.add(edit("tormented light changed", root -> put(root, "booleanEntries.tormentedVillagerLight", new JsonPrimitive(false))));
        cases.add(edit("list of numbers", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.isBossSummonableItem", numbers())));
        cases.add(edit("list as text", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.isBossSummonableItem", new JsonPrimitive("text"))));
        cases.add(edit("list null", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.isBloodCollectableEntity", JsonNull.INSTANCE)));
        cases.add(edit("list empty", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion.isBloodCollectableEntity", new JsonArray())));
    }

    private static void structure(List<Case> cases) {
        cases.add(edit("crypt with chance only", root -> put(root, "structureConfigEntries.crypt", object("chance", new JsonPrimitive(0.5D)))));
        cases.add(edit("lich prison empty", root -> put(root, "structureConfigEntries.lich_prison", new JsonObject())));
        cases.add(edit("structure null", root -> put(root, "structureConfigEntries.altar", JsonNull.INSTANCE)));
        cases.add(edit("ghoul empty", root -> put(root, "mobConfigEntries.ghoul", new JsonObject())));
        cases.add(edit("ghoul without enabled", root -> remove(root, "mobConfigEntries.ghoul.enabled")));
        cases.add(edit("ghoul without burn flag", root -> remove(root, "mobConfigEntries.ghoul.canBurnInSunlight")));
        cases.add(edit("ghoul null", root -> put(root, "mobConfigEntries.ghoul", JsonNull.INSTANCE)));
        cases.add(edit("reaper without enabled", root -> remove(root, "mobConfigEntries.reaper.enabled")));
        cases.add(edit("horde with max distance only", root -> put(root, "hordeConfigEntries.horde_spawn", object("maxDistance", new JsonPrimitive(10)))));
        cases.add(edit("horde empty", root -> put(root, "hordeConfigEntries.horde_spawn", new JsonObject())));
        cases.add(edit("horde null", root -> put(root, "hordeConfigEntries.horde_spawn", JsonNull.INSTANCE)));
        cases.add(edit("particle empty", root -> put(root, "particleConfigEntries.graveyard_fog_particle", new JsonObject())));
        cases.add(edit("champion with armor only", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion", object("armor", new JsonPrimitive(3)))));
        cases.add(edit("champion empty", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion", new JsonObject())));
        cases.add(edit("champion null", root -> put(root, "corruptedChampionConfigEntries.corrupted_champion", JsonNull.INSTANCE)));
        cases.add(edit("section removed horde", root -> root.remove("hordeConfigEntries")));
        cases.add(edit("section removed booleans", root -> root.remove("booleanEntries")));
        cases.add(edit("section removed numbers", root -> root.remove("numberEntries")));
        cases.add(edit("section removed mob switches", root -> root.remove("mobSpawnEnabled")));
        cases.add(edit("section removed champion", root -> root.remove("corruptedChampionConfigEntries")));
        cases.add(edit("section null", root -> root.add("numberEntries", JsonNull.INSTANCE)));
        cases.add(edit("section empty", root -> root.add("mobConfigEntries", new JsonObject())));
        cases.add(edit("section wrong type", root -> root.add("booleanEntries", new JsonPrimitive(5))));
        cases.add(edit("section is array", root -> root.add("structureConfigEntries", new JsonArray())));
        cases.add(edit("entry wrong type", root -> put(root, "mobConfigEntries.ghoul", new JsonPrimitive("x"))));
        cases.add(edit("unknown root", root -> root.add("somethingNew", new JsonPrimitive(1))));
        cases.add(edit("unknown id and field", root -> {
            put(root, "mobConfigEntries.made_up.enabled", new JsonPrimitive(false));
            put(root, "structureConfigEntries.altar.extraField", new JsonPrimitive(5));
            put(root, "booleanEntries.madeUp", new JsonPrimitive(true));
        }));
        cases.add(edit("unknown structure with NaN", root -> put(root, "structureConfigEntries.made_up", object("chance", new JsonPrimitive(Double.NaN)))));
        cases.add(edit("unknown structure with bad type", root -> put(root, "structureConfigEntries.made_up", object("enabled", new JsonPrimitive(5)))));
        cases.add(edit("nameless hanged in mob entries", root -> put(root, "mobConfigEntries.nameless_hanged", object("enabled", new JsonPrimitive(false)))));
    }

    private static JsonObject object(String key, JsonElement value) {
        JsonObject object = new JsonObject();
        object.add(key, value);
        return object;
    }
}
