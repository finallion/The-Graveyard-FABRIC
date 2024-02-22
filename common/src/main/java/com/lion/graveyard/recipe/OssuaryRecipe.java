package com.lion.graveyard.recipe;

import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;

public class OssuaryRecipe extends SingleItemRecipe {

    public OssuaryRecipe(String group, Ingredient input, ItemStack output) {
        super(TGRecipeTypes.OSSUARY_CARVING.get(), TGRecipeTypes.OSSUARY_CARVING_SERIALIZER.get(), group, input, output);
    }
    public boolean matches(Container inventory, Level world) {
        return this.ingredient.test(inventory.getItem(0));
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(TGBlocks.LEANING_SKELETON.get());
    }


    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer2<T extends SingleItemRecipe> extends Serializer<T> {
        public Serializer2(Factory<T> recipeFactory) {
            super(recipeFactory);
        }
    }
}
