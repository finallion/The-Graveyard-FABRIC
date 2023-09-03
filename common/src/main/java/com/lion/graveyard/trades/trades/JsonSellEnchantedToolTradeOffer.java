package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

public class JsonSellEnchantedToolTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public TradeOffers.Factory deserialize(JsonObject json) {
        loadDefaultStats(json);

        ItemStack sell = getItemStackFromJson(json.get("sell").getAsJsonObject());
        ItemStack currency = getItemStackFromJson(json.get("basePriceIn").getAsJsonObject());

        return new Factory(sell, currency, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements TradeOffers.Factory {
        private final ItemStack sell;
        private final ItemStack currency;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(ItemStack sell, ItemStack currency, int maxUses, int experience, float multiplier) {
            this.sell = sell;
            this.currency = currency;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, net.minecraft.util.math.random.Random random) {
            int i = 5 + random.nextInt(15);
            ItemStack itemStack = EnchantmentHelper.enchant(random, new ItemStack(this.sell.getItem()), i, false);
            int j = Math.min(this.currency.getCount() + i, 64);

            return new TradeOffer(new ItemStack(currency.getItem(), j), itemStack, this.maxUses, this.experience, multiplier);
        }
    }
}
