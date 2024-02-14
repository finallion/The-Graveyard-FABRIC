package com.lion.graveyard.blockentities.enums;


import net.minecraft.util.StringRepresentable;

public enum SarcophagusPart implements StringRepresentable {
    HEAD("head"),
    FOOT("foot");

    private final String name;

    SarcophagusPart(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
