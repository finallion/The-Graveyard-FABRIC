package com.lion.graveyard.compat;

import com.lion.graveyard.util.LegacyContainerName;
import com.lion.graveyard.util.LegacyItemStacks;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyContainerNameTest {
    private static HolderLookup.Provider registries;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        registries = VanillaRegistries.createLookup();
    }

    private static CompoundTag container(String customName) {
        CompoundTag tag = new CompoundTag();
        tag.put("Items", new ListTag());
        tag.putString("CustomName", customName);
        return tag;
    }

    private static Optional<Component> read(CompoundTag tag) {
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, registries, tag);
        return LegacyContainerName.read(input);
    }

    @Test
    void unstampedUrnKeepsItsJsonName() {
        Component name = read(container("{\"text\":\"Old Urn\"}")).orElseThrow();
        assertEquals("Old Urn", name.getString());
    }

    @Test
    void unstampedSarcophagusKeepsItsJsonName() {
        Component name = read(container("{\"text\":\"Old Sarcophagus\",\"italic\":false}")).orElseThrow();
        assertEquals("Old Sarcophagus", name.getString());
    }

    @Test
    void unstampedPlainStringIsNotATextComponent() {
        assertTrue(read(container("Old Urn")).isEmpty());
    }

    @Test
    void stampedWithTheCurrentVersionNeedsNoFix() {
        CompoundTag tag = container("{\"text\":\"Urn\"}");
        tag.putInt(LegacyItemStacks.DATA_VERSION_KEY, LegacyItemStacks.currentDataVersion());
        assertTrue(read(tag).isEmpty());
    }

    @Test
    void stampedWithAnOlderVersionIsFixed() {
        CompoundTag tag = container("{\"text\":\"Urn\"}");
        tag.putInt(LegacyItemStacks.DATA_VERSION_KEY, LegacyItemStacks.PORT_1_21_1_DATA_VERSION);
        assertEquals("Urn", read(tag).orElseThrow().getString());
    }

    @Test
    void stampedAs4671WithAComponentNameIsLeftToVanilla() {
        CompoundTag tag = new CompoundTag();
        tag.put("Items", new ListTag());
        CompoundTag name = new CompoundTag();
        name.putString("text", "Urn");
        tag.put("CustomName", name);
        tag.putInt(LegacyItemStacks.DATA_VERSION_KEY, 4671);
        assertTrue(read(tag).isEmpty());
    }
}
