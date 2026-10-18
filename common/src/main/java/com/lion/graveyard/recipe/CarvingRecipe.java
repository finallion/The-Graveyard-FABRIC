package com.lion.graveyard.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public abstract class CarvingRecipe implements Recipe<SingleRecipeInput> {
    protected final Ingredient input;
    protected final ItemStack output;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final String group;

    public CarvingRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, String group, Ingredient input, ItemStack output) {
        this.type = type;
        this.serializer = serializer;
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
    public ItemStack assemble(SingleRecipeInput inventory, HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output;
    }

    private static DataResult<Item> notAir(Item item) {
        return item == Items.AIR ? DataResult.error(() -> "Carving result must not be air") : DataResult.success(item);
    }

    public static class Serializer<T extends CarvingRecipe> implements RecipeSerializer<T> {
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(Serializer.RecipeFactory<T> recipeFactory) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.input),
                    BuiltInRegistries.ITEM.byNameCodec().validate(CarvingRecipe::notAir).fieldOf("result").forGetter(recipe -> recipe.output.getItem()),
                    ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(recipe -> recipe.output.getCount())
            ).apply(instance, (group, ingredient, item, count) -> recipeFactory.create(group, ingredient, new ItemStack(item, count))));
            this.streamCodec = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    recipe -> recipe.group,
                    Ingredient.CONTENTS_STREAM_CODEC,
                    recipe -> recipe.input,
                    ItemStack.STREAM_CODEC,
                    recipe -> recipe.output,
                    recipeFactory::create);
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
        }

        public interface RecipeFactory<T extends CarvingRecipe> {
            T create(String group, Ingredient input, ItemStack output);
        }
    }
}
