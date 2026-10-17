package com.lion.graveyard.chronicle.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lion.graveyard.Graveyard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/// Chapter texts are resource-pack assets (`assets/<ns>/chronicle/<id>.json`), read fresh each
/// time the book opens — a handful of small files, and it keeps the server out of the loop.
@Environment(EnvType.CLIENT)
public final class ChronicleChapters {
    private static final String DIRECTORY = "chronicle";

    /// `pages` are raw paragraphs; the screen wraps and splits them. Titles come from the language file.
    public record Chapter(String id, int act, int order, String source, List<String> pages) {
        public String titleKey() {
            return "chronicle." + Graveyard.MOD_ID + ".page." + id + ".title";
        }
    }

    private ChronicleChapters() {}

    public static List<Chapter> load() {
        List<Chapter> chapters = new ArrayList<>();
        Map<Identifier, Resource> resources = MinecraftClient.getInstance().getResourceManager()
                .findResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
        resources.forEach((id, resource) -> {
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String path = id.getPath();
                String chapterId = path.substring(DIRECTORY.length() + 1, path.length() - ".json".length());
                List<String> pages = new ArrayList<>();
                JsonArray array = JsonHelper.getArray(json, "pages");
                for (JsonElement element : array) {
                    pages.add(element.getAsString());
                }
                chapters.add(new Chapter(chapterId,
                        JsonHelper.getInt(json, "act", 1),
                        JsonHelper.getInt(json, "order", 0),
                        JsonHelper.getString(json, "source", ""),
                        pages));
            } catch (Exception e) {
                Graveyard.LOGGER.error("Skipping chronicle chapter {}: {}", id, e.getMessage());
            }
        });
        chapters.sort(Comparator.comparingInt(Chapter::act).thenComparingInt(Chapter::order).thenComparing(Chapter::id));
        return chapters;
    }
}
