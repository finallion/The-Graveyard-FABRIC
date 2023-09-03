package com.lion.graveyard.platform.fabric;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.trades.TradeOfferManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class NamelessHangedTradeOfferResourceListener extends JsonDataLoader implements IdentifiableResourceReloadListener {

    public NamelessHangedTradeOfferResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "nameless_hanged_trades");
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Graveyard.MOD_ID,"nameless_hanged_trades_data_loader");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) {
                return;
            }

            TradeOfferManager.deserializeJson(jsonElement.getAsJsonObject());
        });
    }
}
