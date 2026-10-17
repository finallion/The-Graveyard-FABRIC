package com.lion.graveyard.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public abstract class CarvingRecipe implements Recipe<Container> {
    protected final Ingredient input;
    protected final ItemStack output;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final String group;

    public CarvingRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient input, ItemStack output) {
        this.type = type;
        this.serializer = serializer;
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> defaultedList = NonNullList.create();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack assemble(Container inventory, RegistryAccess registryManager) {
        return this.output.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
        return this.output;
    }

    public static class Serializer<T extends CarvingRecipe> implements RecipeSerializer<T> {
        final Serializer.RecipeFactory<T> recipeFactory;

        public Serializer(Serializer.RecipeFactory<T> recipeFactory) {
            this.recipeFactory = recipeFactory;
        }

        @Override
        public T fromJson(ResourceLocation identifier, JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "group", "");
            Ingredient ingredient;

            if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            }

            String string2 = GsonHelper.getAsString(jsonObject, "result");
            int i = GsonHelper.getAsInt(jsonObject, "count");
            ItemStack itemStack = new ItemStack((ItemLike) BuiltInRegistries.ITEM.get(new ResourceLocation(string2)), i);
            return this.recipeFactory.create(identifier, string, ingredient, itemStack);
        }

        @Override
        public T fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
            String string = packetByteBuf.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItem();
            return this.recipeFactory.create(identifier, string, ingredient, itemStack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf packetByteBuf, T cuttingRecipe) {
            packetByteBuf.writeUtf(cuttingRecipe.group);
            cuttingRecipe.input.toNetwork(packetByteBuf);
            packetByteBuf.writeItem(cuttingRecipe.output);
        }

        public interface RecipeFactory<T extends CarvingRecipe> {
            T create(ResourceLocation id, String group, Ingredient input, ItemStack output);
        }
    }
}
