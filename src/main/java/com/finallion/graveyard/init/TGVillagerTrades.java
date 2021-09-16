package com.finallion.graveyard.init;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.List;
import java.util.Locale;

public class TGVillagerTrades {


    private static TradeOffer createMapTrade(int price, StructureFeature<?> structure, MapIcon.Type iconType, int maxUses, int experience, Entity entity) {
        if (!(entity.world instanceof ServerWorld)) {
            return null;
        } else {
            ServerWorld serverWorld = (ServerWorld)entity.world;
            BlockPos blockPos = serverWorld.locateStructure(structure, entity.getBlockPos(), 100, true);
            if (blockPos != null) {
                ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                FilledMapItem.fillExplorationMap(serverWorld, itemStack);
                MapState.addDecorationsNbt(itemStack, blockPos, "+", iconType);
                String var10003 = structure.getName();
                itemStack.setCustomName(new TranslatableText("filled_map." + var10003.toLowerCase(Locale.ROOT)));
                return new TradeOffer(new ItemStack(Items.EMERALD, price), new ItemStack(Items.COMPASS), itemStack, maxUses, experience, 0.2F);
            } else {
                return null;
            }
        }
    }

    private static void sellMapItem(List<TradeOffers.Factory> factories, StructureFeature<?> structure, int price, int count, int rewardedExp) {
        factories.add((entity, random) -> createMapTrade(price, structure, MapIcon.Type.TARGET_X, count, rewardedExp, entity));
    }


    public static void init() {

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 1, (factories -> {
            sellMapItem(factories, TGStructures.LARGE_WALLED_GRAVEYARD, 25,1, 10);
        }));

    }


}
