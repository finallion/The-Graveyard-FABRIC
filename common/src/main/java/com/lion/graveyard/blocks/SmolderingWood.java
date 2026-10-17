package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/// Shared behavior of the charred and scorched blocks that can smolder.
/// The player lights them with fire and puts them out with a shovel or water.
public final class SmolderingWood {

    public static final BooleanProperty SMOLDERING = BooleanProperty.of("smoldering");
    public static final String ASH_CONFIG_KEY = "ashParticles";

    private static final int LIGHT_LEVEL = 3;
    private static final int ASH_REACH = 6;
    private static final int ASH_PER_CALL = 3;
    private static final int WHITE_ASH_ONE_IN = 8;

    private SmolderingWood() {
    }

    public static int light(BlockState state) {
        return state.get(SMOLDERING) ? LIGHT_LEVEL : 0;
    }

    private static boolean isFlooded(BlockState state) {
        return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED);
    }

    public static ActionResult use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        boolean lit = state.get(SMOLDERING);
        if (!lit && !isFlooded(state) && (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE))) {
            boolean charge = stack.isOf(Items.FIRE_CHARGE);
            SoundEvent sound = charge ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE;
            world.playSound(player, pos, sound, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            if (!world.isClient) {
                setLit(world, pos, state, true);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    TGAdvancements.IGNITE_SMOLDERING.trigger(serverPlayer);
                }
                if (charge) {
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                } else {
                    stack.damage(1, player, user -> user.sendToolBreakStatus(hand));
                }
            }
            return ActionResult.success(world.isClient);
        }
        if (lit && (stack.getItem() instanceof ShovelItem || stack.isOf(Items.WATER_BUCKET))) {
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (world.isClient) {
                for (int i = 0; i < 6; i++) {
                    world.addParticle(ParticleTypes.SMOKE, pos.getX() + world.random.nextDouble(), pos.getY() + 1.0D, pos.getZ() + world.random.nextDouble(), 0.0D, 0.05D, 0.0D);
                }
            } else {
                setLit(world, pos, state, false);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    TGAdvancements.EXTINGUISH_SMOLDERING.trigger(serverPlayer);
                }
                if (!stack.isOf(Items.WATER_BUCKET)) {
                    stack.damage(1, player, user -> user.sendToolBreakStatus(hand));
                }
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static void setLit(World world, BlockPos pos, BlockState state, boolean lit) {
        world.setBlockState(pos, state.with(SMOLDERING, lit));
        if (!state.contains(Properties.DOUBLE_BLOCK_HALF)) {
            return;
        }
        BlockPos other = pos.offset(state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
        BlockState otherState = world.getBlockState(other);
        if (otherState.isOf(state.getBlock()) && otherState.contains(SMOLDERING)) {
            world.setBlockState(other, otherState.with(SMOLDERING, lit));
        }
    }

    public static void extinguish(World world, BlockPos pos, Entity owner) {
        BlockState state = world.getBlockState(pos);
        if (!state.contains(SMOLDERING) || !state.get(SMOLDERING)) {
            return;
        }
        setLit(world, pos, state, false);
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (owner instanceof ServerPlayerEntity player) {
            TGAdvancements.EXTINGUISH_SMOLDERING.trigger(player);
        }
    }

    public static void displayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(SMOLDERING) || isFlooded(state)) {
            return;
        }
        if (random.nextInt(10) == 0) {
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
        }
        if (random.nextInt(2) == 0) {
            world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.25D + random.nextDouble() * 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.25D + random.nextDouble() * 0.5D, 0.0D, 0.04D, 0.0D);
        }
        if (random.nextInt(5) == 0) {
            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, random.nextFloat() / 2.0F, 5.0E-5D, random.nextFloat() / 2.0F);
        }
        ash(world, pos, random);
    }

    public static void ash(World world, BlockPos pos, Random random) {
        if (!Graveyard.getConfig().booleanEntries.getOrDefault(ASH_CONFIG_KEY, true)) {
            return;
        }
        for (int i = 0; i < ASH_PER_CALL; i++) {
            double x = pos.getX() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * ASH_REACH;
            double y = pos.getY() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * ASH_REACH / 2.0D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * ASH_REACH;
            world.addParticle(random.nextInt(WHITE_ASH_ONE_IN) == 0 ? ParticleTypes.WHITE_ASH : ParticleTypes.ASH, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
