package com.lion.graveyard.support;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public final class DataIndex {
    private static final Pattern TRIGGER = Pattern.compile("(?:new Identifier\\(\\s*\"graveyard:|Graveyard\\.id\\(\\s*\"|\\btrigger\\(\\s*\")([a-z0-9_/]+)\"");
    private static DataIndex mod;

    public final Set<String> blocks;
    public final Set<String> items;
    public final Set<String> entities;
    public final Set<String> otherTypeIds;
    public final Set<String> lootTables;
    public final Set<String> recipes;
    public final Set<String> advancements;
    public final Set<String> itemTags;
    public final Set<String> blockTags;
    public final Set<String> biomeTags;
    public final Set<String> templatePools;
    public final Set<String> processorLists;
    public final Set<String> structureFeatures;

    public DataIndex(
            Set<String> blocks,
            Set<String> items,
            Set<String> entities,
            Set<String> otherTypeIds,
            Set<String> lootTables,
            Set<String> recipes,
            Set<String> advancements,
            Set<String> itemTags,
            Set<String> blockTags,
            Set<String> biomeTags,
            Set<String> templatePools,
            Set<String> processorLists,
            Set<String> structureFeatures
    ) {
        this.blocks = blocks;
        this.items = items;
        this.entities = entities;
        this.otherTypeIds = otherTypeIds;
        this.lootTables = lootTables;
        this.recipes = recipes;
        this.advancements = advancements;
        this.itemTags = itemTags;
        this.blockTags = blockTags;
        this.biomeTags = biomeTags;
        this.templatePools = templatePools;
        this.processorLists = processorLists;
        this.structureFeatures = structureFeatures;
    }

    public static synchronized DataIndex mod() {
        if (mod == null) {
            Set<String> other = new HashSet<>();
            for (String category : new String[]{RegistryIds.RECIPE_SERIALIZER, RegistryIds.RECIPE_TYPE, RegistryIds.LOOT_CONDITION,
                    RegistryIds.STRUCTURE_TYPE, RegistryIds.PROCESSOR, RegistryIds.BLOCK_ENTITY, RegistryIds.PARTICLE}) {
                other.addAll(RegistryIds.of(category));
            }
            Path advancementCode = ModProject.JAVA.resolve("init/TGAdvancements.java");

            if (advancementCode.toFile().isFile()) {
                other.addAll(com.lion.graveyard.support.Sources.matches(TRIGGER, Sources.code(advancementCode), 1));
            }

            mod = new DataIndex(
                    RegistryIds.of(RegistryIds.BLOCK),
                    RegistryIds.of(RegistryIds.ITEM),
                    RegistryIds.of(RegistryIds.ENTITY),
                    other,
                    ids(ModProject.LOOT_TABLES),
                    ids(ModProject.RECIPES),
                    ids(ModProject.ADVANCEMENTS),
                    ids(ModProject.TAGS_ITEMS),
                    ids(ModProject.TAGS_BLOCKS),
                    ids(ModProject.TAGS_BIOMES),
                    ids(ModProject.TEMPLATE_POOLS),
                    ids(ModProject.PROCESSOR_LISTS),
                    ids(ModProject.STRUCTURE_FEATURES)
            );
        }

        return mod;
    }

    private static Set<String> ids(String folder) {
        Path dir = ModProject.DATA.resolve(folder);
        Set<String> result = new TreeSet<>();
        for (Path file : ModProject.files(dir, ".json")) {
            result.add(ModProject.idOf(dir, file, ".json"));
        }
        return result;
    }

    public boolean anyTag(String id) {
        return itemTags.contains(id) || blockTags.contains(id) || biomeTags.contains(id);
    }

    public boolean anyKnownId(String id) {
        return blocks.contains(id) || items.contains(id) || entities.contains(id) || otherTypeIds.contains(id);
    }
}
