package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.recipe.CarvingRecipe;
import com.lion.graveyard.recipe.OssuaryRecipe;
import java.util.function.Supplier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class TGRecipeTypes {

    public static Supplier<RecipeType<OssuaryRecipe>> OSSUARY_CARVING = RegistryHelper.registerRecipeType("ossuary_carving", TGRecipeTypes.Type.INSTANCE);
    public static Supplier<RecipeSerializer<OssuaryRecipe>> OSSUARY_CARVING_SERIALIZER = RegistryHelper.registerRecipeSerializer("ossuary_carving", CarvingRecipe.serializer(OssuaryRecipe::new));

    public static void init() {}


    public static class Type implements RecipeType<OssuaryRecipe> {
        private Type() {}

        public static final Type INSTANCE = new Type();
    }
}
