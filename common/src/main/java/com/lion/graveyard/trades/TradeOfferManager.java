package com.lion.graveyard.trades;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.trades.trades.*;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TradeOfferManager {
    public static final Map<Identifier, JsonTradeOffer> tradeOfferRegistry = new HashMap<>();
    public static final List<TradeOffers.Factory> TRADES_REGISTRY = new ArrayList<>();

    public static void registerTradeOffers() {
        Graveyard.LOGGER.info("Registered JSON trade offer adapter.");
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"sell_item"), new JsonSellItemTradeOffer());
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"buy_item"), new JsonBuyItemTradeOffer());
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"process_item"), new JsonProcessItemTradeOffer());
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"sell_potion"), new JsonSellPotionTradeOffer());
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"sell_enchanted_tool"), new JsonSellEnchantedToolTradeOffer());
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"sell_enchanted_book"), new JsonSellEnchantedBookTradeOffer());
        tradeOfferRegistry.put(new Identifier(Graveyard.MOD_ID,"sell_map"), new JsonSellStructureMapTradeOffer());
    }

    public static void deserializeJson(JsonObject jsonRoot) {
        String professionId = jsonRoot.get("profession").getAsString();

        if (professionId.equals("graveyard:nameless_hanged")) {
            deserializeTrades(jsonRoot, TradeOfferManager::registerVillagerTrade);
        }
    }

    private static void deserializeTrades(@NotNull JsonObject jsonRoot, Consumer<TradeOffers.Factory> tradeConsumer) {
        for (Map.Entry<String, JsonElement> entry : jsonRoot.get("trades").getAsJsonObject().entrySet()) {

            JsonArray tradesArray = entry.getValue().getAsJsonArray();

            for (JsonElement tradeElement : tradesArray) {
                JsonObject trade = tradeElement.getAsJsonObject();
                JsonTradeOffer adapter = tradeOfferRegistry.get(Identifier.tryParse(trade.get("type").getAsString()));

                if (adapter == null) {
                    Graveyard.LOGGER.error("Trade type: " + trade.get("type").getAsString() + " is broken.");
                    Graveyard.LOGGER.error("Error in deserializing trades." +
                            "Trade element: " + tradeElement + " and " +
                            "Trade: " + trade + " in " + tradesArray + " is broken. \n" +
                            "Sending faulty JSON: " + jsonRoot);
                } else {
                    tradeConsumer.accept(adapter.deserialize(trade));
                }
            }
        }
    }

    public static void registerVillagerTrade(TradeOffers.Factory trade) {
        TRADES_REGISTRY.add(trade);
    }
}
