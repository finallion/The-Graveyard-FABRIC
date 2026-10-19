package com.lion.graveyard.util;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class StaffRecipeLock {
    private StaffRecipeLock() {}

    public static boolean isStaffRecipe(RecipeHolder<?> recipe) {
        Identifier id = recipe.id().identifier();
        return id.getNamespace().equals("graveyard") && id.getPath().endsWith("_bone_staff");
    }

    public static boolean isLocked(ServerPlayer player, RecipeHolder<?> recipe) {
        return isStaffRecipe(recipe) && !player.getRecipeBook().contains(recipe.id());
    }

    public static boolean blocksTake(Player player, Container resultInventory) {
        return player instanceof ServerPlayer serverPlayer
                && resultInventory instanceof RecipeCraftingHolder unlocker
                && unlocker.getRecipeUsed() != null
                && isLocked(serverPlayer, unlocker.getRecipeUsed());
    }
}
