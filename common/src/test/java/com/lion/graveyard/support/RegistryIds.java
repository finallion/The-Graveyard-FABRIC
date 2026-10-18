package com.lion.graveyard.support;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegistryIds {
    public static final String BLOCK = "block";
    public static final String ITEM = "item";
    public static final String ENTITY = "entity";
    public static final String BLOCK_ENTITY = "blockentity";
    public static final String SOUND = "sound";
    public static final String PARTICLE = "particle";
    public static final String STRUCTURE_TYPE = "structuretype";
    public static final String PROCESSOR = "processor";
    public static final String RECIPE_TYPE = "recipetype";
    public static final String RECIPE_SERIALIZER = "recipeserializer";
    public static final String LOOT_CONDITION = "lootcondition";
    public static final String SCREEN_HANDLER = "screenhandler";
    public static final String CRITERION = "criterion";
    public static final String DATA_COMPONENT = "datacomponent";

    private static final Pattern HELPER_CALL = Pattern.compile("RegistryHelper\\.register(\\w*)\\(\\s*\"([^\"]+)\"");
    private static final Pattern TRIGGER_CALL = Pattern.compile("(?<![\\w.])trigger\\(\\s*\"([^\"]+)\"");
    private static final Pattern SOUND_CALL = Pattern.compile("(?<![\\w.])register\\(\\s*\"([^\"]+)\"");

    private static final Map<String, String> CATEGORY_BY_CALL = Map.ofEntries(
            Map.entry("Block", BLOCK),
            Map.entry("Item", ITEM),
            Map.entry("SpawnEggItem", ITEM),
            Map.entry("MusicDiscItem", ITEM),
            Map.entry("EntityType", ENTITY),
            Map.entry("BlockEntity", BLOCK_ENTITY),
            Map.entry("SoundEvent", SOUND),
            Map.entry("ParticleType", PARTICLE),
            Map.entry("StructureType", STRUCTURE_TYPE),
            Map.entry("StructureProcessor", PROCESSOR),
            Map.entry("RecipeType", RECIPE_TYPE),
            Map.entry("RecipeSerializer", RECIPE_SERIALIZER),
            Map.entry("LootCondition", LOOT_CONDITION),
            Map.entry("ScreenHandlerType", SCREEN_HANDLER),
            Map.entry("DataComponent", DATA_COMPONENT)
    );

    private static Set<String> cache;

    private RegistryIds() {}

    public static synchronized Set<String> current() {
        if (cache == null) {
            Set<String> all = new TreeSet<>();
            for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
                String code = Sources.code(file);
                boolean soundFile = file.getFileName().toString().equals("TGSounds.java");
                all.addAll(extract(code, soundFile));
            }
            cache = all;
        }

        return cache;
    }

    public static Set<String> extract(String code, boolean soundFile) {
        Set<String> ids = new TreeSet<>();
        Matcher helper = HELPER_CALL.matcher(code);
        while (helper.find()) {
            String category = CATEGORY_BY_CALL.get(helper.group(1));

            if (category != null) {
                ids.add(category + ":" + helper.group(2));
            }
        }

        Matcher trigger = TRIGGER_CALL.matcher(code);
        while (trigger.find()) {
            ids.add(CRITERION + ":" + trigger.group(1));
        }

        if (soundFile) {
            Matcher sound = SOUND_CALL.matcher(code);
            while (sound.find()) {
                ids.add(SOUND + ":" + sound.group(1));
            }
        }

        return ids;
    }

    public static Set<String> of(String category) {
        Set<String> result = new TreeSet<>();
        String prefix = category + ":";
        for (String entry : current()) {
            if (entry.startsWith(prefix)) {
                result.add(entry.substring(prefix.length()));
            }
        }
        return result;
    }

    public static boolean isBlock(String id) {
        return of(BLOCK).contains(id);
    }

    public static boolean isItem(String id) {
        return of(ITEM).contains(id);
    }
}
