package com.lion.graveyard.client;

import com.lion.graveyard.blocks.AltarBlock;
import com.lion.graveyard.blocks.OminousBoneStaffFragment;
import com.lion.graveyard.init.TGBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class FragmentSpotHints {
    private static final int RANGE = 24;
    private static final int RANGE_UP_DOWN = 12;
    private static final int SCAN_INTERVAL = 20;
    private static final int PARTICLE_INTERVAL = 4;
    private static final int ROW_DISTANCE = 7;
    private static final int END_OFFSET = 3;

    private static final List<BlockPos> ALTARS = new ArrayList<>();
    private static int ticks;

    private FragmentSpotHints() {}

    public static void clientTick(Minecraft client) {
        ClientLevel world = client.level;
        LocalPlayer player = client.player;

        if (world == null || player == null || !holdsFragment(player) || !(world.isDarkOutside() || world.dimensionType().hasFixedTime())) {
            ALTARS.clear();
            ticks = 0;
            return;
        }

        if (ticks % SCAN_INTERVAL == 0) {
            scanAltars(world, player.blockPosition());
        }

        if (ticks % PARTICLE_INTERVAL == 0) {
            for (BlockPos altar : ALTARS) {
                spawnParticles(world, altar);
            }
        }

        ticks++;
    }

    private static boolean holdsFragment(LocalPlayer player) {
        return isFragment(player.getMainHandItem()) || isFragment(player.getOffhandItem());
    }

    private static boolean isFragment(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof OminousBoneStaffFragment;
    }

    private static void scanAltars(ClientLevel world, BlockPos center) {
        ALTARS.clear();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dy = -RANGE_UP_DOWN; dy <= RANGE_UP_DOWN; dy++) {
                for (int dz = -RANGE; dz <= RANGE; dz++) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = world.getBlockState(cursor);

                    if (state.is(TGBlocks.ALTAR.get()) && !state.getValue(AltarBlock.BLOODY)) {
                        ALTARS.add(cursor.immutable());
                    }
                }
            }
        }
    }

    private static void spawnParticles(ClientLevel world, BlockPos altar) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos middle = altar.relative(direction, ROW_DISTANCE);
            BlockPos[] spots = {
                    middle.relative(direction.getClockWise(), END_OFFSET),
                    middle,
                    middle.relative(direction.getCounterClockWise(), END_OFFSET)
            };

            if (!isUsableRow(world, spots)) {
                continue;
            }

            for (BlockPos spot : spots) {
                if (world.getBlockState(spot).isAir()) {
                    world.addParticle(
                            ParticleTypes.SOUL,
                            spot.getX() + 0.2D + world.getRandom().nextDouble() * 0.6D,
                            spot.getY() + 0.1D + world.getRandom().nextDouble() * 0.3D,
                            spot.getZ() + 0.2D + world.getRandom().nextDouble() * 0.6D,
                            0.0D,
                            0.03D,
                            0.0D
                    );
                }
            }
        }
    }

    private static boolean isUsableRow(ClientLevel world, BlockPos[] spots) {
        for (BlockPos spot : spots) {
            BlockState state = world.getBlockState(spot);
            boolean free = state.isAir() || state.getBlock() instanceof OminousBoneStaffFragment;

            if (!free || !world.getBlockState(spot.below()).isFaceSturdy(world, spot.below(), Direction.UP)) {
                return false;
            }
        }
        return true;
    }
}
