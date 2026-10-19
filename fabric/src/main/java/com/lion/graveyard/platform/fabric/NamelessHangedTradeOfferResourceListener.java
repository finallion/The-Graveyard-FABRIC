package com.lion.graveyard.platform.fabric;

import com.google.gson.JsonElement;
import com.lion.graveyard.Graveyard;
import com.lion.graveyard.trades.NamelessHangedTrades;
import com.lion.graveyard.util.LenientJsonFiles;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import java.util.Map;

public class NamelessHangedTradeOfferResourceListener extends SimpleJsonResourceReloadListener<JsonElement> implements IdentifiableResourceReloadListener {

    public NamelessHangedTradeOfferResourceListener() {
        super(ExtraCodecs.JSON, FileToIdConverter.json("nameless_hanged_trades"));
    }

    @Override
    public Identifier getFabricId() {
        return Graveyard.id("nameless_hanged_trades_data_loader");
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager manager, ProfilerFiller profiler) {
        return LenientJsonFiles.read(manager, "nameless_hanged_trades");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
        NamelessHangedTrades.clear();

        loader.forEach((identifier, jsonElement) -> {
            if (!jsonElement.isJsonObject()) {
                return;
            }

            NamelessHangedTrades.deserializeJson(jsonElement.getAsJsonObject());
        });
    }
}
