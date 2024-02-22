package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public class JsonBuyItemTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {
        loadDefaultStats(json);

        ItemStack buy = getItemStackFromJson(json.get("buy").getAsJsonObject());
        ItemStack currency = getItemStackFromJson(json.get("reward").getAsJsonObject());

        return new Factory(buy, currency, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
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

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(buy, currency, this.maxUses, this.experience, this.multiplier);
        }

    }
}
