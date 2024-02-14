package com.lion.graveyard.trades.trades;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.NotNull;

public class JsonSellStructureMapTradeOffer extends JsonTradeOffer {

    @Override
    @NotNull
    public TradeOffers.Factory deserialize(JsonObject json) {
        loadDefaultStats(json);

        TagKey<Structure> structure = TagKey.of(RegistryKeys.STRUCTURE, readIdentifier(json, "structure_id", ""));
        String name = readString(json, "name", "");
        ItemStack currency = getItemStackFromJson(json.get("priceIn").getAsJsonObject());
        ItemStack buy = getItemStackFromJson(json.get("buy").getAsJsonObject());

        return new Factory(buy, currency, structure, name, maxUses, experience, priceMultiplier);
    }

    private static class Factory implements TradeOffers.Factory {
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

        public TradeOffer create(Entity entity, net.minecraft.util.math.random.Random random) {
            if (!(entity.getLevel() instanceof ServerWorld serverWorld)) {
                return null;
            } else {
                BlockPos blockPos = serverWorld.locateStructure(this.structure, entity.getBlockPos(), 100, true);
                if (blockPos != null) {
                    ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                    FilledMapItem.fillExplorationMap(serverWorld, itemStack);
                    MapState.addDecorationsNbt(itemStack, blockPos, "+", MapIcon.Type.RED_X);
                    itemStack.setCustomName(Text.translatable(this.nameKey));
                    return new TradeOffer(currency, buy, itemStack, this.maxUses, this.experience, this.multiplier);
                } else {
                    return null;
                }
            }
        }
    }
}
