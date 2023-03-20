package com.finallion.graveyard.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TGRecipeTypes {

    public static RecipeType<OssuaryRecipe> OSSUARY_CARVING = Registry.register(Registries.RECIPE_TYPE, TGRecipeTypes.Type.IDENTIFIER, TGRecipeTypes.Type.INSTANCE);

    public static RecipeSerializer<OssuaryRecipe> OSSUARY_CARVING_SERIALIZER = RecipeSerializer.register("graveyard:ossuary_carving", new CarvingRecipe.Serializer<>(OssuaryRecipe::new));

    public static void init() {}

    public static class Type implements RecipeType<OssuaryRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final Identifier IDENTIFIER = new Identifier("graveyard:ossuary_carving");
    }
}
