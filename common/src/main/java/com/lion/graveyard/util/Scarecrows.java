package com.lion.graveyard.util;

import com.lion.graveyard.init.TGTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class Scarecrows {

    public static final int RADIUS = 8;

    private Scarecrows() {
    }

    public static boolean isNear(World world, BlockPos center, int radius) {
        Box box = new Box(center).expand(radius);
        boolean wornPumpkin = !world.getEntitiesByClass(ArmorStandEntity.class, box, Scarecrows::isWearingPumpkin).isEmpty();
        if (wornPumpkin) {
            return true;
        }

        for (BlockPos pos : BlockPos.iterate(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius)) {
            BlockState state = world.getBlockState(pos);
            if (state.isIn(TGTags.CROW_REPELLENTS)
                    || state.isIn(TGTags.SCARECROW_HEADS) && isBuiltScarecrow(world, pos)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWearingPumpkin(ArmorStandEntity stand) {
        Item head = stand.getEquippedStack(EquipmentSlot.HEAD).getItem();
        return head == Items.CARVED_PUMPKIN || head == Items.JACK_O_LANTERN;
    }

    private static boolean isBuiltScarecrow(World world, BlockPos head) {
        BlockPos body = head.down();
        if (!world.getBlockState(body).isIn(TGTags.SCARECROW_BODIES)) {
            return false;
        }
        for (Direction side : Direction.values()) {
            if (side != Direction.UP && world.getBlockState(body.offset(side)).isIn(BlockTags.FENCES)) {
                return true;
            }
        }
        return false;
    }
}
