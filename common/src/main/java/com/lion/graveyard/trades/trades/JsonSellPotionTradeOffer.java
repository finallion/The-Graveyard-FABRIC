package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class JsonSellPotionTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {
        loadDefaultStats(json);

        ItemStack sell = getItemStackFromJson(json.get("sell").getAsJsonObject());
        ItemStack buy = getItemStackFromJson(json.get("convertible").getAsJsonObject());
        ItemStack currency = getItemStackFromJson(json.get("priceIn").getAsJsonObject());

        return new Factory(buy, sell, currency, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
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

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            List<Potion> list = BuiltInRegistries.POTION.stream().filter((potionx) -> !potionx.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potionx)).collect(Collectors.toList());

            Potion potion = list.get(random.nextInt(list.size()));
            ItemStack potionStack = PotionUtils.setPotion(new ItemStack(this.sell.getItem(), 1), potion);

            return new MerchantOffer(PotionUtils.setPotion(buy, Potions.WATER), currency, potionStack, this.maxUses, this.experience, this.multiplier);
        }

    }
}
