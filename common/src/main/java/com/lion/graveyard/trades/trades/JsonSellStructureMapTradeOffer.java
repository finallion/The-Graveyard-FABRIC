package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;

public class JsonSellStructureMapTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {
        loadDefaultStats(json);

        TagKey<Structure> structure = TagKey.create(Registries.STRUCTURE, readIdentifier(json, "structure_id", ""));
        String name = readString(json, "name", "");
        ItemStack currency = getItemStackFromJson(json.get("priceIn").getAsJsonObject());
        ItemStack buy = getItemStackFromJson(json.get("buy").getAsJsonObject());

        return new Factory(buy, currency, structure, name, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final ItemStack currency;
        private final ItemStack buy;
        private final String nameKey;
        private final TagKey<Structure> structure;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public Factory(ItemStack buy, ItemStack currency, TagKey<Structure> structure, String nameKey, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.currency = currency;
            this.structure = structure;
            this.nameKey = nameKey;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            if (!(entity.level() instanceof ServerLevel serverLevel)) {
                return null;
            } else {
                BlockPos blockPos = serverLevel.findNearestMapStructure(this.structure, entity.blockPosition(), 100, true);
                if (blockPos != null) {
                    ItemStack itemStack = MapItem.create(serverLevel, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                    MapItem.renderBiomePreviewMap(serverLevel, itemStack);
                    MapItemSavedData.addTargetDecoration(itemStack, blockPos, "+", MapDecoration.Type.RED_X);
                    itemStack.setHoverName(Component.translatable(this.nameKey));

                    return new MerchantOffer(currency, buy, itemStack, this.maxUses, this.experience, this.multiplier);
                } else {
                    return null;
                }
            }
        }
    }
}
