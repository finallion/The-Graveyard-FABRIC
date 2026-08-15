package com.lion.graveyard.trades;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.Graveyard;
import com.lion.villagertradingplus.tradeoffers.TradeOfferManager;
import net.minecraft.village.TradeOffers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// Trade pool of the Nameless Hanged, read from `data/graveyard/nameless_hanged_trades/`.
///
/// He is a `MerchantEntity` without a villager profession, so VillagerTradingPlus' file loader
/// cannot take these files - it keys them by profession. Only its per-trade parser is used, which
/// is what gives him every trade type, conditions and dynamic pricing without a parser of our own.
public final class NamelessHangedTrades {

    /// The folder is his, but a datapack can drop anything into it, so files still have to name him.
    private static final String PROFESSION = "graveyard:nameless_hanged";

    /// Flat pool the entity draws its offers from at random. Group keys in the file are for the
    /// author's benefit only - he has no levels to tie them to.
    public static final List<TradeOffers.Factory> TRADES_REGISTRY = new ArrayList<>();

    private NamelessHangedTrades() {
    }

    /// Rebuilt from scratch per reload; appending would hand him a second copy of every trade
    /// after each `/reload`.
    public static void clear() {
        TRADES_REGISTRY.clear();
    }

    public static void deserializeJson(JsonObject jsonRoot) {
        JsonElement profession = jsonRoot.get("profession");
        if (profession == null || !profession.isJsonPrimitive() || !PROFESSION.equals(profession.getAsString())) {
            return;
        }

        JsonElement trades = jsonRoot.get("trades");
        if (trades == null || !trades.isJsonObject()) {
            Graveyard.getLogger().error("Skipping nameless hanged trade file: no 'trades' object.");
            return;
        }

        for (Map.Entry<String, JsonElement> group : trades.getAsJsonObject().entrySet()) {
            if (!group.getValue().isJsonArray()) {
                Graveyard.getLogger().error("Skipping nameless hanged trade group '{}': not an array.", group.getKey());
                continue;
            }

            for (JsonElement element : group.getValue().getAsJsonArray()) {
                if (!element.isJsonObject()) {
                    Graveyard.getLogger().error("Dropped an entry from nameless hanged trade group '{}': not an object.", group.getKey());
                    continue;
                }

                // Null means unusable; the parser has already logged which trade and why.
                TradeOffers.Factory factory = TradeOfferManager.deserializeTrade(element.getAsJsonObject());
                if (factory != null) {
                    TRADES_REGISTRY.add(factory);
                }
            }
        }
    }
}
