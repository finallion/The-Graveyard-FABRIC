package com.lion.graveyard.compat.legacy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/// Test oracle for the legacy 3.0 reader.
public final class HeadOmega {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private HeadOmega() {}

    public record Outcome(HeadConfig config, String rejection) {
        public boolean rejected() {
            return rejection != null;
        }
    }

    public static Outcome read(Path path) {
        HeadConfig config = new HeadConfig();

        try {
            List<String> lines = Files.readAllLines(path);
            lines.removeIf(line -> line.trim().startsWith("//"));
            StringBuilder res = new StringBuilder();
            lines.forEach(res::append);
            HeadConfig object = GSON.fromJson(res.toString(), HeadConfig.class);

            object.save();
            return new Outcome(object, null);
        } catch (IOException | RuntimeException exception) {
            config.fillDefaults();
            return new Outcome(config, exception.toString());
        }
    }
}
