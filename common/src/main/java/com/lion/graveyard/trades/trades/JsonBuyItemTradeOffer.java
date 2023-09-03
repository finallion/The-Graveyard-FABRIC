package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

public class JsonBuyItemTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public TradeOffers.Factory deserialize(JsonObject json) {
        loadDefaultStats(json);

        ItemStack buy = getItemStackFromJson(json.get("buy").getAsJsonObject());
        ItemStack currency = getItemStackFromJson(json.get("reward").getAsJsonObject());

        return new Factory(buy, currency, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements TradeOffers.Factory {
        private final ItemStack buy;
        private final ItemStack currency;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(ItemStack buy, ItemStack currency, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.currency = currency;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, net.minecraft.util.math.random.Random random) {
            return new TradeOffer(buy, currency, this.maxUses, this.experience, this.multiplier);
        }

    }
}
