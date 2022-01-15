package com.finallion.graveyard.blockentities.enums;

import net.minecraft.util.StringIdentifiable;

public enum SarcophagusPart implements StringIdentifiable {
    HEAD("head"),
    FOOT("foot");

    private final String name;

    private SarcophagusPart(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
