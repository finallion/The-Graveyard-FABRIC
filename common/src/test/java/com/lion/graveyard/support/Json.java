package com.lion.graveyard.support;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class Json {
    private static final Gson GSON = new Gson();

    private Json() {}

    public static JsonElement parseStrict(String text) {
        try {
            JsonReader reader = new JsonReader(new StringReader(text));
            reader.setLenient(false);
            JsonElement element = GSON.getAdapter(JsonElement.class).read(reader);

            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw new IllegalStateException("Trailing content after the JSON document");
            }

            return element;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static JsonObject parseObject(String text) {
        return parseStrict(text).getAsJsonObject();
    }

    /// A parsed tree loses the raw key order.
    public static void walkObjects(String text, BiConsumer<String, List<String>> onObject) {
        try {
            JsonReader reader = new JsonReader(new StringReader(text));
            reader.setLenient(false);
            walk(reader, "", onObject);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static void walk(JsonReader reader, String path, BiConsumer<String, List<String>> onObject) throws IOException {
        switch (reader.peek()) {
            case BEGIN_OBJECT -> {
                reader.beginObject();
                List<String> keys = new ArrayList<>();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    keys.add(name);
                    walk(reader, path + "/" + name, onObject);
                }
                reader.endObject();
                onObject.accept(path, keys);
            }
            case BEGIN_ARRAY -> {
                reader.beginArray();
                int index = 0;
                while (reader.hasNext()) {
                    walk(reader, path + "[" + index++ + "]", onObject);
                }
                reader.endArray();
            }
            default -> reader.skipValue();
        }
    }

    public static List<String> duplicateKeys(String text) {
        List<String> problems = new ArrayList<>();
        walkObjects(text, (path, keys) -> {
            Set<String> seen = new HashSet<>();
            for (String key : keys) {
                if (!seen.add(key)) {
                    problems.add("duplicate key '" + key + "' at " + (path.isEmpty() ? "/" : path));
                }
            }
        });
        return problems;
    }

    public static Map<String, String> stringMap(String text) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : parseObject(text).entrySet()) {
            JsonElement value = entry.getValue();
            result.put(entry.getKey(), value.isJsonPrimitive() ? value.getAsString() : "<non-string>");
        }
        return result;
    }

    public record KeyedString(String key, String value) {
    }

    public static List<KeyedString> strings(JsonElement root) {
        List<KeyedString> result = new ArrayList<>();
        collect(root, "", result);
        return result;
    }

    private static void collect(JsonElement element, String key, List<KeyedString> out) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                collect(entry.getValue(), entry.getKey(), out);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                collect(child, key, out);
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            out.add(new KeyedString(key, element.getAsString()));
        }
    }
}
