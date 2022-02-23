package com.finallion.graveyard.config;

public class UrnConfigEntry {
    public final boolean doubleInventory;

    public UrnConfigEntry(boolean doubleInventory) {
        this.doubleInventory = doubleInventory;
    }

    public static UrnConfigEntry of(boolean doubleInventory) {
        return new UrnConfigEntry(doubleInventory);
    }
}
