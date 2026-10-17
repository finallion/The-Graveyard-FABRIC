package com.lion.graveyard.trades;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.Graveyard;
import com.lion.villagertradingplus.tradeoffers.TradeOfferManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.npc.VillagerTrades;

/// Trade pool of the Nameless Hanged, read from `data/graveyard/nameless_hanged_trades/`.
///
/// The VillagerTradingPlus file loader keys files by profession, so it cannot take these files.
public final class NamelessHangedTrades {

    /// The folder is his, but a datapack can drop anything into it, so files still have to name him.
    private static final String PROFESSION = "graveyard:nameless_hanged";

    public static final List<VillagerTrades.ItemListing> TRADES_REGISTRY = new ArrayList<>();

    private NamelessHangedTrades() {
    }

    /// Rebuilt per reload, so `/reload` does not duplicate trades.
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

                // Null means unusable. The parser already logged the reason.
                VillagerTrades.ItemListing factory = TradeOfferManager.deserializeTrade(element.getAsJsonObject());

                if (factory != null) {
                    TRADES_REGISTRY.add(factory);
                }
            }
        }
    }
}
