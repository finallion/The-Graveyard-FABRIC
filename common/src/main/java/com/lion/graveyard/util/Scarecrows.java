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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;

public final class Scarecrows {

    public static final int RADIUS = 8;

    private Scarecrows() {}

    public static boolean isNear(Level world, BlockPos center, int radius) {
        AABB box = new AABB(center).inflate(radius);
        boolean wornPumpkin = !world.getEntitiesOfClass(ArmorStand.class, box, Scarecrows::isWearingPumpkin).isEmpty();

        if (wornPumpkin) {
            return true;
        }

        return hasBlockScarecrow(world, center, radius);
    }

    private static boolean hasBlockScarecrow(Level world, BlockPos center, int radius) {
        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;
        int minY = Math.max(world.getMinY(), center.getY() - radius);
        int maxY = Math.min(world.getMaxY(), center.getY() + radius);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                LevelChunk chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);

                if (chunk == null) {
                    continue;
                }

                for (int index = chunk.getSectionIndex(minY); index <= chunk.getSectionIndex(maxY); index++) {
                    LevelChunkSection section = chunk.getSection(index);

                    if (section.hasOnlyAir() || !section.maybeHas(Scarecrows::isRelevant)) {
                        continue;
                    }

                    int baseY = chunk.getSectionYFromSectionIndex(index) << 4;

                    for (int x = Math.max(minX, chunkX << 4); x <= Math.min(maxX, (chunkX << 4) + 15); x++) {
                        for (int y = Math.max(minY, baseY); y <= Math.min(maxY, baseY + 15); y++) {
                            for (int z = Math.max(minZ, chunkZ << 4); z <= Math.min(maxZ, (chunkZ << 4) + 15); z++) {
                                BlockState state = section.getBlockState(x & 15, y & 15, z & 15);

                                if (!isRelevant(state)) {
                                    continue;
                                }

                                pos.set(x, y, z);

                                if (state.is(TGTags.CROW_REPELLENTS) || isBuiltScarecrow(world, pos)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean isRelevant(BlockState state) {
        return state.is(TGTags.CROW_REPELLENTS) || state.is(TGTags.SCARECROW_HEADS);
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
