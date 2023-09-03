package com.lion.graveyard.platform.forge;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lion.graveyard.trades.TradeOfferManager;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class NamelessHangedTradeOfferResourceListener extends JsonDataLoader implements ResourceReloader {

    public NamelessHangedTradeOfferResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "nameless_hanged_trades");
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
