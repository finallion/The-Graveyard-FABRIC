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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SoundsTest {
    private static final AssetLookup ASSETS = AssetLookup.MOD;
    private static final Path SOUNDS_JSON = ModProject.ASSETS.resolve("sounds.json");

    static JsonObject soundsJson() {
        return Json.parseObject(ModProject.read(SOUNDS_JSON));
    }

    static List<String> soundFileReferences(JsonObject event) {
        List<String> names = new ArrayList<>();

        if (!event.has("sounds")) {
            return names;
        }

        for (JsonElement sound : event.getAsJsonArray("sounds")) {
            if (sound.isJsonPrimitive()) {
                names.add(sound.getAsString());
            } else if (!"event".equals(sound.getAsJsonObject().has("type") ? sound.getAsJsonObject().get("type").getAsString() : "")) {
                names.add(sound.getAsJsonObject().get("name").getAsString());
            }
        }
        return names;
    }

    static List<String> missingSoundFiles(JsonObject sounds, AssetLookup assets) {
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, JsonElement> event : sounds.entrySet()) {
            for (String name : soundFileReferences(event.getValue().getAsJsonObject())) {
                if (!AssetLookup.isForeign(name) && !assets.exists(assets.sound(name))) {
                    problems.add(event.getKey() + " -> missing " + name);
                }
            }
        }
        return problems;
    }

    static boolean hasOggHeader(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            return new String(in.readNBytes(4), java.nio.charset.StandardCharsets.ISO_8859_1).equals("OggS");
        }
    }

    @TestFactory
    Stream<DynamicTest> everySoundEventInSoundsJsonPointsAtExistingFiles() {
        JsonObject sounds = soundsJson();
        return sounds.entrySet().stream()
                .map(event -> DynamicTest.dynamicTest(event.getKey(), () -> {
                    JsonObject single = new JsonObject();
                    single.add(event.getKey(), event.getValue());
                    List<String> problems = missingSoundFiles(single, ASSETS);
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @TestFactory
    Stream<DynamicTest> everyOggFileHasAnOggHeader() {
        Path dir = ASSETS.root().resolve(ModProject.SOUNDS);
        return ModProject.files(dir, ".ogg").stream()
                .map(file -> DynamicTest.dynamicTest(
                        ModProject.idOf(dir, file, ".ogg"),
                        () -> assertTrue(hasOggHeader(file), "Missing OggS header")
                ));
    }

    @TestFactory
    Stream<DynamicTest> everyRegisteredSoundEventIsDefinedInSoundsJson() {
        Set<String> defined = soundsJson().keySet();
        return RegistryIds.of(RegistryIds.SOUND).stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () ->
                                assertTrue(defined.contains(id), "Registered sound event not in sounds.json: " + id)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everySoundsJsonEventIsRegisteredInCode() {
        Set<String> registered = RegistryIds.of(RegistryIds.SOUND);
        return soundsJson().keySet()
                .stream()
                .map(id -> DynamicTest.dynamicTest(
                        id,
                        () ->
                                assertTrue(registered.contains(id), "Event in sounds.json without a registration: " + id)
                ));
    }

    @TestFactory
    Stream<DynamicTest> everySubtitleKeyExistsInEnglish() {
        Map<String, String> english = Json.stringMap(ModProject.read(ModProject.ASSETS.resolve("lang/en_us.json")));
        JsonObject sounds = soundsJson();
        return sounds.entrySet().stream()
                .filter(e -> e.getValue().getAsJsonObject().has("subtitle"))
                .map(e -> DynamicTest.dynamicTest(e.getKey(), () -> {
                    String key = e.getValue().getAsJsonObject().get("subtitle").getAsString();
                    assertTrue(english.containsKey(key), "Subtitle key missing in en_us.json: " + key);
                }));
    }

    @Test
    void checkerFindsMissingOggFile(@TempDir Path temp) throws IOException {
        Files.createDirectories(temp.resolve("sounds"));
        Files.write(temp.resolve("sounds/real.ogg"), "OggS".getBytes());
        JsonObject json = Json.parseObject("{\"a\": {\"sounds\": [\"graveyard:real\", {\"name\": \"graveyard:ghost\", \"stream\": true}]}}");
        assertEquals(List.of("a -> missing graveyard:ghost"), missingSoundFiles(json, new AssetLookup(temp)));
    }

    @Test
    void oggHeaderCheckRejectsOtherData(@TempDir Path temp) throws IOException {
        Path file = temp.resolve("fake.ogg");
        Files.write(file, "RIFF".getBytes());
        assertTrue(!hasOggHeader(file));
    }

    @Test
    void soundsJsonHasEvents() {
        assertTrue(new TreeSet<>(soundsJson().keySet()).size() > 50);
    }
}
