package com.lion.graveyard.data;

import com.google.gson.JsonObject;
import com.lion.graveyard.support.DataIndex;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.NbtReader;
import com.lion.graveyard.support.RegistryIds;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructureDataTest {
    private static final DataIndex INDEX = DataIndex.mod();
    private static final Path STRUCTURE_NBT_DIR = ModProject.DATA.resolve(ModProject.STRUCTURES);

    static Stream<DynamicTest> jsonTests(String folder, java.util.function.Function<String, List<String>> rule) {
        Path dir = ModProject.DATA.resolve(folder);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.idOf(dir, file, ".json"), () -> {
                    List<String> problems = rule.apply(ModProject.read(file));
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    static List<String> structureSetProblems(String json, Set<String> features) {
        List<String> problems = new ArrayList<>();
        for (Json.KeyedString s : Json.strings(Json.parseStrict(json))) {
            if (s.key().equals("structure") && ModProject.isModId(s.value())
                    && !features.contains(ModProject.stripNamespace(s.value()))) {
                problems.add("structure_set points at missing structure " + s.value());
            }
        }
        return problems;
    }

    static List<String> structureFeatureProblems(String json, DataIndex index) {
        List<String> problems = new ArrayList<>();
        JsonObject structure = Json.parseObject(json);
        String type = structure.get("type").getAsString();

        if (ModProject.isModId(type) && !RegistryIds.of(RegistryIds.STRUCTURE_TYPE).contains(ModProject.stripNamespace(type))) {
            problems.add("unregistered structure type " + type);
        }

        if (structure.has("start_pool")) {
            String pool = structure.get("start_pool").getAsString();

            if (ModProject.isModId(pool) && !index.templatePools.contains(ModProject.stripNamespace(pool))) {
                problems.add("missing start pool " + pool);
            }
        }

        if (structure.has("biomes")) {
            String biomes = structure.get("biomes").getAsString();

            if (biomes.startsWith("#") && ModProject.isModId(biomes.substring(1))
                    && !index.biomeTags.contains(ModProject.stripNamespace(biomes.substring(1)))) {
                problems.add("missing biome tag " + biomes);
            }
        }

        return problems;
    }

    static List<String> templatePoolProblems(String json, DataIndex index, Path structureDir) {
        List<String> problems = new ArrayList<>();
        for (Json.KeyedString s : Json.strings(Json.parseStrict(json))) {
            if (!ModProject.isModId(s.value())) {
                continue;
            }

            String id = ModProject.stripNamespace(s.value());
            switch (s.key()) {
                case "location" -> {
                    if (!Files.isRegularFile(structureDir.resolve(id + ".nbt"))) {
                        problems.add("missing structure file " + id + ".nbt");
                    }
                }
                case "processors" -> {
                    if (!index.processorLists.contains(id)) {
                        problems.add("missing processor list " + s.value());
                    }
                }
                case "fallback" -> {
                    if (!index.templatePools.contains(id)) {
                        problems.add("missing fallback pool " + s.value());
                    }
                }
                default -> {
                }
            }
        }
        return problems;
    }

    static List<String> processorListProblems(String json, DataIndex index) {
        List<String> problems = new ArrayList<>();
        for (Json.KeyedString s : Json.strings(Json.parseStrict(json))) {
            if (!ModProject.isModId(s.value())) {
                continue;
            }

            String id = ModProject.stripNamespace(s.value());

            if (s.key().equals("processor_type") && !RegistryIds.of(RegistryIds.PROCESSOR).contains(id)) {
                problems.add("unregistered processor type " + s.value());
            } else if ((s.key().equals("Name")
                    || s.key().equals("block")) && !index.blocks.contains(id)) {
                problems.add("unregistered block " + s.value());
            }
        }
        return problems;
    }

    @SuppressWarnings("unchecked")
    static List<String> nbtProblems(Map<String, Object> root, DataIndex index) {
        List<String> problems = new ArrayList<>();
        List<Object> palettes = new ArrayList<>();

        if (root.get("palette") instanceof List<?> palette) {
            palettes.add(palette);
        }

        if (root.get("palettes") instanceof List<?> many) {
            palettes.addAll(many);
        }

        for (Object palette : palettes) {
            for (Object entry : (List<Object>) palette) {
                String name = (String) ((Map<String, Object>) entry).get("Name");

                if (name != null && ModProject.isModId(name) && !index.blocks.contains(ModProject.stripNamespace(name))) {
                    problems.add("palette block not registered: " + name);
                }
            }
        }

        if (root.get("blocks") instanceof List<?> blocks) {
            for (Object block : blocks) {
                Object nbt = ((Map<String, Object>) block).get("nbt");

                if (nbt instanceof Map<?, ?> tag) {
                    checkBlockEntityTag((Map<String, Object>) tag, index, problems);
                }
            }
        }

        if (root.get("entities") instanceof List<?> entities) {
            for (Object entity : entities) {
                Object nbt = ((Map<String, Object>) entity).get("nbt");

                if (nbt instanceof Map<?, ?> tag && tag.get("id") instanceof String id && ModProject.isModId(id)
                        && !index.entities.contains(ModProject.stripNamespace(id))) {
                    problems.add("entity not registered: " + id);
                }
            }
        }

        return problems;
    }

    private static void checkBlockEntityTag(Map<String, Object> tag, DataIndex index, List<String> problems) {
        if (tag.get("id") instanceof String id && ModProject.isModId(id)
                && !RegistryIds.of(RegistryIds.BLOCK_ENTITY).contains(ModProject.stripNamespace(id))) {
            problems.add("block entity not registered: " + id);
        }

        if (tag.get("pool") instanceof String pool && ModProject.isModId(pool)
                && !index.templatePools.contains(ModProject.stripNamespace(pool))) {
            problems.add("jigsaw points at missing pool " + pool);
        }

        if (tag.get("final_state") instanceof String state && ModProject.isModId(state)) {
            String block = ModProject.stripNamespace(state.split("\\[")[0]);

            if (!index.blocks.contains(block)) {
                problems.add("jigsaw final_state block not registered: " + state);
            }
        }
    }

    @TestFactory
    Stream<DynamicTest> everyStructureSetPointsAtAnExistingStructure() {
        return jsonTests(ModProject.STRUCTURE_SETS, json -> structureSetProblems(json, INDEX.structureFeatures));
    }

    @TestFactory
    Stream<DynamicTest> everyStructureHasItsTypeStartPoolAndBiomeTag() {
        return jsonTests(ModProject.STRUCTURE_FEATURES, json -> structureFeatureProblems(json, INDEX));
    }

    @TestFactory
    Stream<DynamicTest> everyTemplatePoolPointsAtExistingFiles() {
        return jsonTests(ModProject.TEMPLATE_POOLS, json -> templatePoolProblems(json, INDEX, STRUCTURE_NBT_DIR));
    }

    @TestFactory
    Stream<DynamicTest> everyProcessorListUsesRegisteredIds() {
        return jsonTests(ModProject.PROCESSOR_LISTS, json -> processorListProblems(json, INDEX));
    }



    @TestFactory
    Stream<DynamicTest> everyStructureFileUsesRegisteredBlocksEntitiesAndPools() {
        return ModProject.files(STRUCTURE_NBT_DIR, ".nbt").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(STRUCTURE_NBT_DIR, file, ".nbt"), () -> {
                            List<String> problems = nbtProblems(NbtReader.readGzipped(ModProject.readBytes(file)), INDEX);
                            assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                        }));
    }

    @Test
    void structureFilesWereFound() {
        assertTrue(ModProject.files(STRUCTURE_NBT_DIR, ".nbt").size() > 100);
    }

    @Test
    void structureSetCheckFindsMissingStructure() {
        String json = "{\"structures\": [{\"structure\": \"graveyard:ghost\"}, {\"structure\": \"graveyard:real\"}]}";
        assertEquals(1, structureSetProblems(json, Set.of("real")).size());
    }

    @Test
    void nbtCheckFindsUnregisteredPaletteBlock() throws IOException {
        Map<String, Object> root = Map.of("palette", List.of(Map.of("Name", "graveyard:ghost_block"), Map.of("Name", "minecraft:stone")));
        DataIndex index = new DataIndex(
                Set.of("real"),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of()
        );
        assertEquals(1, nbtProblems(root, index).size());
    }
}
