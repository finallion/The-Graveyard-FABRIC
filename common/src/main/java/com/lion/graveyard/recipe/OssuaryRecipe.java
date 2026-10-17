package com.lion.graveyard.recipe;

import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class OssuaryRecipe extends CarvingRecipe {

    public OssuaryRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output) {
        super(TGRecipeTypes.OSSUARY_CARVING.get(), TGRecipeTypes.OSSUARY_CARVING_SERIALIZER.get(), id, group, input, output);
    }

    public boolean matches(Container inventory, Level world) {
        return this.input.test(inventory.getItem(0));
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(TGBlocks.LEANING_SKELETON.get());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
