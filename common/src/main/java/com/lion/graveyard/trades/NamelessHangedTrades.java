package com.lion.graveyard.trades;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.Graveyard;
import com.lion.villagertradingplus.tradeoffers.TradeFactory;
import com.lion.villagertradingplus.tradeoffers.TradeOfferManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// Trade pool of the Nameless Hanged, read from `data/graveyard/nameless_hanged_trades/`.
///
/// The VillagerTradingPlus file loader keys files by profession, so it cannot take these files.
public final class NamelessHangedTrades {

    /// The folder is his, but a datapack can drop anything into it, so files still have to name him.
    private static final String PROFESSION = "graveyard:nameless_hanged";

    private static final List<JsonObject> PENDING_TRADES = new ArrayList<>();

    private static List<TradeFactory> factories;

    private NamelessHangedTrades() {}

    /// Rebuilt per reload, so `/reload` does not duplicate trades.
    public static void clear() {
        PENDING_TRADES.clear();
        factories = null;
    }

    /// Item components are not bound during the reload listener. Parse trades on first use.
    public static List<TradeFactory> factories() {
        if (factories == null) {
            List<TradeFactory> parsed = new ArrayList<>();

            for (JsonObject trade : PENDING_TRADES) {
                // The parser logs the reason before it returns null.
                TradeFactory factory = parse(trade);

                if (factory != null) {
                    parsed.add(factory);
                }
            }

            factories = parsed;
        }

        return factories;
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

                PENDING_TRADES.add(element.getAsJsonObject());
            }
        }
    }

    private static TradeFactory parse(JsonObject entry) {
        try {
            return TradeOfferManager.deserializeTrade(entry);
        } catch (RuntimeException e) {
            Graveyard.getLogger().warn("Skipping nameless hanged trade entry {}: {}", entry, e.toString());
            return null;
        }
    }
}
