package com.lion.graveyard.recipe;

import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class OssuaryRecipe extends CarvingRecipe {

    public OssuaryRecipe(String group, Ingredient input, ItemStack output) {
        super(TGRecipeTypes.OSSUARY_CARVING.get(), TGRecipeTypes.OSSUARY_CARVING_SERIALIZER.get(), group, input, output);
    }

    @Override
    public boolean matches(SingleRecipeInput inventory, Level world) {
        return this.input.test(inventory.item());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
