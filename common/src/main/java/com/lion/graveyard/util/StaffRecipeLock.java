package com.lion.graveyard.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class StaffRecipeLock {
    private StaffRecipeLock() {}

    public static boolean isLocked(ServerPlayerEntity player, Recipe<?> recipe) {
        Identifier id = recipe.getId();
        return id.getNamespace().equals("graveyard") && id.getPath().endsWith("_bone_staff") && !player.getRecipeBook().contains(recipe);
    }

    public static boolean blocksTake(PlayerEntity player, Inventory resultInventory) {
        return player instanceof ServerPlayerEntity serverPlayer
                && resultInventory instanceof RecipeUnlocker unlocker
                && unlocker.getLastRecipe() != null
                && isLocked(serverPlayer, unlocker.getLastRecipe());
    }
}
