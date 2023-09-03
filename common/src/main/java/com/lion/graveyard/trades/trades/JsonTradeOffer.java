package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class JsonTradeOffer {
    protected int maxUses;
    protected int experience;
    protected float priceMultiplier;

    @NotNull
    public abstract TradeOffers.Factory deserialize(JsonObject json);

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

    public static Identifier readIdentifier(JsonObject object, String key, String defaultValue) {
        return object.has(key) ? Identifier.tryParse(object.get(key).getAsString()) : new Identifier(defaultValue);
    }


    public static ItemStack getItemStackFromJson(JsonObject json) {
        Optional<Item> item = Registries.ITEM.getOrEmpty(Identifier.tryParse(json.get("item").getAsString()));

        if (item.isPresent()) {
            int count = json.has("count") ? json.get("count").getAsInt() : 1;
            return new ItemStack(item.get(), count);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getItemStackFromJsonWithoutCount(JsonObject json) {
        Optional<Item> item = Registries.ITEM.getOrEmpty(Identifier.tryParse(json.get("item").getAsString()));
        return item.map(value -> new ItemStack(value, 1)).orElse(ItemStack.EMPTY);
    }
}
