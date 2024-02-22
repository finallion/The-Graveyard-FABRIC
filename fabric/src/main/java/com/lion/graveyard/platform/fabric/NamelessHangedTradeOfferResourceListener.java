package com.lion.graveyard.platform.fabric;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.trades.TradeOfferManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class NamelessHangedTradeOfferResourceListener extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {

    public NamelessHangedTradeOfferResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "nameless_hanged_trades");
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Graveyard.MOD_ID,"nameless_hanged_trades_data_loader");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) {
                return;
            }

            TradeOfferManager.deserializeJson(jsonElement.getAsJsonObject());
        });
    }
}
