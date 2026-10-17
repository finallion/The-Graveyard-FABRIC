package com.lion.graveyard.util;

import com.lion.graveyard.init.TGTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class Scarecrows {

    public static final int RADIUS = 8;

    private Scarecrows() {
    }

    public static boolean isNear(Level world, BlockPos center, int radius) {
        AABB box = new AABB(center).inflate(radius);
        boolean wornPumpkin = !world.getEntitiesOfClass(ArmorStand.class, box, Scarecrows::isWearingPumpkin).isEmpty();

        if (wornPumpkin) {
            return true;
        }

        for (BlockPos pos : BlockPos.betweenClosed(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius)) {
            BlockState state = world.getBlockState(pos);

            if (state.is(TGTags.CROW_REPELLENTS)
                    || state.is(TGTags.SCARECROW_HEADS) && isBuiltScarecrow(world, pos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWearingPumpkin(ArmorStand stand) {
        Item head = stand.getItemBySlot(EquipmentSlot.HEAD).getItem();
        return head == Items.CARVED_PUMPKIN || head == Items.JACK_O_LANTERN;
    }

    private static boolean isBuiltScarecrow(Level world, BlockPos head) {
        BlockPos body = head.below();

        if (!world.getBlockState(body).is(TGTags.SCARECROW_BODIES)) {
            return false;
        }

        for (Direction side : Direction.values()) {
            if (side != Direction.UP && world.getBlockState(body.relative(side)).is(BlockTags.FENCES)) {
                return true;
            }
        }
        return false;
    }
}
