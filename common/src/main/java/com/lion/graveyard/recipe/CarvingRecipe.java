package com.lion.graveyard.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public abstract class CarvingRecipe implements Recipe<SingleRecipeInput> {
    public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.withAlternative(Ingredient.CODEC, new LegacyIngredientCodec());

    protected final Ingredient input;
    protected final ItemStack output;
    private final RecipeType<? extends Recipe<SingleRecipeInput>> type;
    private final RecipeSerializer<? extends Recipe<SingleRecipeInput>> serializer;
    protected final String group;
    private PlacementInfo placementInfo;

    public CarvingRecipe(
            RecipeType<? extends Recipe<SingleRecipeInput>> type,
            RecipeSerializer<? extends Recipe<SingleRecipeInput>> serializer,
            String group,
            Ingredient input,
            ItemStack output
    ) {
        this.type = type;
        this.serializer = serializer;
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return this.serializer;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.input);
        }

        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput inventory) {
        return this.output.copy();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    private static DataResult<Item> notAir(Item item) {
        return item == Items.AIR ? DataResult.error(() -> "Carving result must not be air") : DataResult.success(item);
    }

    private static final class LegacyIngredientCodec implements Codec<Ingredient> {
        @Override
        public <T> DataResult<Pair<Ingredient, T>> decode(DynamicOps<T> ops, T input) {
            return toStringForm(ops, input).flatMap(modern -> Ingredient.CODEC.decode(ops, modern));
        }

        @Override
        public <T> DataResult<T> encode(Ingredient ingredient, DynamicOps<T> ops, T prefix) {
            return Ingredient.CODEC.encode(ingredient, ops, prefix);
        }

        private static <T> DataResult<T> toStringForm(DynamicOps<T> ops, T input) {
            if (ops.getStringValue(input).result().isPresent()) {
                return DataResult.success(input);
            }

            DataResult<List<T>> list = ops.getStream(input)
                    .map(Stream::toList);

            if (list.result().isPresent()) {
                List<DataResult<T>> converted = list.result()
                        .get()
                        .stream()
                        .map(element -> toStringForm(ops, element))
                        .toList();
                Optional<DataResult<T>> failed = converted.stream()
                        .filter(result -> result.result().isEmpty())
                        .findFirst();

                if (failed.isPresent()) {
                    return failed.get();
                }

                Stream<T> elements = converted.stream()
                        .map(result -> result.result().get());
                return DataResult.success(ops.createList(elements));
            }

            return ops.getMap(input)
                    .flatMap(map -> fromObject(ops, map));
        }

        private static <T> DataResult<T> fromObject(DynamicOps<T> ops, MapLike<T> map) {
            T item = map.get("item");

            if (item != null) {
                return ops.getStringValue(item)
                        .map(ops::createString);
            }

            T tag = map.get("tag");

            if (tag != null) {
                return ops.getStringValue(tag)
                        .map(id -> ops.createString("#" + id));
            }

            return DataResult.error(() -> "Ingredient object needs item or tag");
        }
    }

    public static <T extends CarvingRecipe> RecipeSerializer<T> serializer(RecipeFactory<T> recipeFactory) {
        MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                INGREDIENT_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.input),
                BuiltInRegistries.ITEM.byNameCodec().validate(CarvingRecipe::notAir).fieldOf("result").forGetter(recipe -> recipe.output.getItem()),
                ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(recipe -> recipe.output.getCount())
        ).apply(instance, (group, ingredient, item, count) -> recipeFactory.create(group, ingredient, new ItemStack(item, count))));
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                recipe -> recipe.group,
                Ingredient.CONTENTS_STREAM_CODEC,
                recipe -> recipe.input,
                ItemStack.STREAM_CODEC,
                recipe -> recipe.output,
                recipeFactory::create
        );

        return new RecipeSerializer<>(codec, streamCodec);
    }

    public interface RecipeFactory<T extends CarvingRecipe> {
        T create(String group, Ingredient input, ItemStack output);
    }
}
