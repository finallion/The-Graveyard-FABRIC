package com.lion.graveyard.support;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AssetLookup {
    public static final AssetLookup MOD = new AssetLookup(ModProject.ASSETS);

    private final Path root;

    public AssetLookup(Path root) {
        this.root = root;
    }

    public Path root() {
        return root;
    }

    public static boolean isForeign(String ref) {
        return ref.indexOf(':') >= 0 ? !ModProject.isModId(ref) : true;
    }

    public Path model(String ref) {
        return root.resolve(ModProject.MODELS).resolve(ModProject.stripNamespace(ref) + ".json");
    }

    public Path texture(String ref) {
        return root.resolve(ModProject.TEXTURES).resolve(ModProject.stripNamespace(ref) + ".png");
    }

    public Path sound(String ref) {
        return root.resolve(ModProject.SOUNDS).resolve(ModProject.stripNamespace(ref) + ".ogg");
    }

    public Path blockstate(String id) {
        return root.resolve(ModProject.BLOCKSTATES).resolve(id + ".json");
    }

    public boolean exists(Path path) {
        return Files.isRegularFile(path);
    }
}
