package com.lion.graveyard.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VialOfBlood extends Item {
    private static final String BlOOD_KEY = "Blood";

    public VialOfBlood() {
        super(new Item.Properties().stacksTo(1));
    }

    public static float getBlood(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTag();
        if (nbtCompound == null) {
            return 0.1F;
        } else {
            return nbtCompound.getFloat(BlOOD_KEY);
        }
    }

    public static void setBlood(ItemStack stack, float blood) {
        CompoundTag nbtCompound = stack.getOrCreateTag();
        if (blood < 0.9F) {
            nbtCompound.putFloat(BlOOD_KEY, blood);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        float blood = 0;
        if (stack.hasTag()) {
            blood = stack.getTag().getFloat(BlOOD_KEY);
        }

        if (blood > 0.8F && blood < 0.9F) {
            tooltip.add(Component.translatable("Blood level: full").withStyle(ChatFormatting.GRAY));
        } else {
            int level = (int)(blood * 10);
            if (level == 0) {
                tooltip.add(Component.translatable("Blood level: 1/8").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("Blood level: " + level + "/8").withStyle(ChatFormatting.GRAY));
            }
        }

    }
}
