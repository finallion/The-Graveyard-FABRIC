package com.lion.graveyard.item;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.chronicle.ChronicleOpener;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A loose leaf of the chronicle. Placed on a block it becomes a lore book; read in the air it
/// records the page and is used up. The text lives in the chronicle screen, never on the item.
public class DiaryPageItem extends BlockItem {
    public static final String PAGE_KEY = "page";

    public DiaryPageItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static ItemStack of(Item item, String page) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateNbt().putString(PAGE_KEY, page);
        return stack;
    }

    @Nullable
    public static String pageOf(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(PAGE_KEY, NbtElement.STRING_TYPE)) return null;
        String page = nbt.getString(PAGE_KEY);
        return Chronicle.isValidId(page) ? page : null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        String page = pageOf(stack);
        if (page == null) return TypedActionResult.pass(stack);
        if (world.isClient()) ChronicleOpener.open(page);
        if (user instanceof ServerPlayerEntity player) {
            boolean fresh = Chronicle.find(player, page);
            if (!fresh) {
                player.sendMessage(Text.translatable("chronicle.graveyard.already_found").formatted(Formatting.GRAY), true);
            }
            if (!player.getAbilities().creativeMode) stack.decrement(1);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String page = pageOf(stack);
        if (page != null) {
            tooltip.add(Text.translatable("chronicle.graveyard.page." + page + ".title").formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }
}
