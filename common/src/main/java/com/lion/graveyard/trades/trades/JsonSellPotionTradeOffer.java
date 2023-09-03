package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class JsonSellPotionTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public TradeOffers.Factory deserialize(JsonObject json) {
        loadDefaultStats(json);

        ItemStack sell = getItemStackFromJson(json.get("sell").getAsJsonObject());
        ItemStack buy = getItemStackFromJson(json.get("convertible").getAsJsonObject());
        ItemStack currency = getItemStackFromJson(json.get("priceIn").getAsJsonObject());

        return new Factory(buy, sell, currency, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements TradeOffers.Factory {
        private final ItemStack buy;
        private final ItemStack sell;
        private final ItemStack currency;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(ItemStack buy, ItemStack sell, ItemStack currency, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.sell = sell;
            this.currency = currency;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, net.minecraft.util.math.random.Random random) {
            List<Potion> list = Registries.POTION.stream().filter((potionx) -> !potionx.getEffects().isEmpty() && BrewingRecipeRegistry.isBrewable(potionx)).collect(Collectors.toList());

            Potion potion = list.get(random.nextInt(list.size()));
            ItemStack potionStack = PotionUtil.setPotion(new ItemStack(this.sell.getItem(), 1), potion);

            return new TradeOffer(PotionUtil.setPotion(buy, Potions.WATER), currency, potionStack, this.maxUses, this.experience, this.multiplier);
        }

    }
}
