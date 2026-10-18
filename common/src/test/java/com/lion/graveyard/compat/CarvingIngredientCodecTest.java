package com.lion.graveyard.compat;

import com.google.gson.JsonParser;
import com.lion.graveyard.recipe.CarvingRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarvingIngredientCodecTest {
    private static RegistryOps<com.google.gson.JsonElement> ops;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        HolderLookup.Provider lookup = VanillaRegistries.createLookup();
        ops = RegistryOps.create(JsonOps.INSTANCE, lookup);
    }

    private static Optional<Ingredient> parse(String json) {
        return CarvingRecipe.INGREDIENT_CODEC.parse(ops, JsonParser.parseString(json)).result();
    }

    @Test
    void readsTheStringForm() {
        Ingredient ingredient = parse("\"minecraft:bone_block\"").orElseThrow();
        assertTrue(ingredient.test(new ItemStack(Items.BONE_BLOCK)));
        assertFalse(ingredient.test(new ItemStack(Items.OAK_LOG)));
    }

    @Test
    void readsTheOldItemObject() {
        Ingredient ingredient = parse("{\"item\":\"minecraft:bone_block\"}").orElseThrow();
        assertTrue(ingredient.test(new ItemStack(Items.BONE_BLOCK)));
        assertFalse(ingredient.test(new ItemStack(Items.OAK_LOG)));
    }

    @Test
    void readsTheOldTagObject() {
        assertTrue(parse("{\"tag\":\"minecraft:logs\"}").isPresent());
    }

    @Test
    void readsAMixedList() {
        Ingredient ingredient = parse("[{\"item\":\"minecraft:bone_block\"},\"minecraft:stone\"]").orElseThrow();
        assertTrue(ingredient.test(new ItemStack(Items.BONE_BLOCK)));
        assertTrue(ingredient.test(new ItemStack(Items.STONE)));
    }

    @Test
    void rejectsAnObjectWithoutItemOrTag() {
        assertTrue(parse("{}").isEmpty());
    }
}
