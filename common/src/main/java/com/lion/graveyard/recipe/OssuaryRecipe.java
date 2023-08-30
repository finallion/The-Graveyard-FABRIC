package com.lion.graveyard.recipe;

import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class OssuaryRecipe extends CarvingRecipe {

    public OssuaryRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
        super(TGRecipeTypes.OSSUARY_CARVING.get(), TGRecipeTypes.OSSUARY_CARVING_SERIALIZER.get(), id, group, input, output);
    }

    public boolean matches(Inventory inventory, World world) {
        return this.input.test(inventory.getStack(0));
    }

    public ItemStack createIcon() {
        return new ItemStack(TGBlocks.LEANING_SKELETON.get());
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
