package com.lion.graveyard.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigValues {
    private final Map<String, Object> values = new LinkedHashMap<>();

    public static ConfigValues defaults() {
        ConfigValues result = new ConfigValues();

        for (ConfigSchema.Leaf leaf : ConfigSchema.INSTANCE.leaves()) {
            result.values.put(leaf.path(), leaf.defaultValue());
        }

        return result;
    }

    public ConfigValues copy() {
        ConfigValues result = new ConfigValues();
        result.values.putAll(values);
        return result;
    }

    public boolean has(String path) {
        return values.containsKey(path);
    }

    public boolean bool(String path) {
        return (Boolean) get(path);
    }

    public int integer(String path) {
        return (Integer) get(path);
    }

    public double real(String path) {
        return (Double) get(path);
    }

    public float floating(String path) {
        return (float) real(path);
    }

    @SuppressWarnings("unchecked")
    public List<String> strings(String path) {
        return (List<String>) get(path);
    }

    public Object get(String path) {
        Object value = values.get(path);

        if (value == null) {
            throw new IllegalArgumentException("Unknown config key " + path);
        }

        return value;
    }

    void set(String path, Object value) {
        if (!values.containsKey(path)) {
            throw new IllegalArgumentException("Unknown config key " + path);
        }

        values.put(path, value);
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ConfigValues that && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
