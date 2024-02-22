package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class JsonTradeOffer {
    protected int maxUses;
    protected int experience;
    protected float priceMultiplier;

    @NotNull
    public abstract VillagerTrades.ItemListing deserialize(JsonObject json);

    protected void loadDefaultStats(JsonObject jsonObject) {
        this.maxUses = readInt(jsonObject, "max_uses", 12);
        this.experience = readInt(jsonObject, "villager_experience", 1);
        this.priceMultiplier = readFloat(jsonObject, "price_multiplier", 0.05f);
    }

    public static int readInt(JsonObject object, String key, int defaultValue) {
        return object.has(key) ? object.get(key).getAsInt() : defaultValue;
    }

    public static float readFloat(JsonObject object, String key, float defaultValue) {
        return object.has(key) ? object.get(key).getAsFloat() : defaultValue;
    }

    public static String readString(JsonObject object, String key, String defaultValue) {
        return object.has(key) ? object.get(key).getAsString() : defaultValue;
    }

    public static ResourceLocation readIdentifier(JsonObject object, String key, String defaultValue) {
        return object.has(key) ? ResourceLocation.tryParse(object.get(key).getAsString()) : new ResourceLocation(defaultValue);
    }


    public static ItemStack getItemStackFromJson(JsonObject json) {
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(json.get("item").getAsString()));

        if (item.isPresent()) {
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            return new ItemStack(item.get(), count);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getItemStackFromJsonWithoutCount(JsonObject json) {
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(json.get("item").getAsString()));
        return item.map(value -> new ItemStack(value, 1)).orElse(ItemStack.EMPTY);
    }
}
