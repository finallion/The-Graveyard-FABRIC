package com.finallion.graveyard.recipe;

import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class OssuaryRecipe extends CarvingRecipe {

    public OssuaryRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
        super(TGRecipeTypes.OSSUARY_CARVING, TGRecipeTypes.OSSUARY_CARVING_SERIALIZER, id, group, input, output);
    }

    public boolean matches(Inventory inventory, World world) {
        return this.input.test(inventory.getStack(0));
    }

    public ItemStack createIcon() {
        return new ItemStack(TGBlocks.LEANING_SKELETON);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
