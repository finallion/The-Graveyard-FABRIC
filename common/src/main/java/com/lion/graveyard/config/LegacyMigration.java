package com.lion.graveyard.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LegacyMigration {
    private static final Gson GSON = new Gson();

    private static final Set<String> SWITCH_ONLY_MOBS = Set.of(
            "acolyte",
            "wraith",
            "corrupted_vindicator",
            "corrupted_pillager",
            "corrupted_evoker"
    );

    private static final Map<String, String> OLD_PATH_BY_NEW_PATH = oldPathsByNewPath();
    private static final Set<String> KNOWN_PREFIXES = knownPrefixes();

    private LegacyMigration() {}

    public static ConfigReader.Result migrate(String text) {
        String joined = joinLines(text);
        LegacyFile file;

        try {
            file = GSON.fromJson(joined, LegacyFile.class);
        } catch (RuntimeException exception) {
            return rejected(String.valueOf(exception.getMessage()));
        }

        if (file == null) {
            return rejected("The file is empty.");
        }

        if (holdsNonFiniteNumber(file)) {
            return rejected("The file holds a number that is not finite.");
        }

        ConfigReader.Result result = new ConfigReader.Result(ConfigValues.defaults(), null);

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            if (entry.rule() == LegacyKeys.Rule.COPY || entry.rule() == LegacyKeys.Rule.FLOOR) {
                copyValue(file, entry, result);
            }
        }

        for (String mob : ConfigSchema.ALL_MOB_IDS) {
            mergeMobSwitch(file, mob, result);
        }

        List<String> raised = new ArrayList<>();
        ConfigRules.apply(result.values(), raised);

        for (String path : raised) {
            addOnce(result.reset(), OLD_PATH_BY_NEW_PATH.getOrDefault(path, path));
        }

        collectUnknownKeys(joined, result.dropped());
        return result;
    }

    private static ConfigReader.Result rejected(String reason) {
        return new ConfigReader.Result(ConfigValues.defaults(), reason);
    }

    private static String joinLines(String text) {
        StringBuilder joined = new StringBuilder();

        new BufferedReader(new StringReader(text)).lines()
                .filter(line -> !line.trim().startsWith("//"))
                .forEach(joined::append);

        return joined.toString();
    }

    private static boolean holdsNonFiniteNumber(LegacyFile file) {
        if (file.numberEntries != null) {
            for (Double value : file.numberEntries.values()) {
                if (value != null && !Double.isFinite(value)) {
                    return true;
                }
            }
        }

        if (file.structureConfigEntries != null) {
            for (LegacyFile.Structure entry : file.structureConfigEntries.values()) {
                if (entry != null && entry.chance != null && entry.chance.isNaN()) {
                    return true;
                }
            }
        }

        if (file.hordeConfigEntries != null) {
            for (LegacyFile.Horde entry : file.hordeConfigEntries.values()) {
                if (entry != null && entry.chancePerCheck != null && entry.chancePerCheck.isNaN()) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void copyValue(LegacyFile file, LegacyKeys.Entry entry, ConfigReader.Result result) {
        JsonElement element = toJson(rawValue(file, entry.oldPath()));

        if (element == null) {
            return;
        }

        ConfigSchema.Leaf leaf = ConfigSchema.INSTANCE.leaf(entry.newPath());
        ConfigSchema.Coerced coerced = entry.rule() == LegacyKeys.Rule.FLOOR ? leaf.coerceFloored(element) : leaf.coerce(element);
        result.values().set(leaf.path(), coerced.value());

        if (!coerced.exact()) {
            addOnce(result.reset(), entry.oldPath());
        }
    }

    private static void mergeMobSwitch(LegacyFile file, String mob, ConfigReader.Result result) {
        String path = "mobs." + mob + ".enabled";
        Boolean spawn = file.mobSpawnEnabled == null ? null : file.mobSpawnEnabled.get(mob);
        boolean value = spawn == null || spawn;

        if (!SWITCH_ONLY_MOBS.contains(mob)) {
            LegacyFile.Mob entry = file.mobConfigEntries == null ? null : file.mobConfigEntries.get(mob);
            boolean own = entry == null ? (Boolean) ConfigSchema.INSTANCE.leaf(path).defaultValue() : entry.enabled;
            value = value && own;
        }

        result.values().set(path, value);
    }

    /// Ids contain no dots, so each path segment is one key.
    private static Object rawValue(LegacyFile file, String oldPath) {
        String[] part = oldPath.split("\\.");

        return switch (part[0]) {
            case "booleanEntries" -> file.booleanEntries == null ? null : file.booleanEntries.get(part[1]);
            case "numberEntries" -> file.numberEntries == null ? null : file.numberEntries.get(part[1]);
            default -> fieldOf(entryMap(file, part[0]), part[1], part[2]);
        };
    }

    private static Map<String, ?> entryMap(LegacyFile file, String root) {
        return switch (root) {
            case "structureConfigEntries" -> file.structureConfigEntries;
            case "particleConfigEntries" -> file.particleConfigEntries;
            case "mobConfigEntries" -> file.mobConfigEntries;
            case "hordeConfigEntries" -> file.hordeConfigEntries;
            case "corruptedChampionConfigEntries" -> file.corruptedChampionConfigEntries;
            default -> throw new IllegalArgumentException(root);
        };
    }

    private static Object fieldOf(Map<String, ?> entries, String id, String name) {
        Object entry = entries == null ? null : entries.get(id);

        if (entry == null) {
            return null;
        }

        try {
            Field field = entry.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(entry);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static JsonElement toJson(Object value) {
        if (value instanceof Boolean flag) {
            return new JsonPrimitive(flag);
        }

        if (value instanceof Number number) {
            return new JsonPrimitive(number);
        }

        if (value instanceof List<?> list) {
            JsonArray array = new JsonArray();

            for (Object item : list) {
                array.add(item == null ? JsonNull.INSTANCE : new JsonPrimitive((String) item));
            }

            return array;
        }

        return null;
    }

    private static void addOnce(List<String> list, String path) {
        if (!list.contains(path)) {
            list.add(path);
        }
    }

    private static void collectUnknownKeys(String joined, List<String> unknown) {
        try {
            JsonReader reader = new JsonReader(new StringReader(joined));
            reader.setLenient(true);
            collectUnknown(JsonParser.parseReader(reader).getAsJsonObject(), "", unknown);
        } catch (RuntimeException exception) {
        }
    }

    private static void collectUnknown(JsonObject object, String prefix, List<String> unknown) {
        for (Map.Entry<String, JsonElement> member : object.entrySet()) {
            String path = prefix.isEmpty() ? member.getKey() : prefix + "." + member.getKey();

            if (!KNOWN_PREFIXES.contains(path)) {
                unknown.add(path);
            } else if (member.getValue().isJsonObject()) {
                collectUnknown(member.getValue().getAsJsonObject(), path, unknown);
            }
        }
    }

    private static Set<String> knownPrefixes() {
        Set<String> prefixes = new HashSet<>(Set.of("mobConfigEntries.nameless_hanged", "mobConfigEntries.nameless_hanged.enabled"));

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            String path = "";

            for (String segment : entry.oldPath().split("\\.")) {
                path = path.isEmpty() ? segment : path + "." + segment;
                prefixes.add(path);
            }
        }

        return prefixes;
    }

    private static Map<String, String> oldPathsByNewPath() {
        Map<String, String> map = new HashMap<>();

        for (LegacyKeys.Entry entry : LegacyKeys.entries()) {
            if (entry.rule() == LegacyKeys.Rule.COPY || entry.rule() == LegacyKeys.Rule.FLOOR) {
                map.put(entry.newPath(), entry.oldPath());
            }
        }

        return map;
    }
}
