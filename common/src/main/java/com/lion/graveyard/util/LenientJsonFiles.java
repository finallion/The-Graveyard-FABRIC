package com.lion.graveyard.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lion.graveyard.Graveyard;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/// The lenient Gson reader also accepts comments and trailing commas.
public final class LenientJsonFiles {
    private LenientJsonFiles() {}

    public static Map<Identifier, JsonElement> read(ResourceManager manager, String directory) {
        FileToIdConverter lister = FileToIdConverter.json(directory);
        Map<Identifier, JsonElement> result = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry : lister.listMatchingResources(manager).entrySet()) {
            Identifier id = lister.fileToId(entry.getKey());

            try (Reader reader = entry.getValue().openAsReader()) {
                result.put(id, JsonParser.parseReader(reader));
            } catch (IOException | JsonParseException e) {
                Graveyard.LOGGER.error("Could not parse data file {} from {}", id, entry.getKey(), e);
            }
        }

        return result;
    }
}
