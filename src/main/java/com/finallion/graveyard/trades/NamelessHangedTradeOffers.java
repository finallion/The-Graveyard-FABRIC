package com.finallion.graveyard.trades;

import com.finallion.graveyard.init.TGItems;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

public class NamelessHangedTradeOffers {

    public static final Int2ObjectMap<TradeOffers.Factory[]> NAMELESS_HANGED_TRADES;

    private static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> map) {
        return new Int2ObjectOpenHashMap(map);
    }

    static {
        NAMELESS_HANGED_TRADES = copyToFastUtilMap(ImmutableMap.of(1,
                new TradeOffers.Factory[]{
                        new SellItemFactory(TGItems.SKELETON_HAND, 2, 1, 5, 1),
                        new SellItemFactory(TGItems.SKULL_ON_PIKE, 4, 1, 5, 1),
                        new SellItemFactory(TGItems.SKULL_PILE, 2, 1, 5, 1),
                        new SellItemFactory(TGItems.LATERALLY_LYING_SKELETON, 5, 1, 5, 1),
                        new SellItemFactory(TGItems.LYING_SKELETON, 1, 1, 12, 1),
                        new SellItemFactory(TGItems.LEANING_SKELETON, 1, 1, 8, 1),
                        new SellItemFactory(TGItems.SKULL_WITH_RIB_CAGE, 1, 1, 4, 1),
                        new SellItemFactory(TGItems.BONE_REMAINS, 3, 1, 12, 1),
                        new SellItemFactory(TGItems.TORSO_PILE, 3, 1, 8, 1),
                        new SellItemFactory(TGItems.WITHER_SKELETON_HAND, 2, 1, 5, 1),
                        new SellItemFactory(TGItems.WITHER_SKULL_ON_PIKE, 4, 1, 5, 1),
                        new SellItemFactory(TGItems.WITHER_SKULL_PILE, 2, 1, 5, 1),
                        new SellItemFactory(TGItems.LATERALLY_LYING_WITHER_SKELETON, 5, 1, 5, 1),
                        new SellItemFactory(TGItems.LYING_WITHER_SKELETON, 1, 1, 12, 1),
                        new SellItemFactory(TGItems.LEANING_WITHER_SKELETON, 1, 1, 8, 1),
                        new SellItemFactory(TGItems.WITHER_SKULL_WITH_RIB_CAGE, 1, 1, 4, 1),
                        new SellItemFactory(TGItems.WITHER_BONE_REMAINS, 3, 1, 12, 1),
                        new SellItemFactory(TGItems.WITHER_TORSO_PILE, 3, 1, 8, 1),
                        new SellItemFactory(TGItems.DARK_IRON_INGOT, 1, 1, 12, 1),
                        new SellItemFactory(Items.MOSS_BLOCK, 1, 2, 5, 1)}, 2,
                new TradeOffers.Factory[]{
                        new SellItemFactory(TGItems.SOUL_FIRE_BRAZIER, 5, 1, 4, 1),
                        new SellItemFactory(TGItems.FIRE_BRAZIER, 5, 1, 4, 1),
                        new SellItemFactory(TGItems.PEDESTAL, 3, 1, 6, 1),
                        new SellItemFactory(TGItems.CANDLE_HOLDER, 6, 1, 6, 1),
                        new SellMapFactory(13, StructureTags.ON_OCEAN_EXPLORER_MAPS, "filled_map.ruins", MapIcon.Type.TARGET_X, 12, 5),
                        new SellItemFactory(TGItems.CREEPER_SKELETON, 3, 3, 6, 1)}));


    }

    public static class SellItemFactory implements TradeOffers.Factory {
        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        public SellItemFactory(Item item, int price, int count, int experience) {
            this((ItemStack)(new ItemStack(item)), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
            this(stack, price, count, maxUses, experience, 0.05F);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(new ItemStack(TGItems.CORRUPTION, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }

    public static class SellMapFactory implements TradeOffers.Factory {
        private final int price;
        private final TagKey<Structure> structure;
        private final String nameKey;
        private final MapIcon.Type iconType;
        private final int maxUses;
        private final int experience;

        public SellMapFactory(int price, TagKey<Structure> structure, String nameKey, MapIcon.Type iconType, int maxUses, int experience) {
            this.price = price;
            this.structure = structure;
            this.nameKey = nameKey;
            this.iconType = iconType;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        @Nullable
        public TradeOffer create(Entity entity, Random random) {
            if (!(entity.world instanceof ServerWorld)) {
                return null;
            } else {
                ServerWorld serverWorld = (ServerWorld)entity.world;
                BlockPos blockPos = serverWorld.locateStructure(this.structure, entity.getBlockPos(), 100, true);
                if (blockPos != null) {
                    ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                    FilledMapItem.fillExplorationMap(serverWorld, itemStack);
                    MapState.addDecorationsNbt(itemStack, blockPos, "+", this.iconType);
                    itemStack.setCustomName(Text.translatable(this.nameKey));
                    return new TradeOffer(new ItemStack(TGItems.CORRUPTION, this.price), new ItemStack(Items.COMPASS), itemStack, this.maxUses, this.experience, 0.2F);
                } else {
                    return null;
                }
            }
        }
    }
}
