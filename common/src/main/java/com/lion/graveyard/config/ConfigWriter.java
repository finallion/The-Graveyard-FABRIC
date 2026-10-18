package com.lion.graveyard.config;

import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ConfigWriter {
    public static final String HEADER = "// The Graveyard config. Comments are rewritten at start. Values are kept.";

    private static final String INDENT = "  ";

    private ConfigWriter() {}

    public static String write(ConfigValues values) {
        StringBuilder out = new StringBuilder();
        out.append(HEADER).append('\n');
        out.append("{\n");
        writeChildren(out, ConfigSchema.INSTANCE.root(), values, 1, null, "");
        out.append("}\n");
        return out.toString();
    }

    private static void writeChildren(
            StringBuilder out,
            ConfigSchema.Section section,
            ConfigValues values,
            int depth,
            Set<String> seen,
            String relative
    ) {
        List<ConfigSchema.Node> children = section.children();

        for (int i = 0; i < children.size(); i++) {
            ConfigSchema.Node child = children.get(i);
            String comma = i < children.size() - 1 ? "," : "";
            boolean commented = section.isTable() || seen == null || seen.add(relative + child.name() + child.comment());

            if (child instanceof ConfigSchema.Section nested) {
                Set<String> innerSeen = nested.isTable() ? new HashSet<>() : seen;
                String innerRelative = section.isTable() || nested.isTable() ? "" : relative + nested.name() + ".";
                writeSection(out, nested, values, depth, commented, innerSeen, innerRelative, comma);
            } else {
                writeLeaf(out, (ConfigSchema.Leaf) child, values, depth, commented, comma);
            }
        }
    }

    private static void writeSection(
            StringBuilder out,
            ConfigSchema.Section nested,
            ConfigValues values,
            int depth,
            boolean commented,
            Set<String> seen,
            String relative,
            String comma
    ) {
        if (commented && !nested.comment().isEmpty()) {
            line(out, depth, "// " + nested.comment());
        }

        line(out, depth, nested.name() + ": {");
        writeChildren(out, nested, values, depth + 1, seen, relative);
        line(out, depth, "}" + comma);
    }

    private static void writeLeaf(
            StringBuilder out,
            ConfigSchema.Leaf leaf,
            ConfigValues values,
            int depth,
            boolean commented,
            String comma
    ) {
        if (commented) {
            line(out, depth, "// " + leaf.comment());
        }

        line(out, depth, leaf.name() + ": " + format(leaf, values.get(leaf.path())) + comma);
    }

    private static void line(StringBuilder out, int depth, String text) {
        out.append(INDENT.repeat(depth));
        out.append(text);
        out.append('\n');
    }

    static String format(ConfigSchema.Leaf leaf, Object value) {
        return switch (leaf.kind()) {
            case BOOL, INT -> String.valueOf(value);
            case DOUBLE, FLOAT, MULTIPLIER -> decimal((Double) value);
            case STRING_LIST -> list(value);
        };
    }

    static String decimal(double value) {
        String plain = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
        return plain.contains(".") ? plain : plain + ".0";
    }

    private static String list(Object value) {
        StringBuilder out = new StringBuilder("[");
        boolean first = true;

        for (Object item : (List<?>) value) {
            if (!first) {
                out.append(", ");
            }

            out.append(new JsonPrimitive((String) item));
            first = false;
        }

        return out.append(']').toString();
    }
}
