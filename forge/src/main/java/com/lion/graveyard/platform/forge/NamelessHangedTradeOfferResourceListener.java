package com.lion.graveyard.platform.forge;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lion.graveyard.trades.NamelessHangedTrades;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class NamelessHangedTradeOfferResourceListener extends SimpleJsonResourceReloadListener implements PreparableReloadListener {

    public NamelessHangedTradeOfferResourceListener() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create(), "nameless_hanged_trades");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
        NamelessHangedTrades.clear();

        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) {
                return;
            }

            NamelessHangedTrades.deserializeJson(jsonElement.getAsJsonObject());
        });
    }
}
