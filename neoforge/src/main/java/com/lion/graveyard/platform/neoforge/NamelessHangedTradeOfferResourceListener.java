package com.lion.graveyard.platform.neoforge;

import com.google.gson.JsonElement;
import com.lion.graveyard.trades.NamelessHangedTrades;
import com.lion.graveyard.util.LenientJsonFiles;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;

public class NamelessHangedTradeOfferResourceListener extends SimpleJsonResourceReloadListener<JsonElement> implements PreparableReloadListener {

    public NamelessHangedTradeOfferResourceListener() {
        super(ExtraCodecs.JSON, FileToIdConverter.json("nameless_hanged_trades"));
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
