package com.lion.graveyard.assets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.support.AssetLookup;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.RegistryIds;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelReferencesTest {
    private static final AssetLookup ASSETS = AssetLookup.MOD;

    static List<String> blockstateProblems(String json, AssetLookup assets) {
        List<String> problems = new ArrayList<>();
        for (Json.KeyedString entry : Json.strings(Json.parseStrict(json))) {
            if (entry.key().equals("model") && !AssetLookup.isForeign(entry.value())
                    && !assets.exists(assets.model(entry.value()))) {
                problems.add("missing model " + entry.value());
            }
        }
        return problems;
    }

    static List<String> modelProblems(String json, AssetLookup assets) {
        List<String> problems = new ArrayList<>();
        JsonObject model = Json.parseObject(json);
        boolean parentChainClosed = true;

        Set<String> definedTextureKeys = new HashSet<>();
        JsonObject current = model;
        int depth = 0;
        while (current != null && depth++ < 16) {
            if (current.has("textures")) {
                definedTextureKeys.addAll(current.getAsJsonObject("textures").keySet());
            }

            if (!current.has("parent")) {
                break;
            }

            String parent = current.get("parent").getAsString();

            if (AssetLookup.isForeign(parent)) {
                parentChainClosed = false;
                break;
            }

            Path parentFile = assets.model(parent);

            if (!assets.exists(parentFile)) {
                if (current == model) {
                    problems.add("missing parent " + parent);
                }

                parentChainClosed = false;
                break;
            }

            current = Json.parseObject(ModProject.read(parentFile));
        }

        if (model.has("textures")) {
            for (var entry : model.getAsJsonObject("textures").entrySet()) {
                String value = entry.getValue().getAsString();

                if (!value.startsWith("#") && !AssetLookup.isForeign(value) && !assets.exists(assets.texture(value))) {
                    problems.add("missing texture " + value + " (variable " + entry.getKey() + ")");
                }
            }
        }

        if (parentChainClosed && model.has("elements")) {
            Set<String> used = new HashSet<>();
            for (JsonElement element : model.getAsJsonArray("elements")) {
                JsonObject faces = element.getAsJsonObject().getAsJsonObject("faces");

                if (faces == null) {
                    continue;
                }

                for (var face : faces.entrySet()) {
                    JsonElement texture = face.getValue().getAsJsonObject().get("texture");

                    if (texture != null && texture.getAsString().startsWith("#")) {
                        used.add(texture.getAsString().substring(1));
                    }
                }
            }
            for (String name : used) {
                if (!definedTextureKeys.contains(name)) {
                    problems.add("face uses undefined texture variable #" + name);
                }
            }
        }

        if (model.has("overrides")) {
            for (JsonElement override : model.getAsJsonArray("overrides")) {
                String target = override.getAsJsonObject().get("model").getAsString();

                if (!AssetLookup.isForeign(target) && !assets.exists(assets.model(target))) {
                    problems.add("missing override model " + target);
                }
            }
        }

        return problems;
    }

    static List<String> particleProblems(String json, AssetLookup assets) {
        List<String> problems = new ArrayList<>();
        JsonObject particle = Json.parseObject(json);

        if (particle.has("textures")) {
            for (JsonElement texture : particle.getAsJsonArray("textures")) {
                Path file = assets.root()
                        .resolve(ModProject.TEXTURES)
                        .resolve("particle")
                        .resolve(ModProject.stripNamespace(texture.getAsString()) + ".png");

                if (!assets.exists(file)) {
                    problems.add("missing particle texture " + texture.getAsString());
                }
            }
        }

        return problems;
    }

    @TestFactory
    Stream<DynamicTest> everyBlockstateReferencesExistingModels() {
        Path dir = ASSETS.root().resolve(ModProject.BLOCKSTATES);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".json"), () -> {
                            List<String> problems = blockstateProblems(ModProject.read(file), ASSETS);
                            assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                        }));
    }

    @TestFactory
    Stream<DynamicTest> everyModelReferencesExistingParentsAndTextures() {
        Path dir = ASSETS.root().resolve(ModProject.MODELS);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".json"), () -> {
                            List<String> problems = modelProblems(ModProject.read(file), ASSETS);
                            assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                        }));
    }

    @TestFactory
    Stream<DynamicTest> everyParticleReferencesExistingTextures() {
        Path dir = ASSETS.root().resolve(ModProject.PARTICLES);
        return ModProject.files(dir, ".json").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".json"), () -> {
                            List<String> problems = particleProblems(ModProject.read(file), ASSETS);
                            assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                        }));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredBlockHasABlockstate() {
        return RegistryIds.of(RegistryIds.BLOCK).stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () ->
                                assertTrue(ASSETS.exists(ASSETS.blockstate(id)), "No blockstate for block " + id)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredItemHasAnItemModel() {
        return RegistryIds.of(RegistryIds.ITEM).stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () ->
                                assertTrue(ASSETS.exists(ASSETS.model("item/" + id)), "No item model for item " + id)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredItemHasAnItemDefinition() {
        Path dir = ASSETS.root().resolve("items");
        return RegistryIds.of(RegistryIds.ITEM).stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () -> {
                            Path file = dir.resolve(id + ".json");
                            assertTrue(file.toFile().isFile(), "No item definition for item " + id);

                            for (Json.KeyedString entry : Json.strings(Json.parseStrict(ModProject.read(file)))) {
                                if (entry.key().equals("model") && !AssetLookup.isForeign(entry.value())) {
                                    assertTrue(ASSETS.exists(ASSETS.model(entry.value())), "Item definition " + id + " names a missing model " + entry.value());
                                }
                            }
                        }
                ));
    }

    @TestFactory
    Stream<DynamicTest> everySpawnEggHasItsOwnTexture() {
        return RegistryIds.of(RegistryIds.ITEM).stream()
                .filter(id -> id.endsWith("_spawn_egg"))
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () -> assertTrue(ASSETS.exists(ASSETS.texture("item/" + id)), "No texture for spawn egg " + id)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everyBlockstateBelongsToARegisteredBlock() {
        Path dir = ASSETS.root().resolve(ModProject.BLOCKSTATES);
        Set<String> blocks = RegistryIds.of(RegistryIds.BLOCK);
        return ModProject.files(dir, ".json").stream()
                .map(file -> {
                    String id = ModProject.idOf(dir, file, ".json");
                    return DynamicTest.dynamicTest(id, () -> assertTrue(blocks.contains(id), "Blockstate without a registered block: " + id));
                });
    }

    @TestFactory
    Stream<DynamicTest> everyTextureIsAPngFile() {
        Path dir = ASSETS.root().resolve(ModProject.TEXTURES);
        byte[] signature = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        return ModProject.files(dir, ".png").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".png"), () -> {
                            try (InputStream in = Files.newInputStream(file)) {
                                assertTrue(java.util.Arrays.equals(signature, in.readNBytes(8)), "Not a PNG file");
                            }
                        }));
    }

    @Test
    void blockstateCheckFindsMissingModel(@TempDir Path temp) {
        AssetLookup lookup = new AssetLookup(temp);
        String broken = "{\"variants\": {\"\": {\"model\": \"graveyard:block/ghost\"}}}";
        assertEquals(List.of("missing model graveyard:block/ghost"), blockstateProblems(broken, lookup));
    }

    @Test
    void blockstateCheckAcceptsExistingModelAndForeignModel(@TempDir Path temp) throws IOException {
        Files.createDirectories(temp.resolve("models/block"));
        Files.writeString(temp.resolve("models/block/real.json"), "{}");
        String json = "{\"multipart\": [{\"apply\": [{\"model\": \"graveyard:block/real\"}, {\"model\": \"minecraft:block/stone\"}]}]}";
        assertTrue(blockstateProblems(json, new AssetLookup(temp)).isEmpty());
    }

    @Test
    void modelCheckFindsMissingTextureParentAndUndefinedVariable(@TempDir Path temp) {
        String broken = "{\"parent\": \"graveyard:block/ghost_parent\", \"textures\": {\"0\": \"graveyard:block/ghost_tex\"}}";
        List<String> problems = modelProblems(broken, new AssetLookup(temp));
        assertEquals(2, problems.size(), problems.toString());

        String undefined = "{\"textures\": {}, \"elements\": [{\"from\": [0,0,0], \"to\": [1,1,1], \"faces\": {\"north\": {\"texture\": \"#0\"}}}]}";
        assertFalse(modelProblems(undefined, new AssetLookup(temp)).isEmpty());
    }
}
