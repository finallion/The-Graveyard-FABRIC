package com.lion.graveyard.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class ConfigReader {
    private ConfigReader() {}

    public static final class Result {
        private final ConfigValues values;
        private final String failure;
        private final List<String> reset = new ArrayList<>();
        private final List<String> dropped = new ArrayList<>();
        private boolean trailingContent;

        Result(ConfigValues values, String failure) {
            this.values = values;
            this.failure = failure;
        }

        public ConfigValues values() {
            return values;
        }

        public boolean failed() {
            return failure != null;
        }

        public String failure() {
            return failure;
        }

        public List<String> reset() {
            return reset;
        }

        public List<String> dropped() {
            return dropped;
        }

        public boolean hasTrailingContent() {
            return trailingContent;
        }

        public boolean hasProblems() {
            return !reset.isEmpty() || !dropped.isEmpty() || trailingContent;
        }
    }

    public static Result read(String text) {
        Result result = new Result(ConfigValues.defaults(), null);
        JsonObject root;

        try {
            root = parseObject(text, result);
        } catch (RuntimeException exception) {
            return new Result(ConfigValues.defaults(), String.valueOf(exception.getMessage()));
        }

        readSection(ConfigSchema.INSTANCE.root(), root, result);
        ConfigRules.apply(result.values, result.reset);
        return result;
    }

    private static JsonObject parseObject(String text, Result result) {
        String clean = !text.isEmpty() && text.charAt(0) == '\uFEFF' ? text.substring(1) : text;
        JsonReader reader = new JsonReader(new StringReader(clean));
        reader.setLenient(true);
        JsonElement element = JsonParser.parseReader(reader);

        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("The file does not hold a JSON object.");
        }

        result.trailingContent = hasContentAfter(reader);

        return element.getAsJsonObject();
    }

    private static boolean hasContentAfter(JsonReader reader) {
        try {
            return reader.peek() != JsonToken.END_DOCUMENT;
        } catch (IOException | RuntimeException exception) {
            return true;
        }
    }

    private static void readSection(ConfigSchema.Section section, JsonObject json, Result result) {
        for (ConfigSchema.Node child : section.children()) {
            JsonElement element = json.get(child.name());

            if (element == null) {
                continue;
            }

            if (child instanceof ConfigSchema.Section nested) {
                readNested(nested, element, result);
            } else {
                ConfigSchema.Leaf leaf = (ConfigSchema.Leaf) child;
                ConfigSchema.Coerced coerced = leaf.coerce(element);
                result.values.set(leaf.path(), coerced.value());

                if (!coerced.exact()) {
                    result.reset.add(leaf.path());
                }
            }
        }

        for (String key : json.keySet()) {
            if (!hasChild(section, key)) {
                result.dropped.add(section.path().isEmpty() ? key : section.path() + "." + key);
            }
        }
    }

    private static boolean hasChild(ConfigSchema.Section section, String name) {
        for (ConfigSchema.Node child : section.children()) {
            if (child.name().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private static void readNested(ConfigSchema.Section nested, JsonElement element, Result result) {
        if (element.isJsonObject()) {
            readSection(nested, element.getAsJsonObject(), result);
        } else {
            result.reset.add(nested.path());
        }
    }
}
