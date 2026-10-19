package com.lion.graveyard.chronicle.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lion.graveyard.Graveyard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class ChronicleChapters {
    private static final String DIRECTORY = "chronicle";

    public record Chapter(String id, int act, int order, String source, List<String> pages) {
        public String titleKey() {
            return "chronicle." + Graveyard.MOD_ID + ".page." + id + ".title";
        }
    }

    private ChronicleChapters() {}

    public static List<Chapter> load() {
        List<Chapter> chapters = new ArrayList<>();
        Map<Identifier, Resource> resources = Minecraft.getInstance().getResourceManager()
                .listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
        resources.forEach((id, resource) -> {
            try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String path = id.getPath();
                String chapterId = path.substring(DIRECTORY.length() + 1, path.length() - ".json".length());
                List<String> pages = new ArrayList<>();
                JsonArray array = GsonHelper.getAsJsonArray(json, "pages");
                for (JsonElement element : array) {
                    pages.add(element.getAsString());
                }
                chapters.add(new Chapter(
                        chapterId,
                        GsonHelper.getAsInt(json, "act", 1),
                        GsonHelper.getAsInt(json, "order", 0),
                        GsonHelper.getAsString(json, "source", ""),
                        pages
                ));
            } catch (Exception e) {
                Graveyard.LOGGER.error("Skipping chronicle chapter {}: {}", id, e.getMessage());
            }
        });
        chapters.sort(Comparator.comparingInt(Chapter::act).thenComparingInt(Chapter::order).thenComparing(Chapter::id));
        return chapters;
    }
}
