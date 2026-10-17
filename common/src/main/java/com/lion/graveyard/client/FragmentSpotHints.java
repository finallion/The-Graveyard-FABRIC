package com.lion.graveyard.client;

import com.lion.graveyard.blocks.AltarBlock;
import com.lion.graveyard.blocks.OminousBoneStaffFragment;
import com.lion.graveyard.init.TGBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

    private FragmentSpotHints() {
    }

    public static void clientTick(MinecraftClient client) {
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null || !holdsFragment(player) || !(world.isNight() || world.getDimension().hasFixedTime())) {
            ALTARS.clear();
            ticks = 0;
            return;
        }

        if (ticks % SCAN_INTERVAL == 0) {
            scanAltars(world, player.getBlockPos());
        }
        if (ticks % PARTICLE_INTERVAL == 0) {
            for (BlockPos altar : ALTARS) {
                spawnParticles(world, altar);
            }
        }
        ticks++;
    }

    private static boolean holdsFragment(ClientPlayerEntity player) {
        return isFragment(player.getMainHandStack()) || isFragment(player.getOffHandStack());
    }

    private static boolean isFragment(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof OminousBoneStaffFragment;
    }

    private static void scanAltars(ClientWorld world, BlockPos center) {
        ALTARS.clear();
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dy = -RANGE_UP_DOWN; dy <= RANGE_UP_DOWN; dy++) {
                for (int dz = -RANGE; dz <= RANGE; dz++) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = world.getBlockState(cursor);
                    if (state.isOf(TGBlocks.ALTAR.get()) && !state.get(AltarBlock.BLOODY)) {
                        ALTARS.add(cursor.toImmutable());
                    }
                }
            }
        }
    }

    private static void spawnParticles(ClientWorld world, BlockPos altar) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos middle = altar.offset(direction, ROW_DISTANCE);
            BlockPos[] spots = {
                    middle.offset(direction.rotateYClockwise(), END_OFFSET),
                    middle,
                    middle.offset(direction.rotateYCounterclockwise(), END_OFFSET)
            };
            if (!isUsableRow(world, spots)) {
                continue;
            }
            for (BlockPos spot : spots) {
                if (world.getBlockState(spot).isAir()) {
                    world.addParticle(ParticleTypes.SOUL,
                            spot.getX() + 0.2D + world.random.nextDouble() * 0.6D,
                            spot.getY() + 0.1D + world.random.nextDouble() * 0.3D,
                            spot.getZ() + 0.2D + world.random.nextDouble() * 0.6D,
                            0.0D, 0.03D, 0.0D);
                }
            }
        }
    }

    private static boolean isUsableRow(ClientWorld world, BlockPos[] spots) {
        for (BlockPos spot : spots) {
            BlockState state = world.getBlockState(spot);
            boolean free = state.isAir() || state.getBlock() instanceof OminousBoneStaffFragment;
            if (!free || !world.getBlockState(spot.down()).isSideSolidFullSquare(world, spot.down(), Direction.UP)) {
                return false;
            }
        }
        return true;
    }
}
