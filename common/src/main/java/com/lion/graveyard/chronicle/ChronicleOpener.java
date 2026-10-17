package com.lion.graveyard.chronicle;

import java.util.function.Consumer;

/// Client hook to open the chronicle on a page. The client entry point installs it; a dedicated
/// server keeps the no-op.
public final class ChronicleOpener {
    private static Consumer<String> opener = page -> {};

    private ChronicleOpener() {}

    public static void install(Consumer<String> consumer) {
        opener = consumer;
    }

    public static void open(String page) {
        opener.accept(page);
    }
}
