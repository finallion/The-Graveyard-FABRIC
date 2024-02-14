package com.lion.graveyard.recipe;

import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;

public class OssuaryRecipe extends CuttingRecipe {

    public OssuaryRecipe(String group, Ingredient input, ItemStack output) {
        super(TGRecipeTypes.OSSUARY_CARVING.get(), TGRecipeTypes.OSSUARY_CARVING_SERIALIZER.get(), group, input, output);
    }
    public boolean matches(Inventory inventory, Level world) {
        return this.ingredient.test(inventory.getStack(0));
    }

    public ItemStack createIcon() {
        return new ItemStack(TGBlocks.LEANING_SKELETON.get());
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public static class Serializer2<T extends CuttingRecipe> extends Serializer<T> {

        public Serializer2(RecipeFactory<T> recipeFactory) {
            super(recipeFactory);
        }
    }
}
