package com.lion.graveyard.chronicle;

import java.util.function.Consumer;

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
