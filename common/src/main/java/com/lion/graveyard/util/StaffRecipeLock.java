package com.lion.graveyard.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.Recipe;

public final class StaffRecipeLock {
    private StaffRecipeLock() {}

    public static boolean isLocked(ServerPlayer player, Recipe<?> recipe) {
        ResourceLocation id = recipe.getId();
        return id.getNamespace().equals("graveyard") && id.getPath().endsWith("_bone_staff") && !player.getRecipeBook().contains(recipe);
    }

    public static boolean blocksTake(Player player, Container resultInventory) {
        return player instanceof ServerPlayer serverPlayer
                && resultInventory instanceof RecipeHolder unlocker
                && unlocker.getRecipeUsed() != null
                && isLocked(serverPlayer, unlocker.getRecipeUsed());
    }
}
