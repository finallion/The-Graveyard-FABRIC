package com.lion.graveyard.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VialOfBlood extends Item {
    private static final String BlOOD_KEY = "Blood";

    public VialOfBlood() {
        super(new Item.Settings().maxCount(1));
    }

    public static float getBlood(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return 0.1F;
        } else {
            return nbtCompound.getFloat(BlOOD_KEY);
        }
    }

    public static void setBlood(ItemStack stack, float blood) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (blood < 0.9F) {
            nbtCompound.putFloat(BlOOD_KEY, blood);
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        float blood = 0;
        if (stack.hasNbt()) {
            blood = stack.getNbt().getFloat(BlOOD_KEY);
        }

        if (blood > 0.8F && blood < 0.9F) {
            tooltip.add(Text.translatable("Blood level: full").formatted(Formatting.GRAY));
        } else {
            int level = (int)(blood * 10);
            if (level == 0) {
                tooltip.add(Text.translatable("Blood level: 1/8").formatted(Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable("Blood level: " + level + "/8").formatted(Formatting.GRAY));
            }
        }

    }
}
