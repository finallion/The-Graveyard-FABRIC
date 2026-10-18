package com.lion.graveyard.support;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NbtKeys {
    private static final Pattern DECLARES_READER = Pattern.compile("\\b(?:loadAdditional(?=\\s*\\(\\s*(?:CompoundTag|ValueInput))|readAdditionalSaveData|fromNbt)\\s*\\([^)]*\\)\\s*\\{");
    private static final Pattern CONSTANT = Pattern.compile("static\\s+final\\s+String\\s+(\\w+)\\s*=\\s*\"([^\"]+)\"");
    private static final Pattern READ_CALL = Pattern.compile("\\.(?:get\\w*|contains\\w*|has\\w*|remove|read|child\\w*|list\\w*)\\(\\s*(?:\"([^\"]+)\"|([A-Z][A-Z_0-9]+))");
    private static final Pattern WRITE_CALL = Pattern.compile("\\.put\\w*\\(\\s*(?:\"([^\"]+)\"|([A-Z][A-Z_0-9]+))");

    private NbtKeys() {}

    public static boolean declaresNbtReader(String code) {
        return DECLARES_READER.matcher(code).find();
    }

    public static Set<String> readKeys(String code) {
        Set<String> keys = collect(READ_CALL, code);

        if (code.contains("ContainerHelper.loadAllItems") || code.contains("LegacyItemStacks.loadSlots")) {
            keys.add("Items");
        }

        if (code.contains("tryLoadLootTable")) {
            keys.add("LootTable");
        }

        if (code.contains("LegacyItemStacks.readDataVersion") || code.contains("LegacyItemStacks.loadSlots") || code.contains("LegacyEntityData.read(")) {
            keys.add("GraveyardDataVersion");
        }

        return keys;
    }

    public static Set<String> writeKeys(String code) {
        Set<String> keys = collect(WRITE_CALL, code);

        if (code.contains("ContainerHelper.saveAllItems") || code.contains("LegacyItemStacks.saveSlots")) {
            keys.add("Items");
        }

        if (code.matches("(?s).*trySaveLootTable.*")) {
            keys.add("LootTable");
        }

        return keys;
    }

    private static Set<String> collect(Pattern call, String code) {
        Map<String, String> constants = new HashMap<>();
        Matcher c = CONSTANT.matcher(code);
        while (c.find()) {
            constants.put(c.group(1), c.group(2));
        }
        Set<String> keys = new TreeSet<>();
        Matcher m = call.matcher(code);
        while (m.find()) {
            String key = m.group(1) != null ? m.group(1) : constants.get(m.group(2));

            if (key != null) {
                keys.add(key);
            }
        }
        return keys;
    }

    public static Set<String> currentReadLines() {
        Set<String> lines = new TreeSet<>();
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            String code = Sources.code(file);

            if (declaresNbtReader(code)) {
                String name = file.getFileName().toString().replace(".java", "");
                for (String key : readKeys(code)) {
                    lines.add(name + ":" + key);
                }
            }
        }
        return lines;
    }
}
