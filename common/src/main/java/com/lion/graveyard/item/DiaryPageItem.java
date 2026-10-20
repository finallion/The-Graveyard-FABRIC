package com.lion.graveyard.item;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.chronicle.ChronicleOpener;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class DiaryPageItem extends BlockItem {
    public DiaryPageItem(Block block, Properties settings) {
        super(block, settings);
    }

    public static ItemStack of(Item item, String page) {
        ItemStack stack = new ItemStack(item);
        LegacyItemData.edit(stack, data -> data.putString(LegacyItemData.PAGE, page));
        return stack;
    }

    @Nullable
    public static String pageOf(ItemStack stack) {
        CompoundTag nbt = LegacyItemData.view(stack);

        String page = nbt.getString(LegacyItemData.PAGE).orElse(null);
        return Chronicle.isValidId(page) ? page : null;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        String page = pageOf(stack);

        if (page == null) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide()) {
            ChronicleOpener.open(page);
        }

        if (user instanceof ServerPlayer player) {
            boolean fresh = Chronicle.find(player, page);

            if (!fresh) {
                player.sendOverlayMessage(Component.translatable("chronicle.graveyard.already_found").withStyle(ChatFormatting.GRAY));
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag context
    ) {
        String page = pageOf(stack);

        if (page != null) {
            tooltip.accept(Component.translatable("chronicle.graveyard.page." + page + ".title").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }
}
