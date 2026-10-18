package com.lion.graveyard.item;

import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class VialOfBlood extends Item {
    public VialOfBlood(Properties properties) {
        super(properties);
    }

    public static float getBlood(ItemStack stack) {
        if (!LegacyItemData.hasData(stack)) {
            return 0.1F;
        }

        return LegacyItemData.view(stack).getFloatOr(LegacyItemData.BLOOD, 0.0F);
    }

    public static void setBlood(ItemStack stack, float blood) {
        LegacyItemData.edit(stack, data -> {
            if (blood < 0.9F) {
                data.putFloat(LegacyItemData.BLOOD, blood);
            } else {
                data.remove(LegacyItemData.BLOOD);
            }
        });
    }


    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag context
    ) {
        float blood = LegacyItemData.view(stack).getFloatOr(LegacyItemData.BLOOD, 0.0F);

        if (blood > 0.8F && blood < 0.9F) {
            tooltip.accept(Component.translatable("item.graveyard.vial_of_blood.level_full").withStyle(ChatFormatting.GRAY));
        } else {
            int level = Math.max(1, (int) (blood * 10));
            tooltip.accept(Component.translatable("item.graveyard.vial_of_blood.level", level).withStyle(ChatFormatting.GRAY));
        }

        tooltip.accept(Component.translatable("item.graveyard.vial_of_blood.lore").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        tooltip.accept(Component.translatable("item.graveyard.vial_of_blood.hint").withStyle(ChatFormatting.DARK_GRAY));

    }
}
