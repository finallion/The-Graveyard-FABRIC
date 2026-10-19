package com.lion.graveyard.client;

import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.item.VialOfBlood;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public record VialChargedProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<VialChargedProperty> MAP_CODEC = MapCodec.unit(new VialChargedProperty());

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        LivingEntity entity = owner == null ? null : owner.asLivingEntity();

        if (entity != null && stack.is(TGItems.VIAL_OF_BLOOD.get())) {
            return VialOfBlood.getBlood(stack);
        }

        return 0.0F;
    }

    @Override
    public MapCodec<VialChargedProperty> type() {
        return MAP_CODEC;
    }
}
